package tfar.classicbar.config;

import com.google.common.collect.Lists;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class ClassicBarsConfig {

    static ModConfigSpec.BooleanValue displayIcons;
    public static ModConfigSpec.BooleanValue displayToughnessBar;
    public static ModConfigSpec.BooleanValue fullAbsorptionBar;
    public static ModConfigSpec.BooleanValue fullArmorBar;
    public static ModConfigSpec.BooleanValue fullToughnessBar;
    public static ModConfigSpec.BooleanValue lowArmorWarning;
    public static ModConfigSpec.BooleanValue showSaturationBar;
    public static ModConfigSpec.BooleanValue showHydrationBar;
    public static ModConfigSpec.BooleanValue showHeldFoodOverlay;
    public static ModConfigSpec.BooleanValue showHeldDrinkOverlay;
    public static ModConfigSpec.BooleanValue showExhaustionOverlay;
    public static ModConfigSpec.BooleanValue showThirstExhaustionOverlay;

    public static ModConfigSpec.DoubleValue transitionSpeed;
    static ModConfigSpec.ConfigValue<String> hungerBarColor;
    static ModConfigSpec.ConfigValue<String> hungerBarDebuffColor;
    static ModConfigSpec.ConfigValue<String> saturationBarColor;
    static ModConfigSpec.ConfigValue<String> saturationBarDebuffColor;
    static ModConfigSpec.ConfigValue<String> thirstBarColor;
    static ModConfigSpec.ConfigValue<String> thirstBarDebuffColor;
    static ModConfigSpec.ConfigValue<String> hydrationBarColor;
    static ModConfigSpec.ConfigValue<String> hydrationBarDebuffColor;
    static ModConfigSpec.ConfigValue<String> airBarColor;
    static ModConfigSpec.ConfigValue<List<? extends String>> armorColors;
    static ModConfigSpec.ConfigValue<List<? extends String>> armorToughnessColors;
    static ModConfigSpec.ConfigValue<List<? extends String>> absorptionColors;
    static ModConfigSpec.ConfigValue<List<? extends String>> absorptionPoisonColors;
    static ModConfigSpec.ConfigValue<List<? extends String>> absorptionWitherColors;
    public static ModConfigSpec.ConfigValue<List<? extends Double>> normalFractions;
    static ModConfigSpec.ConfigValue<List<? extends String>> normalColors;
    public static ModConfigSpec.ConfigValue<List<? extends Double>> poisonedFractions;
    static ModConfigSpec.ConfigValue<List<? extends String>> poisonedColors;
    public static ModConfigSpec.ConfigValue<List<? extends Double>> witheredFractions;
    static ModConfigSpec.ConfigValue<List<? extends String>> witheredColors;
    public static ModConfigSpec.ConfigValue<String> lavaBarColor;
    public static ModConfigSpec.ConfigValue<String> flightBarColor;

    public static ModConfigSpec.ConfigValue<List<? extends String>> leftorder;
    public static ModConfigSpec.ConfigValue<List<? extends String>> rightorder;

    public ClassicBarsConfig(ModConfigSpec.Builder builder) {
        builder.push("general");
        displayIcons = builder.comment("Whether to show icons next to the bars")
                .define("display_icons", true);

        displayToughnessBar = builder.define("display_toughness_bar", false);
        fullAbsorptionBar = builder.define("full_absorption_bar", false);
        fullArmorBar = builder.define("full_armor_bar", false);
        fullToughnessBar = builder.define("full_toughness_bar", false);
        lowArmorWarning = builder.define("display_low_armor_warning", true);

        showSaturationBar = builder.define("show_saturation_bar", true);
        showHydrationBar = builder.define("show_hydration_bar", true);
        showHeldFoodOverlay = builder.define("show_held_food_overlay", true);
        showHeldDrinkOverlay = builder.define("show_held_drink_overlay", true);
        showExhaustionOverlay = builder.define("show_exhaustion_overlay", true);
        showThirstExhaustionOverlay = builder.define("show_thirst_exhaustion_overlay", true);
        transitionSpeed = builder.defineInRange("transition_speed", 3d, 0d, Double.MAX_VALUE);

        hungerBarColor = builder.define("hunger_bar_color", "#B34D00", String.class::isInstance);
        hungerBarDebuffColor = builder.define("hunger_bar_debuff_color", "#249016", String.class::isInstance);
        thirstBarColor = builder.define("thirstr_bar_color", "#1C5EE4", String.class::isInstance);
        thirstBarDebuffColor = builder.define("thirst_bar_debuff_color", "#5A891C", String.class::isInstance);
        airBarColor = builder.define("air_bar_color", "#00E6E6", String.class::isInstance);
        saturationBarColor = builder.define("saturation_bar_color", "#FFCC00", String.class::isInstance);
        saturationBarDebuffColor = builder.define("saturation_bar_debuff_color", "#87BC00", String.class::isInstance);
        hydrationBarColor = builder.define("hydration_bar_color", "#00A3E2", String.class::isInstance);
        hydrationBarDebuffColor = builder.define("hydration_bar_debuff_color", "#85CF25", String.class::isInstance);
        lavaBarColor = builder.define("lava_bar_color", "#FF8000", String.class::isInstance);
        flightBarColor = builder.define("flight_bar_color", "#FFFFFF", String.class::isInstance);

        armorColors = builder.defineList("armor_color_values", Lists.newArrayList(
                "#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"), String.class::isInstance);
        armorToughnessColors = builder.defineList("armor_toughness_color_values", Lists.newArrayList(
                "#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"), String.class::isInstance);
        absorptionColors = builder.defineList("absorption_color_values", Lists.newArrayList(
                "#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D",
                "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"), String.class::isInstance);
        absorptionPoisonColors = builder.defineList("absorption_poison_color_values", Lists.newArrayList(
                "#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D",
                "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"), String.class::isInstance);
        absorptionWitherColors = builder.defineList("absorption_wither_color_values", Lists.newArrayList(
                "#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D",
                "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"), String.class::isInstance);

        normalColors = builder.defineList("normal_colors",
                Lists.newArrayList("#FF0000", "#FFFF00", "#00FF00"), String.class::isInstance);
        normalFractions = builder.defineList("normal_fractions",
                Lists.newArrayList(.25d, .5d, .75d), Double.class::isInstance);
        poisonedColors = builder.defineList("poisoned_colors",
                Lists.newArrayList("#00FF00", "#55FF55", "#00FF00"), String.class::isInstance);
        poisonedFractions = builder.defineList("poisoned_fractions",
                Lists.newArrayList(.25d, .5d, .75d), Double.class::isInstance);
        witheredColors = builder.defineList("withered_colors",
                Lists.newArrayList("#555555", "#AAAAAA", "#555555"), String.class::isInstance);
        witheredFractions = builder.defineList("withered_fractions",
                Lists.newArrayList(.25d, .5d, .75d), Double.class::isInstance);

        leftorder = builder.defineList("left_order",
                Lists.newArrayList("health", "armor", "absorption", "lavacharm", "lavacharm2"), String.class::isInstance);
        rightorder = builder.defineList("right_order",
                Lists.newArrayList("blood", "health_mount", "food", "thirst_level", "parcool:stamina",
                        "feathers", "armor_toughness", "air", "flighttiara", "decay"), String.class::isInstance);
        builder.pop();
    }
}
