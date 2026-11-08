package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.registry.gear.RFGearProperties;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFGearSpellBookItem extends SpellBook implements GearItem, IPresetSpellContainer {
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.CORD.get(),
            PartTypes.LINING.get()
    ));

    private final Supplier<GearType> gearType;
    private final String curioSlot;

    public RFGearSpellBookItem(Supplier<GearType> gearType, String curioSlot, Properties properties) {
        super(3, properties);
        this.gearType = gearType;
        this.curioSlot = curioSlot;
    }

    public String getCurioSlot() {
        return curioSlot;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public boolean isValidSlot(String slot) {
        return this.curioSlot.equalsIgnoreCase(slot);
    }

    @Override
    public Collection<PartType> getRequiredParts() {
        return REQUIRED_PARTS.get();
    }

    @Override
    public int getMaxSpellSlots() {
        return 3;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @NotNull
    @Override
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(SoundRegistry.EQUIP_SPELL_BOOK.get(), 1.0f, 1.0f);
    }

    @Override
    public void initializeSpellContainer(ItemStack itemStack) {
        if (itemStack == null) {
            return;
        }

        if (!ISpellContainer.isSpellContainer(itemStack)) {
            int maxSlots = getGearSpellSlots(itemStack);
            ISpellContainer.set(itemStack, ISpellContainer.create(maxSlots, true, true));
        }
    }

    private int getGearSpellSlots(ItemStack stack) {
        if (!GearHelper.isGear(stack)) {
            return 3;
        }
        int slots = GearData.getProperties(stack).getNumberInt(RFGearProperties.SPELL_SLOTS);
        return Math.max(1, slots);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        GearHelper.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

        if (!worldIn.isClientSide && entityIn instanceof LivingEntity) {
            syncSpellSlots(stack);
        }
    }

    private void syncSpellSlots(ItemStack stack) {
        if (!GearHelper.isGear(stack)) return;

        int gearSlots = getGearSpellSlots(stack);

        if (!ISpellContainer.isSpellContainer(stack)) {
            ISpellContainer.set(stack, ISpellContainer.create(gearSlots, true, true));
            return;
        }

        ISpellContainer container = ISpellContainer.get(stack);
        int currentSlots = container.getMaxSpellCount();

        if (gearSlots != currentSlots) {
            ISpellContainerMutable mutable = container.mutableCopy();

            if (gearSlots > currentSlots) {
                mutable.setMaxSpellCount(gearSlots);
            } else if (gearSlots < currentSlots) {
                List<SpellSlot> activeSpells = mutable.getActiveSpells();

                for (int i = currentSlots - 1; i >= gearSlots; i--) {
                    SpellData spellData = mutable.getSpellAtIndex(i);
                    if (spellData != SpellData.EMPTY) {
                        mutable.removeSpellAtIndex(i);
                    }
                }

                mutable.setMaxSpellCount(gearSlots);
            }

            ISpellContainer.set(stack, mutable.toImmutable());
        }
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> map = LinkedHashMultimap.create();

        if (GearHelper.isBroken(stack)) {
            return map;
        }

        var properties = GearData.getProperties(stack);

        float spellPower = properties.getNumber(RFGearProperties.SPELL_POWER);
        if (spellPower != 0) {
            map.put(AttributeRegistry.SPELL_POWER,
                    new AttributeModifier(id, spellPower, AttributeModifier.Operation.ADD_VALUE));
        }

        float maxMana = properties.getNumber(RFGearProperties.MAX_MANA);
        if (maxMana != 0) {
            map.put(AttributeRegistry.MAX_MANA,
                    new AttributeModifier(id, maxMana, AttributeModifier.Operation.ADD_VALUE));
        }

        return map;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return super.getDefaultAttributeModifiers(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (!ModList.get().isLoaded(Const.CURIOS)) {
            tooltip.add(TextUtil.misc("curiosNotInstalled").withStyle(ChatFormatting.RED));
        }

        GearClientHelper.addInformation(stack, context, tooltip, flagIn);
        super.appendHoverText(stack, context, tooltip, flagIn);
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
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return GearClientHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
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
