package net.gavirium.bc.item;

import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.ItemAbilities;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlotGroup;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.tags.TagKey;
import net.minecraft.tags.BlockTags;

import net.minecraft.client.Minecraft;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class AntitoolItem extends TieredItem {

    private static final Tier TOOL_TIER = new Tier() {
        @Override
        public int getUses() {
            return 4062;
        }

        @Override
        public float getSpeed() {
            return 12f;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
            return 22;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of();
        }
    };

    public AntitoolItem() {
        super(TOOL_TIER,
            new Item.Properties()
                .attributes(ItemAttributeModifiers.builder()
                    .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 9, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                    .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.4, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                    .build())
                .fireResistant()
        );
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_AXE)
            || state.is(BlockTags.MINEABLE_WITH_HOE)
            || state.is(BlockTags.MINEABLE_WITH_PICKAXE)
            || state.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility action) {
        return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(action)
            || ItemAbilities.DEFAULT_HOE_ACTIONS.contains(action)
            || ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(action)
            || ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(action)
            || ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(action);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (isCorrectToolForDrops(stack, state)) {
            return this.getTier().getSpeed();
        }
        return 1.0F;
    }
    
    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        stack.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
        return true;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(2, attacker, LivingEntity.getSlotForHand(attacker.getUsedItemHand()));
        return true;
    }

    // CUSTOM INTERACTIONS
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player == null) return InteractionResult.PASS;

        if (context.getClickedFace() == Direction.DOWN) {
            return InteractionResult.PASS;
        }

        BlockState state = level.getBlockState(pos);

        if (!level.isClientSide) {

            BlockState stripped = state.getToolModifiedState(context, ItemAbilities.AXE_STRIP, false);

            if (stripped != null) {
                level.setBlock(pos, stripped, 11);
                level.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);

                context.getItemInHand().hurtAndBreak(1, player,
                        LivingEntity.getSlotForHand(context.getHand()));

                return InteractionResult.SUCCESS;
            }
            
            if (level.getBlockState(pos.above()).isAir()) {

                boolean crouching = player.isCrouching();

                if (crouching) {
                    BlockState farmland = state.getToolModifiedState(context, ItemAbilities.HOE_TILL, false);

                    if (farmland != null) {
                        level.setBlock(pos, farmland, 11);
                        level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);

                        context.getItemInHand().hurtAndBreak(1, player,
                                LivingEntity.getSlotForHand(context.getHand()));

                        return InteractionResult.SUCCESS;
                    }

                } else {
                    BlockState path = state.getToolModifiedState(context, ItemAbilities.SHOVEL_FLATTEN, false);

                    if (path != null) {
                        level.setBlock(pos, path, 11);
                        level.playSound(null, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);

                        context.getItemInHand().hurtAndBreak(1, player,
                                LivingEntity.getSlotForHand(context.getHand()));

                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }

    // Animated durability bar
    @Override
    public int getBarColor(ItemStack stack) {
        var mc = Minecraft.getInstance();
        if (mc.level == null) {
            return 0xFFFFFF;
        }

        // Smooth animation using game ticks
        long time = mc.level.getGameTime();
        float periodTicks = 100f;
        float t = (float) ((Math.sin((2 * Math.PI * time) / periodTicks) + 1) / 2.0);
        int colorA = 0x32f432; // green
        int colorB = 0x800080; // purple
        return interpolateColor(colorA, colorB, t);
    }

    private int interpolateColor(int colorA, int colorB, float t) {
        int rA = (colorA >> 16) & 0xFF;
        int gA = (colorA >> 8) & 0xFF;
        int bA = colorA & 0xFF;

        int rB = (colorB >> 16) & 0xFF;
        int gB = (colorB >> 8) & 0xFF;
        int bB = colorB & 0xFF;

        int r = (int) (rA + (rB - rA) * t);
        int g = (int) (gA + (gB - gA) * t);
        int b = (int) (bA + (bB - bA) * t);

        return (r << 16) | (g << 8) | b;
    }
}