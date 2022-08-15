package ltd.icecold.orangeengine.blender.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

public class OrangePlayerRender extends PlayerRenderer {
    private PlayerBlenderModelRender render;
    public OrangePlayerRender(EntityRendererProvider.Context ctx, PlayerRenderer originalRender) {
        super(ctx, false);
        render = new PlayerBlenderModelRender(ctx,originalRender);
    }

    @Override
    public void render(AbstractClientPlayer p_117788_, float p_117789_, float p_117790_, PoseStack p_117791_, MultiBufferSource p_117792_, int p_117793_) {
//        super.render(p_117788_, p_117789_, p_117790_, p_117791_, p_117792_, p_117793_);
        render.render(p_117788_, p_117789_, p_117790_, p_117791_, p_117792_, p_117793_);
    }
}
