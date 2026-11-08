package com.drowningfish233.reveriegear.registry;

import com.drowningfish233.reveriegear.ReverieGear;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class RFDamageTypes {

    public static final ResourceKey<DamageType> TIANSHA_STRIKE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(ReverieGear.MODID, "tiansha_strike")
    );

    public static final ResourceKey<DamageType> TIANSHA_RELEASE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(ReverieGear.MODID, "tiansha_release")
    );

    public static final ResourceKey<DamageType> SUMMON_SWORD = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(ReverieGear.MODID, "summon_sword")
    );

    private RFDamageTypes() {
    }

    public static DamageSource causeTianshaStrike(LivingEntity attacker) {
        Holder<DamageType> damageType = attacker.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(TIANSHA_STRIKE);
        return new DamageSource(damageType, attacker);
    }

    public static DamageSource causeTianshaRelease(LivingEntity attacker) {
        Holder<DamageType> damageType = attacker.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(TIANSHA_RELEASE);
        return new DamageSource(damageType, attacker);
    }

    public static DamageSource causeSummonSword(LivingEntity attacker) {
        Holder<DamageType> damageType = attacker.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(SUMMON_SWORD);
        return new DamageSource(damageType, attacker);
    }

    public static DamageSource causeSummonSwordByPlayer(Player player) {
        Holder<DamageType> damageType = player.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(SUMMON_SWORD);
        return new DamageSource(damageType, player, player);
    }
}
