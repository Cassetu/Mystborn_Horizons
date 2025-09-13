package cassetu.mystbornhorizons.entity.client;


import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.entity.custom.HavenCoreEntity;
import cassetu.mystbornhorizons.entity.custom.HavenicaEntity;
import cassetu.mystbornhorizons.entity.custom.MantisEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class HavenCoreModel<T extends HavenCoreEntity> extends SinglePartEntityModel<T> {
    public static final EntityModelLayer HAVEN_CORE = new EntityModelLayer(Identifier.of(MystbornHorizons.MOD_ID, "haven_core"), "main");
    private final ModelPart root;
    private final ModelPart orb;
    private final ModelPart head;
    private final ModelPart orb3;
    private final ModelPart orb4;
    public HavenCoreModel(ModelPart root) {
        this.root = root.getChild("root");
        this.orb = this.root.getChild("orb");
        this.head = this.root.getChild("head");
        this.orb3 = this.root.getChild("orb3");
        this.orb4 = this.root.getChild("orb4");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData orb = root.addChild("orb", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -24.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData head = root.addChild("head", ModelPartBuilder.create().uv(16, 0).cuboid(-1.0F, -25.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData orb3 = root.addChild("orb3", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = orb3.addChild("cube_r1", ModelPartBuilder.create().uv(0, 8).cuboid(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, -20.0F, -1.0F, -0.6981F, -0.6981F, 0.6981F));

        ModelPartData orb4 = root.addChild("orb4", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r2 = orb4.addChild("cube_r2", ModelPartBuilder.create().uv(0, 16).cuboid(-6.0F, -4.0F, 2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -19.0F, -1.0F, 0.0F, -0.6981F, 0.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }
    @Override
    public void setAngles(HavenCoreEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.setHeadAngles(netHeadYaw, headPitch);

        this.updateAnimation(entity.idleAnimationState, HavenCoreAnimations.HAVENCORE_ROTATE, ageInTicks, 1f);
    }

    private void setHeadAngles(float headYaw, float headPitch) {
        headYaw = MathHelper.clamp(headYaw, -30.0F, 30.0F);
        headPitch = MathHelper.clamp(headPitch, -25.0F, 45.0F);

        this.head.yaw = headYaw * 0.017453292F;
        this.head.pitch = headPitch * 0.017453292F;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        root.render(matrices, vertexConsumer, light, overlay, color);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }
}
