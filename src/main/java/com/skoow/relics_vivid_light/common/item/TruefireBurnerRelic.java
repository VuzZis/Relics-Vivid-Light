package com.skoow.relics_vivid_light.common.item;

import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastPredicate;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastType;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.data.PredicateInfo;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class TruefireBurnerRelic extends RelicItem {
    public Color color = new Color(235,150,0);
    public Color color2 = new Color(150,50,0);

    protected final RelicData data = RelicData.builder()
            .abilityData(RelicAbilityData.builder()
                    .ability("flamous_takeoff", RelicAbilityEntry.builder()
                            .maxLevel(5)
                            .stat("strength", RelicAbilityStat.builder()
                                    .initialValue(2,5)
                                    .upgradeModifier(RelicAbilityStat.Operation.ADD,3)
                                    .build())
                            .active(AbilityCastType.INSTANTANEOUS,
                                    AbilityCastPredicate.builder().predicate("takeoff",(data) ->{
                                        boolean canUse = !AbilityUtils.isAbilityOnCooldown(data.getStack(),"flamous_takeoff");
                                        return PredicateInfo.builder().condition(canUse).build();
                                    }))
                            .build()
                    )
                    .ability("eruption", RelicAbilityEntry.builder()
                            .maxLevel(5)
                            .stat("radius", RelicAbilityStat.builder()
                                    .initialValue(2,3)
                                    .upgradeModifier(RelicAbilityStat.Operation.ADD,2)
                                    .build())
                            .active(AbilityCastType.INSTANTANEOUS,
                                    AbilityCastPredicate.builder().predicate("eruption",(data) ->{
                                        boolean canUse = !AbilityUtils.isAbilityOnCooldown(data.getStack(),"eruption");
                                        return PredicateInfo.builder().condition(canUse).build();
                                    }))
                            .build()
                    )
                    .build())
            .levelingData(new RelicLevelingData(100,5,50))
            .styleData(RelicStyleData.builder()
                    .borders("#D5A07A","#D5A07A")
                    .build())
            .build();

    public TruefireBurnerRelic() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public RelicData getRelicData() {
        return data;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        super.curioTick(slotContext, stack);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
    }

    private void searchForEntities(Player player, int radius) {
        ArrayList<Entity> entities = (ArrayList<Entity>) player.level().getEntities(player,player.getBoundingBox().inflate(radius));
        entities.sort((e,c) -> (int) (-player.distanceTo(c)*10+player.distanceTo(e)*10));
        int max = 0;
        for (Entity e : entities) {
            if(e instanceof Player || !(e instanceof LivingEntity) || e.getType().getCategory().isFriendly()) continue;
            putOnFire((LivingEntity) e,player);
            max++;
            if(max >= 5) break;
        }
    }

    private void putOnFire(LivingEntity e, Player player) {
        ParticleUtils.createLine(
                new CircleTintData(color2, 0.6F, 100, 0.85F, false),
                e.level(),
                e.getEyePosition(),
                player.position(),
                (int) e.getEyePosition().distanceToSqr(player.position())*8
        );
        e.hurt(e.level().damageSources().inFire(),2);
        e.setSecondsOnFire(100);
    }


    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, AbilityCastType type, AbilityCastStage stage) {
        super.castActiveAbility(stack, player, ability, type, stage);
        if(Objects.equals(ability, "flamous_takeoff")) {
            int strength = (int) AbilityUtils.getAbilityValue(stack,"flamous_takeoff","strength");
            int cooldownTime = strength*20*20;
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,40));
            player.setDeltaMovement(0, strength /5d,0);
            player.addTag("prevent_fall");
            for(int x = 0; x < strength*2; x++) {
                for(int z = 0; z < strength*2; z++) {
                    double X = x-strength;
                    double Z = z-strength;

                    BlockPos pos = new BlockPos((int) ((int) player.getX()+X),player.getBlockY(), (int) ((int) player.getZ()+Z));
                    pos.above();
                    while(player.level().getBlockState(pos).isAir()) {
                        pos = pos.below();
                    }
                    player.level().setBlock(pos.above(), BaseFireBlock.getState(player.level(),pos.above()), Block.UPDATE_ALL_IMMEDIATE);
                }
            }
            AbilityUtils.setAbilityCooldown(stack,"flamous_takeoff",cooldownTime);
            LevelingUtils.addExperience(stack,3);
        }
        if(Objects.equals(ability, "eruption")) {
            int rad = (int) AbilityUtils.getAbilityValue(stack,"eruption","radius");
            int cooldownTime = rad*40;
            double step = Math.toRadians(360d/rad/8d);
            for(int x = 0; x < rad*8; x++) {
                double i = step*x;
                double X = Math.sin(i)*rad;
                double Z = Math.cos(i)*rad;
                player.level().addParticle(
                        new CircleTintData(color, 0.5F, 150, 3F, false),
                        player.getX()+X, player.getEyeY(), player.getZ()+Z,
                        0,0,0);
            }
            searchForEntities(player,rad);
            AbilityUtils.setAbilityCooldown(stack,"eruption",cooldownTime);
            LevelingUtils.addExperience(stack,1);
        }

    }

}
