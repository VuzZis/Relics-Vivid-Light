package com.skoow.relics_vivid_light.common.item;

import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.items.relics.back.ArrowQuiverItem;
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
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class BoilerRelic extends RelicItem {
    public final AttributeModifier speedModifier = new AttributeModifier("7107DE5E-4030-4030-940E-514C1F160890",
                                               0.2, AttributeModifier.Operation.MULTIPLY_BASE);

    protected final RelicData data = RelicData.builder()
            .abilityData(RelicAbilityData.builder()
                    .ability("comfort", RelicAbilityEntry.builder()
                            .maxLevel(3)
                            .stat("regeneration_level", RelicAbilityStat.builder()
                                    .initialValue(1,1)
                                    .upgradeModifier(RelicAbilityStat.Operation.ADD,1)
                                    .build())
                            .build()
                    )
                    .ability("emergency_heating", RelicAbilityEntry.builder()
                            .maxLevel(3)
                            .requiredLevel(5)
                            .stat("cooldown", RelicAbilityStat.builder()
                                    .initialValue(20,20)
                                    .upgradeModifier(RelicAbilityStat.Operation.ADD,-5)
                                    .build())
                            .active(AbilityCastType.INSTANTANEOUS,
                                    AbilityCastPredicate.builder().predicate("target",(data) ->{
                                        boolean canUse = !AbilityUtils.isAbilityOnCooldown(data.getStack(),"emergency_heating");
                                        return PredicateInfo.builder().condition(canUse).build();
                                    }))
                            .build()
                    )
                    .build())
            .levelingData(new RelicLevelingData(10,10,50))
            .styleData(RelicStyleData.builder()
                    .borders("#D5A07A","#D5A07A")
                    .build())
            .build();

    public BoilerRelic() {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public RelicData getRelicData() {
        return data;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        super.curioTick(slotContext, stack);
        int regenLevel = (int) AbilityUtils.getAbilityValue(stack,"comfort","regeneration_level");
        slotContext.entity().addEffect(new MobEffectInstance(MobEffects.REGENERATION,2,regenLevel));
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        slotContext.entity().getAttribute(Attributes.MOVEMENT_SPEED)
                .addTransientModifier(speedModifier);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        slotContext.entity().getAttribute(Attributes.MOVEMENT_SPEED)
                .removeModifier(speedModifier);
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, AbilityCastType type, AbilityCastStage stage) {
        super.castActiveAbility(stack, player, ability, type, stage);

        if(Objects.equals(ability, "emergency_heating")) {
            int cooldownTime = (int) (AbilityUtils.getAbilityValue(stack,"emergency_heating","cooldown")*20);
            AbilityUtils.setAbilityCooldown(stack,"emergency_heating",cooldownTime);
            player.setDeltaMovement(player.getLookAngle().multiply(3,1.5,3));
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,3*20,2));
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION,10*20,2));
            LevelingUtils.addExperience(stack,3);
        }
    }
}
