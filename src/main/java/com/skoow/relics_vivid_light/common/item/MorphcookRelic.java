package com.skoow.relics_vivid_light.common.item;

import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
import it.hurts.sskirillss.relics.items.relics.hands.WoolMittenItem;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

public class MorphcookRelic extends RelicItem {

    protected final RelicData data = RelicData.builder()
            .abilityData(RelicAbilityData.builder()
                    .ability("passive_smoking", RelicAbilityEntry.builder()
                            .maxLevel(1)
                            .build()
                    )
                    .ability("fleshfeeding", RelicAbilityEntry.builder()
                            .maxLevel(1)
                            .build()
                    )
                    .build())
            .levelingData(new RelicLevelingData(0,0,0))
            .styleData(RelicStyleData.builder()
                    .borders("#D5A07A","#D5A07A")
                    .build())
            .build();

    public MorphcookRelic() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public RelicData getRelicData() {
        return data;
    }

    @Override
    public void inventoryTick(ItemStack s, Level level, Entity entity, int slot, boolean isSelected) {
        super.inventoryTick(s, level, entity, slot, isSelected);
        if(!(entity instanceof Player player)) return;
        if(entity.level().isClientSide()) return;
        Inventory inv = player.getInventory();
        int invSize = inv.getContainerSize();
        for(int i = 0; i < invSize; i++) {
            ItemStack stack = inv.getItem(i);
            Optional<SmokingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMOKING, new SimpleContainer(stack), level);
            if(!recipe.isPresent()) continue;
            int smokeTime = 60;
            int curTime = stack.getOrCreateTag().getInt("smokprog");
            if(curTime >= smokeTime) {
                stack.getOrCreateTag().putInt("smokprog",0);
                ItemStack newItem = recipe.get().getResultItem(RegistryAccess.EMPTY).copy();
                LevelingUtils.addExperience(stack,2);
                int freeSlot = inv.getFreeSlot();
                if(inv.getSlotWithRemainingSpace(newItem) >= 0) freeSlot = inv.getSlotWithRemainingSpace(newItem);
                if(freeSlot < 0) {
                    player.drop(newItem,true);
                    stack.shrink(1);
                } else {
                    if(inv.add(freeSlot,newItem)) stack.shrink(1);
                }
                break;
            }
            curTime++;
            stack.getOrCreateTag().putInt("smokprog",curTime);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(level.isClientSide()) return super.use(level, player, hand);
        ItemStack stack = player.getItemInHand(hand);
        if(player.getCooldowns().isOnCooldown(stack.getItem())) return super.use(level, player, hand);
        player.getCooldowns().addCooldown(stack.getItem(),20*60);
        useFleshFeeding(player);
        return super.use(level, player, hand);
    }

    private void useFleshFeeding(Player player) {
        ArrayList<Entity> entities = (ArrayList<Entity>) player.level().getEntities(player,player.getBoundingBox().inflate(5));
        int saturation = 0;
        int max = 0;
        for (Entity e : entities) {
            if(e instanceof Player || !(e instanceof LivingEntity) || e instanceof ItemEntity || e.getType().getCategory().isFriendly()) continue;
            int damage = (int) Math.min(15,((LivingEntity) e).getHealth());
            saturation += damage / 5;
            e.hurt(player.level().damageSources().starve(),damage);
            max++;

            if(max >= 6) break;
        }
        player.getFoodData().eat(saturation,saturation);
    }

}
