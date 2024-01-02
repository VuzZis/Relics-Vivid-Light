package com.skoow.relics_vivid_light;

import com.skoow.relics_vivid_light.common.config.ClientConfig;
import com.skoow.relics_vivid_light.common.registry.EffectRegistry;
import com.skoow.relics_vivid_light.common.registry.EntityRegistry;
import com.skoow.relics_vivid_light.common.registry.ItemRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(VividLight.MOD_ID)
public class VividLight {
    public static final String MOD_ID = "relics_vivid_light";
    public static MutableComponent translate(String cat, String item) {
        return Component.translatable(String.format("%s.%s.%s",cat,MOD_ID,item));
    }

    public VividLight() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        EntityRegistry.ENTITIES.register(eventBus);
        ItemRegistry.ITEMS.register(eventBus);
        EffectRegistry.EFFECTS.register(eventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }
}
