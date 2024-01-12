package com.skoow.relics_vivid_light.common.registry;

import com.skoow.relics_vivid_light.VividLight;
import com.skoow.relics_vivid_light.common.item.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VividLight.MOD_ID);

    public static final RegistryObject<Item> STATIC_RIFLE = ITEMS.register("static_rifle", StaticRifleRelic::new);
    public static final RegistryObject<Item> WHIRLWIND_SPEAR = ITEMS.register("whirlwind_spear", WhirlWindRelic::new);
    public static final RegistryObject<Item> MORPHCOOK = ITEMS.register("morphcook", MorphcookRelic::new);
    public static final RegistryObject<Item> STEAM_BOILER = ITEMS.register("steam_boiler", BoilerRelic::new);
    public static final RegistryObject<Item> TRUEFIRE_BURNER = ITEMS.register("truefire_burner", TruefireBurnerRelic::new);
    public static final RegistryObject<Item> STATIC_ENERGIZED_CLAW = ITEMS.register("static_claw", () -> new Item(new Item.Properties()));
}
