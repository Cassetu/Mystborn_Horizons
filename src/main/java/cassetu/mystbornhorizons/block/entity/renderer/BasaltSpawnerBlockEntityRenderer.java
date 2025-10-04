package cassetu.mystbornhorizons.block.entity.renderer;

import cassetu.mystbornhorizons.block.custom.BasaltSpawnerBlock;
import cassetu.mystbornhorizons.block.entity.BasaltSpawnerBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public class BasaltSpawnerBlockEntityRenderer implements BlockEntityRenderer<BasaltSpawnerBlockEntity> {
    private static final Identifier INNER_TEXTURE = Identifier.of("mystbornhorizons", "textures/entity/basalt_spawner_inner.png");
    private static final float CAGE_SIZE = 0.45f;

    public BasaltSpawnerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(BasaltSpawnerBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        boolean isActive = entity.getCachedState().get(BasaltSpawnerBlock.ACTIVE);
        if (!isActive) return;

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);

        long time = entity.getWorld().getTime();
        float rotation = (time + tickDelta) * 2.0f;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(INNER_TEXTURE));

        renderCage(matrices, vertexConsumer, light, overlay);

        matrices.pop();
    }

    private void renderCage(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float halfSize = CAGE_SIZE / 2.0f;

        float u0 = 0.0f;
        float u1 = 1.0f;
        float v0 = 0.0f;
        float v1 = 1.0f;

        renderQuad(matrix, vertexConsumer, light, overlay,
                -halfSize, -halfSize, -halfSize,
                halfSize, -halfSize, -halfSize,
                halfSize, halfSize, -halfSize,
                -halfSize, halfSize, -halfSize,
                u0, v0, u1, v1, 0, 0, -1);

        renderQuad(matrix, vertexConsumer, light, overlay,
                -halfSize, -halfSize, halfSize,
                -halfSize, halfSize, halfSize,
                halfSize, halfSize, halfSize,
                halfSize, -halfSize, halfSize,
                u0, v0, u1, v1, 0, 0, 1);

        renderQuad(matrix, vertexConsumer, light, overlay,
                -halfSize, -halfSize, -halfSize,
                -halfSize, halfSize, -halfSize,
                -halfSize, halfSize, halfSize,
                -halfSize, -halfSize, halfSize,
                u0, v0, u1, v1, -1, 0, 0);

        renderQuad(matrix, vertexConsumer, light, overlay,
                halfSize, -halfSize, -halfSize,
                halfSize, -halfSize, halfSize,
                halfSize, halfSize, halfSize,
                halfSize, halfSize, -halfSize,
                u0, v0, u1, v1, 1, 0, 0);

        renderQuad(matrix, vertexConsumer, light, overlay,
                -halfSize, -halfSize, -halfSize,
                -halfSize, -halfSize, halfSize,
                halfSize, -halfSize, halfSize,
                halfSize, -halfSize, -halfSize,
                u0, v0, u1, v1, 0, -1, 0);

        renderQuad(matrix, vertexConsumer, light, overlay,
                -halfSize, halfSize, -halfSize,
                halfSize, halfSize, -halfSize,
                halfSize, halfSize, halfSize,
                -halfSize, halfSize, halfSize,
                u0, v0, u1, v1, 0, 1, 0);
    }

    private void renderQuad(Matrix4f matrix, VertexConsumer vertexConsumer, int light, int overlay,
                            float x1, float y1, float z1,
                            float x2, float y2, float z2,
                            float x3, float y3, float z3,
                            float x4, float y4, float z4,
                            float u0, float v0, float u1, float v1,
                            float nx, float ny, float nz) {
        vertexConsumer.vertex(matrix, x1, y1, z1).color(255, 255, 255, 255).texture(u0, v1).overlay(overlay).light(light).normal(nx, ny, nz);
        vertexConsumer.vertex(matrix, x2, y2, z2).color(255, 255, 255, 255).texture(u1, v1).overlay(overlay).light(light).normal(nx, ny, nz);
        vertexConsumer.vertex(matrix, x3, y3, z3).color(255, 255, 255, 255).texture(u1, v0).overlay(overlay).light(light).normal(nx, ny, nz);
        vertexConsumer.vertex(matrix, x4, y4, z4).color(255, 255, 255, 255).texture(u0, v0).overlay(overlay).light(light).normal(nx, ny, nz);
    }
}