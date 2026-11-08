package com.drowningfish233.reveriegear.entity.shotgunpellet;

import com.drowningfish233.reveriegear.registry.RFEntityRegistry;
import net.jadenxgamer.netherexp.core.entity.PhasmoPellet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class GearPhasmoPellet extends PhasmoPellet {

    private float damage = 1.0f;

    public GearPhasmoPellet(EntityType<? extends PhasmoPellet> entityType, Level level) {
        super(entityType, level);
    }

    public GearPhasmoPellet(double x, double y, double z, Level level, Entity owner, float damage) {
        super(RFEntityRegistry.GEAR_PHASMO_PELLET.get(), level);
        this.setPos(x, y, z);
        this.setOwner(owner);
        this.damage = damage;
    }

    @Override
    protected float getDamage() {
        return this.damage;
    }
}
