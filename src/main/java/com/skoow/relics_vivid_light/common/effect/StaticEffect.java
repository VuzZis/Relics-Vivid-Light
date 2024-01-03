package com.skoow.relics_vivid_light.common.effect;


import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class StaticEffect extends MobEffect {
    public StaticEffect(MobEffectCategory cat, int color) {
        super(cat, color);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890",
                (double)-0.4F, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void applyEffectTick(LivingEntity e, int ampl) {
        if(e.getDeltaMovement().length() > 0.08) {
            e.hurt(e.level().damageSources().magic(),1);
        }
    }

    @Override
    public boolean isDurationEffectTick(int tick, int ampl) {
        return tick % 20 == 0;
    }
}
