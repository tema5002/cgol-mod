package io.github.tema5002.cgol;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.StateManager;

public class CellBlock extends Block {
    public static final BooleanProperty ALIVE = BooleanProperty.of("alive");

    public static final Settings SETTINGS = Settings.create()
        .strength(5.0f, 10.0f)
        .requiresTool()
        .ticksRandomly()
        .sounds(BlockSoundGroup.IRON);

    public CellBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ALIVE);
    }

    public boolean isAlive(BlockState state) {
        return state.get(ALIVE);
    }

    public BlockState withAlive(boolean alive) {
        return getDefaultState().with(ALIVE, alive);
    }
}
