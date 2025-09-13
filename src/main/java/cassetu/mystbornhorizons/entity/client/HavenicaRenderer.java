package cassetu.mystbornhorizons.entity.client;

import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.entity.custom.HavenicaEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class HavenicaRenderer extends MobEntityRenderer<HavenicaEntity, HavenicaModel<HavenicaEntity>> {
    public HavenicaRenderer(EntityRendererFactory.Context context) {
        super(context, new HavenicaModel<>(context.getPart(HavenicaModel.HAVENICA)), 0.75f);
    }

    @Override
    public Identifier getTexture(HavenicaEntity entity) {
        return Identifier.of(MystbornHorizons.MOD_ID, "textures/entity/havenica.png");
    }

    @Override
    public void render(HavenicaEntity livingEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i) {
        if(livingEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
