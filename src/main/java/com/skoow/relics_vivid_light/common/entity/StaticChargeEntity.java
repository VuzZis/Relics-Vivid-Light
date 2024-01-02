package com.skoow.relics_vivid_light.common.entity;

import com.skoow.relics_vivid_light.common.registry.EffectRegistry;
import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;

public class StaticChargeEntity extends ThrowableProjectile {
    public Vec3 prevPos;
    public Color color = new Color(235,180,235);
    public Color color2 = new Color(160,95,160);
    public int damage = 0;
    public Vec3 deltaMovement = new Vec3(0,0,0);
    public int ricochetsLeft = 0;
    public boolean isEssential;
    public int staticDuration = 0;

    public StaticChargeEntity(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
    }


    @Override
    public void tick() {
        super.tick();
        setDeltaMovement(deltaMovement);
        if (level().isClientSide) {
            ParticleUtils.createBall(
                    new CircleTintData(isEssential ? color2: color, isEssential ? 0.05F : 0.3F , 40, 0.85F, false),
                    prevPos != null ? prevPos : position(),
                    level(),
                    1,
                    0);
            ParticleUtils.createBall(
                    new CircleTintData(isEssential ? color2 : color, isEssential ? 0.1F :0.8F, 100, 0.85F, false),
                    this.position(),
                    level(),
                    1,
                    0);
            ParticleUtils.createLine(
                    new CircleTintData(color2, isEssential ? 0.025F : 0.2F, 50, 0.85F, false),
                    level(),
                    prevPos != null ? prevPos : position(),
                    position(),
                    (int) position().distanceToSqr(prevPos != null ? prevPos : position())*8
            );
        }
        prevPos = position();
        ticks++;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (entity instanceof Player player) {
            player.heal((float) damage /8);
        } else {
            entity.hurt(level().damageSources().thrown(this,null), damage);
            MobEffectInstance eff = new MobEffectInstance(EffectRegistry.STATIC.get(),staticDuration*20);
            if(entity instanceof LivingEntity) ((LivingEntity) entity).addEffect(eff);
            //TODO: RICOCHETING
            Entity e = getClosestEntityForAttack(entity);

            if(e == null || ricochetsLeft <= 0) return;
            ricochetsLeft--;
            damage/=2;
            ticks = 0;
            Vec3 vec = e.getEyePosition().subtract(position()).normalize();

            ParticleUtils.createLine(
                    new CircleTintData(color2, 0.3F, 50, 0.85F, false),
                    level(),
                    e.getEyePosition(),
                    position(),
                    (int) e.getEyePosition().distanceToSqr(position())*8
            );
            deltaMovement = vec;
        }
        super.onHitEntity(result);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {

    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity p_20303_) {
        return true;
    }

    public Entity getClosestEntityForAttack(Entity prevEntity) {

        ArrayList<Entity> entities = (ArrayList<Entity>) level().getEntities(this,getBoundingBox().inflate(25));
        entities.sort((e,c) -> (int) (distanceTo(c)*10-distanceTo(e)*10));
        Entity selectedEntity = null;
        for (Entity e : entities) {
            if(e instanceof Player || e == this || e.getId() == prevEntity.getId() || e instanceof ItemEntity || e.getType().getCategory().isFriendly()) continue;
            if(selectedEntity == null) {
                selectedEntity = e;
                continue;
            }
            if(distanceTo(selectedEntity) > distanceTo(e)) {selectedEntity = e; continue;}
            break;
        }
        return selectedEntity;
    }

    int ticks = 0;

    @Override
    public void checkDespawn() {
        if(ticks > 20*20) {
            this.discard();
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_37262_) {
        super.readAdditionalSaveData(p_37262_);
        discard();
    }
}
