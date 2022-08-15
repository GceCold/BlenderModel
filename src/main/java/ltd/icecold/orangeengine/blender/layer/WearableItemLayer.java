package ltd.icecold.orangeengine.blender.layer;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ltd.icecold.orangeengine.blender.gameasset.ClientModels;
import ltd.icecold.orangeengine.blender.model.ClientModel;
import ltd.icecold.orangeengine.blender.render.OrangeRenderTypes;
import ltd.icecold.orangeengine.utils.math.OpenMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;

import java.util.HashMap;
import java.util.Map;

public class WearableItemLayer<E extends LivingEntity, M extends HumanoidModel<E>> extends BlenderPatchedLayer<E, M, HumanoidArmorLayer<E, M, M>> {
    private static final Map<ResourceLocation, ClientModel> ARMOR_MODELS = new HashMap<>();
    private static final Map<String, ResourceLocation> EPICFIGHT_OVERRIDING_TEXTURES = Maps.newHashMap();

    public static void clear() {
        ARMOR_MODELS.clear();
        EPICFIGHT_OVERRIDING_TEXTURES.clear();
    }

    final boolean doNotRenderHelment;

    public WearableItemLayer() {
        this(false);
    }

    public WearableItemLayer(boolean doNotRenderHelment) {
        this.doNotRenderHelment = doNotRenderHelment;
    }

    private void renderArmor(PoseStack matStack, MultiBufferSource multiBufferSource, int packedLightIn, boolean hasEffect, ClientModel model, float r, float g, float b, ResourceLocation armorTexture, OpenMatrix4f[] poses) {
        VertexConsumer vertexConsumer = OrangeRenderTypes.getArmorVertexBuilder(multiBufferSource, OrangeRenderTypes.animatedArmor(armorTexture, model.getProperties().isTransparent()), hasEffect);
        model.drawAnimatedModel(matStack, vertexConsumer, packedLightIn, r, g, b, 1.0F, OverlayTexture.NO_OVERLAY, poses);
    }

    @Override
    public void renderLayer(E entityliving, HumanoidArmorLayer<E, M, M> originalRenderer, PoseStack poseStack,
                            MultiBufferSource buf, int packedLightIn, OpenMatrix4f[] poses, float netYawHead,
                            float pitchHead, float partialTicks) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.ARMOR) {
                continue;
            }

            if (slot == EquipmentSlot.HEAD && this.doNotRenderHelment) {
                continue;
            }

            ItemStack stack = entityliving.getItemBySlot(slot);
            Item item = stack.getItem();

            if (item instanceof ArmorItem) {
                ArmorItem armorItem = (ArmorItem)stack.getItem();

                if (slot != armorItem.getSlot()) {
                    return;
                }

                poseStack.pushPose();
                float head = 0.0F;

                if (originalRenderer.getParentModel().getHead() != null && originalRenderer.getParentModel().getHead().cubes.size() > 0) {
                    ModelPart.Cube headCube = originalRenderer.getParentModel().getHead().cubes.get(0);
                    head = headCube.maxX - headCube.minY - 12.0F;
                }

                if (slot == EquipmentSlot.HEAD) {
                    poseStack.translate(0.0D, head * 0.055D, 0.0D);
                }

                ClientModel model = this.getArmorModel(originalRenderer, entityliving, armorItem, stack, slot);
                boolean hasEffect = stack.hasFoil();

                if (armorItem instanceof DyeableLeatherItem) {
                    int i = ((DyeableLeatherItem)armorItem).getColor(stack);
                    float r = (float) (i >> 16 & 255) / 255.0F;
                    float g = (float) (i >> 8 & 255) / 255.0F;
                    float b = (float) (i & 255) / 255.0F;
                    this.renderArmor(poseStack, buf, packedLightIn, hasEffect, model, r, g, b, this.getArmorTexture(stack, entityliving, slot, null), poses);
                    this.renderArmor(poseStack, buf, packedLightIn, hasEffect, model, 1.0F, 1.0F, 1.0F, this.getArmorTexture(stack, entityliving, slot, "overlay"), poses);
                } else {
                    this.renderArmor(poseStack, buf, packedLightIn, hasEffect, model, 1.0F, 1.0F, 1.0F, this.getArmorTexture(stack, entityliving, slot, null), poses);
                }

                poseStack.popPose();
            }
        }
    }

    private ClientModel getArmorModel(HumanoidArmorLayer<E, M, M> originalRenderer, E entityliving, ArmorItem armorItem, ItemStack stack, EquipmentSlot slot) {
        ResourceLocation registryName = armorItem.getRegistryName();

        if (ARMOR_MODELS.containsKey(registryName)) {
            return ARMOR_MODELS.get(registryName);
        } else {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            ResourceLocation rl = new ResourceLocation(armorItem.getRegistryName().getNamespace(), "armor/" + armorItem.getRegistryName().getPath());
            ClientModel model = new ClientModel(rl);

            if (resourceManager.hasResource(model.getLocation())) {
                model.loadMeshAndProperties(resourceManager);
            } else {
                HumanoidModel<?> defaultModel = originalRenderer.getArmorModel(slot);
                Model customModel = ForgeHooksClient.getArmorModel(entityliving, stack, slot, defaultModel);

                if (customModel == defaultModel || !(customModel instanceof HumanoidModel)) {
                    model = getDefaultArmorModel(slot);
                }
            }

            ARMOR_MODELS.put(registryName, model);

            return model;
        }
    }

    private ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        ArmorItem item = (ArmorItem) stack.getItem();
        String texture = item.getMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');

        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }

        String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (slot == EquipmentSlot.LEGS ? 2 : 1), type == null ? "" : String.format("_%s", type));
        s1 = ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
        int idx2 = s1.lastIndexOf('/');
        String s2 = String.format("%s/epicfight/%s", s1.substring(0, idx2), s1.substring(idx2 + 1));
        ResourceLocation resourcelocation2 = EPICFIGHT_OVERRIDING_TEXTURES.get(s2);

        if (resourcelocation2 != null) {
            return resourcelocation2;
        } else if (!EPICFIGHT_OVERRIDING_TEXTURES.containsKey(s2)) {
            resourcelocation2 = new ResourceLocation(s2);
            ResourceManager rm = Minecraft.getInstance().getResourceManager();

            if (rm.hasResource(resourcelocation2)) {
                EPICFIGHT_OVERRIDING_TEXTURES.put(s2, resourcelocation2);
                return resourcelocation2;
            } else {
                EPICFIGHT_OVERRIDING_TEXTURES.put(s2, null);
            }
        }

        ResourceLocation resourcelocation = HumanoidArmorLayer.ARMOR_LOCATION_CACHE.get(s1);

        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(s1);
            HumanoidArmorLayer.ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
        }

        return resourcelocation;
    }

    public static ClientModel getDefaultArmorModel(EquipmentSlot slot) {
        ClientModels modelDB = ClientModels.LOGICAL_CLIENT;

        switch (slot) {
            case HEAD:
                return modelDB.helmet;
            case CHEST:
                return modelDB.chestplate;
            case LEGS:
                return modelDB.leggins;
            case FEET:
                return modelDB.boots;
            default:
                return null;
        }
    }
}
