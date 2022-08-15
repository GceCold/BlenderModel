package ltd.icecold.orangeengine.blender.gameasset;

import com.google.common.collect.Maps;
import ltd.icecold.orangeengine.blender.model.Model;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Map;

public abstract class Models<T extends Model> {
	public static final ServerModels LOGICAL_SERVER = new ServerModels();
	protected final Map<ResourceLocation, T> models = Maps.newHashMap();
	
	public T biped;
	public T bipedOldTexture;
	public T bipedAlex;
	
	public static class ServerModels extends Models<Model> {
		public ServerModels() {
			this.biped = register(new ResourceLocation("orangeengine", "entity/biped"));
			this.bipedOldTexture = register(new ResourceLocation("orangeengine", "entity/biped_old_texture"));
			this.bipedAlex = register(new ResourceLocation("orangeengine", "entity/biped_slim_arm"));
		}
		
		@Override
		public Models<?> getModels(boolean isLogicalClient) {
			return LOGICAL_SERVER;
		}
		
		@Override
		public Model register(ResourceLocation rl) {
			Model model = new Model(rl);
			this.models.put(rl, model);
			return model;
		}
	}
	
	public abstract T register(ResourceLocation rl);
	
	public T get(ResourceLocation location) {
		return this.models.get(location);
	}
	
	public void loadArmatures(ResourceManager resourceManager) {
		this.biped.loadArmatureData(resourceManager);
		this.bipedOldTexture.loadArmatureData(this.biped.getArmature());
		this.bipedAlex.loadArmatureData(this.biped.getArmature());
	}
	
	public abstract Models<?> getModels(boolean isLogicalClient);
}