package cassetu.mystbornhorizons.client;

import cassetu.mystbornhorizons.effect.ModEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class DirtOverlayRenderer {
    private static final Identifier DIRT_OVERLAY_TEXTURE = Identifier.of("mystbornhorizons", "textures/misc/dirt_overlay.png");
    private static final float SCROLL_SPEED = 60f;

    public static void renderOverlay(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null && client.player.hasStatusEffect(ModEffects.DIRT_OVERLAY_EFFECT)) {
            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();
            int textureSize = 256;

            long currentTime = System.currentTimeMillis();
            float scrollOffset = (currentTime * SCROLL_SPEED / 1000f) % textureSize;


            int tilesX = (int) Math.ceil((double) screenWidth / textureSize) + 1;
            int tilesY = (int) Math.ceil((double) screenHeight / textureSize);

            for (int y = 0; y < tilesY; y++) {
                for (int x = 0; x < tilesX; x++) {
                    int xPos = (int) (x * textureSize - scrollOffset);
                    int yPos = y * textureSize;

                    context.drawTexture(DIRT_OVERLAY_TEXTURE,
                            xPos, yPos,
                            0, 0,
                            textureSize, textureSize,
                            textureSize, textureSize);
                }
            }
        }
    }
}