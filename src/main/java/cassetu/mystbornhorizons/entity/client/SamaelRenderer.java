package cassetu.mystbornhorizons.entity.client;

import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.entity.custom.SamaelEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class SamaelRenderer extends MobEntityRenderer<SamaelEntity, SamaelModel<SamaelEntity>> {
    public SamaelRenderer(EntityRendererFactory.Context context) {
        super(context, new SamaelModel<>(context.getPart(SamaelModel.SAMAEL)), 0.75f);
    }

    @Override
    public Identifier getTexture(SamaelEntity entity) {
        return Identifier.of(MystbornHorizons.MOD_ID, "textures/entity/samael.png");
    }

    @Override
    public void render(SamaelEntity livingEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i) {
        if(livingEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
