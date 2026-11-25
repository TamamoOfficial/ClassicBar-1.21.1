package tfar.classicbar.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ModUtils {
    public static final Minecraft mc = Minecraft.getInstance();

    /**
     * The vanilla icon sheet as it existed up to 1.20.1.
     *
     * <p>1.21.1 split {@code textures/gui/icons.png} into individual sprites under
     * {@code textures/gui/sprites/hud/}, which would mean rewriting every UV in this mod.
     * Instead the original 1.20.1 sheet is shipped inside the mod jar under our own namespace,
     * so all of Classic Bar's icon coordinates keep working unchanged.
     */
    public static final ResourceLocation VANILLA_ICONS = ResourceLocation.fromNamespaceAndPath("classicbar",
            "textures/gui/icons.png");

    private static final Font fontRenderer = mc.font;

    public static ResourceLocation CURRENT_TEXTURE = VANILLA_ICONS;

    /**
     * Draws one rectangular slice of the current texture.
     *
     * <p>The 1.20.1 helper used {@code 256x256} as the texture size by default, which happens to
     * match both of our sheets ({@code icons.png} and {@code health.png}), so the plain overload
     * keeps the old semantics exactly.
     */
    public static void drawTexturedModalRect(GuiGraphics graphics, double x, int y, int textureX, int textureY,
                                             double width, int height) {
        graphics.blit(CURRENT_TEXTURE, (int) x, y, textureX, textureY, (int) width, height, 256, 256);
    }

    public static double getWidth(double d1, double d2) {
        double ratio = 77 * d1 / d2;
        return Math.ceil(ratio);
    }

    public static int getStringLength(String s) {
        return fontRenderer.width(s);
    }

    public static void drawStringOnHUD(GuiGraphics graphics, String string, int xOffset, int yOffset, int color) {
        xOffset += 2;
        yOffset += 2;
        graphics.drawString(fontRenderer, string, xOffset, yOffset, color, true);
    }
}
