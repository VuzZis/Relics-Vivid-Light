package com.skoow.relics_vivid_light.common.event;

import com.skoow.relics_vivid_light.VividLight;
import com.skoow.relics_vivid_light.common.registry.EntityRegistry;
import it.hurts.sskirillss.relics.client.renderer.entities.NullRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VividLight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientBusEvents {
    @SubscribeEvent
    public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.STATIC_CHARGE.get(), new NullRenderer.RenderFactory());
        event.registerEntityRenderer(EntityRegistry.FORCEFIELD.get(),new NullRenderer.RenderFactory());
        event.registerEntityRenderer(EntityRegistry.SWIRL.get(),new NullRenderer.RenderFactory());
        event.registerEntityRenderer(EntityRegistry.BUBBLE.get(),new NullRenderer.RenderFactory());
    }
}
