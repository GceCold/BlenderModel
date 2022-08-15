package ltd.icecold.orangeengine.blender.render;

import ltd.icecold.orangeengine.blender.layer.*;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;


public class PlayerBlenderModelRender extends BlenderModelEntityRender<PlayerRenderer, AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public PlayerBlenderModelRender(EntityRendererProvider.Context ctx, PlayerRenderer originalRender) {
        super(ctx, originalRender);
        this.addPatchedLayer(ArrowLayer.class, new EmptyLayer<>());
        this.addPatchedLayer(BeeStingerLayer.class, new EmptyLayer<>());
        this.addPatchedLayer(ElytraLayer.class, new PatchedElytraLayer());
        this.addPatchedLayer(HumanoidArmorLayer.class, new WearableItemLayer<>());
        this.addPatchedLayer(CapeLayer.class, new PatchedCapeLayer());
        this.addPatchedLayer(PlayerItemInHandLayer.class, new PatchedItemInHandLayer());
    }
}
