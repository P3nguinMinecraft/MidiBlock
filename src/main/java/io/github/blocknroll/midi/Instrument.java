package io.github.blocknroll.midi;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import java.util.HashMap;
import java.util.Map;

public enum Instrument {
    HARP(       NoteBlockInstrument.HARP,        Blocks.AIR.defaultBlockState(),                       42, new int[]{}),
    BASS(       NoteBlockInstrument.BASS,        Blocks.OAK_PLANKS.defaultBlockState(),                18, new int[]{32,33,34,35,36,37,38,39}),
    SNARE(      NoteBlockInstrument.SNARE,       Blocks.SAND.defaultBlockState(),                      -1, new int[]{}),
    HAT(        NoteBlockInstrument.HAT,         Blocks.GLASS.defaultBlockState(),                     -1, new int[]{}),
    BASEDRUM(   NoteBlockInstrument.BASEDRUM,    Blocks.STONE.defaultBlockState(),                     -1, new int[]{}),
    BELL(       NoteBlockInstrument.BELL,        Blocks.GOLD_BLOCK.defaultBlockState(),                66, new int[]{9,14}),
    FLUTE(      NoteBlockInstrument.FLUTE,       Blocks.CLAY.defaultBlockState(),                      54, new int[]{73,74,75,76,77,78,79,72}),
    CHIME(      NoteBlockInstrument.CHIME,       Blocks.PACKED_ICE.defaultBlockState(),                66, new int[]{8}),
    GUITAR(     NoteBlockInstrument.GUITAR,      Blocks.WHITE_WOOL.defaultBlockState(),                30, new int[]{24,25,26,27,28,29,30,31}),
    XYLOPHONE(  NoteBlockInstrument.XYLOPHONE,   Blocks.BONE_BLOCK.defaultBlockState(),                66, new int[]{13}),
    IRON_XYLOPHONE(NoteBlockInstrument.IRON_XYLOPHONE, Blocks.IRON_BLOCK.defaultBlockState(),          42, new int[]{11,12}),
    COW_BELL(   NoteBlockInstrument.COW_BELL,    Blocks.SOUL_SAND.defaultBlockState(),                 54, new int[]{113,114,115}),
    DIDGERIDOO( NoteBlockInstrument.DIDGERIDOO,  Blocks.PUMPKIN.defaultBlockState(),                   18, new int[]{109,110,111,112}),
    BIT(        NoteBlockInstrument.BIT,         Blocks.EMERALD_BLOCK.defaultBlockState(),             42, new int[]{80,81,82,83,84,85,86,87}),
    BANJO(      NoteBlockInstrument.BANJO,       Blocks.HAY_BLOCK.defaultBlockState(),                 42, new int[]{105}),
    PLING(      NoteBlockInstrument.PLING,       Blocks.GLOWSTONE.defaultBlockState(),                 42, new int[]{0,1,2,3,4,5,6,7});

    private final NoteBlockInstrument noteBlockInstrument;
    private final BlockState block;
    private final int lowMidi;
    private final int[] gmPrograms;

    private static final Map<Integer, Instrument> PROGRAM_MAP = new HashMap<>();
    private static final Map<NoteBlockInstrument, Instrument> NBI_MAP = new HashMap<>();

    static {
        for (Instrument inst : values()) {
            for (int prog : inst.gmPrograms) {
                PROGRAM_MAP.put(prog, inst);
            }
            NBI_MAP.put(inst.noteBlockInstrument, inst);
        }
    }

    Instrument(NoteBlockInstrument noteBlockInstrument, BlockState block, int lowMidi, int[] gmPrograms) {
        this.noteBlockInstrument = noteBlockInstrument;
        this.block = block;
        this.lowMidi = lowMidi;
        this.gmPrograms = gmPrograms;
    }

    public NoteBlockInstrument getNoteBlockInstrument() {
        return noteBlockInstrument;
    }

    public BlockState getBlock() {
        return block;
    }

    public int getLowMidi() {
        return lowMidi;
    }

    public int getHighMidi() {
        return lowMidi + 24;
    }

    public boolean isPitched() {
        return lowMidi >= 0;
    }

    public static Instrument fromMidiProgram(int program) {
        return PROGRAM_MAP.getOrDefault(program, HARP);
    }

    public static Instrument fromNoteBlockInstrument(NoteBlockInstrument nbi) {
        return NBI_MAP.getOrDefault(nbi, HARP);
    }

    public static Instrument bestForPitch(int rawMidiNote, Instrument hint) {
        if (hint != null && hint.isPitched() && rawMidiNote >= hint.lowMidi && rawMidiNote <= hint.getHighMidi()) {
            return hint;
        }
        if (rawMidiNote >= HARP.lowMidi && rawMidiNote <= HARP.getHighMidi()) {
            return HARP;
        }
        Instrument best = null;
        int bestDist = Integer.MAX_VALUE;
        for (Instrument inst : values()) {
            if (!inst.isPitched()) continue;
            if (rawMidiNote >= inst.lowMidi && rawMidiNote <= inst.getHighMidi()) {
                return inst;
            }
            int dist = Math.min(Math.abs(rawMidiNote - inst.lowMidi), Math.abs(rawMidiNote - inst.getHighMidi()));
            if (dist < bestDist) {
                bestDist = dist;
                best = inst;
            }
        }
        return best != null ? best : HARP;
    }

    public int toMcPitch(int rawMidiNote) {
        if (!isPitched()) return 0;
        int pitch = rawMidiNote - lowMidi;
        return Math.max(0, Math.min(24, pitch));
    }
}
