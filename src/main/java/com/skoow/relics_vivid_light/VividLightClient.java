package com.skoow.relics_vivid_light;

import com.skoow.relics_vivid_light.client.render.Item2DRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class VividLightClient {
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onModelRegistryEvent(ModelEvent.RegisterAdditional event) {
            //if (ClientConfig.IN_HAND_MODELS_32X.get()) {
                for (String item : Item2DRenderer.HAND_MODEL_ITEMS) {
                    event.register(new ModelResourceLocation(new ResourceLocation(VividLight.MOD_ID, item + "_in_hand"), "inventory"));
                }
            //}
        }

        @SubscribeEvent
        public static void onModelBakeEvent(ModelEvent.ModifyBakingResult event) {
            //if (ClientConfig.IN_HAND_MODELS_32X.get()) {
                Item2DRenderer.onModelBakeEvent(event);
            //}
        }
    }
}
