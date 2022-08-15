package ltd.icecold.orangeengine.blender.gameasset;

import ltd.icecold.orangeengine.blender.model.ClientModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ClientModels extends Models<ClientModel>{
    public static final ClientModels LOGICAL_CLIENT = new ClientModels();
    public final ClientModel playerFirstPerson;

    public final ClientModel playerFirstPersonAlex;

    public final ClientModel helmet;
    public final ClientModel chestplate;
    public final ClientModel leggins;
    public final ClientModel boots;

    public ClientModels() {
        this.biped = register(new ResourceLocation("orangeengine", "entity/biped"));
        this.bipedOldTexture = register(new ResourceLocation("orangeengine", "entity/biped_old_texture"));
        this.bipedAlex = register(new ResourceLocation("orangeengine", "entity/biped_slim_arm"));
        this.playerFirstPerson = register(new ResourceLocation("orangeengine", "entity/biped_firstperson"));
        this.playerFirstPersonAlex = register(new ResourceLocation("orangeengine", "entity/biped_firstperson_slim"));

        this.helmet = register(new ResourceLocation("orangeengine", "armor/helmet_default"));
        this.chestplate = register(new ResourceLocation("orangeengine", "armor/chestplate_default"));
        this.leggins = register(new ResourceLocation("orangeengine", "armor/leggings_default"));
        this.boots = register(new ResourceLocation("orangeengine", "armor/boots_default"));
    }

    @Override
    public ClientModel register(ResourceLocation rl) {
        ClientModel model = new ClientModel(rl);
        this.register(rl, model);
        return model;
    }

    public void register(ResourceLocation rl, ClientModel model) {
        this.models.put(rl, model);
    }

    public void loadModels(ResourceManager resourceManager) {
        List<ResourceLocation> emptyResourceLocations = Lists.newArrayList();

        this.models.entrySet().forEach((entry) -> {
            if (!entry.getValue().loadMeshAndProperties(resourceManager)) {
                emptyResourceLocations.add(entry.getKey());
            }
        });

        emptyResourceLocations.forEach(this.models::remove);
    }

    @Override
    public Models<?> getModels(boolean isLogicalClient) {
        return isLogicalClient ? LOGICAL_CLIENT : LOGICAL_SERVER;
    }

}
