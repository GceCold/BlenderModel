package ltd.icecold.orangeengine.blender.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import ltd.icecold.orangeengine.blender.gameasset.ClientModels;
import ltd.icecold.orangeengine.utils.math.OpenMatrix4f;
import ltd.icecold.orangeengine.utils.math.Vec4f;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientModel extends Model {
    protected Mesh mesh;
    protected RenderProperties properties;

    public ClientModel(ResourceLocation location) {
        this(location, null);
    }

    public ClientModel(ResourceLocation location, Mesh mesh) {
        super(location);
        this.mesh = mesh;
        this.properties = RenderProperties.DEFAULT;
    }

    public RenderProperties getProperties() {
        return this.properties;
    }

    public boolean loadMeshAndProperties(ResourceManager resourceManager) {
        JsonModelLoader loader = new JsonModelLoader(resourceManager, this.getLocation());

        if (loader.isValidSource()) {
            ResourceLocation parent = loader.getParent();

            if (parent == null) {
                this.mesh = loader.getMesh();
            } else {
                ClientModel model = ClientModels.LOGICAL_CLIENT.get(parent);
                if (model == null) {
                    throw new IllegalStateException("the parent location " + parent + " not exists!");
                }

                this.mesh = ClientModels.LOGICAL_CLIENT.get(parent).getMesh();
            }

            this.properties = loader.getRenderProperties();

            return true;
        }

        return false;
    }

    public Mesh getMesh() {
        return this.mesh;
    }

    public void drawRawModel(PoseStack posetStack, VertexConsumer builder, int packedLightIn, float r, float g, float b, float a, int overlayCoord) {
        Matrix4f matrix4f = posetStack.last().pose();
        Matrix3f matrix3f = posetStack.last().normal();
        Mesh mesh = this.getMesh();

        for (VertexIndicator vi : mesh.vertexIndicators) {
            int pos = vi.position * 3;
            int norm = vi.normal * 3;
            int uv = vi.uv * 2;
            Vector4f posVec = new Vector4f(mesh.positions[pos], mesh.positions[pos + 1], mesh.positions[pos + 2], 1.0F);
            Vector3f normVec = new Vector3f(mesh.noramls[norm], mesh.noramls[norm + 1], mesh.noramls[norm + 2]);
            posVec.transform(matrix4f);
            normVec.transform(matrix3f);
            builder.vertex(posVec.x(), posVec.y(), posVec.z(), r, g, b, a, mesh.uvs[uv], mesh.uvs[uv + 1], overlayCoord, packedLightIn, normVec.x(), normVec.y(), normVec.z());
        }
    }

    public void drawAnimatedModel(PoseStack posetStack, VertexConsumer builder, int packedLightIn, float r, float g, float b, float a, int overlayCoord, OpenMatrix4f[] poses) {
        Matrix4f matrix4f = posetStack.last().pose();
        Matrix3f matrix3f = posetStack.last().normal();
        OpenMatrix4f[] posesNoTranslation = new OpenMatrix4f[poses.length];
        Mesh mesh = this.getMesh();

        for (int i = 0; i < poses.length; i++) {
            posesNoTranslation[i] = poses[i].removeTranslation();
        }

        for (VertexIndicator vi : mesh.vertexIndicators) {
            int pos = vi.position * 3;
            int norm = vi.normal * 3;
            int uv = vi.uv * 2;
            Vec4f position = new Vec4f(mesh.positions[pos], mesh.positions[pos + 1], mesh.positions[pos + 2], 1.0F);
            Vec4f normal = new Vec4f(mesh.noramls[norm], mesh.noramls[norm + 1], mesh.noramls[norm + 2], 1.0F);
            Vec4f totalPos = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);
            Vec4f totalNorm = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);

            for (int i = 0; i < vi.joint.size(); i++) {
                int jointIndex = vi.joint.get(i);
                int weightIndex = vi.weight.get(i);
                float weight = mesh.weights[weightIndex];
                Vec4f.add(OpenMatrix4f.transform(poses[jointIndex], position, null).scale(weight), totalPos, totalPos);
                Vec4f.add(OpenMatrix4f.transform(posesNoTranslation[jointIndex], normal, null).scale(weight), totalNorm, totalNorm);
            }

            Vector4f posVec = new Vector4f(totalPos.x, totalPos.y, totalPos.z, 1.0F);
            Vector3f normVec = new Vector3f(totalNorm.x, totalNorm.y, totalNorm.z);
            posVec.transform(matrix4f);
            normVec.transform(matrix3f);
            builder.vertex(posVec.x(), posVec.y(), posVec.z(), r, g, b, a, mesh.uvs[uv], mesh.uvs[uv + 1], overlayCoord, packedLightIn, normVec.x(), normVec.y(), normVec.z());
        }
    }

    public void drawAnimatedModelNoTexture(PoseStack posetStack, VertexConsumer builder, int packedLightIn, float r, float g, float b, float a, int overlayCoord, OpenMatrix4f[] poses) {
        Matrix4f matrix4f = posetStack.last().pose();
        Matrix3f matrix3f = posetStack.last().normal();
        OpenMatrix4f[] posesNoTranslation = new OpenMatrix4f[poses.length];
        Mesh mesh = this.getMesh();

        for (int i = 0; i < poses.length; i++) {
            posesNoTranslation[i] = poses[i].removeTranslation();
        }

        for (VertexIndicator vi : mesh.vertexIndicators) {
            int pos = vi.position * 3;
            int norm = vi.normal * 3;
            Vec4f position = new Vec4f(mesh.positions[pos], mesh.positions[pos + 1], mesh.positions[pos + 2], 1.0F);
            Vec4f normal = new Vec4f(mesh.noramls[norm], mesh.noramls[norm + 1], mesh.noramls[norm + 2], 1.0F);
            Vec4f totalPos = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);
            Vec4f totalNorm = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);

            for (int i = 0; i < vi.joint.size(); i++) {
                int jointIndex = vi.joint.get(i);
                int weightIndex = vi.weight.get(i);
                float weight = mesh.weights[weightIndex];
                Vec4f.add(OpenMatrix4f.transform(poses[jointIndex], position, null).scale(weight), totalPos, totalPos);
                Vec4f.add(OpenMatrix4f.transform(posesNoTranslation[jointIndex], normal, null).scale(weight), totalNorm, totalNorm);
            }

            Vector4f posVec = new Vector4f(totalPos.x, totalPos.y, totalPos.z, 1.0F);
            Vector3f normVec = new Vector3f(totalNorm.x, totalNorm.y, totalNorm.z);
            posVec.transform(matrix4f);
            normVec.transform(matrix3f);
            builder.vertex(posVec.x(), posVec.y(), posVec.z());
            builder.color(r, g, b, a);
            builder.uv2(packedLightIn);
            builder.endVertex();
        }
    }

}