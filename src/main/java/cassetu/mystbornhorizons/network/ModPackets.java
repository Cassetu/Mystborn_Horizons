package cassetu.mystbornhorizons.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModPackets {

    public static void registerPackets() {
        // Register the packet type for server-to-client communication
        PayloadTypeRegistry.playS2C().register(BossMusicPacket.ID, BossMusicPacket.CODEC);
    }

    public static void sendToPlayer(BossMusicPacket packet, ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, packet);
    }
}