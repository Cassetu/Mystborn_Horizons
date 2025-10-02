package cassetu.mystbornhorizons.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CurseShaderPacket {
    public static final Identifier APPLY_SHADER_ID = Identifier.of("mystbornhorizons", "apply_shader");
    public static final Identifier REMOVE_SHADER_ID = Identifier.of("mystbornhorizons", "remove_shader");

    public record ApplyShaderPayload(String shaderName) implements CustomPayload {
        public static final CustomPayload.Id<ApplyShaderPayload> ID = new CustomPayload.Id<>(APPLY_SHADER_ID);
        public static final PacketCodec<RegistryByteBuf, ApplyShaderPayload> CODEC = PacketCodec.tuple(
                PacketCodecs.STRING, ApplyShaderPayload::shaderName,
                ApplyShaderPayload::new
        );

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record RemoveShaderPayload() implements CustomPayload {
        public static final CustomPayload.Id<RemoveShaderPayload> ID = new CustomPayload.Id<>(REMOVE_SHADER_ID);
        public static final PacketCodec<RegistryByteBuf, RemoveShaderPayload> CODEC = PacketCodec.unit(new RemoveShaderPayload());

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public static void sendApplyShader(ServerPlayerEntity player, String shaderName) {
        ServerPlayNetworking.send(player, new ApplyShaderPayload(shaderName));
    }

    public static void sendRemoveShader(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new RemoveShaderPayload());
    }
}