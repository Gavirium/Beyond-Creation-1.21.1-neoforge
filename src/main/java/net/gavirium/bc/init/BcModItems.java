/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.gavirium.bc.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

import net.minecraft.world.item.Item;

import net.gavirium.bc.item.VoidBundleItem;
import net.gavirium.bc.item.MobBagItem;
import net.gavirium.bc.item.AntitoolItem;
import net.gavirium.bc.item.AmethystTrowelItem;
import net.gavirium.bc.BcMod;

public class BcModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(BcMod.MODID);
	public static final DeferredItem<Item> ANTITOOL;
	public static final DeferredItem<Item> VOID_BUNDLE;
	public static final DeferredItem<Item> AMETHYST_TROWEL;
	public static final DeferredItem<Item> MOB_BAG;
	static {
		ANTITOOL = REGISTRY.register("antitool", AntitoolItem::new);
		VOID_BUNDLE = REGISTRY.register("void_bundle", VoidBundleItem::new);
		AMETHYST_TROWEL = REGISTRY.register("amethyst_trowel", AmethystTrowelItem::new);
		MOB_BAG = REGISTRY.register("mob_bag", MobBagItem::new);
	}
	// Start of user code block custom items
	// End of user code block custom items
}