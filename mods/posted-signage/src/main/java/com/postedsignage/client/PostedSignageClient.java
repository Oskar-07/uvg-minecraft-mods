package com.postedsignage.client;

import com.postedsignage.PostedSignage;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** Registers block-entity renderers only on the client; static block models are handled by Minecraft. */
@EventBusSubscriber(modid = PostedSignage.MOD_ID, value = Dist.CLIENT)
public final class PostedSignageClient {
    private PostedSignageClient() {}

    /** Bind each registered entity type to its client renderer during renderer setup. */
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(PostedSignage.TEXTURE_COVER_ENTITY.get(), TextureCoverRenderer::new);
        event.registerBlockEntityRenderer(
                PostedSignage.CUSTOMIZABLE_HANGING_SIGN_BLOCK_ENTITY.get(),
                CustomizableHangingSignRenderer::new);
    }
}
