package tfar.classicbar.impl.overlays.mod;

import com.alrex.parcool.api.stamina.IReadableStamina;
import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.hud.stamina.HUDType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;

/**
 * ParCool stamina bar.
 *
 * <p>Written against ParCool 4.x for 1.21.1: stamina is read through
 * {@link IReadableStamina} and the HUD replacement is gated on ParCool's own HUD type being
 * {@code Light}, exactly like the 1.20.1 port did. Registered only when {@code parcool} is
 * present, so this class is never loaded without it.
 */
public class StaminaB extends BarOverlayImpl {

    public static final String NAME = "parcool:stamina";

    public StaminaB() {
        super(NAME);
    }

    @Override
    public boolean shouldRender(Player player) {
        if (!checkConfigs()) return false;
        IReadableStamina stamina = IReadableStamina.get(player);
        if (stamina == null) return false;
        return stamina.max() > stamina.value();
    }

    /** Whether ParCool defers to a custom HUD (i.e. its own bar would only draw a light overlay). */
    public static boolean checkConfigs() {
        return ParCool.getConfig().client().staminaHud.type().get() == HUDType.Light;
    }

    @Override
    public void renderBar(GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;
        double barWidth = getBarWidth(player);
        Color.reset(graphics);
        renderFullBarBackground(graphics, xStart, yStart);
        double f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidth : 0);
        getPrimaryBarColor(0, player).color2Gl();
        renderPartialBar(graphics, f + 2, yStart + 2, barWidth);
    }

    @Override
    public double getBarWidth(Player player) {
        IReadableStamina stamina = IReadableStamina.get(player);
        if (stamina == null || stamina.max() <= 0) return 0;
        return Math.ceil(BarOverlayImpl.WIDTH * stamina.value() / stamina.max());
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        return Color.YELLOW;
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        IReadableStamina stamina = IReadableStamina.get(player);
        if (stamina == null) return;
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        // The 1.20.1 port divided by 20 so the number reads like a hunger bar.
        textHelper(graphics, xStart, yStart, stamina.value() / 20d, getPrimaryBarColor(0, player).colorToText());
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        IReadableStamina stamina = IReadableStamina.get(player);
        int textureX = (stamina != null && stamina.isExhausted()) ? 16 : 0;
        graphics.blit(getIconRL(), xStart, yStart, textureX, 119, 8, 9, 128, 128);
    }

    @Override
    public void bindIconTexture() {
        // ParCool ships its own icon sheet; BarOverlayImpl#getIconRL is final and points at the
        // vanilla sheet, so the override has to happen here (this is also what the 1.20.1
        // port's ModUtils.ICONS constant achieved).
        tfar.classicbar.util.ModUtils.CURRENT_TEXTURE = PARCOOL_ICONS;
    }

    private static final net.minecraft.resources.ResourceLocation PARCOOL_ICONS =
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("parcool",
                    "textures/gui/stamina_bar.png");
}
