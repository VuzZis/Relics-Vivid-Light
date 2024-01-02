package com.skoow.relics_vivid_light.common.item;

import com.skoow.relics_vivid_light.common.entity.ForcefieldEntity;
import com.skoow.relics_vivid_light.common.entity.StaticChargeEntity;
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
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class StaticRifleRelic extends RelicItem {
    public static int forceFieldCooldown = 0;
    public static int defFieldCooldown = 60*20;
    protected final RelicData data = RelicData.builder()
            .abilityData(RelicAbilityData.builder()
                    .ability("bzzshot", RelicAbilityEntry.builder()
                            .maxLevel(5)
                            .stat("enemy_count", RelicAbilityStat.builder()
                                    .initialValue(3,3)
                                    .upgradeModifier(RelicAbilityStat.Operation.ADD,1)
                                    .formatValue(value -> (int) Math.round(value))
                                    .build())
                            .stat("damage", RelicAbilityStat.builder()
                                    .initialValue(3,5)
                                    .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE,1.5)
                                    .formatValue(value -> (int) Math.round(value))
                                    .build())
                            .stat("static_duration", RelicAbilityStat.builder()
                                    .initialValue(2,3)
                                    .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE,2)
                                    .formatValue(value -> (int) Math.round(value))
                                    .build())
                            .build()
                    )
                    .ability("forcefield", RelicAbilityEntry.builder()
                            .maxLevel(3)
                            .stat("radius", RelicAbilityStat.builder()
                                    .initialValue(3,5)
                                    .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE,0.6)
                                    .formatValue(value -> (int) Math.round(value))
                                    .build())
                            .stat("duration", RelicAbilityStat.builder()
                                    .initialValue(8,10)
                                    .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE,1.5)
                                    .formatValue(value -> (int) Math.round(value))
                                    .build())
                            .stat("static_probability", RelicAbilityStat.builder()
                                    .initialValue(1,1)
                                    .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_TOTAL,5)
                                    .formatValue(value -> Math.min((int) Math.round(value),100))
                                    .build())
                            .build()
                    )
                    .build())
            .levelingData(new RelicLevelingData(150,8,150))
            .styleData(RelicStyleData.builder()
                    .borders("#D5A07A","#D5A07A")
                    .build())
            .build();
    public StaticRifleRelic() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).defaultDurability(StaticRifleRelic.defFieldCooldown+1));
    }

    @Override
    public RelicData getRelicData() {
        return data;
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, AbilityCastType type, AbilityCastStage stage) {
        if (ability.equals("bzzshot")) useBzShot(stack,player);
    }

    private void useBzShot(ItemStack stack, Player player) {
        createBzShot(stack,player,new Vec3(0,0,0),false);
        if(!AbilityUtils.isAbilityMaxLevel(stack,"bzzshot")) return;
        createBzShot(stack,player,new Vec3(-3,0,0),true);
        createBzShot(stack,player,new Vec3(3,0,0),true);
        createBzShot(stack,player,new Vec3(0,0,3),true);
        createBzShot(stack,player,new Vec3(0,0,-3),true);
    }

    private void createBzShot(ItemStack stack, Player player, Vec3 translate, boolean isEssential) {
        StaticChargeEntity charge = EntityRegistry.STATIC_CHARGE.get().spawn((ServerLevel) player.level(),stack,player, BlockPos.containing(player.getEyePosition()), MobSpawnType.EVENT, true, true);
        float xRot = player.getXRot();
        float yRot = player.getYHeadRot();
        float f = -Mth.sin(yRot * ((float)Math.PI / 180F)) * Mth.cos(xRot * ((float)Math.PI / 180F));
        float f1 = -Mth.sin((xRot) * ((float)Math.PI / 180F));
        float f2 = Mth.cos(yRot * ((float)Math.PI / 180F)) * Mth.cos(xRot * ((float)Math.PI / 180F));
        charge.deltaMovement = new Vec3(f,f1,f2);
        charge.setPos(player.getEyePosition().add(translate));
        charge.damage = (int) AbilityUtils.getAbilityValue(stack,"bzzshot","damage");
        if(isEssential) charge.damage /= 20;
        if(isEssential) charge.deltaMovement = charge.deltaMovement.multiply(0.5,0.5,0.5);
        charge.isEssential = isEssential;
        charge.ricochetsLeft = (int) AbilityUtils.getAbilityValue(stack,"bzzshot","enemy_count");
        charge.staticDuration = (int) AbilityUtils.getAbilityValue(stack,"bzzshot","static_duration");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(level.isClientSide()) return super.use(level, player, hand);
        ItemStack stack = player.getItemInHand(hand);
        if(!player.isShiftKeyDown()) {
            if(!player.getCooldowns().isOnCooldown(stack.getItem())) {
                useBzShot(stack,player);
                if(!player.isCreative()) player.getCooldowns().addCooldown(stack.getItem(),3*20);
                return InteractionResultHolder.consume(stack);
            }
            return InteractionResultHolder.fail(stack);
        }
        if(forceFieldCooldown > 0) {return super.use(level, player, hand);}
        useForcefield(stack,player);
        StaticRifleRelic.forceFieldCooldown = defFieldCooldown;
        return super.use(level, player, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        if(level.isClientSide()) return;
        if(StaticRifleRelic.forceFieldCooldown > 0) StaticRifleRelic.forceFieldCooldown--;
        setDamage(stack,forceFieldCooldown);
    }


    private void useForcefield(ItemStack stack, Player player) {
        ForcefieldEntity forcefield =  EntityRegistry.FORCEFIELD.get().spawn((ServerLevel) player.level(),stack,player,
                BlockPos.containing(player.getEyePosition()), MobSpawnType.EVENT,
                true, true);
        forcefield.setRadius((int) AbilityUtils.getAbilityValue(stack,"forcefield","radius"));
        forcefield.setMaxTime((int) AbilityUtils.getAbilityValue(stack,"forcefield","duration"));
        forcefield.setProp((int) AbilityUtils.getAbilityValue(stack,"forcefield","static_probability"));
    }
}
