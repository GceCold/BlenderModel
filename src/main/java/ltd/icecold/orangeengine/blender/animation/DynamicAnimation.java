package ltd.icecold.orangeengine.blender.animation;

import ltd.icecold.orangeengine.blender.model.Model;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class DynamicAnimation {
    private Map<String, TransformSheet> jointTransforms;
    private final boolean isRepeat;
    private final float convertTime;
    private float totalTime = 0.0F;
    private Model model;
    private ResourceLocation resourceLocation;

    public DynamicAnimation() {
        this(1.5F, true);
    }

    public DynamicAnimation(Model model, ResourceLocation resourceLocation) {
        this.isRepeat = false;
        this.convertTime = 1.5F;
        this.model = model;
        this.resourceLocation = resourceLocation;
    }

    public DynamicAnimation(float convertTime, boolean isRepeat) {
        this.jointTransforms = new HashMap<String, TransformSheet>();
        this.isRepeat = isRepeat;
        this.convertTime = convertTime;
        this.resourceLocation = null;
        this.model = null;
    }

    public void addSheet(String jointName, TransformSheet sheet) {
        this.jointTransforms.put(jointName, sheet);
    }

    public Pose getPoseByTime(float time, float partialTicks) {
        Pose pose = new Pose();

        for (String jointName : this.jointTransforms.keySet()) {
                pose.putJointData(jointName, this.jointTransforms.get(jointName).getInterpolatedTransform(time));
        }

        return pose;
    }

    public Map<String, TransformSheet> getJointTransforms() {
        return jointTransforms;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public float getConvertTime() {
        return convertTime;
    }

    public float getTotalTime() {
        return totalTime;
    }

    public Model getModel() {
        return model;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public void setJointTransforms(Map<String, TransformSheet> jointTransforms) {
        this.jointTransforms = jointTransforms;
    }

    public void setTotalTime(float totalTime) {
        this.totalTime = totalTime;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setResourceLocation(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }
}
