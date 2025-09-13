package cassetu.mystbornhorizons.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKeys;
import java.util.Optional;

public record BossMusicPacket(boolean startMusic, SoundEvent soundEvent) implements CustomPayload {
    public static final CustomPayload.Id<BossMusicPacket> ID =
            new CustomPayload.Id<>(Identifier.of("mystbornhorizons", "boss_music"));

    public static final PacketCodec<RegistryByteBuf, BossMusicPacket> CODEC = PacketCodec.of(
            BossMusicPacket::write,
            BossMusicPacket::read
    );

    private static void write(BossMusicPacket packet, RegistryByteBuf buf) {
        buf.writeBoolean(packet.startMusic());
        if (packet.startMusic() && packet.soundEvent() != null) {
            buf.writeBoolean(true);
            PacketCodecs.registryValue(RegistryKeys.SOUND_EVENT).encode(buf, packet.soundEvent());
        } else {
            buf.writeBoolean(false);
        }
    }

    private static BossMusicPacket read(RegistryByteBuf buf) {
        boolean startMusic = buf.readBoolean();
        SoundEvent soundEvent = null;
        if (buf.readBoolean()) {
            soundEvent = PacketCodecs.registryValue(RegistryKeys.SOUND_EVENT).decode(buf);
        }
        return new BossMusicPacket(startMusic, soundEvent);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}