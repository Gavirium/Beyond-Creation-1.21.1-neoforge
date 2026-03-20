package net.gavirium.bc.item;

import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;
import java.util.Optional;

public class VoidBucketItem extends Item {

    public VoidBucketItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hitResult.getType() != BlockHitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);}
        BlockPos pos = hitResult.getBlockPos();
        if (!level.mayInteract(player, pos)) {
            return InteractionResultHolder.fail(stack);}
        BlockState blockState = level.getBlockState(pos);
        FluidState fluidState = level.getFluidState(pos);

        // FLUID HANDLING
        
        if (!fluidState.isEmpty() && fluidState.isSource()) {
            Optional<SoundEvent> sound = fluidState.getType().getPickupSound();
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            sound.ifPresent(s ->level.playSound(player, pos, s, SoundSource.BLOCKS, 1.0F, 1.0F));
            level.playSound(player, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.MASTER, 1.0F, 2.0F);
            level.playSound(player, pos, SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.MASTER, 0.4F, 1.4F);
            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SMOKE,pos.getX() + 0.5,pos.getY() + 0.4,pos.getZ() + 0.5,20,0.3, 0.3, 0.3,0.01);}
            level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        // POWDER SNOW HANDLING
        if (blockState.is(Blocks.POWDER_SNOW)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            level.playSound(player, pos, SoundEvents.BUCKET_FILL_POWDER_SNOW, SoundSource.MASTER, 1.0F, 1.0F);
            level.playSound(player, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.MASTER, 1.0F, 2.0F);
            level.playSound(player, pos, SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.MASTER, 0.4F, 1.4F);
            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SMOKE,pos.getX() + 0.5,pos.getY() + 0.4,pos.getZ() + 0.5,20,0.3, 0.3, 0.3,0.01);}
            level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        return InteractionResultHolder.pass(stack);
    }

    // TOOLTIP

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
        	tooltip.add(Component.literal("§8Hold [§fShift§8] for Summary"));
        	tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("§7Sends liquids straight to the"));
            tooltip.add(Component.literal("§7spirit realm. Right-click to delete."));
        } else {
            tooltip.add(Component.literal("§8Hold [§7Shift§8] for Summary"));
        }
    }
}