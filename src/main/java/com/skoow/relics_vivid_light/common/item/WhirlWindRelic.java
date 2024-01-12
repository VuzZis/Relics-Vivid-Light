package com.skoow.relics_vivid_light.common.item;

import com.skoow.relics_vivid_light.common.entity.BubbleEntity;
import com.skoow.relics_vivid_light.common.entity.StaticChargeEntity;
import com.skoow.relics_vivid_light.common.entity.SwirlEntity;
import com.skoow.relics_vivid_light.common.registry.EntityRegistry;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class WhirlWindRelic extends RelicItem {
    public static int swirlCooldown = 0;
    public static int defCooldown = 800;
    protected final RelicData data = RelicData.builder()
            .abilityData(RelicAbilityData.builder()
                    .ability("whirl_swirl", RelicAbilityEntry.builder()
                            .stat("radius", RelicAbilityStat.builder()
                                    .initialValue(2,4)
                                    .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE,0.3)
                                    .formatValue(Math::round)
                                    .build())
                            .stat("drowning_speed", RelicAbilityStat.builder()
                                    .initialValue(1,2)
                                    .upgradeModifier(RelicAbilityStat.Operation.ADD,1)
                                    .formatValue(Math::round)
                                    .build())
                            .maxLevel(3)
                            .build()
                    )
                    .ability("blob_a_bubble", RelicAbilityEntry.builder()
                            .maxLevel(5)
                            .stat("duration", RelicAbilityStat.builder()
                                    .initialValue(3,5)
                                    .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE,1.2)
                                    .formatValue(Math::round)
                                    .build())
                            .stat("cooldown", RelicAbilityStat.builder()
                                    .initialValue(6,10)
                                    .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE,0.5)
                                    .formatValue(Math::round)
                                    .build())
                            .build()
                    )
                    .build())
            .levelingData(new RelicLevelingData(100,10,75))
            .styleData(RelicStyleData.builder()
                    .borders("#7AA0D5","#7AA0D5")
                    .build())
            .build();
    public WhirlWindRelic() {
        super(new Properties().stacksTo(1).rarity(Rarity.RARE).defaultDurability(WhirlWindRelic.defCooldown +1));
    }

    @Override
    public RelicData getRelicData() {
        return data;
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, AbilityCastType type, AbilityCastStage stage) {
        if (ability.equals("bzzshot")) createBubble(stack,player);
    }

    private void createBubble(ItemStack stack, Entity entity) {
        summonBubble(stack,entity,new Vec3(0,0,0));
        LevelingUtils.addExperience(stack,10);
    }

    private void summonBubble(ItemStack stack, Entity entity, Vec3 translate) {
        BubbleEntity bubble = EntityRegistry.BUBBLE.get().spawn((ServerLevel) entity.level(),stack,null, BlockPos.containing(entity.getEyePosition()), MobSpawnType.EVENT, true, true);
        bubble.setPos(entity.getEyePosition().add(translate));
        bubble.duration = (int) AbilityUtils.getAbilityValue(stack,"blob_a_bubble","duration")*20;
        bubble.capturedEntity = entity;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(level.isClientSide()) return super.use(level, player, hand);
        ItemStack stack = player.getItemInHand(hand);
        if(!player.isShiftKeyDown()) {
            if(!player.getCooldowns().isOnCooldown(stack.getItem())) {
                createBubble(stack,player);
                if(!player.isCreative()) player.getCooldowns().addCooldown(stack.getItem(),
                        (int) AbilityUtils.getAbilityValue(stack,"blob_a_bubble","cooldown")*20);
                return InteractionResultHolder.consume(stack);
            }
        } else {
            if(swirlCooldown <= 0) {
                useSwirl(stack,player);
                WhirlWindRelic.swirlCooldown = defCooldown;
                return InteractionResultHolder.consume(stack);
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        if(level.isClientSide()) return;
        if(WhirlWindRelic.swirlCooldown > 0) WhirlWindRelic.swirlCooldown--;
        setDamage(stack, swirlCooldown);
    }


    private void useSwirl(ItemStack stack, Player player) {
        SwirlEntity swirl =  EntityRegistry.SWIRL.get().spawn((ServerLevel) player.level(),stack,player,
                BlockPos.containing(player.getEyePosition()), MobSpawnType.EVENT,
                true, true);
        LevelingUtils.addExperience(stack,2);
        swirl.setRadius((int) AbilityUtils.getAbilityValue(stack,"whirl_swirl","radius"));
        swirl.drownSpeed = (int) AbilityUtils.getAbilityValue(stack,"whirl_swirl","drowning_speed");
    }
}
