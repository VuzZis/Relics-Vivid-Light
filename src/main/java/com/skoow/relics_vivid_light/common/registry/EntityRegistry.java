package com.skoow.relics_vivid_light.common.registry;

import com.skoow.relics_vivid_light.VividLight;
import com.skoow.relics_vivid_light.common.entity.BubbleEntity;
import com.skoow.relics_vivid_light.common.entity.ForcefieldEntity;
import com.skoow.relics_vivid_light.common.entity.StaticChargeEntity;
import com.skoow.relics_vivid_light.common.entity.SwirlEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, VividLight.MOD_ID);

    public static final RegistryObject<EntityType<StaticChargeEntity>> STATIC_CHARGE = ENTITIES.register("static_charge",() ->
            EntityType.Builder.of(StaticChargeEntity::new, MobCategory.MISC).sized(1f,1f).build("static_charge"));
    public static final RegistryObject<EntityType<ForcefieldEntity>> FORCEFIELD = ENTITIES.register("forcefield",() ->
            EntityType.Builder.of(ForcefieldEntity::new, MobCategory.MISC).sized(1f,1f).build("forcefield"));
    public static final RegistryObject<EntityType<SwirlEntity>> SWIRL = ENTITIES.register("swirl",() ->
            EntityType.Builder.of(SwirlEntity::new, MobCategory.MISC).sized(1f,1f).build("swirl"));

    public static final RegistryObject<EntityType<BubbleEntity>> BUBBLE = ENTITIES.register("bubble",() ->
            EntityType.Builder.of(BubbleEntity::new, MobCategory.MISC).sized(2.5f,2.5f).build("bubble"));
}
