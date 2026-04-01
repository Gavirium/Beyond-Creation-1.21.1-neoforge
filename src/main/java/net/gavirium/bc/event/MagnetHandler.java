package net.gavirium.bc.event;

import net.gavirium.bc.item.AntitoolItem;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;

import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber
public class MagnetHandler {

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof Player player)) return;

        ItemStack held = player.getMainHandItem();

        // Check if using your "Antitool"
        if (!(held.getItem() instanceof AntitoolItem)) return;

        if (!player.isCrouching()) return;

        List<ItemEntity> drops = event.getDrops();

        for (ItemEntity itemEntity : drops) {
            ItemStack stack = itemEntity.getItem();

            // Try to insert into inventory
            boolean added = player.getInventory().add(stack);

            if (added) {
                itemEntity.discard(); // remove entity if fully inserted
            } else {
                // If partial insert is needed:
                int originalCount = stack.getCount();
                ItemStack copy = stack.copy();

                player.getInventory().add(copy);

                int remaining = copy.getCount();
                stack.setCount(remaining);

                if (remaining <= 0) {
                    itemEntity.discard();
                }
            }
        }
    }
}