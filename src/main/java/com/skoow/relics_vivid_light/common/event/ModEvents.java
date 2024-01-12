package com.skoow.relics_vivid_light.common.event;

import com.skoow.relics_vivid_light.VividLight;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VividLight.MOD_ID)
public class ModEvents {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onFall(LivingFallEvent event) {
        if(event.getEntity().getTags().contains("prevent_fall")) {
            event.setCanceled(true);
            event.setDamageMultiplier(0);
            event.getEntity().removeTag("prevent_fall");
        }
    }
}
