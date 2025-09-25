package cassetu.mystbornhorizons.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

public class HavenicaDefeatState extends PersistentState {
    private boolean havenicaDefeated = false;
    private long defeatTime = 0;

    public static HavenicaDefeatState getOrCreate(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                new Type<>(
                        HavenicaDefeatState::new,
                        HavenicaDefeatState::fromNbt,
                        null
                ),
                "havenica_defeat_state"
        );
    }

    public static HavenicaDefeatState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        HavenicaDefeatState state = new HavenicaDefeatState();
        state.havenicaDefeated = nbt.getBoolean("defeated");
        state.defeatTime = nbt.getLong("defeat_time");
        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putBoolean("defeated", havenicaDefeated);
        nbt.putLong("defeat_time", defeatTime);
        return nbt;
    }

    public void setHavenicaDefeated(ServerWorld world) {
        this.havenicaDefeated = true;
        this.defeatTime = world.getTime();
        this.markDirty();
    }

    public boolean isHavenicaDefeated() {
        return havenicaDefeated;
    }

    public long getDefeatTime() {
        return defeatTime;
    }
}