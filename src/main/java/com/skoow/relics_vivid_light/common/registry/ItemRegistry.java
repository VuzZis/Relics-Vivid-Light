package com.skoow.relics_vivid_light.common.registry;

import com.skoow.relics_vivid_light.VividLight;
import com.skoow.relics_vivid_light.common.item.MorphcookRelic;
import com.skoow.relics_vivid_light.common.item.StaticRifleRelic;
import com.skoow.relics_vivid_light.common.item.WhirlWindRelic;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VividLight.MOD_ID);

    public static final RegistryObject<Item> STATIC_RIFLE = ITEMS.register("static_rifle", StaticRifleRelic::new);
    public static final RegistryObject<Item> WHIRLWIND_SPEAR = ITEMS.register("whirlwind_spear", WhirlWindRelic::new);
    public static final RegistryObject<Item> MORPHCOOK = ITEMS.register("morphcook", MorphcookRelic::new);
}
