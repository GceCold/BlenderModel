package ltd.icecold.orangeengine.blender.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import ltd.icecold.orangeengine.blender.gameasset.ClientModels;
import ltd.icecold.orangeengine.utils.math.MathUtils;
import ltd.icecold.orangeengine.utils.math.OpenMatrix4f;
import ltd.icecold.orangeengine.utils.math.Vec3f;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.world.entity.EquipmentSlot;

public class PatchedElytraLayer extends BlenderPatchedLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>, ElytraLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>> {
    @Override
    public void renderLayer(AbstractClientPlayer livingentity, ElytraLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> originalRenderer, PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
        if (originalRenderer.shouldRender(livingentity.getItemBySlot(EquipmentSlot.CHEST), livingentity)) {
            OpenMatrix4f modelMatrix = new OpenMatrix4f();
            modelMatrix.scale(new Vec3f(-0.9F, -0.9F, 0.9F)).translate(new Vec3f(0.0F, -0.5F, -0.1F))
                    .mulFront(ClientModels.LOGICAL_CLIENT.biped.getArmature().searchJointById(8).getAnimatedTransform());
            OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
            matrixStackIn.pushPose();
            MathUtils.translateStack(matrixStackIn, modelMatrix);
            MathUtils.rotateStack(matrixStackIn, transpose);
            originalRenderer.render(matrixStackIn, buffer, packedLightIn, livingentity, livingentity.animationPosition, livingentity.animationSpeed, partialTicks, livingentity.tickCount, netYawHead, pitchHead);
            matrixStackIn.popPose();
        }
    }
}
