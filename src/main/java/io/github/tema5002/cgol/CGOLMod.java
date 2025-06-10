package io.github.tema5002.cgol;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

public class CGOLMod implements ModInitializer {
	static CellBlock CELL_BLOCK = (CellBlock)ModBlocks.register(
		"cell",
		CellBlock::new,
		CellBlock.SETTINGS,
		true
	);

	public static final GameRules.Key<GameRules.IntRule> CGOL_SPEED = GameRuleRegistry.register(
		"cgolSpeed",
		GameRules.Category.UPDATES,
		GameRuleFactory.createIntRule(20, 1)
	);

	public static final GameRules.Key<GameRules.BooleanRule> CGOL_ENABLED = GameRuleRegistry.register(
		"cgolEnabled",
		GameRules.Category.UPDATES,
		GameRuleFactory.createBooleanRule(false)
	);

	@Override
	public void onInitialize() {
		RegistryKey<ItemGroup> groupKey = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of("cgol", "item_group"));
		ItemGroup itemGroup = FabricItemGroup.builder()
			.icon(() -> new ItemStack(CELL_BLOCK))
			.displayName(Text.translatable("itemGroup.cgol"))
			.build();

		Registry.register(Registries.ITEM_GROUP, groupKey, itemGroup);

		ItemGroupEvents.modifyEntriesEvent(groupKey).register(
			(ig) -> {
				ig.add(new ItemStack(CELL_BLOCK.withAlive(true).getBlock())); // Alive Cell
				ig.add(new ItemStack(CELL_BLOCK.withAlive(false).getBlock())); // Dead Cell
			}
		);
		CommandRegistrationCallback.EVENT.register(
			(dispatcher, registryAccess, environment) -> CGOLCommands.register(dispatcher)
		);
		ServerTickEvents.END_SERVER_TICK.register(CGOLSimulation::tick);
	}
}
