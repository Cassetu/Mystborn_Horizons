package cassetu.mystbornhorizons.sound;

import cassetu.mystbornhorizons.MystbornHorizons;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent DOOMSDAY = registerSoundEvent("doomsday");
    public static final RegistryKey<JukeboxSong> DOOMSDAY_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(MystbornHorizons.MOD_ID, "doomsday"));

    public static final SoundEvent TITAN_SANDS = registerSoundEvent("titan_sands");
    public static final RegistryKey<JukeboxSong> TITAN_SANDS_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(MystbornHorizons.MOD_ID, "titan_sands"));

    public static final SoundEvent ENDER_FURY = registerSoundEvent("ender_fury");
    public static final RegistryKey<JukeboxSong> ENDER_FURY_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(MystbornHorizons.MOD_ID, "ender_fury"));

    public static final SoundEvent ECHOES_OF_THE_ABYSS = registerSoundEvent("echoes_of_the_abyss");
    public static final RegistryKey<JukeboxSong> ECHOES_OF_THE_ABYSS_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(MystbornHorizons.MOD_ID, "echoes_of_the_abyss"));

    public static final SoundEvent WATER_HORIZONS = registerSoundEvent("water_horizons");
    public static final RegistryKey<JukeboxSong> WATER_HORIZONS_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(MystbornHorizons.MOD_ID, "water_horizons"));

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(MystbornHorizons.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        MystbornHorizons.LOGGER.info("Registering Mod Sounds for" + MystbornHorizons.MOD_ID);
    }
}
