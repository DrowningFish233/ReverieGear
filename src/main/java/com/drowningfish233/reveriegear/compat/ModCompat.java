package com.drowningfish233.reveriegear.compat;

import net.neoforged.fml.ModList;


public final class ModCompat {
    public static final boolean MALUM = ModList.get().isLoaded("malum");
    public static final boolean LODESTONE = ModList.get().isLoaded("lodestone");
    public static final boolean IRONS_SPELLBOOKS = ModList.get().isLoaded("irons_spellbooks");
    public static final boolean CATACLYSM = ModList.get().isLoaded("cataclysm");
    public static final boolean TERRA_ENTITY = ModList.get().isLoaded("terra_entity");
    public static final boolean NETHEREXP = ModList.get().isLoaded("netherexp");
    public static final boolean ELYSIUM_API = ModList.get().isLoaded("elysium_api");
    public static final boolean GECKOLIB = ModList.get().isLoaded("geckolib");
    public static final boolean HAZENNSTUFF = ModList.get().isLoaded("hazennstuff");
    public static final boolean APOTHIC_ATTRIBUTES = ModList.get().isLoaded("apothic_attributes");
    public static final boolean CURIOS = ModList.get().isLoaded("curios");

    private ModCompat() {
    }
}
