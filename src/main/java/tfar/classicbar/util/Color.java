package tfar.classicbar.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;

/**
 * A plain RGB colour holder.
 *
 * <p>On 1.20.1 this class pushed the colour into {@code RenderSystem} directly. That still works
 * on 1.21.1, but {@code GuiGraphics#setColor} is preferred because it flushes the pending batch
 * first — necessary here since the bar texture and the vanilla icon sheet are batched separately.
 */
public class Color {
    public final int r, g, b;
    public static final Color BLACK = Color.from(0, 0, 0);
    public static final Color RED = Color.from(0xff, 0, 0);
    public static final Color YELLOW = Color.from(0xff, 0xff, 0);

    protected Color(int red, int green, int blue) {
        this.r = red;
        this.g = green;
        this.b = blue;
    }

    public static Color from(int red, int green, int blue) {
        return new Color(red, green, blue);
    }

    public static Color from(String s) {
        return BLACK;
    }

    /** Applies this colour as the current shader tint, fully opaque. */
    public void color2Gl() {
        RenderSystem.setShaderColor(this.r / 255f, this.g / 255f, this.b / 255f, 1f);
    }

    /** Applies this colour as the current shader tint with the given alpha. */
    public void color2Gla(float a) {
        RenderSystem.setShaderColor(this.r / 255f, this.g / 255f, this.b / 255f, a);
    }

    /**
     * Applies this colour through the {@link GuiGraphics} batch so that pending geometry is
     * flushed before the tint changes. Preferred over {@link #color2Gl()} while drawing.
     */
    public void apply(GuiGraphics graphics) {
        graphics.setColor(this.r / 255f, this.g / 255f, this.b / 255f, 1f);
    }

    /** @see #apply(GuiGraphics) */
    public void apply(GuiGraphics graphics, float a) {
        graphics.setColor(this.r / 255f, this.g / 255f, this.b / 255f, a);
    }

    public Color colorBlend(Color c2, double d) {
        int r = (int) Math.floor(this.r * (1 - d) + c2.r * d);
        int g = (int) Math.floor(this.g * (1 - d) + c2.g * d);
        int b = (int) Math.floor(this.b * (1 - d) + c2.b * d);
        return Color.from(r, g, b);
    }

    public int colorToText() {
        return (this.r << 16) + (this.g << 8) + this.b;
    }

    /** Restores an untinted, fully opaque shader colour. */
    public static void reset(GuiGraphics graphics) {
        graphics.setColor(1f, 1f, 1f, 1f);
    }

    /** Restores an untinted, fully opaque shader colour without a graphics context. */
    public static void reset() {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    @Override
    public String toString() {
        return String.format("#%02X%02X%02X", r, g, b);
    }
}
