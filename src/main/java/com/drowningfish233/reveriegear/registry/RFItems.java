package com.drowningfish233.reveriegear.registry;

import com.drowningfish233.reveriegear.ReverieGear;
import com.drowningfish233.reveriegear.compat.ModCompat;
import com.drowningfish233.reveriegear.compat.cataclysm.CataclysmGearSets;
import com.drowningfish233.reveriegear.compat.gecko.GeckoGearSets;
import com.drowningfish233.reveriegear.compat.gecko.RangerGearSets;
import com.drowningfish233.reveriegear.compat.irons.IronsCuriosGearSets;
import com.drowningfish233.reveriegear.compat.irons.IronsGearSets;
import com.drowningfish233.reveriegear.compat.irons.IronsMalumGearSets;
import com.drowningfish233.reveriegear.compat.lodestone.LodestoneGearSets;
import com.drowningfish233.reveriegear.compat.malum.MalumGearSets;
import com.drowningfish233.reveriegear.compat.netherexp.NetherexpGearSets;
import com.drowningfish233.reveriegear.compat.terra.TerraGeckoGearSets;
import com.drowningfish233.reveriegear.compat.terra.TerraGearSets;
import com.drowningfish233.reveriegear.item.gear.PlaceholderGearItem;
import com.drowningfish233.reveriegear.item.gear.RFGearArmorItem;
import com.drowningfish233.reveriegear.item.gear.RFGearFlambergeItem;
import com.drowningfish233.reveriegear.item.gear.RFGearGaleSpinner;
import com.drowningfish233.reveriegear.item.gear.RFGearGreatsword;
import com.drowningfish233.reveriegear.item.gear.RFGearLungeSpearItem;
import com.drowningfish233.reveriegear.item.gear.RFGearModePickaxe;
import com.drowningfish233.reveriegear.item.gear.RFGearWideExcavator;
import com.drowningfish233.reveriegear.registry.gear.RFGearTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.api.item.GearType;

import java.util.List;

