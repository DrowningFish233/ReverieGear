package com.drowningfish233.reveriegear.entity;

import com.drowningfish233.reveriegear.registry.RFItems;
import com.drowningfish233.reveriegear.registry.RFDamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.entity.summon.SummonSword;
import org.confluence.terraentity.init.TEAttributes;

public class RFSummonSwordEntity extends SummonSword {

    private float customDamage = 2.0f;

    public RFSummonSwordEntity(EntityType<? extends RFSummonSwordEntity> type, Level level) {
        super(type, level, RFItems.SUMMON_SWORD::gearItem, 0x88CCFF);
    }

    public void setCustomDamage(float damage) {
        this.customDamage = damage;
        var attribute = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attribute != null) {
            attribute.setBaseValue(damage);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SummonSword.createAttributes();
    }

    @Override
    public void summon(Player player, ItemStack stack) {
        super.summon(player, stack);
        var attribute = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attribute != null) {
            attribute.setBaseValue(customDamage);
        }
        this.setCustomName(stack.getHoverName());
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!(target instanceof LivingEntity living)) {
            return super.doHurtTarget(target);
        }

        LivingEntity owner = this.getOwner();
        if (owner == null) {
            return super.doHurtTarget(target);
        }

        if (owner instanceof Player player) {
            double summonBonus = owner.getAttributeValue(TEAttributes.SUMMON_DAMAGE);
            float bonusMultiplier = (float) Math.max(0.0D, summonBonus);
            float finalDamage = customDamage * bonusMultiplier;

            DamageSource damageSource = RFDamageTypes.causeSummonSwordByPlayer(player);
            boolean success = living.hurt(damageSource, finalDamage);

            if (success) {
                living.setLastHurtByPlayer(player);
            }
            return success;
        }

        double summonBonus = owner.getAttributeValue(TEAttributes.SUMMON_DAMAGE);
        float bonusMultiplier = (float) Math.max(0.0D, summonBonus);
        float finalDamage = customDamage * bonusMultiplier;

        return living.hurt(RFDamageTypes.causeSummonSword(owner), finalDamage);
    }
}
