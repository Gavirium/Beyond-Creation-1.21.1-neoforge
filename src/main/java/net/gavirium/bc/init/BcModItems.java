/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.gavirium.bc.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

import net.minecraft.world.item.Item;

import net.gavirium.bc.item.*;
import net.gavirium.bc.BcMod;

public class BcModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(BcMod.MODID);
	public static final DeferredItem<Item> VOID_BUNDLE;
	public static final DeferredItem<Item> AMETHYST_TROWEL;
	public static final DeferredItem<Item> MOB_BAG;
	public static final DeferredItem<Item> VOID_BUCKET;
	public static final DeferredItem<Item> HARD_HAT_HELMET;
	public static final DeferredItem<Item> PRISMARINE_CHISEL;
	public static final DeferredItem<Item> ANTITOOL;
	public static final DeferredItem<Item> BRASSMARK;
	public static final DeferredItem<Item> WALLET;
	static {
		VOID_BUNDLE = REGISTRY.register("void_bundle", VoidBundleItem::new);
		AMETHYST_TROWEL = REGISTRY.register("amethyst_trowel", AmethystTrowelItem::new);
		MOB_BAG = REGISTRY.register("mob_bag", MobBagItem::new);
		VOID_BUCKET = REGISTRY.register("void_bucket", VoidBucketItem::new);
		HARD_HAT_HELMET = REGISTRY.register("hard_hat_helmet", HardHatItem.Helmet::new);
		PRISMARINE_CHISEL = REGISTRY.register("prismarine_chisel", PrismarineChiselItem::new);
		ANTITOOL = REGISTRY.register("antitool", AntitoolItem::new);
		BRASSMARK = REGISTRY.register("brassmark", BrassmarkItem::new);
		WALLET = REGISTRY.register("wallet", WalletItem::new);
	}
	// Start of user code block custom items
	// End of user code block custom items
}