public class RFItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(ReverieGear.MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ReverieGear.MODID);

    public static final DeferredItem<net.minecraft.world.item.BlockItem> GEAR_FORGE =
            ITEMS.registerSimpleBlockItem(RFBlocks.GEAR_FORGE);


    public static final GearSet FLAMBERGE = new GearSet(RFGearTypes.FLAMBERGE, "flamberge_blade",
            () -> new RFGearFlambergeItem(RFGearTypes.FLAMBERGE));

    public static final GearSet GREATSWORD = new GearSet(RFGearTypes.GREATSWORD, "greatsword_blade",
            () -> new RFGearGreatsword(RFGearTypes.GREATSWORD));

    public static final GearSet LUNGE_SPEAR = new GearSet(RFGearTypes.LUNGE_SPEAR, "lunge_spear_tip",
            () -> new RFGearLungeSpearItem(RFGearTypes.LUNGE_SPEAR));

    public static final GearSet WIDE_EXCAVATOR = new GearSet(RFGearTypes.WIDE_EXCAVATOR, "wide_excavator_head",
            () -> new RFGearWideExcavator(RFGearTypes.WIDE_EXCAVATOR));

    public static final GearSet MODE_PICKAXE = new GearSet(RFGearTypes.MODE_PICKAXE, "mode_pickaxe_head",
            () -> new RFGearModePickaxe(RFGearTypes.MODE_PICKAXE));

    public static final GearSet GALE_SPINNER = new GearSet(RFGearTypes.GALE_SPINNER, "gale_spinner_blade",
            () -> new RFGearGaleSpinner(RFGearTypes.GALE_SPINNER));

    public static final GearSet HEAVY_HELMET = new GearSet(RFGearTypes.HEAVY_HELMET, "heavy_helmet_plate",
            () -> new RFGearArmorItem(RFGearTypes.HEAVY_HELMET, ArmorItem.Type.HELMET));

    public static final GearSet HEAVY_CHESTPLATE = new GearSet(RFGearTypes.HEAVY_CHESTPLATE, "heavy_chestplate_plate",
            () -> new RFGearArmorItem(RFGearTypes.HEAVY_CHESTPLATE, ArmorItem.Type.CHESTPLATE));

    public static final GearSet HEAVY_LEGGINGS = new GearSet(RFGearTypes.HEAVY_LEGGINGS, "heavy_leggings_plate",
            () -> new RFGearArmorItem(RFGearTypes.HEAVY_LEGGINGS, ArmorItem.Type.LEGGINGS));

    public static final GearSet HEAVY_BOOTS = new GearSet(RFGearTypes.HEAVY_BOOTS, "heavy_boots_plate",
            () -> new RFGearArmorItem(RFGearTypes.HEAVY_BOOTS, ArmorItem.Type.BOOTS));


    public static final GearSet EROSION_SCEPTER = ModCompat.MALUM
            ? MalumGearSets.erosionScepter()
            : placeholder(RFGearTypes.EROSION_SCEPTER, "erosion_scepter_head");

    public static final GearSet SUNDERING_ANCHOR = ModCompat.MALUM
            ? MalumGearSets.sunderingAnchor()
            : placeholder(RFGearTypes.SUNDERING_ANCHOR, "sundering_anchor_blade");

    public static final GearSet MALUM_SCYTHE = ModCompat.MALUM
            ? MalumGearSets.scythe()
            : placeholder(RFGearTypes.SCYTHE, "scythe_blade");

    public static final GearSet MAGIC_SCYTHE = ModCompat.MALUM
            ? MalumGearSets.magicScythe()
            : placeholder(RFGearTypes.MAGIC_SCYTHE, "magic_scythe_blade");

    public static final GearSet DELIVERANCE_SCYTHE = ModCompat.MALUM
            ? MalumGearSets.deliveranceScythe()
            : placeholder(RFGearTypes.DELIVERANCE_SCYTHE, "deliverance_scythe_blade");

    public static final GearSet TYRVING = ModCompat.MALUM
            ? MalumGearSets.tyrving()
            : placeholder(RFGearTypes.TYRVING, "tyrving_blade");

    public static final GearSet HEX_STAFF = ModCompat.MALUM
            ? MalumGearSets.hexStaff()
            : placeholder(RFGearTypes.HEX_STAFF, "hex_staff_head");

    public static final GearSet UNWINDING_CHAOS_STAFF = ModCompat.MALUM
            ? MalumGearSets.unwindingChaosStaff()
            : placeholder(RFGearTypes.UNWINDING_CHAOS_STAFF, "unwinding_chaos_staff_head");

    public static final GearSet WEIGHT_OF_WORLDS = ModCompat.MALUM
            ? MalumGearSets.weightOfWorlds()
            : placeholder(RFGearTypes.WEIGHT_OF_WORLDS, "weight_of_worlds_blade");

    public static final GearSet MALIGNANT_STRONGHOLD_HELMET = ModCompat.MALUM
            ? MalumGearSets.malignantHelmet()
            : placeholder(RFGearTypes.MALIGNANT_STRONGHOLD_HELMET, "malignant_stronghold_helmet_plate");

    public static final GearSet MALIGNANT_STRONGHOLD_CHESTPLATE = ModCompat.MALUM
            ? MalumGearSets.malignantChestplate()
            : placeholder(RFGearTypes.MALIGNANT_STRONGHOLD_CHESTPLATE, "malignant_stronghold_chestplate_plate");

    public static final GearSet MALIGNANT_STRONGHOLD_LEGGINGS = ModCompat.MALUM
            ? MalumGearSets.malignantLeggings()
            : placeholder(RFGearTypes.MALIGNANT_STRONGHOLD_LEGGINGS, "malignant_stronghold_leggings_plate");

    public static final GearSet MALIGNANT_STRONGHOLD_BOOTS = ModCompat.MALUM
            ? MalumGearSets.malignantBoots()
            : placeholder(RFGearTypes.MALIGNANT_STRONGHOLD_BOOTS, "malignant_stronghold_boots_plate");

    public static final GearSet SOUL_HUNTER_HELMET = ModCompat.MALUM
            ? MalumGearSets.soulHunterHelmet()
            : placeholder(RFGearTypes.SOUL_HUNTER_HELMET, "soul_hunter_helmet_plate");

    public static final GearSet SOUL_HUNTER_CHESTPLATE = ModCompat.MALUM
            ? MalumGearSets.soulHunterChestplate()
            : placeholder(RFGearTypes.SOUL_HUNTER_CHESTPLATE, "soul_hunter_chestplate_plate");

    public static final GearSet SOUL_HUNTER_LEGGINGS = ModCompat.MALUM
            ? MalumGearSets.soulHunterLeggings()
            : placeholder(RFGearTypes.SOUL_HUNTER_LEGGINGS, "soul_hunter_leggings_plate");

    public static final GearSet SOUL_HUNTER_BOOTS = ModCompat.MALUM
            ? MalumGearSets.soulHunterBoots()
            : placeholder(RFGearTypes.SOUL_HUNTER_BOOTS, "soul_hunter_boots_plate");

    public static final GearSet CANE = ModCompat.LODESTONE
            ? LodestoneGearSets.cane()
            : placeholder(RFGearTypes.CANE, "cane_head");

    public static final GearSet STAFF = ModCompat.IRONS_SPELLBOOKS
            ? IronsGearSets.staff()
            : placeholder(RFGearTypes.STAFF, "staff_head");

    public static final GearSet AUTOLOADER_CROSSBOW = ModCompat.IRONS_SPELLBOOKS
            ? IronsGearSets.autoloaderCrossbow()
            : placeholder(RFGearTypes.AUTOLOADER_CROSSBOW, "autoloader_crossbow_limb");

    public static final GearSet TIANSHA_STAR_BLADE = ModCompat.IRONS_SPELLBOOKS
            ? IronsGearSets.starBlade()
            : placeholder(RFGearTypes.TIANSHA_STAR_BLADE, "tiansha_star_blade_blade");

    public static final GearSet LIGHTNING_SPIKE = ModCompat.IRONS_SPELLBOOKS
            ? IronsGearSets.lightningSpike()
            : placeholder(RFGearTypes.LIGHTNING_SPIKE, "lightning_spike_blade");

    public static final GearSet SPELL_BOOK = ModCompat.IRONS_SPELLBOOKS && ModCompat.CURIOS
            ? IronsCuriosGearSets.spellBook()
            : placeholder(RFGearTypes.SPELLBOOK, "spellbook_cover");

    public static final GearSet FURIOSO = ModCompat.IRONS_SPELLBOOKS && ModCompat.MALUM
            ? IronsMalumGearSets.furioso()
            : placeholder(RFGearTypes.FURIOSO, "furioso_blade");

    public static final GearSet ROBE_HELMET = ModCompat.IRONS_SPELLBOOKS
            ? IronsGearSets.robeHelmet()
            : placeholder(RFGearTypes.ROBE_HELMET, "robe_helmet_fabric");

    public static final GearSet ROBE_CHESTPLATE = ModCompat.IRONS_SPELLBOOKS
            ? IronsGearSets.robeChestplate()
            : placeholder(RFGearTypes.ROBE_CHESTPLATE, "robe_chestplate_fabric");

    public static final GearSet ROBE_LEGGINGS = ModCompat.IRONS_SPELLBOOKS
            ? IronsGearSets.robeLeggings()
            : placeholder(RFGearTypes.ROBE_LEGGINGS, "robe_leggings_fabric");

    public static final GearSet ROBE_BOOTS = ModCompat.IRONS_SPELLBOOKS
            ? IronsGearSets.robeBoots()
            : placeholder(RFGearTypes.ROBE_BOOTS, "robe_boots_fabric");

    public static final GearSet VOID_ASSAULT_SHOULDER_WEAPON = ModCompat.CATACLYSM
            ? CataclysmGearSets.voidAssault()
            : placeholder(RFGearTypes.VOID_ASSAULT_SHOULDER_WEAPON, "void_assault_shoulder_weapon_head");

    public static final GearSet FLAME_BULWARK = ModCompat.CATACLYSM
            ? CataclysmGearSets.flameBulwark()
            : placeholder(RFGearTypes.FLAME_BULWARK, "flame_bulwark_plate");

    public static final GearSet INFERNAL_FORGE = ModCompat.CATACLYSM
            ? CataclysmGearSets.infernalForge()
            : placeholder(RFGearTypes.INFERNAL_FORGE, "infernal_forge_head");

    public static final GearSet SOUL_SPEAR = ModCompat.CATACLYSM
            ? CataclysmGearSets.soulSpear()
            : placeholder(RFGearTypes.SOUL_SPEAR, "soul_spear_blade");

    public static final GearSet CURSED_BOW = ModCompat.CATACLYSM
            ? CataclysmGearSets.cursedBow()
            : placeholder(RFGearTypes.CURSED_BOW, "cursed_bow_limb");

    public static final GearSet INCINERATOR = ModCompat.CATACLYSM
            ? CataclysmGearSets.incinerator()
            : placeholder(RFGearTypes.INCINERATOR, "incinerator_blade");

    public static final GearSet BRONTES = ModCompat.CATACLYSM
            ? CataclysmGearSets.brontes()
            : placeholder(RFGearTypes.BRONTES, "brontes_head");

    public static final GearSet ANCIENT_SPEAR = ModCompat.CATACLYSM
            ? CataclysmGearSets.ancientSpear()
            : placeholder(RFGearTypes.ANCIENT_SPEAR, "ancient_spear_head");

    public static final GearSet CERAUNUS = ModCompat.CATACLYSM
            ? CataclysmGearSets.ceraunus()
            : placeholder(RFGearTypes.CERAUNUS, "ceraunus_blade");

    public static final GearSet ANNIHILATOR = ModCompat.CATACLYSM
            ? CataclysmGearSets.annihilator()
            : placeholder(RFGearTypes.ANNIHILATOR, "annihilator_blade");

    public static final GearSet MEAT_SHREDDER = ModCompat.CATACLYSM
            ? CataclysmGearSets.meatShredder()
            : placeholder(RFGearTypes.MEAT_SHREDDER, "meat_shredder_blade");

    public static final GearSet SEEKING_BOW = ModCompat.CATACLYSM
            ? CataclysmGearSets.seekingBow()
            : placeholder(RFGearTypes.SEEKING_BOW, "seeking_bow_limb");

    public static final GearSet ASTRAPE = ModCompat.CATACLYSM
            ? CataclysmGearSets.astrape()
            : placeholder(RFGearTypes.ASTRAPE, "astrape_blade");

    public static final GearSet TIDAL_CLAWS = ModCompat.CATACLYSM
            ? CataclysmGearSets.tidalClaws()
            : placeholder(RFGearTypes.TIDAL_CLAWS, "tidal_claws_blade");

    public static final GearSet BOOMERANG = ModCompat.TERRA_ENTITY
            ? TerraGearSets.boomerang()
            : placeholder(RFGearTypes.BOOMERANG, "boomerang_blade");

    public static final GearSet WHIP = ModCompat.TERRA_ENTITY
            ? TerraGearSets.whip()
            : placeholder(RFGearTypes.WHIP, "whip_head");

    public static final GearSet YOYOS = ModCompat.TERRA_ENTITY
            ? TerraGearSets.yoyos()
            : placeholder(RFGearTypes.YOYOS, "yoyos_head");

    public static final GearSet SUMMON_SWORD = ModCompat.TERRA_ENTITY
            ? TerraGearSets.summonSword()
            : placeholder(RFGearTypes.SUMMON_SWORD, "summon_sword_blade");

    private static final boolean GECKO_ARMOR =
            ModCompat.GECKOLIB && ModCompat.HAZENNSTUFF && ModCompat.IRONS_SPELLBOOKS;

    public static final GearSet SUMMONER_HELMET = ModCompat.TERRA_ENTITY && GECKO_ARMOR
            ? TerraGeckoGearSets.summonerHelmet()
            : placeholder(RFGearTypes.SUMMONER_HELMET, "summoner_helmet_plate");

    public static final GearSet SUMMONER_CHESTPLATE = ModCompat.TERRA_ENTITY && GECKO_ARMOR
            ? TerraGeckoGearSets.summonerChestplate()
            : placeholder(RFGearTypes.SUMMONER_CHESTPLATE, "summoner_chestplate_plate");

    public static final GearSet SUMMONER_LEGGINGS = ModCompat.TERRA_ENTITY && GECKO_ARMOR
            ? TerraGeckoGearSets.summonerLeggings()
            : placeholder(RFGearTypes.SUMMONER_LEGGINGS, "summoner_leggings_plate");

    public static final GearSet SUMMONER_BOOTS = ModCompat.TERRA_ENTITY && GECKO_ARMOR
            ? TerraGeckoGearSets.summonerBoots()
            : placeholder(RFGearTypes.SUMMONER_BOOTS, "summoner_boots_plate");

    public static final GearSet MONK_HELMET = GECKO_ARMOR
            ? GeckoGearSets.monkHelmet()
            : placeholder(RFGearTypes.MONK_HELMET, "monk_helmet_plate");

    public static final GearSet MONK_CHESTPLATE = GECKO_ARMOR
            ? GeckoGearSets.monkChestplate()
            : placeholder(RFGearTypes.MONK_CHESTPLATE, "monk_chestplate_plate");

    public static final GearSet MONK_LEGGINGS = GECKO_ARMOR
            ? GeckoGearSets.monkLeggings()
            : placeholder(RFGearTypes.MONK_LEGGINGS, "monk_leggings_plate");

    public static final GearSet MONK_BOOTS = GECKO_ARMOR
            ? GeckoGearSets.monkBoots()
            : placeholder(RFGearTypes.MONK_BOOTS, "monk_boots_plate");

    private static final boolean RANGER_ARMOR = GECKO_ARMOR && ModCompat.APOTHIC_ATTRIBUTES;

    public static final GearSet RANGER_HELMET = RANGER_ARMOR
            ? RangerGearSets.helmet()
            : placeholder(RFGearTypes.RANGER_HELMET, "ranger_helmet_plate");

    public static final GearSet RANGER_CHESTPLATE = RANGER_ARMOR
            ? RangerGearSets.chestplate()
            : placeholder(RFGearTypes.RANGER_CHESTPLATE, "ranger_chestplate_plate");

    public static final GearSet RANGER_LEGGINGS = RANGER_ARMOR
            ? RangerGearSets.leggings()
            : placeholder(RFGearTypes.RANGER_LEGGINGS, "ranger_leggings_plate");

    public static final GearSet RANGER_BOOTS = RANGER_ARMOR
            ? RangerGearSets.boots()
            : placeholder(RFGearTypes.RANGER_BOOTS, "ranger_boots_plate");

    public static final GearSet TRAVELER_HELMET = GECKO_ARMOR
            ? GeckoGearSets.travelerHelmet()
            : placeholder(RFGearTypes.TRAVELER_HELMET, "traveler_helmet_plate");

    public static final GearSet TRAVELER_CHESTPLATE = GECKO_ARMOR
            ? GeckoGearSets.travelerChestplate()
            : placeholder(RFGearTypes.TRAVELER_CHESTPLATE, "traveler_chestplate_plate");

    public static final GearSet TRAVELER_LEGGINGS = GECKO_ARMOR
            ? GeckoGearSets.travelerLeggings()
            : placeholder(RFGearTypes.TRAVELER_LEGGINGS, "traveler_leggings_plate");

    public static final GearSet TRAVELER_BOOTS = GECKO_ARMOR
            ? GeckoGearSets.travelerBoots()
            : placeholder(RFGearTypes.TRAVELER_BOOTS, "traveler_boots_plate");

    public static final GearSet PUMP_CHARGE_SHOTGUN = ModCompat.NETHEREXP && ModCompat.ELYSIUM_API
            ? NetherexpGearSets.pumpChargeShotgun()
            : placeholder(RFGearTypes.PUMP_CHARGE_SHOTGUN, "pump_charge_shotgun_barrel");

    private static GearSet placeholder(DeferredHolder<GearType, GearType> type, String partName) {
        return new GearSet(type, partName, () -> new PlaceholderGearItem(type));
    }


    public static final List<GearSet> ALL_SETS = List.of(
            FLAMBERGE, GREATSWORD, LUNGE_SPEAR, WIDE_EXCAVATOR, MODE_PICKAXE, GALE_SPINNER,
            HEAVY_HELMET, HEAVY_CHESTPLATE, HEAVY_LEGGINGS, HEAVY_BOOTS,
            EROSION_SCEPTER, SUNDERING_ANCHOR, MALUM_SCYTHE, MAGIC_SCYTHE, DELIVERANCE_SCYTHE,
            TYRVING, HEX_STAFF, UNWINDING_CHAOS_STAFF, WEIGHT_OF_WORLDS,
            MALIGNANT_STRONGHOLD_HELMET, MALIGNANT_STRONGHOLD_CHESTPLATE, MALIGNANT_STRONGHOLD_LEGGINGS, MALIGNANT_STRONGHOLD_BOOTS,
            SOUL_HUNTER_HELMET, SOUL_HUNTER_CHESTPLATE, SOUL_HUNTER_LEGGINGS, SOUL_HUNTER_BOOTS,
            CANE,
            STAFF, SPELL_BOOK, AUTOLOADER_CROSSBOW, TIANSHA_STAR_BLADE, LIGHTNING_SPIKE,
            ROBE_HELMET, ROBE_CHESTPLATE, ROBE_LEGGINGS, ROBE_BOOTS, FURIOSO,
            VOID_ASSAULT_SHOULDER_WEAPON, FLAME_BULWARK, INFERNAL_FORGE, SOUL_SPEAR, CURSED_BOW,
            INCINERATOR, BRONTES, ANCIENT_SPEAR, CERAUNUS, ANNIHILATOR, MEAT_SHREDDER,
            SEEKING_BOW, ASTRAPE, TIDAL_CLAWS,
            BOOMERANG, WHIP, YOYOS, SUMMON_SWORD,
            SUMMONER_HELMET, SUMMONER_CHESTPLATE, SUMMONER_LEGGINGS, SUMMONER_BOOTS,
            MONK_HELMET, MONK_CHESTPLATE, MONK_LEGGINGS, MONK_BOOTS,
            RANGER_HELMET, RANGER_CHESTPLATE, RANGER_LEGGINGS, RANGER_BOOTS,
            TRAVELER_HELMET, TRAVELER_CHESTPLATE, TRAVELER_LEGGINGS, TRAVELER_BOOTS,
            PUMP_CHARGE_SHOTGUN
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB_REVERIE_GEAR =
            CREATIVE_MODE_TABS.register("reverie_gear", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.reveriegear"))
                    .icon(() -> FLAMBERGE.gearItem().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(GEAR_FORGE.get());
                        for (GearSet set : ALL_SETS) {
                            acceptGearSet(output, set);
                        }
                    })
                    .build()
            );

    private RFItems() {
    }

    private static void acceptGearSet(CreativeModeTab.Output output, GearSet set) {
        output.accept(set.gearItem());
        output.accept(set.mainPart());
        output.accept(set.blueprint());
        output.accept(set.template());
    }


    public static void register() {
        for (GearSet set : ALL_SETS) {
            set.register(ITEMS);
        }
    }
}
