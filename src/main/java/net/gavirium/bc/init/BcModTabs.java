/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.gavirium.bc.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import net.gavirium.bc.BcMod;

public class BcModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BcMod.MODID);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BC_TAB = REGISTRY.register("bc_tab",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.bc.bc_tab")).icon(() -> new ItemStack(BcModItems.ANTITOOL.get())).displayItems((parameters, tabData) -> {
				tabData.accept(BcModItems.ANTITOOL.get());
				tabData.accept(BcModItems.AMETHYST_TROWEL.get());
				tabData.accept(BcModItems.PRISMARINE_CHISEL.get());
				tabData.accept(BcModItems.HARD_HAT_HELMET.get());
				tabData.accept(BcModItems.VOID_BUCKET.get());
				tabData.accept(BcModItems.VOID_BUNDLE.get());
				tabData.accept(BcModItems.MOB_BAG.get());
			}).build());
}