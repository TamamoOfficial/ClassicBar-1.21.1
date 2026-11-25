package tfar.classicbar.compat;

import net.neoforged.fml.ModList;

public enum ModCompat {
    vampirism, feathers, parcool, toughasnails;

    public final boolean loaded;

    ModCompat() {
        loaded = ModList.get() != null && ModList.get().isLoaded(name());
    }
}
