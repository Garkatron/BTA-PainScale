package deus.painscale;

import deus.painscale.command.PainScaleCommand;
import deus.painscale.entity.mob_skeleton_armored.MobSkeletonArmored;
import deus.painscale.entity.mob_zombie_armored.MobPainScaleZombieArmored;
import deus.painscale.item.PainScaleItems;
import deus.painscale.mechanics.ArmorSets;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.data.gamerule.GameRuleBoolean;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.EntityHelper;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;


public class PainScaleMod implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint {
	public static final String MOD_ID = "painscale";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static TomlConfigHandler CFG;
	private static final Toml TOML = new Toml("Customize your pain :)");
	public static final RecipeNamespace PSDF = new RecipeNamespace();

	public static double base_attack_multiplier_per_level = 0.0;
	public static double base_health_multiplier_per_level = 0.0;
	public static double start_attack_multiplier = 0.0;
	public static double start_health_multiplier = 0.0;

	public static GameRuleBoolean DYNAMIC_DIFFICULTY = null;
	public static GameRuleBoolean MORE_HEARTS = null;
	public static GameRuleBoolean ARMORED_ZOMBIES_PLUS = null;
	public static GameRuleBoolean MP_NEARBY_PLAYER_AFFECTS = null;
	public static GameRuleBoolean DAY_SURVIVED_MESSAGE = null;

	static {
		TOML.addCategory("IDs")
			.addEntry("item_id_olivine_heart", 25000);

		TOML.addCategory("Levels")
			.addEntry("min_points_to_level_up", 10) // Minimum points required to level up
			.addEntry("points_per_level_multiplier", 50.0) // Points multiplier per level
			.addEntry("minimum_level", 0) // Minimum allowed level
			.addEntry("maximum_level", 100); // Maximum allowed level

		TOML.addCategory("Enemies")
			.addEntry("enable_armored_skeletons", true) // Enable armored skeleton enemies
			.addEntry("scale_monster_health", true) // Scale monster health by level
			.addEntry("scale_monster_strength", true) // Scale monster strength by level

			.addEntry("base_attack_multiplier", 0.05) // Base attack multiplier for monsters
			.addEntry("attack_multiplier_per_level", 0.05) // Additional attack multiplier per level

			.addEntry("base_health_multiplier", 1.00) // Base health multiplier for monsters
			.addEntry("health_multiplier_per_level", 0.05); // Additional health multiplier per level

		TOML.addCategory("Multiplayer")
			.addEntry("player_proximity_radius", 8); // Radius for detecting nearby players

		TOML.addCategory("Points")
			.addEntry("base_points_per_monster", 10) // Base points awarded per monster killed
			.addEntry("points_per_level_multiplier_per_monster", 10.0)
			.addEntry("level_cost_points_multiplier", 50.0) // Multiplier for point cost per level
			.addEntry("points_gained_per_monster_hit", 1) // Points gained per hit on a monster
			.addEntry("points_per_day_survived_per_level", 20) // Points gained per day survived * current level
			.addEntry("points_lost_on_death_per_level", 20) // Points lost on death * current level
			.addEntry("points_lost_when_killed_by_player_per_level", 10); // Points lost when killed by another player * current level


		CFG = new TomlConfigHandler(MOD_ID, TOML);

		DYNAMIC_DIFFICULTY = GameRules.register(new GameRuleBoolean("dynamicDifficulty", true));
		MORE_HEARTS = GameRules.register(new GameRuleBoolean("moreHearts", true));
		ARMORED_ZOMBIES_PLUS = GameRules.register(new GameRuleBoolean("armoredZombiesPlus", true));
		MP_NEARBY_PLAYER_AFFECTS = GameRules.register(new GameRuleBoolean("mpNearbyPlayerAffects", true));
		DAY_SURVIVED_MESSAGE = GameRules.register(new GameRuleBoolean("daySurvivedMessage", true));
	}

	@Override
	public void onInitialize() {

		base_attack_multiplier_per_level = PainScaleMod.CFG.getDouble("Enemies.base_attack_multiplier");
		base_health_multiplier_per_level = PainScaleMod.CFG.getDouble("Enemies.base_attack_multiplier");
		start_attack_multiplier = PainScaleMod.CFG.getDouble("Enemies.attack_multiplier_per_level");
		start_health_multiplier = PainScaleMod.CFG.getDouble("Enemies.attack_multiplier_per_level");

		PainScaleItems.init();
		CommandManager.registerCommand(new PainScaleCommand());

		EntityHelper.createEntity(MobSkeletonArmored.class, NamespaceID.getPermanent(MOD_ID, "skeleton_armored"), "skeleton_armored");
		EntityHelper.createEntity(MobPainScaleZombieArmored.class, NamespaceID.getPermanent(MOD_ID, "ps_zombie_armored"), "ps_zombie_armored");



		LOGGER.info("PainScale initialized.");

	}

	@Override
	public void onRecipesReady() {
		RecipeBuilder.Shaped(MOD_ID).setShape("OOO", "OGO", "OOO").addInput('O', Items.OLIVINE).addInput('G', Items.FOOD_APPLE_GOLD).create("painscale:recipe/olivine_heart", PainScaleItems.OLIVINE_HEART.getDefaultStack());
	}

	@Override
	public void initNamespaces() {
		final RecipeGroup<RecipeEntryCrafting<?, ?>> painscale = new RecipeGroup<>(
			new RecipeSymbol(new ItemStack(PainScaleItems.OLIVINE_HEART))
		);


		PSDF.register("painscale", painscale);

		Registries.RECIPES.register(MOD_ID, PSDF);
	}

	public void beforeGameStart() {
		try {
			TextureRegistry.initializeAllFiles(MOD_ID, TextureRegistry.guiSpriteAtlas, true);
			TextureRegistry.initializeAllFiles(MOD_ID, TextureRegistry.particleAtlas, true);
		} catch (Exception var2) {
			LOGGER.warn("PainScale: Failed to fully initialize assets, some issue may occur!", var2);
		}
	}

	@Override
	public void afterGameStart() {
		ArmorSets.initialize();
	}
}
