package cassetu.mystbornhorizons.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NetherAccessState extends PersistentState {
    private final Set<UUID> playersWithAccess = new HashSet<>();

    public NetherAccessState() {
    }

    public static NetherAccessState getOrCreate(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(
                new Type<>(NetherAccessState::new, NetherAccessState::fromNbt, null),
                "nether_access"
        );
    }

    public boolean hasNetherAccess(UUID playerUuid) {
        return playersWithAccess.contains(playerUuid);
    }

    public void grantNetherAccess(UUID playerUuid, MinecraftServer server) {
        playersWithAccess.add(playerUuid);
        markDirty();
    }

    public void revokeNetherAccess(UUID playerUuid, MinecraftServer server) {
        playersWithAccess.remove(playerUuid);
        markDirty();
    }

    public void clearAll(MinecraftServer server) {
        playersWithAccess.clear();
        markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList playerList = new NbtList();
        for (UUID uuid : playersWithAccess) {
            NbtCompound playerNbt = new NbtCompound();
            playerNbt.putUuid("uuid", uuid);
            playerList.add(playerNbt);
        }
        nbt.put("players", playerList);
        return nbt;
    }

    public static NetherAccessState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NetherAccessState state = new NetherAccessState();
        NbtList playerList = nbt.getList("players", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < playerList.size(); i++) {
            NbtCompound playerNbt = playerList.getCompound(i);
            state.playersWithAccess.add(playerNbt.getUuid("uuid"));
        }
        return state;
    }
}