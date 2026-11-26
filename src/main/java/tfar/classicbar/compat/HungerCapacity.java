package tfar.classicbar.compat;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

/**
 * Reads a player's hunger ceiling from an attribute when one exists, falling back to the vanilla 20.
 *
 * <h2>Why this is a lookup and not a compile dependency</h2>
 * The attribute {@code apotheosis_expansion:max_food} is added by Apotheosis Expansion, which is an optional
 * companion. Classic Bar must keep working with it absent, so the attribute is resolved by id out of
 * {@code BuiltInRegistries.ATTRIBUTE} at the moment it is needed - no {@code compileOnly}, no reflection, no
 * {@code ClassNotFoundException} on a pack that does not ship that mod.
 *
 * <h2>Why a missing attribute must not throw</h2>
 * {@code AttributeMap#getInstance} delegates to {@code AttributeSupplier#createInstance}, which returns {@code null}
 * for an attribute the entity type was never given. {@code Player#getAttributeValue} then fails inside
 * {@code AttributeSupplier#getAttributeInstance} with {@code IllegalArgumentException} rather than returning the
 * attribute's default - so "the mod is not installed" and "the mod is installed but this entity type lacks the
 * attribute" both have to be handled before any value is read.
 *
 * <h2>Why the bar keeps its 77-pixel width</h2>
 * Only the denominator changes, exactly as {@code Health} already does with {@code getMaxHealth()}: a ceiling of 30
 * means a full bar at 30. That keeps one consistent rule across every bar in the HUD - a bar shows how full it is
 * relative to its own maximum - and the raw number next to the bar comes from {@code Hunger#renderText}, which is
 * already unclamped and needs no change.
 */
public final class HungerCapacity {

    /** The attribute id added by Apotheosis Expansion. */
    public static final ResourceLocation MAX_FOOD_ID =
            ResourceLocation.fromNamespaceAndPath("apotheosis_expansion", "max_food");

    /** The vanilla ceiling, used whenever the attribute cannot be resolved. */
    public static final int VANILLA_MAX_FOOD = 20;

    /**
     * Resolved once and cached, because this is read every frame for every player.
     * <p>
     * A {@code null} cache entry means "asked and the attribute is not registered"; that is not re-queried, since
     * the attribute registry is frozen after mod loading and can never gain entries later. {@code resolved} is what
     * distinguishes "not looked up yet" from "looked up, absent".
     */
    private static boolean resolved;
    private static Holder<Attribute> maxFood;

    private HungerCapacity() {}

    /**
     * The player's hunger ceiling in half-drumsticks - 20 unless something raised it.
     *
     * @param player the player being rendered, never {@code null} in the bar pass
     * @return a value of at least 1, so callers can divide by it safely
     */
    public static int maxFood(Player player) {
        Holder<Attribute> attribute = attribute();
        if (attribute == null) {
            return VANILLA_MAX_FOOD;
        }
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            // Player type has no such attribute: the companion mod's EntityAttributeModificationEvent did not run,
            // or this is a client whose attribute supplier predates it. Either way, vanilla applies.
            return VANILLA_MAX_FOOD;
        }
        double value = instance.getValue();
        if (!(value >= 1.0D)) {
            // NaN and non-positive values would make every ratio division meaningless.
            return VANILLA_MAX_FOOD;
        }
        return (int) Math.floor(value);
    }

    /** @return the attribute holder, or {@code null} when the companion mod is absent */
    private static Holder<Attribute> attribute() {
        if (!resolved) {
            resolved = true;
            maxFood = BuiltInRegistries.ATTRIBUTE.getHolder(MAX_FOOD_ID).orElse(null);
        }
        return maxFood;
    }
}
