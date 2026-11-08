package com.drowningfish233.reveriegear.entity.shotgunpellet;

import com.drowningfish233.reveriegear.registry.RFEntityRegistry;
import net.jadenxgamer.netherexp.core.entity.SlugPellet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class GearSlugPellet extends SlugPellet {

    private float damage = 1.0f;

    public GearSlugPellet(EntityType<? extends SlugPellet> entityType, Level level) {
        super(entityType, level);
    }

    public GearSlugPellet(double x, double y, double z, Level level, Entity owner, float damage) {
        super(RFEntityRegistry.GEAR_SLUG_PELLET.get(), level);
        this.setPos(x, y, z);
        this.setOwner(owner);
        this.damage = damage;
    }

    @Override
    protected float getDamage() {
        return this.damage;
    }
}
