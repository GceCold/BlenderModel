package ltd.icecold.orangeengine.blender.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import ltd.icecold.orangeengine.utils.math.OpenMatrix4f;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

public abstract class BlenderPatchedLayer<E extends LivingEntity, M extends EntityModel<E>, R extends RenderLayer<E, M>> {
    public final void renderLayer(int z, E entityliving, RenderLayer<E, M> originalRenderer, PoseStack matrixStackIn,
                                  MultiBufferSource buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead,
                                  float pitchHead, float partialTicks) {
        this.renderLayer(entityliving, this.cast(originalRenderer), matrixStackIn, buffer, packedLightIn, poses, netYawHead, pitchHead, partialTicks);
    }

    public abstract void renderLayer(E entityliving, R originalRenderer, PoseStack matrixStackIn, MultiBufferSource buffer,
                                     int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead,
                                     float partialTicks);

    @SuppressWarnings("unchecked")
    private R cast(RenderLayer<E, M> layer) {
        return (R)layer;
    }
}
