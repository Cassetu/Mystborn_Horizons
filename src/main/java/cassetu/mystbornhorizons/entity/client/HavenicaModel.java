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
    private final ModelPart cape;
    private final ModelPart part4;
    private final ModelPart part3;
    private final ModelPart part2;
    private final ModelPart part1;
    private final ModelPart head;
    private final ModelPart skull;
    private final ModelPart horns;
    private final ModelPart horn1;
    private final ModelPart horn2;
    private final ModelPart arms;
    private final ModelPart left;
    private final ModelPart right;
    private final ModelPart staff;
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
        this.cape = this.body.getChild("cape");
        this.part4 = this.cape.getChild("part4");
        this.part3 = this.cape.getChild("part3");
        this.part2 = this.cape.getChild("part2");
        this.part1 = this.cape.getChild("part1");
        this.head = this.root.getChild("head");
        this.skull = this.head.getChild("skull");
        this.horns = this.head.getChild("horns");
        this.horn1 = this.horns.getChild("horn1");
        this.horn2 = this.horns.getChild("horn2");
        this.arms = this.root.getChild("arms");
        this.left = this.arms.getChild("left");
        this.right = this.arms.getChild("right");
        this.staff = this.right.getChild("staff");
        this.legs = this.root.getChild("legs");
        this.left2 = this.legs.getChild("left2");
        this.right2 = this.legs.getChild("right2");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, 0.0F, -2.0F));

        ModelPartData roots = root.addChild("roots", ModelPartBuilder.create(), ModelTransform.pivot(-3.0F, -3.0F, -1.0F));

        ModelPartData root1 = roots.addChild("root1", ModelPartBuilder.create().uv(40, 16).cuboid(-1.0F, 25.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(32, 59).cuboid(0.0F, 22.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 60).cuboid(0.0F, 19.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, 0.0F, 4.0F, 0.0F, 2.1817F, 0.0F));

        ModelPartData cube_r1 = root1.addChild("cube_r1", ModelPartBuilder.create().uv(54, 46).cuboid(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 26.0F, 1.0F, 0.0F, 0.0F, -0.3927F));

        ModelPartData cube_r2 = root1.addChild("cube_r2", ModelPartBuilder.create().uv(40, 59).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 23.0F, 1.0F, 0.0F, 0.0F, -0.1309F));

        ModelPartData cube_r3 = root1.addChild("cube_r3", ModelPartBuilder.create().uv(22, 33).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 19.0F, 1.0F, 0.1309F, 0.0F, -0.0873F));

        ModelPartData cube_r4 = root1.addChild("cube_r4", ModelPartBuilder.create().uv(8, 60).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 18.0F, 1.0F, 0.0F, 0.0F, -0.5236F));

        ModelPartData root3 = roots.addChild("root3", ModelPartBuilder.create().uv(0, 64).cuboid(0.0F, 25.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(8, 64).cuboid(0.0F, 22.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(64, 10).cuboid(0.0F, 19.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 0.0F, 8.0F, 0.0F, 2.1817F, 0.0F));

        ModelPartData cube_r5 = root3.addChild("cube_r5", ModelPartBuilder.create().uv(56, 0).cuboid(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 26.0F, 1.0F, 0.0F, 0.0F, -0.3927F));

        ModelPartData cube_r6 = root3.addChild("cube_r6", ModelPartBuilder.create().uv(24, 64).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 23.0F, 1.0F, 0.0F, 0.0F, -0.1309F));

        ModelPartData cube_r7 = root3.addChild("cube_r7", ModelPartBuilder.create().uv(16, 46).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 19.0F, 1.0F, 0.1309F, 0.0F, -0.0873F));

        ModelPartData cube_r8 = root3.addChild("cube_r8", ModelPartBuilder.create().uv(16, 64).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 18.0F, 2.0F, 0.0F, 0.0F, -0.5236F));

        ModelPartData root4 = roots.addChild("root4", ModelPartBuilder.create().uv(64, 46).cuboid(0.0F, 25.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(64, 50).cuboid(0.0F, 22.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(64, 60).cuboid(0.0F, 19.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 0.0F, 8.0F, -2.6791F, 1.4733F, -2.6772F));

        ModelPartData cube_r9 = root4.addChild("cube_r9", ModelPartBuilder.create().uv(56, 5).cuboid(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 26.0F, 1.0F, 0.0F, 0.0F, -0.3927F));

        ModelPartData cube_r10 = root4.addChild("cube_r10", ModelPartBuilder.create().uv(66, 0).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 23.0F, 1.0F, 0.0F, 0.0F, -0.1309F));

        ModelPartData cube_r11 = root4.addChild("cube_r11", ModelPartBuilder.create().uv(16, 48).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 19.0F, 1.0F, 0.1309F, 0.0F, -0.0873F));

        ModelPartData cube_r12 = root4.addChild("cube_r12", ModelPartBuilder.create().uv(64, 64).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 18.0F, 1.0F, 0.0F, -0.9163F, -0.5236F));

        ModelPartData root2 = roots.addChild("root2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r13 = root2.addChild("cube_r13", ModelPartBuilder.create().uv(54, 51).cuboid(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, 26.0F, 2.0F, 0.2856F, -0.5973F, -0.4812F));

        ModelPartData cube_r14 = root2.addChild("cube_r14", ModelPartBuilder.create().uv(48, 62).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 23.0F, 2.0F, 0.0998F, -0.648F, -0.1644F));

        ModelPartData cube_r15 = root2.addChild("cube_r15", ModelPartBuilder.create().uv(16, 44).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, 19.0F, 2.0F, 0.1977F, -0.6516F, -0.1098F));

        ModelPartData cube_r16 = root2.addChild("cube_r16", ModelPartBuilder.create().uv(56, 62).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, 18.0F, 2.0F, 0.3663F, -0.5553F, -0.6291F));

        ModelPartData cube_r17 = root2.addChild("cube_r17", ModelPartBuilder.create().uv(62, 56).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(32, 63).cuboid(-1.0F, 1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, 21.0F, 2.0F, 0.0F, -0.6545F, 0.0F));

        ModelPartData cube_r18 = root2.addChild("cube_r18", ModelPartBuilder.create().uv(40, 63).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 27.0F, 2.0F, 0.0F, -0.6545F, 0.0F));

        ModelPartData body = root.addChild("body", ModelPartBuilder.create().uv(28, 20).cuboid(-7.0F, 0.0F, 1.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cape = body.addChild("cape", ModelPartBuilder.create().uv(22, 36).cuboid(-8.0F, 0.0F, 5.0F, 10.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData part4 = cape.addChild("part4", ModelPartBuilder.create().uv(52, 22).cuboid(-7.0F, 8.0F, -1.0F, 8.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, 6.0F));

        ModelPartData part3 = cape.addChild("part3", ModelPartBuilder.create().uv(52, 27).cuboid(-7.0F, 6.0F, -1.0F, 8.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, 6.0F));

        ModelPartData part2 = cape.addChild("part2", ModelPartBuilder.create().uv(52, 31).cuboid(-7.0F, 4.0F, -1.0F, 8.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, 6.0F));

        ModelPartData part1 = cape.addChild("part1", ModelPartBuilder.create().uv(52, 16).cuboid(-7.0F, -3.0F, -1.0F, 8.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 5.0F, 6.0F));

        ModelPartData head = root.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 4.0F));

        ModelPartData skull = head.addChild("skull", ModelPartBuilder.create().uv(0, 33).cuboid(-7.0F, -8.0F, 4.0F, 8.0F, 8.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 20).cuboid(-7.0F, -7.0F, -2.0F, 8.0F, 7.0F, 6.0F, new Dilation(0.0F))
                .uv(48, 36).cuboid(-7.0F, -6.0F, -4.0F, 8.0F, 5.0F, 2.0F, new Dilation(0.0F))
                .uv(54, 43).cuboid(-6.0F, -1.0F, -4.0F, 6.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -4.0F));

        ModelPartData horns = head.addChild("horns", ModelPartBuilder.create(), ModelTransform.of(1.0F, 1.0F, -11.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData horn1 = horns.addChild("horn1", ModelPartBuilder.create().uv(56, 66).cuboid(-2.0F, -8.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, -1.0F, 7.0F));

        ModelPartData cube_r19 = horn1.addChild("cube_r19", ModelPartBuilder.create().uv(16, 59).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, -8.0F, 2.0F, -0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r20 = horn1.addChild("cube_r20", ModelPartBuilder.create().uv(56, 10).cuboid(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, -7.0F, 0.0F, -1.0908F, 0.0F, 0.0F));

        ModelPartData horn2 = horns.addChild("horn2", ModelPartBuilder.create().uv(32, 67).cuboid(-6.0F, -8.0F, 1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, -1.0F, 6.0F, 0.0F, -0.5672F, 0.0F));

        ModelPartData cube_r21 = horn2.addChild("cube_r21", ModelPartBuilder.create().uv(24, 59).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, -8.0F, 4.0F, -0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r22 = horn2.addChild("cube_r22", ModelPartBuilder.create().uv(54, 56).cuboid(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, -7.0F, 2.0F, -1.0908F, 0.0F, 0.0F));

        ModelPartData arms = root.addChild("arms", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData left = arms.addChild("left", ModelPartBuilder.create().uv(22, 43).cuboid(1.0F, 0.0F, 1.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData right = arms.addChild("right", ModelPartBuilder.create().uv(40, 0).cuboid(-11.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.0F, 3.0F));

        ModelPartData staff = right.addChild("staff", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -2.0F, -8.0F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, 11.0F, -1.0F));

        ModelPartData cube_r23 = staff.addChild("cube_r23", ModelPartBuilder.create().uv(48, 66).cuboid(-3.0F, -3.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(66, 4).cuboid(-3.0F, -3.0F, -12.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 2.0F, 6.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData legs = root.addChild("legs", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData left2 = legs.addChild("left2", ModelPartBuilder.create().uv(38, 43).cuboid(-3.0F, 12.0F, 1.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData right2 = legs.addChild("right2", ModelPartBuilder.create().uv(0, 44).cuboid(-7.0F, 12.0F, 1.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }
    @Override
    public void setAngles(HavenicaEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.setHeadAngles(netHeadYaw, headPitch);
        if (entity.isDead() || entity.deathAnimationState.isRunning()) {
            this.updateAnimation(entity.deathAnimationState, HavenicaAnimations.DEATH, ageInTicks, 1f);
        } else if (entity.castSpellAnimationState.isRunning()) {
            this.updateAnimation(entity.castSpellAnimationState, HavenicaAnimations.CAST_SPELL, ageInTicks, 1f);
        } else {
            this.updateAnimation(entity.idleAnimationState, HavenicaAnimations.IDLE, ageInTicks, 1f);
        }
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