package net.gavirium.bc.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class AmethystTrowelItem extends Item {

    public AmethystTrowelItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();

        if (player == null) {
            return InteractionResult.PASS;
        }

        // Prevent client-side desync
        if (context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // Collect valid hotbar slots (BlockItems only)
        List<Integer> validSlots = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                validSlots.add(i);
            }
        }

        // No valid blocks - show action bar message
        if (validSlots.isEmpty()) {
            player.displayClientMessage(
                    Component.literal("No placeable blocks in hotbar")
                            .withStyle(style -> style.withColor(0xFF5555)),
                    true
            );
            return InteractionResult.FAIL;
        }

        // Pick random slot
        RandomSource random = player.getRandom();
        int chosenSlot = validSlots.get(random.nextInt(validSlots.size()));

        ItemStack chosenStack = player.getInventory().getItem(chosenSlot);

        if (!(chosenStack.getItem() instanceof BlockItem blockItem)) {
            return InteractionResult.FAIL;
        }

        // Create a single-use placement stack
        ItemStack placementStack = chosenStack.copy();
        placementStack.setCount(1);

        // Rebuild hit result (avoids protected access issue)
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        Vec3 location = context.getClickLocation();

        BlockHitResult hitResult = new BlockHitResult(location, face, pos, false);

        // Create new context with placement stack
        UseOnContext newContext = new UseOnContext(
                context.getLevel(),
                player,
                context.getHand(),
                placementStack,
                hitResult
        );

        // Attempt placement
        InteractionResult result = blockItem.useOn(newContext);

        if (result.consumesAction()) {
            if (!player.getAbilities().instabuild) {
                chosenStack.shrink(1);
            }

            // Play correct placement sound manually
            var block = blockItem.getBlock();
            var soundType = block.defaultBlockState().getSoundType();

            context.getLevel().playSound(null,pos,soundType.getPlaceSound(),SoundSource.BLOCKS,(soundType.getVolume() + 1.0F) / 2.0F,soundType.getPitch() * 0.8F);
            context.getLevel().playSound(null,pos,net.minecraft.sounds.SoundEvents.DEEPSLATE_BREAK,SoundSource.PLAYERS,1.0F,1.2F);
            context.getLevel().playSound(null,pos,net.minecraft.sounds.SoundEvents.GRAVEL_BREAK,SoundSource.PLAYERS,0.1F,1.0F);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
        	tooltip.add(Component.literal("§8Hold [§fShift§8] for Summary"));
        	tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("§7Deletes items permanently."));
            tooltip.add(Component.literal("§7Right-click items to void them."));
        } else {
            tooltip.add(Component.literal("§8Hold [§7Shift§8] for Summary"));
        }
    }
}