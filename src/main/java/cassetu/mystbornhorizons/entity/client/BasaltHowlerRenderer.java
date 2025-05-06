package cassetu.mystbornhorizons.entity.client;

import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.entity.custom.BasaltHowlerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class BasaltHowlerRenderer extends MobEntityRenderer<BasaltHowlerEntity, BasaltHowlerModel<BasaltHowlerEntity>> {
    public BasaltHowlerRenderer(EntityRendererFactory.Context context) {
        super(context, new BasaltHowlerModel<>(context.getPart(BasaltHowlerModel.BASALTHOWLER)), 0.75f);
    }

    @Override
    public Identifier getTexture(BasaltHowlerEntity entity) {
        return Identifier.of(MystbornHorizons.MOD_ID, "textures/entity/basalt_howler/basalt_howler.png");
    }

    @Override
    public void render(BasaltHowlerEntity livingEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i) {
        if(livingEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
