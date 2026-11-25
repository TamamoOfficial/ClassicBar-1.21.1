package tfar.classicbar;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.network.Message;
import tfar.classicbar.network.SyncHandler;

@Mod(value = ClassicBar.MODID)
public class ClassicBar {

    public static final String MODID = "classicbar";

    public static final Logger logger = LoggerFactory.getLogger(MODID);

    public static final ClassicBarsConfig CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;

    static {
        final Pair<ClassicBarsConfig, ModConfigSpec> specPair =
                new ModConfigSpec.Builder().configure(ClassicBarsConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public ClassicBar(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);

        modBus.addListener(this::registerPayloads);
        modBus.addListener(EventHandler::onConfigChanged);
        modBus.addListener(EventHandler::sendModMessage);

        if (FMLEnvironment.dist.isClient()) {
            modBus.addListener(this::registerGuiLayers);
            NeoForge.EVENT_BUS.addListener(EventHandler::disableOtherOverlays);
            NeoForge.EVENT_BUS.addListener(EventHandler::onClientLoggingIn);
        }

        // Food-data sync runs on the server side only.
        NeoForge.EVENT_BUS.addListener(SyncHandler.instance()::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(SyncHandler.instance()::onPlayerLoggedOut);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Message.NETWORK_VERSION).optional();
        registrar.playToClient(Message.SaturationSync.TYPE, Message.SaturationSync.CODEC,
                Message.SaturationSync::handle);
        registrar.playToClient(Message.ExhaustionSync.TYPE, Message.ExhaustionSync.CODEC,
                Message.ExhaustionSync::handle);
    }

    private void registerGuiLayers(RegisterGuiLayersEvent e) {
        // Vanilla draws the hotbar first and then health/armor/food/air on top of it.
        // Classic Bar replaces all of those, so its layer has to sit above the air layer to
        // be able to occupy the same space. It keeps driving the vanilla leftHeight/rightHeight
        // counters so the selected-item-name overlay still stacks correctly.
        e.registerAbove(VanillaGuiLayers.AIR_LEVEL,
                ResourceLocation.fromNamespaceAndPath(MODID, "bars"),
                EventHandler.INSTANCE);
        EventHandler.setupOverlays();
    }
}
