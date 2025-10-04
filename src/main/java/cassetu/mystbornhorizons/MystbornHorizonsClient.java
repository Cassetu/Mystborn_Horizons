package cassetu.mystbornhorizons;

import cassetu.mystbornhorizons.block.ModBlocks;
import cassetu.mystbornhorizons.block.entity.ModBlockEntities;
import cassetu.mystbornhorizons.block.entity.renderer.BasaltSpawnerBlockEntityRenderer;
import cassetu.mystbornhorizons.client.DirtOverlayRenderer;
import cassetu.mystbornhorizons.entity.ModEntities;
import cassetu.mystbornhorizons.entity.client.*;
import cassetu.mystbornhorizons.mixin.ShaderAccessor;
import cassetu.mystbornhorizons.network.CurseShaderPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import cassetu.mystbornhorizons.network.ClientPacketHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Identifier;

public class MystbornHorizonsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            DirtOverlayRenderer.renderOverlay(context);
        });
        BlockEntityRendererFactories.register(ModBlockEntities.BASALT_SPAWNER, BasaltSpawnerBlockEntityRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BASALT_SPAWNER, RenderLayer.getCutoutMipped());

        ClientPlayNetworking.registerGlobalReceiver(
                CurseShaderPacket.ApplyShaderPayload.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        MinecraftClient mc = MinecraftClient.getInstance();
                        if (mc.gameRenderer != null) {
                            try {
                                String shaderName = payload.shaderName();
                                Identifier shaderId;

                                if (shaderName.contains(":")) {
                                    Identifier parsedId = Identifier.of(shaderName);
                                    shaderId = Identifier.of(parsedId.getNamespace(), "shaders/post/" + parsedId.getPath() + ".json");
                                } else {
                                    shaderId = Identifier.of("minecraft", "shaders/post/" + shaderName + ".json");
                                }

                                System.out.println("Attempting to load shader: " + shaderId);
                                System.out.println("Full path: " + shaderId.toString());

                                ((ShaderAccessor) mc.gameRenderer).invokeLoadPostProcessor(shaderId);
                            } catch (Exception e) {
                                System.err.println("Failed to load shader: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    });
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(
                CurseShaderPacket.RemoveShaderPayload.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        MinecraftClient mc = MinecraftClient.getInstance();
                        if (mc.gameRenderer != null) {
                            PostEffectProcessor processor = ((ShaderAccessor) mc.gameRenderer).getPostProcessor();
                            if (processor != null) {
                                processor.close();
                                ((ShaderAccessor) mc.gameRenderer).setPostProcessor(null);
                            }
                        }
                    });
                }
        );
        EntityModelLayerRegistry.registerModelLayer(MantisModel.MANTIS, MantisModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.MANTIS, MantisRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(CopperBulbModel.COPPERBULB, CopperBulbModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.COPPERBULB, CopperBulbRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(IceSpiderModel.ICESPIDER, IceSpiderModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.ICESPIDER, IceSpiderRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(BasaltHowlerModel.BASALTHOWLER, BasaltHowlerModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.BASALTHOWLER, BasaltHowlerRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(HavenicaModel.HAVENICA, HavenicaModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.HAVENICA, HavenicaRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(TomahawkProjectileModel.TOMAHAWK, TomahawkProjectileModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.TOMAHAWK, TomahawkProjectileRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(HavenCoreModel.HAVEN_CORE, HavenCoreModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.HAVEN_CORE, HavenCoreRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.HONEY_BERRY_BUSH, RenderLayer.getCutout());
        ClientPacketHandler.registerClientPackets();

    }

    private void applyShader(String shaderName) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.gameRenderer != null) {
            Identifier shaderId = Identifier.of("mystbornhorizons", shaderName);
            ((cassetu.mystbornhorizons.mixin.ShaderAccessor) client.gameRenderer)
                    .invokeLoadPostProcessor(shaderId);
        }
    }

    private void removeShader() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.gameRenderer != null) {
            ((cassetu.mystbornhorizons.mixin.ShaderAccessor) client.gameRenderer)
                    .setPostProcessor(null);
        }
    }
}