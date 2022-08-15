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
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PatchedItemInHandLayer extends BlenderPatchedLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>, ItemInHandLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>> {
    @Override
    public void renderLayer(AbstractClientPlayer entityliving, ItemInHandLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> originalRenderer, PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
        if (!entityliving.isInvisible()) {
            OpenMatrix4f modelMatrix = new OpenMatrix4f();
            modelMatrix.scale(new Vec3f(-1.0F, -1.0F, 1.0F)).mulFront(ClientModels.LOGICAL_CLIENT.biped.getArmature().searchJointById(8).getAnimatedTransform());
            OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
            matrixStackIn.pushPose();
            MathUtils.translateStack(matrixStackIn, modelMatrix);
            MathUtils.rotateStack(matrixStackIn, transpose);
            matrixStackIn.translate(0.0D, 0.0D, -0.025D);
            originalRenderer.render(matrixStackIn, buffer, packedLightIn, entityliving, entityliving.animationPosition, entityliving.animationSpeed, partialTicks, entityliving.tickCount, netYawHead, pitchHead);
            matrixStackIn.popPose();
        }
    }
}