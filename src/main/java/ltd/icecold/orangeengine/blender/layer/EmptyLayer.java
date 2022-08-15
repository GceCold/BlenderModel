package ltd.icecold.orangeengine.blender.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import ltd.icecold.orangeengine.utils.math.OpenMatrix4f;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EmptyLayer<E extends LivingEntity, M extends EntityModel<E>> extends BlenderPatchedLayer<E, M, RenderLayer<E, M>> {
    @Override
    public void renderLayer(E entityliving, RenderLayer<E, M> originalRenderer, PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {

    }
}