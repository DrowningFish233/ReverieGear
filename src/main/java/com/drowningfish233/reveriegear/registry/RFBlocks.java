package com.drowningfish233.reveriegear.registry;

import com.drowningfish233.reveriegear.ReverieGear;
import com.drowningfish233.reveriegear.block.forge.GearForgeBlock;
import com.drowningfish233.reveriegear.block.forge.GearForgeBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RFBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(ReverieGear.MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ReverieGear.MODID);

    public static final DeferredBlock<GearForgeBlock> GEAR_FORGE = BLOCKS.register("gear_forge",
            () -> new GearForgeBlock(BlockBehaviour.Properties.of()
                    .strength(3.5F, 6.0F)
                    .sound(SoundType.WOOD)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GearForgeBlockEntity>> GEAR_FORGE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("gear_forge",
                    () -> BlockEntityType.Builder.of(GearForgeBlockEntity::new, GEAR_FORGE.get()).build(null));

    private RFBlocks() {
    }
}
