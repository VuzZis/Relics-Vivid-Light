package com.skoow.relics_vivid_light.common.entity;

import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.client.particles.spark.SparkTintData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.phys.AABB;

import java.awt.*;
import java.util.ArrayList;

public class SwirlEntity extends ThrowableProjectile {
    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(SwirlEntity.class, EntityDataSerializers.FLOAT);
    public Color color = new Color(102,153,204);

    private final NormalNoise.NoiseParameters noisePars = new NormalNoise.NoiseParameters(3,10);
    private final NormalNoise noise = NormalNoise.create(RandomSource.create(), noisePars);
    public int drownSpeed = 1;
    public SwirlEntity(EntityType<? extends ThrowableProjectile> entity, Level level) {
        super(entity, level);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(RADIUS,3F);
    }

    @Override
    public void checkDespawn() {
        super.checkDespawn();
        if(tickCount > 15*20) discard();
    }

    @Override
    public void tick() {
        super.tick();
        if(level().isClientSide()) {renderSwirl(); return;}
        setDeltaMovement(
                noise.getValue((double) tickCount /500,0,0)/30, getBlockStateOn().isAir() ? -0.1 : 0,
                noise.getValue(0,0, (double) tickCount+ (double) tickCount /500)/30);
        attractEnemies();
    }

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }
    private void attractEnemies() {
        ArrayList<Entity> entities = (ArrayList<Entity>) level().getEntities(this,getBoundingBox().inflate(getRadius()*1.2,getRadius()*2*1.2,getRadius()*1.2));
        entities.sort((e,c) -> (int) (distanceTo(c)*10-distanceTo(e)*10));
        int max = 0;
        for (Entity e : entities) {
            if(e instanceof Player || e == this || !(e instanceof LivingEntity) || e instanceof ItemEntity || e.getType().getCategory().isFriendly()) continue;
            attract((LivingEntity) e);
            if(ticks%20 <= drownSpeed) drown(e);
            max++;
            if(max >= 5) break;
        }
        ticks++;
    }

    private void drown(Entity e) {
        e.hurt(e.level().damageSources().drown(),2);
    }

    private void attract(LivingEntity e) {
        e.setDeltaMovement(getEyePosition().subtract(e.getEyePosition())
                .normalize().add(Math.sin(ticks/30),Math.sin(ticks/30)+1,Math.cos(ticks/30)).multiply(0.2,0.2,0.2));
    }

    int ticks = 0;

    private void renderSwirl() {
        if(getRadius() == 0) setRadius(3);
        final float maxRadians = (float) Math.toRadians(360);
        float radius = getRadius();
        for    (float i = 0; i <= getRadius()*2 ; i+=1f  )
            for(int x = 0;   x < i; x+=1   ) {
                float step = (float) (maxRadians/(i));
                float animOffset = (float) Math.toRadians(ticks*(i));
                double X = Math.sin(x*step+animOffset)*(i/2);
                double Z = Math.cos(x*step+animOffset)*(i/2);
                level().addParticle(
                        new CircleTintData(color, 1F, 4,0.2f,false),
                        getX()+X, getY()+i, getZ()+Z,
                        0,0,0);
            }
        ticks++;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(RADIUS,tag.getFloat("radius"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("radius",entityData.get(RADIUS));
    }


    public float getRadius() {return entityData.get(RADIUS);}
    public void setRadius(float i) {entityData.set(RADIUS,i);}
}
