package com.skoow.relics_vivid_light;

import com.google.common.collect.ImmutableMap;
import com.skoow.relics_vivid_light.common.config.ClientConfig;
import com.skoow.relics_vivid_light.common.loot_modifiers.LootModifiers;
import com.skoow.relics_vivid_light.common.registry.EffectRegistry;
import com.skoow.relics_vivid_light.common.registry.EntityRegistry;
import com.skoow.relics_vivid_light.common.registry.ItemRegistry;
import it.hurts.sskirillss.relics.world.RelicLootModifier;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
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
        LootModifiers.LOOT_MODIFIER_SERIALIZERS.register(eventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        eventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ItemRegistry.STATIC_ENERGIZED_CLAW);
        }
    }

    private static Int2ObjectMap<VillagerTrades.ItemListing[]> toIntMap(ImmutableMap<Integer, VillagerTrades.ItemListing[]> p_35631_) {
        return new Int2ObjectOpenHashMap<>(p_35631_);
    }
}
