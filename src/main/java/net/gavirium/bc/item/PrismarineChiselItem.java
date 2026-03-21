package net.gavirium.bc.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PrismarineChiselItem extends Item {

    public PrismarineChiselItem() {
        super(new Item.Properties().stacksTo(1).durability(512));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (player == null) return InteractionResult.PASS;

        BlockPos pos = context.getClickedPos();
        BlockState oldState = level.getBlockState(pos);

        // === BLACKLIST ===
        if (oldState.is(Blocks.BEDROCK)) return InteractionResult.PASS;
        if (level.getBlockEntity(pos) != null) return InteractionResult.PASS;
        if (oldState.isAir()) return InteractionResult.PASS;

        // === GET HOTBAR ITEM (RIGHT SLOT) ===
        int currentSlot = player.getInventory().selected;
        int targetSlot = (currentSlot + 1) % 9;

        ItemStack rightStack = player.getInventory().getItem(targetSlot);

        // === VALIDATE BLOCK ITEM ===
        if (rightStack.isEmpty() || !(rightStack.getItem() instanceof BlockItem blockItem)) {
            if (level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("No placeable block in right slot"),
                        true
                );
            }
            return InteractionResult.FAIL;
        }

        // === GET PLACEMENT STATE ===
        BlockPlaceContext placeContext = new BlockPlaceContext(context);
        BlockState newState = blockItem.getBlock().getStateForPlacement(placeContext);
        if (newState == null) return InteractionResult.PASS;

        // === PRESERVE WATERLOGGING ===
        if (oldState.hasProperty(BlockStateProperties.WATERLOGGED)
                && newState.hasProperty(BlockStateProperties.WATERLOGGED)) {
            boolean wasWaterlogged = oldState.getValue(BlockStateProperties.WATERLOGGED);
            newState = newState.setValue(BlockStateProperties.WATERLOGGED, wasWaterlogged);
        }

        // === PREVENT SAME BLOCK REPLACEMENT ===
        if (oldState.equals(newState)) return InteractionResult.FAIL;

        // === PERFORM REPLACEMENT ===
        if (!level.isClientSide) {

            // Break particles without sound
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, oldState),
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        20,
                        0.25, 0.25, 0.25,
                        0.05
                );
            }

            // Drop old block (survival only)
            if (!player.getAbilities().instabuild && level instanceof ServerLevel serverLevel) {
                for (ItemStack drop : Block.getDrops(oldState, serverLevel, pos, null)) {
                    ItemEntity itemEntity = new ItemEntity(
                            level,
                            pos.getX() + 0.5,
                            pos.getY() + 1.1,
                            pos.getZ() + 0.5,
                            drop
                    );
                    level.addFreshEntity(itemEntity);
                }
            }

            // Place replacement
            level.setBlock(pos, newState, 3);

            // Play placement sound
            SoundType soundType = newState.getSoundType();
            level.playSound(null,pos,soundType.getPlaceSound(),SoundSource.BLOCKS,(soundType.getVolume() + 1.0F) / 2.0F,soundType.getPitch() * 0.8F);
            level.playSound(null, pos, SoundEvents.NETHER_BRICKS_PLACE, SoundSource.MASTER, 0.8F, 1.5F);
            level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.MASTER, 0.32F, 2.0F);

            // Consume placed block item
            if (!player.getAbilities().instabuild) {
                rightStack.shrink(1);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}