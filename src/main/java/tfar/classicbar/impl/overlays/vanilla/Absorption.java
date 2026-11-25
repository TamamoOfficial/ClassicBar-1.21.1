package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;

public class Absorption extends BarOverlayImpl {

    public Absorption() {
        super("absorption");
    }

    @Override
    public boolean shouldRender(Player player) {
        return player.getAbsorptionAmount() > 0;
    }

    @Override
    public void renderBar(GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {

        double absorb = player.getAbsorptionAmount();
        double barWidth = getBarWidth(player);

        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;
        double maxHealth = player.getMaxHealth();

        if (rightHandSide()) {
            xStart += BarOverlayImpl.WIDTH - barWidth;
        }

        int index = Math.min((int) Math.ceil(absorb / maxHealth), ConfigCache.absorption.size()) - 1;
        Color primary = getPrimaryBarColor(index, player);
        Color.reset(graphics);
        //draw background bar
        renderBarBackground(graphics, player, screenWidth, screenHeight, vOffset);
        if (index == 0) {//no wrapping
            primary.color2Gl();
            renderPartialBar(graphics, xStart + 2, yStart + 2, barWidth);
        } else {
            //we have wrapped, draw 2 bars
            Color secondary = getSecondaryBarColor(index - 1, player);
            secondary.color2Gl();
            renderFullBar(graphics, xStart + 2, yStart + 2);
            if (absorb % maxHealth != 0 && index < ConfigCache.absorption.size() - 1) {
                primary.color2Gl();
                renderPartialBar(graphics, xStart + 2, yStart + 2, ModUtils.getWidth(absorb % maxHealth, maxHealth));
            }
        }
    }

    @Override
    public double getBarWidth(Player player) {
        double absorb = player.getAbsorptionAmount();
        double maxHealth = player.getMaxHealth();
        return (int) Math.ceil(BarOverlayImpl.WIDTH * Math.min(maxHealth, absorb) / maxHealth);
    }

    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        HealthEffect effect = getHealthEffect(player);
        return switch (effect) {
            case NONE -> ConfigCache.absorption.get(index);
            case POISON -> ConfigCache.absorptionPoison.get(index);
            case WITHER -> ConfigCache.absorptionWither.get(index);
        };
    }

    @Override
    public boolean isFitted() {
        return !ClassicBarsConfig.fullAbsorptionBar.get();
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {

        double absorb = player.getAbsorptionAmount();
        double maxHealth = player.getMaxHealth();
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        int index = Math.min((int) Math.ceil(absorb / maxHealth), ConfigCache.absorption.size()) - 1;
        Color c = getPrimaryBarColor(index, player);
        textHelper(graphics, xStart, yStart, absorb, c.colorToText());
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;

        int i5 = (player.level().getLevelData().isHardcore()) ? 5 : 0;
        //draw absorption heart background
        ModUtils.drawTexturedModalRect(graphics, xStart, yStart, 16, 9 * i5, 9, 9);
        //absorbing heart
        ModUtils.drawTexturedModalRect(graphics, xStart, yStart, 160, 0, 9, 9);
    }
}
