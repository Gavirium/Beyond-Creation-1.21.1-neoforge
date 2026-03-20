package net.gavirium.bc.client;

import net.gavirium.bc.BcMod;
import net.gavirium.bc.init.BcModItems;
import net.gavirium.bc.item.MobBagItem;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.api.distmarker.Dist;

@EventBusSubscriber(modid = BcMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {

            ItemProperties.register(
                    BcModItems.MOB_BAG.get(),
                    ResourceLocation.fromNamespaceAndPath(BcMod.MODID, "has_mob"),
                    (stack, level, entity, seed) -> {
                        return MobBagItem.hasMob(stack) ? 1.0F : 0.0F;
                    }
            );

        });
    }
}