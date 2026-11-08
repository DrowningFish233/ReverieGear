package com.drowningfish233.reveriegear.compat.terra;

import com.drowningfish233.reveriegear.item.gear.RFGearSummonerArmor;
import com.drowningfish233.reveriegear.registry.GearSet;
import com.drowningfish233.reveriegear.registry.gear.RFGearTypes;
import net.minecraft.world.item.ArmorItem;


public final class TerraGeckoGearSets {
    private TerraGeckoGearSets() {
    }

    public static GearSet summonerHelmet() {
        return new GearSet(RFGearTypes.SUMMONER_HELMET, "summoner_helmet_plate",
                () -> new RFGearSummonerArmor(RFGearTypes.SUMMONER_HELMET, ArmorItem.Type.HELMET));
    }

    public static GearSet summonerChestplate() {
        return new GearSet(RFGearTypes.SUMMONER_CHESTPLATE, "summoner_chestplate_plate",
                () -> new RFGearSummonerArmor(RFGearTypes.SUMMONER_CHESTPLATE, ArmorItem.Type.CHESTPLATE));
    }

    public static GearSet summonerLeggings() {
        return new GearSet(RFGearTypes.SUMMONER_LEGGINGS, "summoner_leggings_plate",
                () -> new RFGearSummonerArmor(RFGearTypes.SUMMONER_LEGGINGS, ArmorItem.Type.LEGGINGS));
    }

    public static GearSet summonerBoots() {
        return new GearSet(RFGearTypes.SUMMONER_BOOTS, "summoner_boots_plate",
                () -> new RFGearSummonerArmor(RFGearTypes.SUMMONER_BOOTS, ArmorItem.Type.BOOTS));
    }
}
