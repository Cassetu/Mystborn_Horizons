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
    private static SoundEvent currentBossMusicEvent = null;

    public static void registerClientPackets() {
        ClientPlayNetworking.registerGlobalReceiver(BossMusicPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                SoundManager soundManager = context.client().getSoundManager();

                if (payload.startMusic() && payload.soundEvent() != null) {
                    System.out.println("DEBUG: Received request to start boss music: " + payload.soundEvent().getId());

                    if (currentBossMusic == null || !payload.soundEvent().equals(currentBossMusicEvent)) {
                        stopCurrentBossMusic(soundManager);

                        if (!backgroundMusicWasStopped) {
                            soundManager.stopSounds(null, SoundCategory.MUSIC);
                            backgroundMusicWasStopped = true;
                            System.out.println("DEBUG: Stopped background music for boss fight");
                        }

                        currentBossMusicEvent = payload.soundEvent();
                        currentBossMusic = createRepeatingBossMusic(payload.soundEvent());
                        soundManager.play(currentBossMusic);

                        System.out.println("DEBUG: Started looping boss music: " + payload.soundEvent().getId());
                        System.out.println("DEBUG: Music instance created successfully");
                    } else {
                        System.out.println("DEBUG: Boss music already playing, skipping");
                    }

                } else {
                    System.out.println("DEBUG: Received request to stop boss music");
                    stopCurrentBossMusic(soundManager);
                    currentBossMusicEvent = null;

                    if (backgroundMusicWasStopped) {
                        backgroundMusicWasStopped = false;
                        System.out.println("DEBUG: Allowing background music to resume");
                    }
                }
            });
        });
    }

    private static void stopCurrentBossMusic(SoundManager soundManager) {
        if (currentBossMusic != null) {
            soundManager.stop(currentBossMusic);
            System.out.println("DEBUG: Stopped boss music: " + (currentBossMusicEvent != null ? currentBossMusicEvent.getId() : "unknown"));
            currentBossMusic = null;
        }
    }

    private static PositionedSoundInstance createRepeatingBossMusic(SoundEvent soundEvent) {
        System.out.println("DEBUG: Creating repeating boss music instance for: " + soundEvent.getId());

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
        ) {
            @Override
            public boolean isRepeatable() {
                return true;
            }

            @Override
            public boolean shouldAlwaysPlay() {
                return true;
            }
        };
    }

    public static boolean isBossMusicPlaying() {
        return currentBossMusic != null;
    }

    public static SoundEvent getCurrentBossMusicEvent() {
        return currentBossMusicEvent;
    }
}