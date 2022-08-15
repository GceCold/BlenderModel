package ltd.icecold.orangeengine.blender.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import ltd.icecold.orangeengine.blender.animation.AnimationPlayer;
import ltd.icecold.orangeengine.blender.animation.Joint;
import ltd.icecold.orangeengine.blender.gameasset.ClientModels;
import ltd.icecold.orangeengine.blender.layer.BlenderPatchedLayer;
import ltd.icecold.orangeengine.blender.model.Armature;
import ltd.icecold.orangeengine.blender.model.ClientModel;
import ltd.icecold.orangeengine.blender.model.JsonModelLoader;
import ltd.icecold.orangeengine.utils.math.MathUtils;
import ltd.icecold.orangeengine.utils.math.OpenMatrix4f;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import ltd.icecold.orangeengine.blender.animation.Pose;
import net.minecraft.world.entity.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class BlenderModelEntityRender<T extends LivingEntityRenderer, M extends LivingEntity, E extends EntityModel<M>> extends EntityRenderer<M> {
    private T originalRender;
    private ClientModel model = ClientModels.LOGICAL_CLIENT.biped;
    protected Pose prevPose = new Pose();
    protected Pose currentPose = new Pose();
    private AnimationPlayer animationPlayer;
    private static final ResourceLocation STEVE_SKIN_LOCATION = new ResourceLocation("textures/entity/steve.png");
    private Map<Class<?>, BlenderPatchedLayer<M, E, ? extends RenderLayer<M, E>>> patchedLayers = Maps.newHashMap();

    public BlenderModelEntityRender(EntityRendererProvider.Context ctx, T originalRender) {
        super(ctx);
        this.originalRender = originalRender;
        animationPlayer = new AnimationPlayer();
        animationPlayer.setPlayAnimation(JsonModelLoader.testAnimation);
    }

    public void addPatchedLayer(Class<?> originalLayerClass, BlenderPatchedLayer<M, E, ? extends RenderLayer<M, E>> patchedLayer) {
        this.patchedLayers.put(originalLayerClass, patchedLayer);
    }

    @Override
    public void render(M entityIn, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
            prevPose = currentPose;
            animationPlayer.update();
            currentPose = animationPlayer.getCurrentPose(partialTicks);
            RenderType renderType = OrangeRenderTypes.itemEntityTranslucentCull(STEVE_SKIN_LOCATION);
            Armature armature = model.getArmature();
            poseStack.pushPose();
            this.mulPoseStack(poseStack, armature, entityIn, partialTicks);
            OpenMatrix4f[] poseMatrices = this.getPoseMatrices(armature, partialTicks);
            if (renderType != null) {
                VertexConsumer builder = buffer.getBuffer(renderType);
                model.drawAnimatedModel(poseStack, builder, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.pack(0, OverlayTexture.v(entityIn.hurtTime > 5)), poseMatrices);
            }
            if (!entityIn.isSpectator()) {
                this.renderLayer(originalRender, entityIn, poseMatrices, buffer, poseStack, packedLight, partialTicks);
            }
            poseStack.popPose();
        
        //originalRender.render(entityIn, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    protected void renderLayer(T renderer, M entityIn, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLightIn, float partialTicks) {
        List<RenderLayer<M,E>> layers = Lists.newArrayList(renderer.layers);
        Iterator<RenderLayer<M,E>> iter = layers.iterator();
        float f = MathUtils.lerpBetween(entityIn.yBodyRotO, entityIn.yBodyRot, partialTicks);
        float f1 = MathUtils.lerpBetween(entityIn.yHeadRotO, entityIn.yHeadRot, partialTicks);
        float f2 = f1 - f;
        float f7 = entityIn.getViewXRot(partialTicks);

        while (iter.hasNext()) {
            RenderLayer<M,E> layer = iter.next();
            Class<?> rendererClass = layer.getClass();

            if (rendererClass.isAnonymousClass()) {
                rendererClass = rendererClass.getSuperclass();
            }

            this.patchedLayers.computeIfPresent(rendererClass, (key, val) -> {
                val.renderLayer(0,entityIn, layer, poseStack, buffer, packedLightIn, poses, f2, f7, partialTicks);
                iter.remove();
                return val;
            });
        }

        OpenMatrix4f modelMatrix = new OpenMatrix4f();
        modelMatrix.mulFront(model.getArmature().searchJointById(0).getAnimatedTransform());
        OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);

        poseStack.pushPose();
        MathUtils.translateStack(poseStack, modelMatrix);
        MathUtils.rotateStack(poseStack, transpose);
        poseStack.translate(0.0D, 0.75D, 0.0D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        layers.forEach((layer) -> {
            layer.render(poseStack, buffer, packedLightIn, entityIn, entityIn.animationPosition, entityIn.animationSpeed, partialTicks, entityIn.tickCount, f2, f7);
        });

        poseStack.popPose();
    }

    public OpenMatrix4f[] getPoseMatrices(Armature armature, float partialTicks) {
        armature.initializeTransform();
        Joint rootJoint = model.getArmature().getJointHierarcy();
        OpenMatrix4f[] poseMatrices = armature.getJointTransforms();
        this.applyPoseToJoint(rootJoint, new OpenMatrix4f(), this.getPose(partialTicks), partialTicks);
        return armature.getJointTransforms();
    }

    public void applyPoseToJoint(Joint joint, OpenMatrix4f parentTransform, Pose pose, float partialTicks) {
        OpenMatrix4f result = pose.getOrDefaultTransform(joint.getName()).getAnimationBindedMatrix(joint, parentTransform);
        joint.setAnimatedTransform(result);

        for (Joint joints : joint.getSubJoints()) {
            this.applyPoseToJoint(joints, result, pose, partialTicks);
        }
    }

    public Pose getPose(float partialTicks) {
        return Pose.interpolatePose(this.prevPose, this.currentPose, partialTicks);
    }

    public void mulPoseStack(PoseStack poseStack, Armature armature, M entityIn, float partialTicks) {
        OpenMatrix4f modelMatrix = MathUtils.getModelMatrixIntegral(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, entityIn.yBodyRotO, entityIn.yBodyRot, partialTicks, 1.0f, 1.0f, 1.0f);
        OpenMatrix4f transpose = modelMatrix.transpose(null);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        MathUtils.translateStack(poseStack, modelMatrix);
        MathUtils.rotateStack(poseStack, transpose);
        MathUtils.scaleStack(poseStack, transpose);
    }

    @Override
    public ResourceLocation getTextureLocation(M entity) {
        return originalRender.getTextureLocation(entity);
    }

}
