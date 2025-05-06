package cassetu.mystbornhorizons.entity.client;

import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.entity.custom.IceSpiderEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class IceSpiderModel<T extends IceSpiderEntity> extends SinglePartEntityModel<T> {
    public static final EntityModelLayer ICESPIDER = new EntityModelLayer(Identifier.of(MystbornHorizons.MOD_ID, "icespider"), "main");
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart abdomen;
    private final ModelPart legs;
    private final ModelPart left;
    private final ModelPart right;
    private final ModelPart shards;
    public IceSpiderModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.head = this.root.getChild("head");
        this.abdomen = this.root.getChild("abdomen");
        this.legs = this.root.getChild("legs");
        this.left = this.legs.getChild("left");
        this.right = this.legs.getChild("right");
        this.shards = this.root.getChild("shards");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, -2.0F));

        ModelPartData body = root.addChild("body", ModelPartBuilder.create().uv(0, 15).cuboid(-5.0F, -6.0F, -1.0F, 6.0F, 6.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData head = root.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(24, 35).cuboid(-5.0F, -1.0F, -14.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-6.0F, 0.0F, 8.0F, 0.0F, -0.4363F, 0.0F));

        ModelPartData cube_r2 = head.addChild("cube_r2", ModelPartBuilder.create().uv(34, 12).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -6.0F, 0.0F, 0.4363F, 0.0F));

        ModelPartData cube_r3 = head.addChild("cube_r3", ModelPartBuilder.create().uv(32, 31).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -5.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r4 = head.addChild("cube_r4", ModelPartBuilder.create().uv(24, 31).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 0.0F, -5.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r5 = head.addChild("cube_r5", ModelPartBuilder.create().uv(26, 15).cuboid(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, -1.0F, -4.0F, 0.0436F, 0.0F, 0.0F));

        ModelPartData abdomen = root.addChild("abdomen", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r6 = abdomen.addChild("cube_r6", ModelPartBuilder.create().uv(0, 0).cuboid(-7.0F, -6.0F, -1.0F, 8.0F, 6.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, -1.0F, 7.0F, 0.1309F, 0.0F, 0.0F));

        ModelPartData legs = root.addChild("legs", ModelPartBuilder.create(), ModelTransform.pivot(-3.0F, 0.0F, 0.0F));

        ModelPartData left = legs.addChild("left", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r7 = left.addChild("cube_r7", ModelPartBuilder.create().uv(8, 28).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, 1.0F, 5.0F, 0.0F, 0.2618F, -0.3491F));

        ModelPartData cube_r8 = left.addChild("cube_r8", ModelPartBuilder.create().uv(16, 28).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, 1.0F, -1.0F, 0.0F, -0.2618F, -0.3491F));

        ModelPartData right = legs.addChild("right", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r9 = right.addChild("cube_r9", ModelPartBuilder.create().uv(26, 23).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 1.0F, -1.0F, 0.0F, -0.2618F, 0.3491F));

        ModelPartData cube_r10 = right.addChild("cube_r10", ModelPartBuilder.create().uv(0, 28).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, 1.0F, 5.0F, 0.0F, 0.2618F, 0.3491F));

        ModelPartData shards = root.addChild("shards", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r11 = shards.addChild("cube_r11", ModelPartBuilder.create().uv(34, 23).cuboid(1.0F, -4.0F, -1.0F, 0.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, -5.0F, 1.0F, -0.6545F, 0.0F, 0.0F));

        ModelPartData cube_r12 = shards.addChild("cube_r12", ModelPartBuilder.create().uv(34, 6).cuboid(1.0F, -4.0F, -1.0F, 0.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, -5.0F, 3.0F, -0.7418F, 0.0F, 0.0F));

        ModelPartData cube_r13 = shards.addChild("cube_r13", ModelPartBuilder.create().uv(34, 0).cuboid(1.0F, -4.0F, -1.0F, 0.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, -5.0F, 4.0F, -0.7418F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
    @Override
    public void setAngles(IceSpiderEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.setHeadAngles(netHeadYaw, headPitch);

        this.animateMovement(IceSpiderAnimations.WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.updateAnimation(entity.idleAnimationState, IceSpiderAnimations.IDLE, ageInTicks, 1f);
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
