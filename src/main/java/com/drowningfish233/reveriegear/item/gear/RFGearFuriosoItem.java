package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.Utils.GearCombatUtils;
import com.drowningfish233.reveriegear.Utils.gear.RFGearHelper;
import com.drowningfish233.reveriegear.handler.TianshaMaxHealthHandler;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.sammy.malum.core.handlers.enchantment.AscensionHandler;
import com.sammy.malum.registry.common.MalumAttributes;
import com.sammy.malum.registry.common.MalumParticleEffectTypes;
import com.sammy.malum.registry.common.enchantment.EnchantmentKeys;
import com.sammy.malum.registry.common.item.MalumItems;
import com.sammy.malum.visual_effects.networked.MalumNetworkedWeaponParticleEffectType;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearWeapon;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.ColorHandlers;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.client.util.GearColorUtils;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.item.gear.GearPickaxeItem;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import team.lodestar.lodestone.helpers.CurioHelper;
import team.lodestar.lodestone.helpers.SoundHelper;
import team.lodestar.lodestone.systems.network.WeaponParticleEffectType;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RFGearFuriosoItem extends GearPickaxeItem implements GearWeapon {
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get()
    ));

    private final Supplier<GearType> gearType;
    private static final Random RANDOM = new Random();

    private static List<Holder<MobEffect>> allEffects = null;
    private static List<Holder<MobEffect>> harmfulEffects = null;
    private static List<Holder<MobEffect>> beneficialEffects = null;

    private enum EffectType {
        EXTRA_DAMAGE,
        APPLY_DEBUFF,
        TRUE_DAMAGE,
        HEAL_SELF,
        APPLY_BUFF,
        KNOCKBACK,
        EXTRA_PERCENT,
        SPACE_CLEAVE
    }

    public RFGearFuriosoItem(Supplier<GearType> gearType) {
        super(gearType);
        this.gearType = gearType;
        initEffectCache();
    }

    private static void initEffectCache() {
        if (allEffects == null) {
            allEffects = BuiltInRegistries.MOB_EFFECT.holders()
                    .collect(Collectors.toList());

            harmfulEffects = allEffects.stream()
                    .filter(holder -> holder.value().getCategory() == MobEffectCategory.HARMFUL)
                    .collect(Collectors.toList());

            beneficialEffects = allEffects.stream()
                    .filter(holder -> holder.value().getCategory() == MobEffectCategory.BENEFICIAL)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public TagKey<Block> getToolBlockSet(GearPropertiesData properties) {
        return SgTags.Blocks.MINEABLE_WITH_PAXEL;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public Collection<PartType> getRequiredParts() {
        return REQUIRED_PARTS.get();
    }

    @Override
    public boolean isValidSlot(String slot) {
        return EquipmentSlot.MAINHAND.getName().equalsIgnoreCase(slot);
    }

    @Override
    public ItemColor getItemColors() {
        return ColorHandlers::getShieldColor;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getProperties(stack).getNumberInt(getDurabilityStat());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        GearClientHelper.addInformation(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.reveriegear.furioso.desc")
                .withStyle(ChatFormatting.DARK_GREEN));
        tooltip.add(Component.translatable("item.reveriegear.furioso.desc2")
                .withStyle(ChatFormatting.GOLD));
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        if (GearHelper.isBroken(stack)) {
            return false;
        }
        return getGearType().canPerformAction(ability);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (GearHelper.isBroken(context.getItemInHand()) || context.getPlayer() != null && context.getPlayer().isCrouching())
            return InteractionResult.PASS;

        InteractionResult result = GearHelper.onItemUse(context);
        if (result == InteractionResult.PASS) {
            return GearHelper.useAndCheckBroken(context, Items.NETHERITE_AXE::useOn);
        }
        return result;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (EnchantmentKeys.getEnchantmentLevel(level, EnchantmentKeys.ASCENSION, stack) > 0) {
            AscensionHandler.triggerAscension(level, player, hand, stack);
            return InteractionResultHolder.success(stack);
        }

        if (EnchantmentKeys.getEnchantmentLevel(level, EnchantmentKeys.REBOUND, stack) > 0) {
            return InteractionResultHolder.pass(stack);
        }

        return super.use(level, player, hand);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();

        GearHelper.addAttributeModifiers(stack, builder, true);

        EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.MAINHAND;
        var resourcelocation = ResourceLocation.withDefaultNamespace("furioso_armor");

        float armor = GearData.getProperties(stack)
                .getNumber(GearProperties.ARMOR, 0.0F);
        if (armor > 0) {
            builder.add(Attributes.ARMOR,
                    new AttributeModifier(resourcelocation, armor, AttributeModifier.Operation.ADD_VALUE),
                    equipmentSlotGroup);
        }

        float armorToughness = GearData.getProperties(stack)
                .getNumber(GearProperties.ARMOR_TOUGHNESS, 0.0F) / 4f;
        if (armorToughness > 0) {
            builder.add(Attributes.ARMOR_TOUGHNESS,
                    new AttributeModifier(resourcelocation, armorToughness, AttributeModifier.Operation.ADD_VALUE),
                    equipmentSlotGroup);
        }

        float knockbackResistance = GearData.getProperties(stack)
                .getNumber(GearProperties.KNOCKBACK_RESISTANCE, 0.0F) / 10f;
        if (knockbackResistance > 0) {
            builder.add(Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(resourcelocation, knockbackResistance, AttributeModifier.Operation.ADD_VALUE),
                    equipmentSlotGroup);
        }

        float spellPower = RFGearHelper.getSpellPower(stack);
        if (spellPower > 0) {
            builder.add(AttributeRegistry.SPELL_POWER,
                    new AttributeModifier(resourcelocation, spellPower,
                            AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
        }

        float spellResist = RFGearHelper.getSpellResist(stack);
        if (spellResist > 0) {
            builder.add(AttributeRegistry.SPELL_RESIST,
                    new AttributeModifier(resourcelocation, spellResist,
                            AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
        }

        float manaRegen = RFGearHelper.getManaRegen(stack);
        if (manaRegen > 0) {
            builder.add(AttributeRegistry.MANA_REGEN,
                    new AttributeModifier(resourcelocation, manaRegen,
                            AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
        }

        float maxMana = RFGearHelper.getMaxMana(stack);
        if (maxMana > 0) {
            builder.add(AttributeRegistry.MAX_MANA,
                    new AttributeModifier(resourcelocation, maxMana,
                            AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
        }

        float geasLimitBonus = RFGearHelper.getGeasLimitBonus(stack);
        if (geasLimitBonus > 0) {
            builder.add(MalumAttributes.GEAS_LIMIT,
                    new AttributeModifier(resourcelocation, geasLimitBonus,
                            AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
        }

        float healingMultiplier = RFGearHelper.getHealingReceivedMultiplier(stack);
        if (healingMultiplier > 0f) {
            builder.add(MalumAttributes.HEALING_MULTIPLIER,
                    new AttributeModifier(resourcelocation, healingMultiplier,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE), equipmentSlotGroup);
        }

        return builder.build();
    }

    private int getMainPartColor(ItemStack stack) {
        PartType mainType = PartTypes.MAIN.get();
        return GearColorUtils.getBlendedColorForPartInGear(stack, mainType);
    }

    public static boolean canSweep(LivingEntity attacker) {
        return !CurioHelper.hasCurioEquipped(attacker, MalumItems.NECKLACE_OF_THE_HIDDEN_BLADE.get());
    }

    public static void trySweep(LivingEntity attacker, LivingEntity target, float damage) {
        if (!canSweep(attacker)) return;
        Level level = attacker.level();
        if (level.isClientSide) return;
        AABB aabb = target.getBoundingBox().inflate(1.0D, 0.25D, 1.0D);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb);
        for (LivingEntity entity : entities) {
            if (entity != attacker && entity != target && !attacker.isAlliedTo(entity)) {
                entity.hurt(attacker.damageSources().mobAttack(attacker), damage * 0.5F);
            }
        }
    }

    public void outgoingDamageEvent(LivingDamageEvent.Pre event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        Level level = attacker.level();
        if (level instanceof ServerLevel serverLevel) {
            int mainPartColor = getMainPartColor(stack);
            float damage = event.getNewDamage();
            MalumNetworkedWeaponParticleEffectType.MalumWeaponParticleEffectBuilder<WeaponParticleEffectType.WeaponParticleEffectData> particle =
                    MalumParticleEffectTypes.SCYTHE_SLASH.createEffect()
                            .originatesFrom(attacker)
                            .targets(target)
                            .color(new Color(mainPartColor))
                            .upwardOffset(-0.4F)
                            .forwardOffset(0.8F);
            if (canSweep(attacker)) {
                SoundHelper.playSound(attacker, SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
                particle.mirroredRandomly(attacker.getRandom()).spawn(serverLevel);
                trySweep(attacker, target, damage);
            } else {
                SoundHelper.playSound(attacker, SoundEvents.PLAYER_ATTACK_STRONG, 1.0F, 0.75F);
                particle.verticalSlashRotation().horizontalOffset(0.6F).spawn(serverLevel);
            }
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);

        if (!GearHelper.isBroken(stack) && attacker instanceof Player player) {
            float baseDamage = getBaseDamage(stack, attacker);
            EffectType effect = getRandomEffect();

            if (!player.level().isClientSide && player.level() instanceof ServerLevel serverLevel) {
                int mainPartColor = getMainPartColor(stack);
                MalumNetworkedWeaponParticleEffectType.MalumWeaponParticleEffectBuilder<WeaponParticleEffectType.WeaponParticleEffectData> particle =
                        MalumParticleEffectTypes.SCYTHE_SLASH.createEffect()
                                .originatesFrom(player)
                                .targets(target)
                                .color(new Color(mainPartColor))
                                .upwardOffset(-0.4F)
                                .forwardOffset(0.8F);
                if (canSweep(player)) {
                    SoundHelper.playSound(player, SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
                    particle.mirroredRandomly(player.getRandom()).spawn(serverLevel);
                    AABB aabb = target.getBoundingBox().inflate(1.0D, 0.25D, 1.0D);
                    List<LivingEntity> entities = serverLevel.getEntitiesOfClass(LivingEntity.class, aabb);
                    for (LivingEntity entity : entities) {
                        if (entity != player && entity != target && !player.isAlliedTo(entity)) {
                            entity.hurt(player.damageSources().playerAttack(player), baseDamage * 0.5F);
                        }
                    }
                } else {
                    SoundHelper.playSound(player, SoundEvents.PLAYER_ATTACK_STRONG, 1.0F, 0.75F);
                    particle.verticalSlashRotation().horizontalOffset(0.6F).spawn(serverLevel);
                }
            }

            applyEffect(effect, stack, target, player, baseDamage);
        }

        return result;
    }

    private EffectType getRandomEffect() {
        EffectType[] effects = EffectType.values();
        return effects[RANDOM.nextInt(effects.length)];
    }

    private void applyEffect(EffectType effect, ItemStack stack, LivingEntity target, Player attacker, float baseDamage) {
        Level level = attacker.level();

        switch (effect) {
            case EXTRA_DAMAGE:
                float extraDamage = baseDamage * 0.3f;
                float radius = 1.5f;
                level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(radius))
                        .stream()
                        .filter(e -> e != target && e != attacker)
                        .forEach(e -> e.hurt(attacker.damageSources().playerAttack(attacker), extraDamage));
                target.hurt(attacker.damageSources().playerAttack(attacker), extraDamage);
                spawnParticles(level, target);
                break;

            case APPLY_DEBUFF:
                GearCombatUtils.applyRandomDebuff(target, 100, 1);
                break;

            case TRUE_DAMAGE:
                float trueDamage = target.getMaxHealth() * 0.1f;
                float newHealth = target.getHealth() - trueDamage;
                if (newHealth <= 0) {
                    target.hurt(attacker.damageSources().playerAttack(attacker), Float.MAX_VALUE);
                } else {
                    target.setHealth(newHealth);
                }
                spawnParticles(level, target);
                break;

            case HEAL_SELF:
                float healAmount = attacker.getMaxHealth() * 0.05f;
                attacker.heal(healAmount);
                attacker.playSound(SoundEvents.PLAYER_LEVELUP, 0.5f, 1.0f);
                break;

            case APPLY_BUFF:
                GearCombatUtils.applyRandomBuff(attacker, 200, 0);
                break;

            case KNOCKBACK:
                float maxHealth = target.getMaxHealth();
                float reductionAmount = maxHealth * 0.25f;
                TianshaMaxHealthHandler.reduceMaxHealth(target, reductionAmount, attacker);
                TianshaMaxHealthHandler.spawnCutEffect(target, reductionAmount);
                attacker.playSound(SoundRegistry.DARK_SPELL_02.get(), 1.0f, 0.8f);
                break;

            case EXTRA_PERCENT:
                float extraPercentDamage = baseDamage * 0.1f;
                target.hurt(attacker.damageSources().playerAttack(attacker), extraPercentDamage);
                break;

            case SPACE_CLEAVE:
                float cleaveDamage = baseDamage * 3.0f;
                target.hurt(attacker.damageSources().playerAttack(attacker), cleaveDamage);

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            ParticleRegistry.BLOOD_PARTICLE.get(),
                            target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                            60, 2.5, 1.5, 2.5, 0.4
                    );

                    serverLevel.sendParticles(
                            ParticleRegistry.SNOWFLAKE_PARTICLE.get(),
                            target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                            40, 2.0, 1.0, 2.0, 0.3
                    );

                    attacker.playSound(SoundRegistry.ICE_IMPACT.get(), 1.5f, 0.8f);
                }

                break;

            default:
                break;
        }
    }

    private void spawnParticles(Level level, LivingEntity target) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME,
                    target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                    20, 0.5, 0.5, 0.5, 0.1
            );

            serverLevel.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.SOUL,
                    target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                    15, 0.8, 0.8, 0.8, 0.05
            );
        }
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        boolean result = super.mineBlock(stack, level, state, pos, entity);

        if (!level.isClientSide && entity instanceof Player player && !player.isCreative()) {
            if (isLog(state)) {
                breakTree(stack, level, player, pos, state);
            } else {
                breakAOE(stack, level, player, pos, state);
            }
        }

        return result;
    }

    private void breakAOE(ItemStack stack, Level level, Player player, BlockPos pos, BlockState state) {
        if (player.getAbilities().instabuild) return;

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;

                    BlockPos targetPos = pos.offset(x, y, z);
                    BlockState targetState = level.getBlockState(targetPos);

                    if (targetState.getDestroySpeed(level, targetPos) >= 0) {
                        level.destroyBlock(targetPos, true, player);
                        super.mineBlock(stack, level, targetState, targetPos, player);
                    }
                }
            }
        }
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (GearHelper.isBroken(stack)) {
            return 0.0F;
        }

        float speed = GearHelper.getDestroySpeed(stack, state);
        return Math.max(4.0F, speed);
    }

    private void breakTree(ItemStack stack, Level level, Player player, BlockPos pos, BlockState state) {
        if (player.getAbilities().instabuild) return;

        TreeBreakResult result = new TreeBreakResult(stack, player);
        breakTreeRecursive(result, level, pos, pos, 0);
    }

    private void breakTreeRecursive(TreeBreakResult result, Level level, BlockPos pos, BlockPos startPos, int depth) {
        if (depth > 100) return;

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos localPos = pos.offset(x, y, z);
                    BlockState localState = level.getBlockState(localPos);

                    boolean isLog = localState.is(BlockTags.LOGS);
                    boolean isLeaf = localState.is(BlockTags.LEAVES) || localState.getBlock() instanceof LeavesBlock;

                    if (isLog || isLeaf) {
                        if (result.firstLog == null && isLog) {
                            result.firstLog = localState.getBlock();
                        }

                        if (!result.player.getAbilities().instabuild) {
                            level.destroyBlock(localPos, true, result.player);
                            super.mineBlock(result.tool, level, localState, localPos, result.player);
                            result.blocksBroken++;
                        }

                        breakTreeRecursive(result, level, localPos, startPos, depth + 1);
                    }
                }
            }
        }
    }

    private boolean isLog(BlockState state) {
        return state.is(BlockTags.LOGS);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return GearHelper.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return GearHelper.getBarColor(stack);
    }

    private float getBaseDamage(ItemStack stack, LivingEntity attacker) {
        return GearCombatUtils.getActualWeaponDamage(attacker, stack);
    }

    private static class TreeBreakResult {
        final ItemStack tool;
        final Player player;
        int blocksBroken;
        Block firstLog;

        TreeBreakResult(ItemStack tool, Player player) {
            this.tool = tool;
            this.player = player;
        }
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        GearHelper.postHurtEnemy(stack, target, attacker);
    }

}
