package net.gavirium.bc.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;

import java.util.List;
import java.util.Optional;

public class MobBagItem extends Item {

    private static final String HAS_MOB_TAG = "HasMob";
    private static final String ENTITY_DATA_TAG = "EntityData";

    public MobBagItem() {
        super(new Item.Properties().stacksTo(1));
    }

    // DATA HANDLING

    public static boolean hasMob(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return false;
        CompoundTag tag = data.copyTag();
        return tag.getBoolean(HAS_MOB_TAG);
    }

    public static void setHasMob(ItemStack stack, boolean value) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();
        tag.putBoolean(HAS_MOB_TAG, value);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static void saveEntity(ItemStack stack, Entity entity) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();
        CompoundTag entityTag = new CompoundTag();
        entity.save(entityTag);
        tag.put(ENTITY_DATA_TAG, entityTag);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static CompoundTag getSavedEntity(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        CompoundTag tag = data.copyTag();
        return tag.getCompound(ENTITY_DATA_TAG);
    }

    // CAPTURE LOGIC

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();
        BlockPos pos = target.blockPosition();
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (hasMob(stack)) return InteractionResult.FAIL;
        if (!(target instanceof Animal || target instanceof Villager || target instanceof WanderingTrader)) {
            player.displayClientMessage(Component.literal("This creature cannot be captured"), true);
            return InteractionResult.FAIL;}
        if (target instanceof Player) return InteractionResult.FAIL;
        saveEntity(stack, target);
        setHasMob(stack, true);
        level.playSound(null,pos,net.minecraft.sounds.SoundEvents.BUNDLE_INSERT,SoundSource.PLAYERS,2.0F,0.1F);
        level.playSound(null,pos,net.minecraft.sounds.SoundEvents.ILLUSIONER_CAST_SPELL,SoundSource.PLAYERS,0.2F,2.0F);
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CLOUD,target.getX(),target.getY() + target.getBbHeight() / 2.0,target.getZ(),20,0.4,0.4,0.4,0.1);}
        target.discard();
        return InteractionResult.CONSUME;
    }

    // RELEASE LOGIC

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        if (player == null) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!hasMob(stack)) return InteractionResult.FAIL;
        CompoundTag entityTag = getSavedEntity(stack);
        if (entityTag == null) return InteractionResult.FAIL;
        var optionalEntity = EntityType.create(entityTag, level);
        if (optionalEntity.isEmpty()) return InteractionResult.FAIL;
        Entity entity = optionalEntity.get();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        level.addFreshEntity(entity);
        level.playSound(null,pos,net.minecraft.sounds.SoundEvents.BUNDLE_DROP_CONTENTS,SoundSource.PLAYERS,2.0F,0.7F);
        level.playSound(null,pos,net.minecraft.sounds.SoundEvents.ILLUSIONER_CAST_SPELL,SoundSource.PLAYERS,0.2F,2.0F);
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
        serverLevel.sendParticles(ParticleTypes.CLOUD,entity.getX(),entity.getY() + entity.getBbHeight() / 2.0,entity.getZ(),20,0.4,0.4,0.4,0.1);}
        setHasMob(stack, false);
        return InteractionResult.SUCCESS;
    }

    // TOOLTIP

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
            tooltip.add(Component.literal("§8Hold [§fShift§8] for Summary"));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("§5A spacious bag capable of"));
            tooltip.add(Component.literal("§5tapping into Hammerspace!"));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("§7When used on Mobs"));
        } else {
            tooltip.add(Component.literal("§8Hold [§7Shift§8] for Summary"));
        }
        if (!hasMob(stack)) return;
        CompoundTag entityTag = getSavedEntity(stack);
        if (entityTag == null) return;
        String name = null;
        if (entityTag.contains("CustomName", 8)) {
            String raw = entityTag.getString("CustomName");
            try {
                Component comp = Component.Serializer.fromJsonLenient(raw, null);
                if (comp != null) {
                    name = comp.getString();
                }
            } catch (Exception ignored) { }
        }
        if (name == null || name.isEmpty()) {
            Optional<EntityType<?>> optionalType = EntityType.by(entityTag);
            if (optionalType.isPresent()) {
                name = optionalType.get().getDescription().getString();
            } else {
                name = "Unknown Creature";
            }
        }
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7Contains:"));
        tooltip.add(Component.literal("§9" + name));
    }
}