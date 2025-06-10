package io.github.tema5002.cgol;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CGOLSimulation {
    private static final BlockPos[] NEIGHBOR_OFFSETS = {
        new BlockPos(-1, -1, -1),
        new BlockPos(0, -1, -1),
        new BlockPos(1, -1, -1),
        new BlockPos(-1, 0, -1),
        new BlockPos(0, 0, -1),
        new BlockPos(1, 0, -1),
        new BlockPos(-1, 1, -1),
        new BlockPos(0, 1, -1),
        new BlockPos(1, 1, -1),
        new BlockPos(-1, -1, 0),
        new BlockPos(0, -1, 0),
        new BlockPos(1, -1, 0),
        new BlockPos(-1, 0, 0),
        new BlockPos(1, 0, 0),
        new BlockPos(-1, 1, 0),
        new BlockPos(0, 1, 0),
        new BlockPos(1, 1, 0),
        new BlockPos(-1, -1, 1),
        new BlockPos(0, -1, 1),
        new BlockPos(1, -1, 1),
        new BlockPos(-1, 0, 1),
        new BlockPos(0, 0, 1),
        new BlockPos(1, 0, 1),
        new BlockPos(-1, 1, 1),
        new BlockPos(0, 1, 1),
        new BlockPos(1, 1, 1)
    };

    private static int tickCounter = 0;

    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors() - 1));

    public static void tick(MinecraftServer server) {
        if (!server.getGameRules().getBoolean(CGOLMod.CGOL_ENABLED)) return;

        tickCounter++;
        int speed = server.getGameRules().getInt(CGOLMod.CGOL_SPEED);
        if (tickCounter % speed != 0) return;

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (ServerWorld world : server.getWorlds()) {
            futures.add(CompletableFuture.runAsync(() -> processWorld(world), THREAD_POOL));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        }).join();
    }

    private static void processWorld(ServerWorld world) {
        Set<BlockPos> toCheck = new HashSet<>();
        Map<BlockPos, Boolean> currentState = new HashMap<>();

        world.getPlayers().forEach(player -> {
            BlockPos playerPos = player.getBlockPos();
            int radius = 32;

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos pos = playerPos.add(x, y, z);
                        BlockState state = world.getBlockState(pos);

                        if (state.getBlock() instanceof CellBlock cellBlock) {
                            toCheck.add(pos);
                            for (BlockPos offset : NEIGHBOR_OFFSETS) {
                                toCheck.add(pos.add(offset));
                            }
                            currentState.put(pos, cellBlock.isAlive(state));
                        }
                    }
                }
            }
        });

        Map<BlockPos, Boolean> nextState = new HashMap<>();

        toCheck.forEach(pos -> {
            int aliveNeighbors = countAliveNeighbors(pos, currentState);
            boolean currentlyAlive = currentState.getOrDefault(pos, false);
            nextState.put(pos, (currentlyAlive && (aliveNeighbors == 2 || aliveNeighbors == 3)) || (!currentlyAlive && aliveNeighbors == 3));
        });

        nextState.forEach((pos, shouldBeAlive) -> {
            BlockState currentBlockState = world.getBlockState(pos);
            Block block = currentBlockState.getBlock();

            if (block instanceof CellBlock cellBlock) {
                boolean currentlyAlive = cellBlock.isAlive(currentBlockState);
                if (currentlyAlive != shouldBeAlive) {
                    world.setBlockState(pos, cellBlock.withAlive(shouldBeAlive));
                }
            } else if (shouldBeAlive && (currentBlockState.isAir())) {
                world.setBlockState(pos, CGOLMod.CELL_BLOCK.withAlive(true));
            }
        });
    }

    private static int countAliveNeighbors(BlockPos center, Map<BlockPos, Boolean> stateMap) {
        int count = 0;

        for (BlockPos offset : NEIGHBOR_OFFSETS) {
            BlockPos neighborPos = center.add(offset);
            if (Boolean.TRUE.equals(stateMap.get(neighborPos))) {
                count++;
            }
        }

        return count;
    }
}