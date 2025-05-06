package cassetu.mystbornhorizons.entity.client;

import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.entity.custom.BasaltHowlerEntity;
import cassetu.mystbornhorizons.entity.custom.IceSpiderEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class BasaltHowlerModel<T extends BasaltHowlerEntity> extends SinglePartEntityModel<T> {
    public static final EntityModelLayer BASALTHOWLER = new EntityModelLayer(Identifier.of(MystbornHorizons.MOD_ID, "basalthowler"), "main");
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart torso;
    private final ModelPart waist;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart leg;
    private final ModelPart right;
    private final ModelPart left;
    private final ModelPart arms;
    private final ModelPart left2;
    private final ModelPart right2;
    private final ModelPart pillars;

    public BasaltHowlerModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.torso = this.body.getChild("torso");
        this.waist = this.body.getChild("waist");
        this.head = this.root.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.leg = this.root.getChild("leg");
        this.right = this.leg.getChild("right");
        this.left = this.leg.getChild("left");
        this.arms = this.root.getChild("arms");
        this.left2 = this.arms.getChild("left2");
        this.right2 = this.arms.getChild("right2");
        this.pillars = this.root.getChild("pillars");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(1.0F, 21.0F, 0.0F));

        ModelPartData body = root.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(-1.0F, 0.0F, 0.0F));

        ModelPartData torso = body.addChild("torso", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -19.0F, -5.0F, 14.0F, 7.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData waist = body.addChild("waist", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = waist.addChild("cube_r1", ModelPartBuilder.create().uv(0, 17).cuboid(-11.0F, -6.0F, -3.0F, 10.0F, 6.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(7.0F, -7.0F, 1.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData head = root.addChild("head", ModelPartBuilder.create().uv(48, 0).cuboid(-2.0F, -18.0F, -11.0F, 6.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 0.0F, 0.0F));

        ModelPartData jaw = head.addChild("jaw", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r2 = jaw.addChild("cube_r2", ModelPartBuilder.create().uv(36, 23).cuboid(-2.0F, -1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(36, 21).cuboid(-7.0F, -1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, -14.0F, -5.0F, 0.0436F, 0.0F, 0.0F));

        ModelPartData cube_r3 = jaw.addChild("cube_r3", ModelPartBuilder.create().uv(36, 19).cuboid(-2.0F, -1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(36, 17).cuboid(3.0F, -1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -14.0F, -7.0F, 0.1745F, 0.0F, 0.0F));

        ModelPartData cube_r4 = jaw.addChild("cube_r4", ModelPartBuilder.create().uv(48, 9).cuboid(-7.0F, -1.0F, -3.0F, 6.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, -13.0F, -7.0F, 0.1745F, 0.0F, 0.0F));

        ModelPartData leg = root.addChild("leg", ModelPartBuilder.create(), ModelTransform.pivot(-1.0F, 0.0F, 0.0F));

        ModelPartData right = leg.addChild("right", ModelPartBuilder.create().uv(40, 34).cuboid(-4.0F, -10.0F, 0.0F, 4.0F, 13.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData left = leg.addChild("left", ModelPartBuilder.create().uv(40, 17).cuboid(2.0F, -10.0F, 0.0F, 4.0F, 13.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData arms = root.addChild("arms", ModelPartBuilder.create(), ModelTransform.pivot(-1.0F, 0.0F, 0.0F));

        ModelPartData left2 = arms.addChild("left2", ModelPartBuilder.create().uv(16, 47).cuboid(8.0F, -17.0F, 0.0F, 4.0F, 13.0F, 4.0F, new Dilation(0.0F))
                .uv(20, 31).cuboid(8.0F, -10.0F, -1.0F, 5.0F, 11.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, -3.0F, -0.1309F, 0.0F, 0.0F));

        ModelPartData right2 = arms.addChild("right2", ModelPartBuilder.create().uv(0, 47).cuboid(-10.0F, -17.0F, 0.0F, 4.0F, 13.0F, 4.0F, new Dilation(0.0F))
                .uv(0, 31).cuboid(-11.0F, -10.0F, -1.0F, 5.0F, 11.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, -3.0F, -0.1309F, 0.0F, 0.0F));

        ModelPartData pillars = root.addChild("pillars", ModelPartBuilder.create(), ModelTransform.pivot(-1.0F, 0.0F, 0.0F));

        ModelPartData cube_r5 = pillars.addChild("cube_r5", ModelPartBuilder.create().uv(56, 16).cuboid(-4.0F, -7.0F, -3.0F, 3.0F, 7.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, -18.0F, 4.0F, 1.3284F, 1.3757F, 1.0685F));

        ModelPartData cube_r6 = pillars.addChild("cube_r6", ModelPartBuilder.create().uv(44, 51).cuboid(-4.0F, -7.0F, -3.0F, 3.0F, 7.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(7.0F, -18.0F, 0.0F, 0.2126F, 0.6357F, 0.1375F));

        ModelPartData cube_r7 = pillars.addChild("cube_r7", ModelPartBuilder.create().uv(32, 51).cuboid(-4.0F, -7.0F, -3.0F, 3.0F, 7.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, -18.0F, 0.0F, 0.2229F, 0.5522F, -0.137F));

        ModelPartData cube_r8 = pillars.addChild("cube_r8", ModelPartBuilder.create().uv(56, 34).cuboid(-3.0F, -7.0F, -3.0F, 2.0F, 7.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, -18.0F, 4.0F, -0.3879F, 0.5522F, -0.137F));

        ModelPartData cube_r9 = pillars.addChild("cube_r9", ModelPartBuilder.create().uv(56, 26).cuboid(-4.0F, -5.0F, -3.0F, 3.0F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, -18.0F, 4.0F, -0.2233F, -0.2129F, 0.0479F));
        return TexturedModelData.of(modelData, 128, 128);
    }
        @Override
    public void setAngles(BasaltHowlerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);

        this.animateMovement(BasaltHowlerAnimations.WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.updateAnimation(entity.idleAnimationState, BasaltHowlerAnimations.IDLE, ageInTicks, 1f);
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
