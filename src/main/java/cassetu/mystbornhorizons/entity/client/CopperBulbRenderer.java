package cassetu.mystbornhorizons.entity.client;

import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.entity.custom.CopperBulbEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CopperBulbRenderer extends MobEntityRenderer<CopperBulbEntity, CopperBulbModel<CopperBulbEntity>> {
    public CopperBulbRenderer(EntityRendererFactory.Context context) {
        super(context, new CopperBulbModel<>(context.getPart(CopperBulbModel.COPPERBULB)), 0.75f);
    }

    @Override
    public Identifier getTexture(CopperBulbEntity entity) {
        return Identifier.of(MystbornHorizons.MOD_ID, "textures/entity/copperbulb/copperbulb.png");
    }

    @Override
    public void render(CopperBulbEntity livingEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i) {
        if(livingEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
