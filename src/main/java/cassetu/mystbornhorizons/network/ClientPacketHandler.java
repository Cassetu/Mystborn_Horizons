package cassetu.mystbornhorizons.network;

import cassetu.mystbornhorizons.MystbornHorizons;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundCategory;

public class ClientPacketHandler {
    private static SoundInstance currentBossMusic = null;
    private static boolean backgroundMusicWasStopped = false;

    public static void registerClientPackets() {
        ClientPlayNetworking.registerGlobalReceiver(BossMusicPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                SoundManager soundManager = context.client().getSoundManager();

                if (payload.startMusic() && payload.soundEvent() != null) {
                    // Stop any existing boss music
                    if (currentBossMusic != null) {
                        soundManager.stop(currentBossMusic);
                    }

                    // Stop background music if it's playing
                    if (!backgroundMusicWasStopped) {
                        soundManager.stopSounds(null, SoundCategory.MUSIC);
                        backgroundMusicWasStopped = true;
                    }

                    // Start boss music
                    currentBossMusic = PositionedSoundInstance.music(payload.soundEvent());
                    soundManager.play(currentBossMusic);

                } else {
                    // Stop boss music
                    if (currentBossMusic != null) {
                        soundManager.stop(currentBossMusic);
                        currentBossMusic = null;
                    }

                    // Allow background music to resume naturally
                    if (backgroundMusicWasStopped) {
                        backgroundMusicWasStopped = false;
                        // Note: Minecraft will naturally start playing background music again
                        // after a short delay when no music is playing
                    }
                }
            });
        });
    }
}