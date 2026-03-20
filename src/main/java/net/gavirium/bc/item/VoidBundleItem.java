package net.gavirium.bc.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class VoidBundleItem extends Item {

    public VoidBundleItem() {
        super(new Item.Properties().stacksTo(1));
    }

    //Handles right-clicking THIS item onto another stack    
    public boolean overrideStackedOnOther(ItemStack voidBag, Slot slot, ClickAction action, Player player) {
        if (action != ClickAction.SECONDARY) {
            return false;
        }

        ItemStack target = slot.getItem();

        if (target.isEmpty()) {
            return false;
        }

        // Delete the target stack
        slot.set(ItemStack.EMPTY);

        // Play sound feedback
        player.level().playSound(null,player.getX(),player.getY(),player.getZ(),SoundEvents.PUFFER_FISH_BLOW_UP,SoundSource.PLAYERS,0.4F,1.0F);
        player.level().playSound(null,player.getX(),player.getY(),player.getZ(),SoundEvents.ILLUSIONER_MIRROR_MOVE,SoundSource.PLAYERS,0.5F,1.4F);

        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
        	tooltip.add(Component.literal("§8Hold [§fShift§8] for Summary"));
        	tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("§7Let's you void items on the fly."));
            tooltip.add(Component.literal("§7Right-click items to delete them."));
        } else {
            tooltip.add(Component.literal("§8Hold [§7Shift§8] for Summary"));
        }
    }

    /**
     * Normal right-click (does nothing for now)
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
}