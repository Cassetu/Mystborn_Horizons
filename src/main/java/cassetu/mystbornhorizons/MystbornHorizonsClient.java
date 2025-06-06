package cassetu.mystbornhorizons;

import cassetu.mystbornhorizons.block.ModBlocks;
import cassetu.mystbornhorizons.entity.ModEntities;
import cassetu.mystbornhorizons.entity.client.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;

public class MystbornHorizonsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(MantisModel.MANTIS, MantisModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.MANTIS, MantisRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(CopperBulbModel.COPPERBULB, CopperBulbModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.COPPERBULB, CopperBulbRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(IceSpiderModel.ICESPIDER, IceSpiderModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.ICESPIDER, IceSpiderRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(BasaltHowlerModel.BASALTHOWLER, BasaltHowlerModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.BASALTHOWLER, BasaltHowlerRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.HONEY_BERRY_BUSH, RenderLayer.getCutout());

    }
}
