package cassetu.mystbornhorizons.client;

import cassetu.mystbornhorizons.effect.ModEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DirtOverlayRenderer {
    public static final Identifier DIRT_OVERLAY_TEXTURE = Identifier.of("mystbornhorizons", "textures/misc/dirt_overlay.png");

    public static void renderOverlay(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.player.hasStatusEffect(ModEffects.DIRT_OVERLAY_EFFECT)) {
            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.8f);
            RenderSystem.setShaderTexture(0, DIRT_OVERLAY_TEXTURE);

            context.drawTexture(DIRT_OVERLAY_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();
        }
    }
}