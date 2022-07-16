package de.alexbrumbart.postalpigeons.rendering;

import de.alexbrumbart.postalpigeons.ModRegistries;
import de.alexbrumbart.postalpigeons.PostalPigeons;
import de.alexbrumbart.postalpigeons.entity.Pigeon;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PostalPigeons.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PigeonRenderer extends MobRenderer<Pigeon, PigeonModel> {
    private static final ResourceLocation texture = new ResourceLocation(PostalPigeons.ID, "textures/entity/pigeon.png");

    public PigeonRenderer(EntityRendererProvider.Context context) {
        super(context, new PigeonModel(context.bakeLayer(PigeonModel.MODEL_LOCATION)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(Pigeon entity) {
        return texture;
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(PigeonModel.MODEL_LOCATION, PigeonModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModRegistries.PIGEON.get(), PigeonRenderer::new);
    }
}
