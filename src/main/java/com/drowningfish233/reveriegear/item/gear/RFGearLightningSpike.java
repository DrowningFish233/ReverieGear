package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.ReverieGear;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import io.redspace.ironsspellbooks.api.events.SpellCooldownAddedEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.network.casting.SyncCooldownPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearWeapon;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.client.util.GearColorUtils;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFGearLightningSpike extends Item implements GearWeapon , IPresetSpellContainer {
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.CORD.get(),
            PartTypes.ROD.get()
    ));

    private final Supplier<GearType> gearType;
    public RFGearLightningSpike(Supplier<GearType> gearType) {
        super(GearHelper.getBaseItemProperties()
                .stacksTo(1)
                .durability(500)
                .fireResistant());
        this.gearType = gearType;
    }
    private static AbstractSpell cachedIonicSlashSpell;
    private static AbstractSpell getCachedIonicSlashSpell(){
        if (cachedIonicSlashSpell==null)
            cachedIonicSlashSpell = SpellRegistry.getSpell("hazennstuff:ionic_slash");
        return cachedIonicSlashSpell;
    }
    private static final int IONIC_SLASH_MANA = 30;
    private static final int IONIC_SLASH_MANA_INCREMENT = 15;
    private static final int IONIC_SLASH_BASIC_CD = 200;

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public boolean isValidSlot(String slot) {
        return EquipmentSlot.MAINHAND.getName().equalsIgnoreCase(slot);
    }

    @Override
    public float getRepairModifier(ItemStack stack) {
        return getGearType().armorDurabilityMultiplier();
    }

    @Deprecated
    @OnlyIn(Dist.CLIENT)
    public ItemColor getItemColors() {
        return RFGearLightningSpike::getColor;
    }
    public static int getColor(ItemStack stack, int tintIndex){
        int color;
        switch (tintIndex) {
            case 0 -> color = GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.ROD.get());
            case 1 -> color = GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.MAIN.get());
            case 2 -> color = GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.CORD.get());
            default -> color = -1;
        }
        return color;
    }

    @Override
    public Collection<PartType> getRequiredParts() {
        return REQUIRED_PARTS.get();
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        GearHelper.addAttributeModifiers(stack, builder, true);

        float baseAttackDamage = GearData.getProperties(stack).getNumber(GearProperties.ATTACK_DAMAGE, 1.0F);
        float magicDamage = GearData.getProperties(stack).getNumber(GearProperties.MAGIC_DAMAGE, 0.0F);

        float maxModifier = baseAttackDamage * 1.5F;
        float rawModifier = Math.min(magicDamage, maxModifier);

        float rawBonus = rawModifier * 0.1f;

        float softCap = 0.75f;
        float finalBonus;
        if (rawBonus <= softCap) {
            finalBonus = rawBonus;
        } else {
            float excess = rawBonus - softCap;
            finalBonus = softCap + excess * 0.5f;
        }

        finalBonus *= 0.5f;

        builder.add(AttributeRegistry.LIGHTNING_SPELL_POWER.getDelegate(),
                new AttributeModifier(ReverieGear.getId("lightning_spike_power"),
                        finalBonus,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                EquipmentSlotGroup.MAINHAND);
        return builder.build();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return GearHelper.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        GearHelper.postHurtEnemy(stack, target, attacker);
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
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount,
                                                   @Nullable T entity, Consumer<Item> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity,
                              int slot, boolean selected) {
        GearHelper.inventoryTick(stack, level, entity, slot, selected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack,
                                               boolean slotChanged) {
        return GearClientHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        if (GearHelper.isBroken(stack)) {
            return UseAnim.NONE;
        }
        return UseAnim.BLOCK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (GearHelper.isBroken(stack)) {
            return InteractionResultHolder.pass(stack);
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(stack);
        }

        if (!performIonicSlash(level,player,stack,usedHand))
            return InteractionResultHolder.pass(stack);
        GearHelper.attemptDamage(stack,1,player,usedHand);
        player.getCooldowns().addCooldown(stack.getItem(),10);

        return InteractionResultHolder.consume(stack);
    }

    private boolean performIonicSlash(Level level, Player player, ItemStack stack,InteractionHand hand){
        var spell = getCachedIonicSlashSpell();
        if (spell==null) return false;
        var extraLvl = (int) (GearHelper.getAttackDamageModifier(stack)/10f);
        int mana = IONIC_SLASH_MANA + extraLvl*IONIC_SLASH_MANA_INCREMENT;
        var magicData = MagicData.getPlayerMagicData(player);
        if (magicData.getMana()<mana) return false;
        if (!(player instanceof ServerPlayer serverPlayer)) return false;
        if (magicData.getPlayerCooldowns().isOnCooldown(spell)) return false;
        boolean b = spell.attemptInitiateCast(stack,1+extraLvl,
                level,player, CastSource.SCROLL,false,hand==InteractionHand.OFF_HAND? SpellSelectionManager.OFFHAND:SpellSelectionManager.MAINHAND);
        if (b){
            magicData.addMana(-mana);
            var speed = player.getAttributeValue(Attributes.ATTACK_SPEED.getDelegate());
            addSpecificCooldown(serverPlayer,spell,(int) (IONIC_SLASH_BASIC_CD/speed),CastSource.NONE);
        }
        return b;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        GearClientHelper.addInformation(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.reveriegear.lightning_spike.desc")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("item.reveriegear.lightning_spike.desc2")
                .withStyle(ChatFormatting.AQUA));
    }

    private void addSpecificCooldown(ServerPlayer serverPlayer, AbstractSpell spell,int forcedCooldown, CastSource castSource) {
        int effectiveCooldown = getEffectiveSpecificCooldown(forcedCooldown, serverPlayer);
        SpellCooldownAddedEvent.Pre pre = NeoForge.EVENT_BUS.post(new SpellCooldownAddedEvent.Pre(effectiveCooldown, spell, serverPlayer, castSource));
        if (castSource != CastSource.SCROLL && !pre.isCanceled()) {
            effectiveCooldown = pre.getEffectiveCooldown();
            MagicData.getPlayerMagicData(serverPlayer).getPlayerCooldowns().addCooldown(spell, effectiveCooldown);
            PacketDistributor.sendToPlayer(serverPlayer, new SyncCooldownPacket(spell.getSpellId(), effectiveCooldown));
            NeoForge.EVENT_BUS.post(new SpellCooldownAddedEvent.Post(effectiveCooldown, spell, serverPlayer, castSource));
        }
    }
    public static int getEffectiveSpecificCooldown(int originalCooldown, Player player) {
        double playerCooldownModifier = player.getAttributeValue(AttributeRegistry.COOLDOWN_REDUCTION);
        return (int)(originalCooldown * (2 - Utils.softCapFormula(playerCooldownModifier)));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float gearSpeed = GearHelper.getDestroySpeed(stack, state);
        return state.is(BlockTags.SWORD_EFFICIENT) ? Math.max(15.0F, gearSpeed) : gearSpeed;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        if (GearHelper.isBroken(stack)) {
            return false;
        }
        return ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(ability);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return GearHelper.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return GearHelper.getBarColor(stack);
    }

    @Override
    public void initializeSpellContainer(ItemStack itemStack) {
        if (itemStack == null) {
            return;
        }

        if (!ISpellContainer.isSpellContainer(itemStack)) {
            var spellContainer = ISpellContainer.create(1, true, false).mutableCopy();
            spellContainer.addSpell(getCachedIonicSlashSpell(),1,true);
            ISpellContainer.set(itemStack, spellContainer.toImmutable());
        }
    }

}
