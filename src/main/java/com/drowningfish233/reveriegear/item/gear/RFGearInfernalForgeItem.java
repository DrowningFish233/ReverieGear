package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.Utils.GearCombatUtils;
import com.drowningfish233.reveriegear.Utils.gear.RFGearCombatStats;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
import com.github.L_Ender.cataclysm.items.Infernal_forge;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import net.silentchaos512.gear.api.item.GearTool;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.ColorHandlers;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFGearInfernalForgeItem extends Infernal_forge implements GearTool {
    private final Supplier<GearType> gearType;

    public RFGearInfernalForgeItem(Supplier<GearType> gearType, Properties properties) {
        super(GearHelper.DEFAULT_DUMMY_TIER,GearHelper.getBaseItemProperties()
                .stacksTo(1)
                .fireResistant()
                .durability(500)
        );
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return gearType.get();
    }

    @Override
    public Collection<PartType> getRequiredParts() {
        return ImmutableList.of(PartTypes.MAIN.get(), PartTypes.ROD.get());
    }

    @Override
    public boolean isValidSlot(String slot) {
        return EquipmentSlot.MAINHAND.getName().equalsIgnoreCase(slot);
    }

    @Override
    public float getRepairModifier(ItemStack stack) {
        return getGearType().armorDurabilityMultiplier();
    }

    @Override
    public ItemColor getItemColors() {
        return ColorHandlers::getShieldColor;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
        GearClientHelper.addInformation(stack, context, tooltip, flags);
        tooltip.add(Component.translatable("item.reveriegear.infernal_forge.desc").withStyle(ChatFormatting.DARK_GREEN));
        tooltip.add(Component.translatable("item.reveriegear.infernal_forge.desc2").withStyle(ChatFormatting.DARK_GREEN));
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        GearHelper.addAttributeModifiers(stack, builder, true);
        return builder.build();
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (GearHelper.isBroken(stack)) {
            return 0.25F;
        }
        return GearHelper.getDestroySpeed(stack, state);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if (GearHelper.isBroken(stack)) {
            return false;
        }

        GearPropertiesData properties = GearData.getProperties(stack);
        TagKey<Block> toolBlockSet = getToolBlockSet(properties);
        if (toolBlockSet != null) {
            return state.is(toolBlockSet);
        }

        return state.is(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    public TagKey<Block> getToolBlockSet(GearPropertiesData properties) {
        return BlockTags.MINEABLE_WITH_PICKAXE;
    }
    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        if (GearHelper.isBroken(stack)) {
            return false;
        }
        return getGearType().canPerformAction(ability);
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return GearHelper.getEnchantmentValue(stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        GearHelper.setDamage(stack, damage, super::setDamage);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getProperties(stack).getNumberInt(getDurabilityStat());
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!GearHelper.isBroken(stack)) {
            target.playSound(ModSounds.HAMMERTIME.get(), 0.5F, 0.5F);
            target.knockback(1.0F, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
        }
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        GearHelper.postHurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        return GearHelper.onBlockDestroyed(stack, level, state, pos, entity);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        if (player != null && player.getMainHandItem() == stack && !GearHelper.isBroken(stack)) {
            earthQuake(context, player, stack);
            player.getCooldowns().addCooldown(this, RFGearCombatStats.scaleMeleeCooldown(player, stack, 80, 1.0F));
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    private void earthQuake(UseOnContext context, Player player, ItemStack stack) {
        Level world = context.getLevel();
        boolean berserk = player.getMaxHealth() * 1.0F / 2.0F >= player.getHealth();
        double radius = 4.0D;

        float actualDamage = GearCombatUtils.getActualWeaponDamage(player, stack);
        float finalDamage = actualDamage * 2.0F;

        if (world.isClientSide) {
            BlockState block = world.getBlockState(player.blockPosition().below());
            double numberofParticles = radius * 4.0D;

            for (double i = 0.0D; i < 80; i++) {
                double angle = i / numberofParticles * 360.0D;
                double d0 = player.getX() + radius * Mth.sin((float) angle);
                double d1 = player.getY() + 0.15;
                double d2 = player.getZ() + radius * Mth.cos((float) angle);
                double d3 = world.getRandom().nextGaussian() * 0.2D;
                double d4 = world.getRandom().nextGaussian() * 0.2D;
                double d5 = world.getRandom().nextGaussian() * 0.2D;

                world.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, block), d0, d1, d2, d3, d4, d5);
                if (berserk) {
                    world.addParticle(ParticleTypes.FLAME, d0, d1, d2, d3, d4, d5);
                }
            }
        } else {
            ScreenShake_Entity.ScreenShake(world, player.position(), 30, 0.1f, 0, 30);
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.EXPLOSION.get(), SoundSource.PLAYERS,
                    1.5f, 1.0F / (player.getRandom().nextFloat() * 0.4F + 0.8F));

            List<Entity> list = world.getEntities(player, player.getBoundingBox().inflate(radius, radius, radius));
            for (Entity entity : list) {
                if (entity instanceof LivingEntity && entity != player) {
                    entity.hurt(world.damageSources().mobAttack(player), finalDamage);
                    entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.0D, 2.0D, 0.0D));
                    if (berserk) {
                        entity.igniteForSeconds(5);
                    }
                }
            }
        }
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return GearHelper.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return GearHelper.getBarColor(stack);
    }

}
