package com.skoow.relics_vivid_light.common.entity;

import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;

import java.awt.*;

public class BubbleEntity extends ThrowableProjectile {
    public Color color = new Color(102,153,204);
    public int duration = 20;
    public Entity capturedEntity;
    public BubbleEntity(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    int ticks = 0;

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        setNoGravity(true);
        if(level().isClientSide()) {renderBubble(); return;}
        setDeltaMovement(0,Math.sin(Math.toRadians(tickCount*9))/4,0);
        ticks++;
        if(capturedEntity == null || ticks > 20) return;
        capturedEntity.startRiding(this);
        capturedEntity.setInvulnerable(true);
    }

    @Override
    public void checkDespawn() {
        super.checkDespawn();
        if(ticks >= duration) discard();
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if(capturedEntity == null) return;
        capturedEntity.setInvulnerable(false);
    }

    private void renderBubble() {
        float step = (float) Math.toRadians(18);
        for(int x = 0; x < 20; x++) {
            double X = Math.sin(x*step)*2;
            double Z = Math.cos(x*step)*2;
            level().addParticle(
                    new CircleTintData(color, 0.6F, 4,0f,false),
                    getX()+X, getY()+Z, getZ(),
                    0,0,0);
            level().addParticle(
                    new CircleTintData(color, 0.6F, 4,0f,false),
                    getX(), getY()+Z, getZ()+X,
                    0,0,0);
        }
    }

    @Override
    protected void defineSynchedData() {
        
    }
}
