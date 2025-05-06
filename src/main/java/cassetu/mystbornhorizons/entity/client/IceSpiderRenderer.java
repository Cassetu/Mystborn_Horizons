package cassetu.mystbornhorizons.entity.client;

import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.entity.custom.IceSpiderEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class IceSpiderRenderer extends MobEntityRenderer<IceSpiderEntity, IceSpiderModel<IceSpiderEntity>> {
    public IceSpiderRenderer(EntityRendererFactory.Context context) {
        super(context, new IceSpiderModel<>(context.getPart(IceSpiderModel.ICESPIDER)), 0.75f);
    }

    @Override
    public Identifier getTexture(IceSpiderEntity entity) {
        return Identifier.of(MystbornHorizons.MOD_ID, "textures/entity/ice_spider/ice_spider.png");
    }

    @Override
    public void render(IceSpiderEntity livingEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i) {
        if(livingEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
