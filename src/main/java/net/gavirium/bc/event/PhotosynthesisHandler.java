package net.gavirium.bc.event;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.core.Holder;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = "bc")
public class PhotosynthesisHandler {

    // JSON-id for Photosynthesis-enchantmenet
    private static final ResourceLocation PHOTOSYNTHESIS_ID = ResourceLocation.tryParse("bc:photosynthesis");

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();

        // Kør kun på server-siden
        if (level.isClientSide) return;
        // Kør kun hvert 20. tick (ca. hvert sekund)
        if (player.tickCount % 60 != 0) return;
        // Kræv fri himmel
        if (!level.canSeeSky(player.blockPosition())) return;

        // Hent enchantment-holder fra registret
        Optional<? extends Holder.Reference<Enchantment>> opt = 
        level.registryAccess()
             .registryOrThrow(Registries.ENCHANTMENT)
             .getHolder(PHOTOSYNTHESIS_ID)
             .filter(h -> h instanceof Holder.Reference);
        if (opt.isEmpty()) return;
        Holder.Reference<Enchantment> photosynthesis = opt.get();

        // Gather Candidates: mainhand, offhand and armor
        List<ItemStack> candidates = new ArrayList<>();
        addIfValid(candidates, player.getMainHandItem(), photosynthesis);
        addIfValid(candidates, player.getOffhandItem(), photosynthesis);
        for (ItemStack stack : player.getInventory().armor) {
            addIfValid(candidates, stack, photosynthesis);
        }

        if (candidates.isEmpty()) return;

        // Vælg et tilfældigt item og reparer det
        ItemStack chosen = candidates.get(player.getRandom().nextInt(candidates.size()));
        int levelEnch = EnchantmentHelper.getItemEnchantmentLevel(photosynthesis, chosen);
        chosen.setDamageValue(Math.max(0, chosen.getDamageValue() - levelEnch));
    }

    // Hjælpefunktion: læg kun varer på listen hvis de har Photosynthesis-enchant (niveau >0)
    private static void addIfValid(List<ItemStack> list, ItemStack stack, Holder<Enchantment> enchantment) {
        if (stack.isEmpty() || !stack.isDamaged()) return;
        int level = EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack);
        if (level > 0) list.add(stack);
    }
}
