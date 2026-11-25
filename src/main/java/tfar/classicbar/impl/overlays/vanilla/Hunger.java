package tfar.classicbar.impl.overlays.vanilla;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.network.Message;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

public class Hunger extends BarOverlayImpl {

    public Hunger() {
        super("food");
    }

    @Override
    public boolean shouldRender(Player player) {
        // Vampirism support was never finished in the 1.20.1 port either; the compat enum
        // only records whether the mod is present so this stays false when it is absent.
        return !ModCompat.vampirism.loaded;
    }

    @Override
    public void renderBar(GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        double hunger = player.getFoodData().getFoodLevel();
        double maxHunger = 20;

        double barWidthH = getBarWidth(player);

        double currentSat = player.getFoodData().getSaturationLevel();
        double maxSat = maxHunger;
        double barWidthS = getSatBarWidth(player);

        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;

        //Bar background
        Color.reset(graphics);
        renderFullBarBackground(graphics, xStart, yStart);
        //draw portion of bar based on hunger amount
        double f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidthH : 0);

        Color hungerColor = getSecondaryBarColor(0, player);
        Color satColor = getPrimaryBarColor(0, player);

        hungerColor.color2Gl();
        renderPartialBar(graphics, f + 2, yStart + 2, barWidthH);
        if (currentSat > 0 && ClassicBarsConfig.showSaturationBar.get()) {
            //draw saturation
            satColor.color2Gl();
            f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidthS : 0);
            renderPartialBar(graphics, f + 2, yStart + 2, barWidthS);
        }
        //render held food overlay
        renderHeldFood(graphics, player, xStart, yStart, hunger, maxHunger, currentSat, maxSat,
                hungerColor, satColor);

        double exhaustion = player.getFoodData().getExhaustionLevel();
        if (ClassicBarsConfig.showExhaustionOverlay.get() && Message.presentOnServer) {
            exhaustion = Math.min(exhaustion, 4);
            f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - ModUtils.getWidth(exhaustion, 4) : 0);
            //draw exhaustion
            graphics.setColor(1f, 1f, 1f, .25f);
            ModUtils.drawTexturedModalRect(graphics, f + 2, yStart + 1, 1, 28, ModUtils.getWidth(exhaustion, 4f), 9);
            Color.reset(graphics);
        }
    }

    private void renderHeldFood(GuiGraphics graphics, Player player, int xStart, int yStart,
                                double hunger, double maxHunger, double currentSat, double maxSat,
                                Color hungerColor, Color satColor) {
        if (!ClassicBarsConfig.showHeldFoodOverlay.get()) return;

        ItemStack stack = player.getMainHandItem();
        // 1.21.1 moved food data into the FOOD data component; getFoodProperties() is the
        // NeoForge extension that reads it (the old Item#isEdible() no longer exists).
        FoodProperties food = stack.getFoodProperties(player);
        if (food == null) return;

        double time = System.currentTimeMillis() / 1000d * ClassicBarsConfig.transitionSpeed.get();
        double foodAlpha = Math.sin(time) / 2 + .5;

        double hungerOverlay = food.nutrition();
        // In 1.21.1 FoodProperties#saturation is already the finished value
        // (nutrition * saturationModifier * 2), where 1.20.1 exposed the raw modifier.
        double potentialSat = food.saturation();

        //Draw Potential hunger
        double hungerWidth = Math.min(maxHunger - hunger, hungerOverlay);
        //don't render the bar at all if hunger is full
        if (hunger < maxHunger) {
            double w = ModUtils.getWidth(hungerWidth + hunger, maxHunger);

            double f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - w : 0);
            hungerColor.color2Gla((float) foodAlpha);
            renderPartialBar(graphics, f + 2, yStart + 2, w);
        }

        //Draw Potential saturation
        if (ClassicBarsConfig.showSaturationBar.get()) {
            //maximum potential saturation cannot combine with current saturation to go over 20
            double saturationWidth = Math.min(potentialSat, maxSat - currentSat);

            //Potential Saturation cannot go over potential hunger + current hunger combined
            saturationWidth = Math.min(saturationWidth, hunger + hungerWidth);
            saturationWidth = Math.min(saturationWidth, hungerOverlay + hunger);
            if ((potentialSat + currentSat) > (hunger + hungerWidth)) {
                double diff = (potentialSat + currentSat) - (hunger + hungerWidth);
                saturationWidth = potentialSat - diff;
            }
            saturationWidth = Math.max(0, saturationWidth);

            double w = ModUtils.getWidth(saturationWidth + currentSat, maxSat);

            double f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - w : 0);
            satColor.color2Gla((float) foodAlpha);
            renderPartialBar(graphics, f + 2, yStart + 2, w);
        }
        Color.reset(graphics);
    }

    @Override
    public double getBarWidth(Player player) {
        double hunger = player.getFoodData().getFoodLevel();
        double maxHunger = 20;
        return Math.ceil(BarOverlayImpl.WIDTH * hunger / maxHunger);
    }

    public int getSatBarWidth(Player player) {
        double saturation = player.getFoodData().getSaturationLevel();
        double maxSat = 20;
        return (int) Math.ceil(BarOverlayImpl.WIDTH * saturation / maxSat);
    }

    //saturation
    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        boolean hunger = player.hasEffect(MobEffects.HUNGER);
        return hunger ? ConfigCache.saturationDebuff : ConfigCache.saturation;
    }

    //hunger
    @Override
    public Color getSecondaryBarColor(int index, Player player) {
        boolean hunger = player.hasEffect(MobEffects.HUNGER);
        return hunger ? ConfigCache.hungerDebuff : ConfigCache.hunger;
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        double hunger = player.getFoodData().getFoodLevel();
        int c = getSecondaryBarColor(0, player).colorToText();
        textHelper(graphics, xStart, yStart, hunger, c);
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        boolean hungerActive = player.hasEffect(MobEffects.HUNGER);

        int k5 = 52;
        int k6 = 16;
        if (hungerActive) {
            k5 += 36;
            k6 = k5 + 45;
        }
        //hunger background
        ModUtils.drawTexturedModalRect(graphics, xStart, yStart, k6, 27, 9, 9);
        //hunger
        ModUtils.drawTexturedModalRect(graphics, xStart, yStart, k5, 27, 9, 9);
    }
}
