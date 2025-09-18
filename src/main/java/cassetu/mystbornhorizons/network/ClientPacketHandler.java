package cassetu.mystbornhorizons.network;

import cassetu.mystbornhorizons.MystbornHorizons;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class ClientPacketHandler {
    private static SoundInstance currentBossMusic = null;
    private static boolean backgroundMusicWasStopped = false;

    public static void registerClientPackets() {
        ClientPlayNetworking.registerGlobalReceiver(BossMusicPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                SoundManager soundManager = context.client().getSoundManager();

                if (payload.startMusic() && payload.soundEvent() != null) {
                    if (currentBossMusic != null) {
                        soundManager.stop(currentBossMusic);
                    }

                    if (!backgroundMusicWasStopped) {
                        soundManager.stopSounds(null, SoundCategory.MUSIC);
                        backgroundMusicWasStopped = true;
                    }

                    currentBossMusic = createRepeatingBossMusic(payload.soundEvent());
                    soundManager.play(currentBossMusic);

                } else {
                    if (currentBossMusic != null) {
                        soundManager.stop(currentBossMusic);
                        currentBossMusic = null;
                    }

                    if (backgroundMusicWasStopped) {
                        backgroundMusicWasStopped = false;
                    }
                }
            });
        });
    }

    private static SoundInstance createRepeatingBossMusic(SoundEvent soundEvent) {
        return new PositionedSoundInstance(
                soundEvent.getId(),
                SoundCategory.MUSIC,
                1.0f,
                1.0f,
                SoundInstance.createRandom(),
                true,
                0,
                SoundInstance.AttenuationType.NONE,
                0.0,
                0.0,
                0.0,
                true
        );
    }
}