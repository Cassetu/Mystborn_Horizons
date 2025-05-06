package cassetu.mystbornhorizons.entity.client;


import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.entity.custom.CopperBulbEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class CopperBulbModel<T extends CopperBulbEntity> extends SinglePartEntityModel<T> {
    public static final EntityModelLayer COPPERBULB = new EntityModelLayer(Identifier.of(MystbornHorizons.MOD_ID, "copperbulb"), "main");
    private final ModelPart head;
    private final ModelPart face;
    private final ModelPart root;
    private final ModelPart base;
    private final ModelPart mainWire;
    private final ModelPart wires1;
    private final ModelPart wires2;
    public CopperBulbModel(ModelPart root) {
        this.head = root.getChild("head");
        this.face = this.head.getChild("face");
        this.root = this.head.getChild("root");
        this.base = this.root.getChild("base");
        this.mainWire = this.root.getChild("mainWire");
        this.wires1 = this.mainWire.getChild("wires1");
        this.wires2 = this.mainWire.getChild("wires2");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, new Dilation(0.0F))
                .uv(36, 0).cuboid(-3.5F, -6.0F, 0.0F, 7.0F, 6.0F, 0.0F, new Dilation(0.0F))
                .uv(24, 17).cuboid(0.0F, -6.0F, -3.5F, 0.0F, 6.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 12.0F, 1.0F));

        ModelPartData face = head.addChild("face", ModelPartBuilder.create().uv(36, 6).cuboid(-3.5F, -2.0F, 0.0F, 7.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -4.0F, -5.0F));

        ModelPartData root = head.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 9.0F, -1.0F));

        ModelPartData base = root.addChild("base", ModelPartBuilder.create().uv(32, 24).cuboid(-4.0F, -3.0F, -4.0F, 8.0F, 3.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, 1.0F));

        ModelPartData cube_r1 = base.addChild("cube_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData mainWire = root.addChild("mainWire", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 9.5F, 1.0F));

        ModelPartData wires1 = mainWire.addChild("wires1", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r2 = wires1.addChild("cube_r2", ModelPartBuilder.create().uv(0, 24).cuboid(-4.0F, -3.5F, -4.0F, 8.0F, 7.0F, 8.0F, new Dilation(0.3F)), ModelTransform.of(0.0F, -10.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData wires2 = mainWire.addChild("wires2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -4.0F, 0.0F));

        ModelPartData cube_r3 = wires2.addChild("cube_r3", ModelPartBuilder.create().uv(0, 46).cuboid(-4.0F, -3.5F, -4.0F, 8.0F, 7.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
    @Override
    public void setAngles(CopperBulbEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.setHeadAngles(netHeadYaw, headPitch);

        this.animateMovement(CopperBulbAnimations.WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.updateAnimation(entity.idleAnimationState, CopperBulbAnimations.IDLE, ageInTicks, 1f);
    }
    private void setHeadAngles(float headYaw, float headPitch) {
        headYaw = MathHelper.clamp(headYaw, -30.0F, 30.0F);
        headPitch = MathHelper.clamp(headPitch, -25.0F, 45.0F);

        this.head.yaw = headYaw * 0.017453292F;
        this.head.pitch = headPitch * 0.017453292F;
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        head.render(matrices, vertexConsumer, light, overlay, color);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }



}
