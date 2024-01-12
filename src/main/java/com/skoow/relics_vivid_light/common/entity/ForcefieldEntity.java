package com.skoow.relics_vivid_light.common.entity;

import com.skoow.relics_vivid_light.common.config.ClientConfig;
import com.skoow.relics_vivid_light.common.registry.EffectRegistry;
import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.commands.EffectCommands;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;

public class ForcefieldEntity extends Entity {
    public Color color = new Color(235-50,180-50,235-50);
    public Color color2 = new Color(160-50,95-50,160-50);
    public int partCount = ClientConfig.FORCEFIELD_PARTICLE_COUNT.get();


    private static final EntityDataAccessor<Integer> RADIUS = SynchedEntityData.defineId(ForcefieldEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MAX_TIME = SynchedEntityData.defineId(ForcefieldEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> STATIC_PROP = SynchedEntityData.defineId(ForcefieldEntity.class, EntityDataSerializers.INT);

    public ForcefieldEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if(level().isClientSide()) {
            drawForcefield();
        }
        statificateEnemies();
    }

    private void statificateEnemies() {
        ArrayList<Entity> entities = (ArrayList<Entity>) level().getEntities(this,getBoundingBox().inflate(getRadius()));
        entities.sort((e,c) -> (int) (distanceTo(c)*10-distanceTo(e)*10));
        int max = 0;
        for (Entity e : entities) {
            if(e instanceof Player || e == this || !(e instanceof LivingEntity) || e instanceof ItemEntity || e.getType().getCategory().isFriendly()) continue;
            statificateEnemy((LivingEntity) e);
            max++;
            if(max >= 8) break;
        }
    }

    private void statificateEnemy(LivingEntity e) {
        ParticleUtils.createLine(
                new CircleTintData(color2, 0.3F, 50, 0.85F, false),
                level(),
                e.getEyePosition(),
                position(),
                (int) e.getEyePosition().distanceToSqr(position())*8
        );
        double prop = getProp();
        float r = random.nextFloat();
        if(r <= prop && !level().isClientSide()) {
            MobEffectInstance eff = new MobEffectInstance(EffectRegistry.STATIC.get(),20);
            MobEffectInstance eff2 = new MobEffectInstance(MobEffects.GLOWING,20);
            e.addEffect(eff);
            e.addEffect(eff2);
        }

    }

    public void drawForcefield() {
        int radius = getRadius();
        float step = (float) (Math.toRadians((double) 90 /radius));
        int particleCount = Math.min(tickCount,partCount);
        float partStep = (float) 360 /particleCount;
        for(int i = 1; i < radius*2; i++) {
            float sphereRadius = (float) (Math.sin(step*i)*radius);
                for(int z = 0; z <= particleCount; z++) {
                    double prevX = (Math.sin(Math.toRadians((z-1)*partStep))*sphereRadius);
                    double prevZ = (Math.cos(Math.toRadians((z-1)*partStep))*sphereRadius);
                    double X = (Math.sin(Math.toRadians(z*partStep))*sphereRadius);
                    double Z = (Math.cos(Math.toRadians(z*partStep))*sphereRadius);
                    level().addParticle(
                            new CircleTintData(color, 0.5F, 30, 0.1F, false),
                            getX()+X, getY()+i- (double) radius, getZ()+Z,
                            0,0,0);
                    if(z != 0) ParticleUtils.createLine(
                            new CircleTintData(color2, 0.15F, 30, 0.1F, false),
                            level(),
                            new Vec3(getX()+X,getY()+i-radius,getZ()+Z),
                            new Vec3(getX()+prevX,getY()+i-radius,getZ()+prevZ),
                            particleCount*2
                    );
                }
        }
    }

    @Override
    public void checkDespawn() {
        if(tickCount > getMaxTime()*20) discard();
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(MAX_TIME,100);
        this.entityData.define(RADIUS,0);
        this.entityData.define(STATIC_PROP,1);
    }

    public int getMaxTime() {
        return this.entityData.get(MAX_TIME);
    }
    public int getRadius() {
        return this.entityData.get(RADIUS);
    }
    public double getProp() {return (double) this.entityData.get(STATIC_PROP) /100;}

    public void setMaxTime(int v) {
        this.entityData.set(MAX_TIME,v);
    }
    public void setRadius(int v) {
        this.entityData.set(RADIUS,v);
    }
    public void setProp(int v) {this.entityData.set(STATIC_PROP,v);}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        discard();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {

    }
}
