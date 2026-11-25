package tfar.classicbar;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.overlays.mod.StaminaB;
import tfar.classicbar.impl.overlays.vanilla.*;
import tfar.classicbar.network.Message;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Owns the bar registry, the ordered render pass, and the vanilla HUD layer suppression.
 *
 * <p>1.21.1 replaced Forge's {@code IGuiOverlay}/{@code ForgeGui} pair with vanilla
 * {@link LayeredDraw.Layer}s. The bar pass is registered as a single layer above the vanilla
 * air layer and still drives the vanilla {@code leftHeight}/{@code rightHeight} counters, so
 * everything downstream (selected item name, effects, ...) keeps stacking on top of it.
 */
public class EventHandler implements LayeredDraw.Layer {

    public static final EventHandler INSTANCE = new EventHandler();

    private static final List<BarOverlay> all = new ArrayList<>();
    public static final Map<String, BarOverlay> registry = new HashMap<>();

    private static final List<BarOverlay> errored = new ArrayList<>();

    private EventHandler() {}

    public static void register(BarOverlay iBarOverlay) {
        registry.put(iBarOverlay.name(), iBarOverlay);
    }

    public static void registerAll(BarOverlay... iBarOverlay) {
        Arrays.stream(iBarOverlay).forEach(overlay -> {
            if (overlay != null) {
                registry.put(overlay.name(), overlay);
            }
        });
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker partialTick) {
        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.getCameraEntity();
        if (!(entity instanceof Player player)) return;
        if (player.getAbilities().instabuild || player.isSpectator()) return;

        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        mc.getProfiler().push("classicbars_hud");

        for (BarOverlay overlay : all) {
            boolean rightHand = overlay.rightHandSide();
            try {
                overlay.render(graphics, player, screenWidth, screenHeight, getOffset(rightHand));
            } catch (Throwable e) {
                ClassicBar.logger.error("Removing broken overlay {}", overlay.name(), e);
                errored.add(overlay);
            }
        }
        if (!errored.isEmpty()) all.removeAll(errored);

        // The bar pass sets its own tints; make sure nothing leaks into later HUD layers.
        Color.reset(graphics);
        ModUtils.CURRENT_TEXTURE = ModUtils.VANILLA_ICONS;

        mc.getProfiler().pop();
    }

    public static void increment(boolean side, int amount) {
        Minecraft mc = Minecraft.getInstance();
        if (side) mc.gui.rightHeight += amount;
        else mc.gui.leftHeight += amount;
    }

    public static int getOffset(boolean right) {
        Minecraft mc = Minecraft.getInstance();
        return right ? mc.gui.rightHeight : mc.gui.leftHeight;
    }

    public static void cacheConfigs() {
        all.clear();
        List<? extends String> left = ClassicBarsConfig.leftorder.get();
        if (left != null) {
            left.stream().filter(s -> registry.get(s) != null)
                    .forEach(e -> all.add(registry.get(e).setSide(false)));
        }
        List<? extends String> right = ClassicBarsConfig.rightorder.get();
        if (right != null) {
            right.stream().filter(s -> registry.get(s) != null)
                    .forEach(e -> all.add(registry.get(e).setSide(true)));
        }
        all.removeAll(errored);
        ConfigCache.bake();
        ClassicBar.logger.info("Classic Bar render order: left={} right={} active={}", left, right,
                all.stream().map(BarOverlay::name).toList());
    }

    public static void sendModMessage(net.neoforged.fml.event.lifecycle.InterModEnqueueEvent e) {
        net.neoforged.fml.InterModComms.sendTo("vampirism", "disable-blood-bar", () -> true);
    }

    /** Registers every bar. Called once from {@link ClassicBar} on the mod bus. */
    public static void setupOverlays() {
        ClassicBar.logger.info("Registering Vanilla Overlays");
        EventHandler.registerAll(new Absorption(), new Air(), new Armor(), new ArmorToughness(),
                new Health(), new Hunger(), new MountHealth());

        ClassicBar.logger.info("Registering Mod Overlays");
        if (ModCompat.parcool.loaded) EventHandler.register(new StaminaB());

        // The client config is loaded on a worker thread and {@code ModConfigEvent} can fire
        // BEFORE this method runs, which would leave the ordered render list empty forever.
        // Always (re)build it once the registry is populated.
        cacheConfigs();
    }

    /** Fired on the mod bus once the client config has been read (and again on every reload). */
    public static void onConfigChanged(net.neoforged.fml.event.config.ModConfigEvent event) {
        cacheConfigs();
        ClassicBar.logger.info("Syncing Classic Bar Configs");
    }

    private static final List<ResourceLocation> vanilla_overlays = List.of(VanillaGuiLayers.AIR_LEVEL,
            VanillaGuiLayers.ARMOR_LEVEL, VanillaGuiLayers.PLAYER_HEALTH, VanillaGuiLayers.VEHICLE_HEALTH,
            VanillaGuiLayers.FOOD_LEVEL);

    public static void disableOtherOverlays(RenderGuiLayerEvent.Pre e) {
        ResourceLocation overlay = e.getName();
        if (vanilla_overlays.contains(overlay)) {
            e.setCanceled(true);
        } else if (overlay.getNamespace().equals("parcool") && StaminaB.checkConfigs()) {
            e.setCanceled(true);
        }
    }

    public static void onClientLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        Message.onLoggedIn(event.getConnection());
    }
}
