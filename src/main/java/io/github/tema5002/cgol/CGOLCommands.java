package io.github.tema5002.cgol;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class CGOLCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("cgol")
            .then(CommandManager.literal("enable")
                .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                    .executes(context -> {
                        boolean enabled = BoolArgumentType.getBool(context, "enabled");
                        context.getSource().getServer().getGameRules().get(CGOLMod.CGOL_ENABLED).set(enabled, context.getSource().getServer());

                        context.getSource().sendMessage(Text.translatable(
                            "cgol.simulation.status",
                            Text.translatable(enabled ? "cgol.simulation.enabled" : "cgol.simulation.disabled")
                        ));
                        return 1;
                    })
                )
            )
            .then(CommandManager.literal("speed")
                .then(CommandManager.argument("ticks", IntegerArgumentType.integer(1, 1200))
                    .executes(context -> {
                        int speed = IntegerArgumentType.getInteger(context, "ticks");
                        context.getSource().getServer().getGameRules().get(CGOLMod.CGOL_SPEED).set(speed, context.getSource().getServer());
                        context.getSource().sendMessage(Text.translatable("cgol.speed.set", speed));
                        return 1;
                    })
                )
            )
            .then(CommandManager.literal("status")
                .executes(context -> {
                    boolean enabled = context.getSource().getServer().getGameRules().getBoolean(CGOLMod.CGOL_ENABLED);
                    int speed = context.getSource().getServer().getGameRules().getInt(CGOLMod.CGOL_SPEED);

                    context.getSource().sendMessage(Text.translatable(
                        "cgol.status",
                        Text.translatable(enabled ? "cgol.status.enabled" : "cgol.status.disabled"),
                        speed
                    ));
                    return 1;
                })
            )
        );
    }
}