/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.gavirium.bc.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.gavirium.bc.client.model.Modelhard_hat;

@EventBusSubscriber(Dist.CLIENT)
public class BcModModels {
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(Modelhard_hat.LAYER_LOCATION, Modelhard_hat::createBodyLayer);
	}
}