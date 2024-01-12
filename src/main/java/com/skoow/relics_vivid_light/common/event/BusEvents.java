package com.skoow.relics_vivid_light.common.event;

import com.skoow.relics_vivid_light.VividLight;
import com.skoow.relics_vivid_light.common.registry.ItemRegistry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VividLight.MOD_ID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class BusEvents {
    /*@SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        System.out.println("!!! Started adding vanilla tab items");
        if (event.getTabKey().equals(CreativeModeTabs.INGREDIENTS)) {
            event.accept(ItemRegistry.STATIC_ENERGIZED_CLAW);
            System.out.println("!!! Added vanilla ingredients tab items");
        }
    }*/
}
