package com.drowningfish233.reveriegear.registry;

import com.drowningfish233.reveriegear.ReverieGear;
import com.drowningfish233.reveriegear.compat.ModCompat;
import com.drowningfish233.reveriegear.entity.RFSummonSwordEntity;
import com.drowningfish233.reveriegear.entity.shotgunpellet.GearPhasmoPellet;
import com.drowningfish233.reveriegear.entity.shotgunpellet.GearShotgunPellet;
import com.drowningfish233.reveriegear.entity.shotgunpellet.GearSlugPellet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RFEntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, ReverieGear.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<RFSummonSwordEntity>> RF_SUMMON_SWORD =
            ModCompat.TERRA_ENTITY
                    ? ENTITIES.register("rf_summon_sword",
                    () -> EntityType.Builder.<RFSummonSwordEntity>of(RFSummonSwordEntity::new, MobCategory.MISC)
                            .sized(0.75f, 0.75f)
                            .clientTrackingRange(64)
                            .updateInterval(2)
                            .build(ResourceLocation.fromNamespaceAndPath(ReverieGear.MODID, "rf_summon_sword").toString()))
                    : null;

    public static final DeferredHolder<EntityType<?>, EntityType<GearShotgunPellet>> GEAR_SHOTGUN_PELLET =
            ModCompat.NETHEREXP
                    ? ENTITIES.register("gear_shotgun_pellet",
                    () -> EntityType.Builder.<GearShotgunPellet>of(GearShotgunPellet::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(64)
                            .updateInterval(2)
                            .build(ResourceLocation.fromNamespaceAndPath(ReverieGear.MODID, "gear_shotgun_pellet").toString()))
                    : null;

    public static final DeferredHolder<EntityType<?>, EntityType<GearSlugPellet>> GEAR_SLUG_PELLET =
            ModCompat.NETHEREXP
                    ? ENTITIES.register("gear_slug_pellet",
                    () -> EntityType.Builder.<GearSlugPellet>of(GearSlugPellet::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(64)
                            .updateInterval(2)
                            .build(ResourceLocation.fromNamespaceAndPath(ReverieGear.MODID, "gear_slug_pellet").toString()))
                    : null;

    public static final DeferredHolder<EntityType<?>, EntityType<GearPhasmoPellet>> GEAR_PHASMO_PELLET =
            ModCompat.NETHEREXP
                    ? ENTITIES.register("gear_phasmo_pellet",
                    () -> EntityType.Builder.<GearPhasmoPellet>of(GearPhasmoPellet::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(64)
                            .updateInterval(2)
                            .build(ResourceLocation.fromNamespaceAndPath(ReverieGear.MODID, "gear_phasmo_pellet").toString()))
                    : null;

    private RFEntityRegistry() {
    }

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        if (RF_SUMMON_SWORD != null) {
            event.put(RF_SUMMON_SWORD.get(), RFSummonSwordEntity.createAttributes().build());
        }
    }
}
