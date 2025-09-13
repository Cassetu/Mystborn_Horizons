package cassetu.mystbornhorizons.entity.client;

import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.entity.custom.HavenicaEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class HavenicaModel<T extends HavenicaEntity> extends SinglePartEntityModel<T> {
    public static final EntityModelLayer HAVENICA = new EntityModelLayer(Identifier.of(MystbornHorizons.MOD_ID, "havenica"), "main");

    private final ModelPart root;
    private final ModelPart roots;
    private final ModelPart root1;
    private final ModelPart root3;
    private final ModelPart root4;
    private final ModelPart root2;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart left_horn;
    private final ModelPart right_horn;
    private final ModelPart arms;
    private final ModelPart left;
    private final ModelPart right;
    private final ModelPart legs;
    private final ModelPart left2;
    private final ModelPart right2;
    public HavenicaModel(ModelPart root) {
        this.root = root.getChild("root");
        this.roots = this.root.getChild("roots");
        this.root1 = this.roots.getChild("root1");
        this.root3 = this.roots.getChild("root3");
        this.root4 = this.roots.getChild("root4");
        this.root2 = this.roots.getChild("root2");
        this.body = this.root.getChild("body");
        this.head = this.root.getChild("head");
        this.left_horn = this.head.getChild("left_horn");
        this.right_horn = this.head.getChild("right_horn");
        this.arms = this.root.getChild("arms");
        this.left = this.arms.getChild("left");
        this.right = this.arms.getChild("right");
        this.legs = this.root.getChild("legs");
        this.left2 = this.legs.getChild("left2");
        this.right2 = this.legs.getChild("right2");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData roots = root.addChild("roots", ModelPartBuilder.create(), ModelTransform.pivot(-3.0F, -3.0F, -1.0F));

        ModelPartData root1 = roots.addChild("root1", ModelPartBuilder.create().uv(0, 48).cuboid(-1.0F, 25.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(48, 0).cuboid(0.0F, 22.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(8, 48).cuboid(0.0F, 19.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, 0.0F, 4.0F, 0.0F, 2.1817F, 0.0F));

        ModelPartData cube_r1 = root1.addChild("cube_r1", ModelPartBuilder.create().uv(32, 32).cuboid(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 26.0F, 1.0F, 0.0F, 0.0F, -0.3927F));

        ModelPartData cube_r2 = root1.addChild("cube_r2", ModelPartBuilder.create().uv(48, 4).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 23.0F, 1.0F, 0.0F, 0.0F, -0.1309F));

        ModelPartData cube_r3 = root1.addChild("cube_r3", ModelPartBuilder.create().uv(50, 28).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 19.0F, 1.0F, 0.1309F, 0.0F, -0.0873F));

        ModelPartData cube_r4 = root1.addChild("cube_r4", ModelPartBuilder.create().uv(48, 8).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 18.0F, 1.0F, 0.0F, 0.0F, -0.5236F));

        ModelPartData root3 = roots.addChild("root3", ModelPartBuilder.create().uv(40, 49).cuboid(0.0F, 25.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(50, 16).cuboid(0.0F, 22.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(50, 20).cuboid(0.0F, 19.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 0.0F, 8.0F, 0.0F, 2.1817F, 0.0F));

        ModelPartData cube_r5 = root3.addChild("cube_r5", ModelPartBuilder.create().uv(40, 16).cuboid(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 26.0F, 1.0F, 0.0F, 0.0F, -0.3927F));

        ModelPartData cube_r6 = root3.addChild("cube_r6", ModelPartBuilder.create().uv(50, 34).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 23.0F, 1.0F, 0.0F, 0.0F, -0.1309F));

        ModelPartData cube_r7 = root3.addChild("cube_r7", ModelPartBuilder.create().uv(24, 52).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 19.0F, 1.0F, 0.1309F, 0.0F, -0.0873F));

        ModelPartData cube_r8 = root3.addChild("cube_r8", ModelPartBuilder.create().uv(50, 24).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 18.0F, 2.0F, 0.0F, 0.0F, -0.5236F));

        ModelPartData root4 = roots.addChild("root4", ModelPartBuilder.create().uv(50, 38).cuboid(0.0F, 25.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(32, 51).cuboid(0.0F, 22.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 52).cuboid(0.0F, 19.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 0.0F, 8.0F, -2.6791F, 1.4733F, -2.6772F));

        ModelPartData cube_r9 = root4.addChild("cube_r9", ModelPartBuilder.create().uv(40, 21).cuboid(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 26.0F, 1.0F, 0.0F, 0.0F, -0.3927F));

        ModelPartData cube_r10 = root4.addChild("cube_r10", ModelPartBuilder.create().uv(16, 52).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 23.0F, 1.0F, 0.0F, 0.0F, -0.1309F));

        ModelPartData cube_r11 = root4.addChild("cube_r11", ModelPartBuilder.create().uv(28, 52).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 19.0F, 1.0F, 0.1309F, 0.0F, -0.0873F));

        ModelPartData cube_r12 = root4.addChild("cube_r12", ModelPartBuilder.create().uv(8, 52).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 18.0F, 1.0F, 0.0F, -0.9163F, -0.5236F));

        ModelPartData root2 = roots.addChild("root2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r13 = root2.addChild("cube_r13", ModelPartBuilder.create().uv(32, 37).cuboid(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, 26.0F, 2.0F, 0.2856F, -0.5973F, -0.4812F));

        ModelPartData cube_r14 = root2.addChild("cube_r14", ModelPartBuilder.create().uv(48, 12).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 23.0F, 2.0F, 0.0998F, -0.648F, -0.1644F));

        ModelPartData cube_r15 = root2.addChild("cube_r15", ModelPartBuilder.create().uv(50, 42).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, 19.0F, 2.0F, 0.1977F, -0.6516F, -0.1098F));

        ModelPartData cube_r16 = root2.addChild("cube_r16", ModelPartBuilder.create().uv(16, 48).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, 18.0F, 2.0F, 0.3663F, -0.5553F, -0.6291F));

        ModelPartData cube_r17 = root2.addChild("cube_r17", ModelPartBuilder.create().uv(24, 48).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, 21.0F, 2.0F, 0.0F, -0.6545F, 0.0F));

        ModelPartData cube_r18 = root2.addChild("cube_r18", ModelPartBuilder.create().uv(48, 44).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, 24.0F, 2.0F, 0.0F, -0.6545F, 0.0F));

        ModelPartData cube_r19 = root2.addChild("cube_r19", ModelPartBuilder.create().uv(48, 48).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 27.0F, 2.0F, 0.0F, -0.6545F, 0.0F));

        ModelPartData body = root.addChild("body", ModelPartBuilder.create().uv(0, 16).cuboid(-7.0F, 0.0F, 1.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData head = root.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-7.0F, -8.0F, -1.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(32, 47).cuboid(-4.0F, -5.0F, -2.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData left_horn = head.addChild("left_horn", ModelPartBuilder.create().uv(32, 42).cuboid(1.0F, -6.0F, 2.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r20 = left_horn.addChild("cube_r20", ModelPartBuilder.create().uv(40, 26).cuboid(-1.0F, -2.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, -3.0F, 3.0F, 0.0F, 0.0F, -0.3054F));

        ModelPartData cube_r21 = left_horn.addChild("cube_r21", ModelPartBuilder.create().uv(42, 34).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, -5.0F, 3.0F, 0.0F, 0.0F, 1.3526F));

        ModelPartData right_horn = head.addChild("right_horn", ModelPartBuilder.create().uv(40, 44).cuboid(-9.0F, -6.0F, 2.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r22 = right_horn.addChild("cube_r22", ModelPartBuilder.create().uv(42, 30).cuboid(-1.0F, -2.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-10.0F, -3.0F, 3.0F, 0.0F, 0.0F, 0.3054F));

        ModelPartData cube_r23 = right_horn.addChild("cube_r23", ModelPartBuilder.create().uv(42, 39).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-9.0F, -5.0F, 3.0F, 0.0F, 0.0F, -1.3526F));

        ModelPartData arms = root.addChild("arms", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData left = arms.addChild("left", ModelPartBuilder.create().uv(0, 32).cuboid(1.0F, 0.0F, 1.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData right = arms.addChild("right", ModelPartBuilder.create().uv(24, 16).cuboid(-11.0F, 0.0F, 1.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData legs = root.addChild("legs", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData left2 = legs.addChild("left2", ModelPartBuilder.create().uv(32, 0).cuboid(-3.0F, 12.0F, 1.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData right2 = legs.addChild("right2", ModelPartBuilder.create().uv(16, 32).cuboid(-7.0F, 12.0F, 1.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
    @Override
    public void setAngles(HavenicaEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.setHeadAngles(netHeadYaw, headPitch);

//        this.animateMovement(havenicaAnimations.RAWR, limbSwing, limbSwingAmount, 2f, 2.5f);
//        this.updateAnimation(entity.idleAnimationState, MantisAnimations.ANIM_MANTIS_IDLE, ageInTicks, 1f);
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
        return this.root;
    }
}
