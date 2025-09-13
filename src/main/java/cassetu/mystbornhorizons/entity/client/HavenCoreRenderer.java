package cassetu.mystbornhorizons.entity.client;

import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.entity.custom.HavenCoreEntity;
import cassetu.mystbornhorizons.entity.custom.MantisEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class HavenCoreRenderer extends MobEntityRenderer<HavenCoreEntity, HavenCoreModel<HavenCoreEntity>> {
    public HavenCoreRenderer(EntityRendererFactory.Context context) {
        super(context, new HavenCoreModel<>(context.getPart(HavenCoreModel.HAVEN_CORE)), 0.75f);
    }

    @Override
    public Identifier getTexture(HavenCoreEntity entity) {
        return Identifier.of(MystbornHorizons.MOD_ID, "textures/entity/haven_core.png");
    }

    @Override
    public void render(HavenCoreEntity livingEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i) {
        if(livingEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
