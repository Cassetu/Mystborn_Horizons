package cassetu.mystbornhorizons.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;

public class ClientPacketHandler {
    private static SoundInstance currentBossMusic = null;

    public static void registerClientPackets() {
        ClientPlayNetworking.registerGlobalReceiver(BossMusicPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (payload.startMusic() && payload.soundEvent() != null) {
                    if (currentBossMusic != null) {
                        context.client().getSoundManager().stop(currentBossMusic);
                    }

                    currentBossMusic = PositionedSoundInstance.music(payload.soundEvent());
                    context.client().getSoundManager().play(currentBossMusic);
                } else {
                    if (currentBossMusic != null) {
                        context.client().getSoundManager().stop(currentBossMusic);
                        currentBossMusic = null;
                    }
                }
            });
        });
    }
}