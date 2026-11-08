package com.drowningfish233.reveriegear.registry.gear;

import com.drowningfish233.reveriegear.ReverieGear;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.property.GearPropertyGroups;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.GearTypes;

public class RFGearTypes {
    public static final DeferredRegister<GearType> REGISTRAR = DeferredRegister.create(
            SgRegistries.GEAR_TYPE,
            ReverieGear.MODID
    );

    public static final DeferredHolder<GearType, GearType> STAFF = REGISTRAR.register("staff",
            () -> GearType.Builder.of(GearTypes.MELEE_WEAPON)
                    .durabilityStat(GearProperties.DURABILITY)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> SPELLBOOK = REGISTRAR.register("spellbook",
            () -> GearType.Builder.of(GearTypes.CURIO)
                    .durabilityStat(GearProperties.DURABILITY)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> HEAVY_HELMET = REGISTRAR.register("heavy_helmet",
            () -> GearType.Builder.of(GearTypes.HELMET)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(14)
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> HEAVY_CHESTPLATE = REGISTRAR.register("heavy_chestplate",
            () -> GearType.Builder.of(GearTypes.CHESTPLATE)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(19)
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> HEAVY_LEGGINGS = REGISTRAR.register("heavy_leggings",
            () -> GearType.Builder.of(GearTypes.LEGGINGS)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(18)
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> HEAVY_BOOTS = REGISTRAR.register("heavy_boots",
            () -> GearType.Builder.of(GearTypes.BOOTS)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(16)
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> ROBE_HELMET = REGISTRAR.register("robe_helmet",
            () -> GearType.Builder.of(GearTypes.HELMET)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(8)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ARMOR,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> ROBE_CHESTPLATE = REGISTRAR.register("robe_chestplate",
            () -> GearType.Builder.of(GearTypes.CHESTPLATE)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(12)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ARMOR,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> ROBE_LEGGINGS = REGISTRAR.register("robe_leggings",
            () -> GearType.Builder.of(GearTypes.LEGGINGS)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(10)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ARMOR,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> ROBE_BOOTS = REGISTRAR.register("robe_boots",
            () -> GearType.Builder.of(GearTypes.BOOTS)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(7)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ARMOR,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> SCYTHE = REGISTRAR.register("scythe",
            () -> GearType.Builder.of(GearTypes.MELEE_WEAPON)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.SWORD_DIG,
                            ItemAbilities.SWORD_SWEEP
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> FLAME_BULWARK = REGISTRAR.register("flame_bulwark",
            () -> GearType.Builder.of(GearTypes.SHIELD)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.SHIELD_BLOCK
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ARMOR,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> INFERNAL_FORGE = REGISTRAR.register("infernal_forge",
            () -> GearType.Builder.of(GearTypes.PICKAXE)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.PICKAXE_DIG,
                            ItemAbilities.AXE_STRIP
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.HARVEST,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> SOUL_SPEAR = REGISTRAR.register("soul_spear",
            () -> GearType.Builder.of(GearTypes.SWORD)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.SWORD_DIG,
                            ItemAbilities.SWORD_SWEEP
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> ANNIHILATOR = REGISTRAR.register("annihilator",
            () -> GearType.Builder.of(GearTypes.SWORD)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.SWORD_DIG,
                            ItemAbilities.SWORD_SWEEP
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> MEAT_SHREDDER = REGISTRAR.register("meat_shredder",
            () -> GearType.Builder.of(GearTypes.AXE)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.AXE_DIG
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.HARVEST,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> SEEKING_BOW = REGISTRAR.register("seeking_bow",
            () -> GearType.Builder.of(GearTypes.BOW)
                    .durabilityStat(GearProperties.DURABILITY)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> AUTOLOADER_CROSSBOW = REGISTRAR.register("autoloader_crossbow",
            () -> GearType.Builder.of(GearTypes.RANGED_WEAPON)
                    .durabilityStat(GearProperties.DURABILITY)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> VOID_ASSAULT_SHOULDER_WEAPON = REGISTRAR.register("void_assault_shoulder_weapon",
            () -> GearType.Builder.of(GearTypes.RANGED_WEAPON)
                    .durabilityStat(GearProperties.DURABILITY)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> CURSED_BOW = REGISTRAR.register("cursed_bow",
            () -> GearType.Builder.of(GearTypes.BOW)
                    .durabilityStat(GearProperties.DURABILITY)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> INCINERATOR = REGISTRAR.register("incinerator",
            () -> GearType.Builder.of(GearTypes.PICKAXE)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.PICKAXE_DIG
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.HARVEST,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> BRONTES = REGISTRAR.register("brontes",
            () -> GearType.Builder.of(GearTypes.PICKAXE)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.PICKAXE_DIG
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.HARVEST,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> CERAUNUS = REGISTRAR.register("ceraunus",
            () -> GearType.Builder.of(GearTypes.SWORD)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.SWORD_DIG,
                            ItemAbilities.SWORD_SWEEP
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> TIDAL_CLAWS = REGISTRAR.register("tidal_claws",
            () -> GearType.Builder.of(GearTypes.SWORD)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.SWORD_DIG,
                            ItemAbilities.SWORD_SWEEP
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> ANCIENT_SPEAR = REGISTRAR.register("ancient_spear",
            () -> GearType.Builder.of(GearTypes.TRIDENT)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.DEFAULT_TRIDENT_ACTIONS
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> ASTRAPE = REGISTRAR.register("astrape",
            () -> GearType.Builder.of(GearTypes.TRIDENT)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.DEFAULT_TRIDENT_ACTIONS
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> FURIOSO = REGISTRAR.register("furioso",
            () -> GearType.Builder.of(GearTypes.TOOL)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.PICKAXE_DIG,
                            ItemAbilities.AXE_DIG,
                            ItemAbilities.SHOVEL_DIG,
                            ItemAbilities.SWORD_DIG
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.HARVEST,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.ARMOR,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> FLAMBERGE = REGISTRAR.register("flamberge",
            () -> GearType.Builder.of(GearTypes.SWORD)
                    .durabilityStat(GearProperties.DURABILITY)
                    .armorDurabilityMultiplier(20)
                    .toolActions(
                            ItemAbilities.SWORD_DIG,
                            ItemAbilities.SWORD_SWEEP
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.ARMOR
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> WIDE_EXCAVATOR = REGISTRAR.register("wide_excavator",
            () -> GearType.Builder.of(GearTypes.PICKAXE)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.PICKAXE_DIG
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.HARVEST,
                            GearPropertyGroups.ATTACK
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> SUNDERING_ANCHOR = REGISTRAR.register("sundering_anchor",
            () -> GearType.Builder.of(GearTypes.SWORD)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.SWORD_DIG,
                            ItemAbilities.SWORD_SWEEP
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> TIANSHA_STAR_BLADE = REGISTRAR.register("tiansha_star_blade",
            () -> GearType.Builder.of(GearTypes.SWORD)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.SWORD_DIG,
                            ItemAbilities.SWORD_SWEEP
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> LIGHTNING_SPIKE = REGISTRAR.register("lightning_spike",
            () -> GearType.Builder.of(GearTypes.SWORD)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.SWORD_DIG,
                            ItemAbilities.SWORD_SWEEP
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> GREATSWORD = REGISTRAR.register("greatsword",
            () -> GearType.Builder.of(GearTypes.SWORD)
                    .durabilityStat(GearProperties.DURABILITY)
                    .armorDurabilityMultiplier(20)
                    .toolActions(
                            ItemAbilities.SWORD_DIG,
                            ItemAbilities.SWORD_SWEEP,
                            ItemAbilities.SHIELD_BLOCK
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.ARMOR,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> SUMMON_SWORD = REGISTRAR.register("summon_sword",
            () -> GearType.Builder.of(GearTypes.SWORD)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.SWORD_DIG,
                            ItemAbilities.SWORD_SWEEP
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> TYRVING = REGISTRAR.register("tyrving",
            () -> GearType.Builder.of(GearTypes.SWORD)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.SWORD_DIG,
                            ItemAbilities.SWORD_SWEEP
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> HEX_STAFF = REGISTRAR.register("hex_staff",
            () -> GearType.Builder.of(GearTypes.MELEE_WEAPON)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.SWORD_DIG
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> UNWINDING_CHAOS_STAFF = REGISTRAR.register("unwinding_chaos_staff",
            () -> GearType.Builder.of(GearTypes.MELEE_WEAPON)
                    .durabilityStat(GearProperties.DURABILITY)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> WEIGHT_OF_WORLDS = REGISTRAR.register("weight_of_worlds",
            () -> GearType.Builder.of(GearTypes.MELEE_WEAPON)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.AXE_DIG,
                            ItemAbilities.SWORD_DIG
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.HARVEST,
                            GearPropertyGroups.ATTACK
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> CANE = REGISTRAR.register("cane",
            () -> GearType.Builder.of(GearTypes.MELEE_WEAPON)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(ItemAbilities.SWORD_DIG)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> YOYOS = REGISTRAR.register("yoyos",
            () -> GearType.Builder.of(GearTypes.MELEE_WEAPON)
                    .durabilityStat(GearProperties.DURABILITY)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> WHIP = REGISTRAR.register("whip",
            () -> GearType.Builder.of(GearTypes.MELEE_WEAPON)
                    .durabilityStat(GearProperties.DURABILITY)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> BOOMERANG = REGISTRAR.register("boomerang",
            () -> GearType.Builder.of(GearTypes.RANGED_WEAPON)
                    .durabilityStat(GearProperties.DURABILITY)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> PUMP_CHARGE_SHOTGUN = REGISTRAR.register("pump_charge_shotgun",
            () -> GearType.Builder.of(GearTypes.RANGED_WEAPON)
                    .durabilityStat(GearProperties.DURABILITY)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> MALIGNANT_STRONGHOLD_HELMET = REGISTRAR.register("malignant_stronghold_helmet",
            () -> GearType.Builder.of(GearTypes.HELMET)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(14)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ARMOR,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> MALIGNANT_STRONGHOLD_CHESTPLATE = REGISTRAR.register("malignant_stronghold_chestplate",
            () -> GearType.Builder.of(GearTypes.CHESTPLATE)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(19)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ARMOR,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> MALIGNANT_STRONGHOLD_LEGGINGS = REGISTRAR.register("malignant_stronghold_leggings",
            () -> GearType.Builder.of(GearTypes.LEGGINGS)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(18)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ARMOR,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> MALIGNANT_STRONGHOLD_BOOTS = REGISTRAR.register("malignant_stronghold_boots",
            () -> GearType.Builder.of(GearTypes.BOOTS)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(16)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ARMOR,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> SOUL_HUNTER_HELMET = REGISTRAR.register("soul_hunter_helmet",
            () -> GearType.Builder.of(GearTypes.HELMET)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(14)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ARMOR,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> SOUL_HUNTER_CHESTPLATE = REGISTRAR.register("soul_hunter_chestplate",
            () -> GearType.Builder.of(GearTypes.CHESTPLATE)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(19)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ARMOR,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> SOUL_HUNTER_LEGGINGS = REGISTRAR.register("soul_hunter_leggings",
            () -> GearType.Builder.of(GearTypes.LEGGINGS)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(18)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ARMOR,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> SOUL_HUNTER_BOOTS = REGISTRAR.register("soul_hunter_boots",
            () -> GearType.Builder.of(GearTypes.BOOTS)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(16)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ARMOR,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> MAGIC_SCYTHE = REGISTRAR.register("magic_scythe",
            () -> GearType.Builder.of(GearTypes.MELEE_WEAPON)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.SWORD_DIG,
                            ItemAbilities.SWORD_SWEEP
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> DELIVERANCE_SCYTHE = REGISTRAR.register("deliverance_scythe",
            () -> GearType.Builder.of(GearTypes.MELEE_WEAPON)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.SWORD_DIG,
                            ItemAbilities.SWORD_SWEEP
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> MODE_PICKAXE = REGISTRAR.register("mode_pickaxe",
            () -> GearType.Builder.of(GearTypes.PICKAXE)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.PICKAXE_DIG
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.HARVEST,
                            GearPropertyGroups.ATTACK
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> EROSION_SCEPTER = REGISTRAR.register("erosion_scepter",
            () -> GearType.Builder.of(GearTypes.MELEE_WEAPON)
                    .durabilityStat(GearProperties.DURABILITY)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK,
                            RFGearPropertyGroups.SPELL
                    )
                    .build()
    );

    public static final DeferredHolder<GearType, GearType> GALE_SPINNER = REGISTRAR.register("gale_spinner",
            () -> GearType.Builder.of(GearTypes.SWORD)
                    .durabilityStat(GearProperties.DURABILITY)
                    .toolActions(
                            ItemAbilities.SWORD_DIG,
                            ItemAbilities.SWORD_SWEEP
                    )
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ATTACK
                    )
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> SUMMONER_HELMET = armorSet(
            "summoner_helmet", GearTypes.HELMET, 14,
            GearPropertyGroups.SPECIAL, GearPropertyGroups.GENERAL,
            GearPropertyGroups.ARMOR, GearPropertyGroups.ATTACK
    );

    public static final DeferredHolder<GearType, GearType> SUMMONER_CHESTPLATE = armorSet(
            "summoner_chestplate", GearTypes.CHESTPLATE, 19,
            GearPropertyGroups.SPECIAL, GearPropertyGroups.GENERAL,
            GearPropertyGroups.ARMOR, GearPropertyGroups.ATTACK
    );

    public static final DeferredHolder<GearType, GearType> SUMMONER_LEGGINGS = armorSet(
            "summoner_leggings", GearTypes.LEGGINGS, 18,
            GearPropertyGroups.SPECIAL, GearPropertyGroups.GENERAL,
            GearPropertyGroups.ARMOR, GearPropertyGroups.ATTACK
    );

    public static final DeferredHolder<GearType, GearType> SUMMONER_BOOTS = armorSet(
            "summoner_boots", GearTypes.BOOTS, 16,
            GearPropertyGroups.SPECIAL, GearPropertyGroups.GENERAL,
            GearPropertyGroups.ARMOR, GearPropertyGroups.ATTACK
    );

    public static final DeferredHolder<GearType, GearType> MONK_HELMET = armorSet(
            "monk_helmet", GearTypes.HELMET, 14,
            GearPropertyGroups.SPECIAL, GearPropertyGroups.GENERAL, GearPropertyGroups.ARMOR
    );

    public static final DeferredHolder<GearType, GearType> MONK_CHESTPLATE = armorSet(
            "monk_chestplate", GearTypes.CHESTPLATE, 19,
            GearPropertyGroups.SPECIAL, GearPropertyGroups.GENERAL, GearPropertyGroups.ARMOR
    );

    public static final DeferredHolder<GearType, GearType> MONK_LEGGINGS = armorSet(
            "monk_leggings", GearTypes.LEGGINGS, 18,
            GearPropertyGroups.SPECIAL, GearPropertyGroups.GENERAL, GearPropertyGroups.ARMOR
    );

    public static final DeferredHolder<GearType, GearType> MONK_BOOTS = armorSet(
            "monk_boots", GearTypes.BOOTS, 16,
            GearPropertyGroups.SPECIAL, GearPropertyGroups.GENERAL, GearPropertyGroups.ARMOR
    );

    public static final DeferredHolder<GearType, GearType> RANGER_HELMET = armorSet(
            "ranger_helmet", GearTypes.HELMET, 14,
            GearPropertyGroups.SPECIAL, GearPropertyGroups.GENERAL,
            GearPropertyGroups.ARMOR, GearPropertyGroups.PROJECTILE
    );

    public static final DeferredHolder<GearType, GearType> RANGER_CHESTPLATE = armorSet(
            "ranger_chestplate", GearTypes.CHESTPLATE, 19,
            GearPropertyGroups.SPECIAL, GearPropertyGroups.GENERAL,
            GearPropertyGroups.ARMOR, GearPropertyGroups.PROJECTILE
    );

    public static final DeferredHolder<GearType, GearType> RANGER_LEGGINGS = armorSet(
            "ranger_leggings", GearTypes.LEGGINGS, 18,
            GearPropertyGroups.SPECIAL, GearPropertyGroups.GENERAL,
            GearPropertyGroups.ARMOR, GearPropertyGroups.PROJECTILE
    );

    public static final DeferredHolder<GearType, GearType> RANGER_BOOTS = armorSet(
            "ranger_boots", GearTypes.BOOTS, 16,
            GearPropertyGroups.SPECIAL, GearPropertyGroups.GENERAL,
            GearPropertyGroups.ARMOR, GearPropertyGroups.PROJECTILE
    );

    public static final DeferredHolder<GearType, GearType> TRAVELER_HELMET = armorSet(
            "traveler_helmet", GearTypes.HELMET, 12,
            GearPropertyGroups.SPECIAL, GearPropertyGroups.GENERAL, GearPropertyGroups.ARMOR
    );

    public static final DeferredHolder<GearType, GearType> TRAVELER_CHESTPLATE = armorSet(
            "traveler_chestplate", GearTypes.CHESTPLATE, 16,
            GearPropertyGroups.SPECIAL, GearPropertyGroups.GENERAL, GearPropertyGroups.ARMOR
    );

    public static final DeferredHolder<GearType, GearType> TRAVELER_LEGGINGS = armorSet(
            "traveler_leggings", GearTypes.LEGGINGS, 15,
            GearPropertyGroups.SPECIAL, GearPropertyGroups.GENERAL, GearPropertyGroups.ARMOR
    );

    public static final DeferredHolder<GearType, GearType> TRAVELER_BOOTS = armorSet(
            "traveler_boots", GearTypes.BOOTS, 14,
            GearPropertyGroups.SPECIAL, GearPropertyGroups.GENERAL, GearPropertyGroups.ARMOR
    );


    public static final DeferredHolder<GearType, GearType> LUNGE_SPEAR = REGISTRAR.register("lunge_spear",
            () -> GearType.Builder.of(GearTypes.SPEAR)
                    .toolActions(
                            ItemAbilities.SWORD_DIG
                    )
                    .build()
    );

    private static DeferredHolder<GearType, GearType> armorSet(String name, java.util.function.Supplier<GearType> base,
                                                               int durability,
                                                               net.silentchaos512.gear.api.property.GearPropertyGroup... groups) {
        return REGISTRAR.register(name, () -> {
            var builder = GearType.Builder.of(base)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(durability);
            if (groups.length > 0) {
                builder.relevantPropertyGroups(groups);
            }
            return builder.build();
        });
    }

}
