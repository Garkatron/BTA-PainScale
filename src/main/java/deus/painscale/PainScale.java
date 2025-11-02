package deus.painscale;

import deus.painscale.api.IPainScaleSettings;
import deus.painscale.entity.mob_skeleton_armored.MobSkeletonArmored;
import deus.painscale.entity.mob_zombie_armored.MobPainScaleZombieArmored;
import deus.painscale.gui.HudManager;
import deus.painscale.item.PainScaleItems;
import deus.painscale.mechanics.ArmorSets;
import deus.painscale.newsystem.DifficultySystem;
import deus.painscale.newsystem.Factor;
import deus.painscale.newsystem.HierarchicalFactorManager;
import deus.painscale.newsystem.Level;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.data.gamerule.GameRuleBoolean;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.EntityHelper;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Date;


public class PainScale implements ModInitializer, GameStartEntrypoint {
	public static final String MOD_ID = "painscale";
	public static final int CONFIG_VERSION = 0;
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static TomlConfigHandler CFG;
	private static final Toml TOML = new Toml("Customize your pain :)");
	public static final RecipeNamespace PSDF = new RecipeNamespace();
	private static boolean FlagIsCatalystPresent = false;
	public static double base_attack_multiplier_per_level = 0.0;
	public static double base_health_multiplier_per_level = 0.0;
	public static double start_attack_multiplier = 0.0;
	public static double start_health_multiplier = 0.0;

	public static GameRuleBoolean DYNAMIC_DIFFICULTY = null;
	public static GameRuleBoolean MORE_HEARTS = null;
	public static GameRuleBoolean ARMORED_ZOMBIES_PLUS = null;
	public static GameRuleBoolean MP_NEARBY_PLAYER_AFFECTS = null;
	public static GameRuleBoolean DAY_SURVIVED_MESSAGE = null;

	public static IPainScaleSettings OPTIONS;

	public static boolean isCatalystPresent() {
		return FlagIsCatalystPresent;
	}

	public static void setIsCatalystPresent(boolean b) {
		if (FlagIsCatalystPresent && !b) return;
		FlagIsCatalystPresent = b;
	}

	static {

		TOML.addCategory("Config")
				.addEntry("version", CONFIG_VERSION);

		/*
		TOML.addCategory("GUI")
			.addEntry("level_disappear_delay_milliseconds", 3000)
			.addEntry("points_disappear_delay_milliseconds", 5000);
		*/

		TOML.addCategory("IDs")
			.addEntry("item_id_olivine_heart", 25000);

		TOML.addCategory("Levels")
			.addEntry("min_points_to_level_up", 10) // Minimum points required to level up
			.addEntry("points_per_level_multiplier", 20.0) // Points multiplier per level
			.addEntry("minimum_level", 1) // Minimum allowed level
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
			.addEntry("points_per_level_multiplier_per_monster", 3.0)
			.addEntry("level_cost_points_multiplier", 25.0) // Multiplier for point cost per level on death
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

		try {
			File file = PainScale.CFG.getConfigFile();

			if (!file.exists()) {
				LOGGER.info("Configuration file not found. Creating a new one...");
				PainScale.CFG.create();
			}

			Integer versionObj = PainScale.CFG.getInt("Config.version");
			int version = (versionObj != null) ? versionObj : -1;

			if (CONFIG_VERSION != version) {
				LOGGER.warn("The configuration version is outdated (found {}, expected {}). Recreating config.", version, CONFIG_VERSION);

				Path source = file.toPath();

				LOGGER.info("Reading config file: {}", source);
				Path backupDir = source.getParent().resolve("painscale_bk");
				try {
					if (!Files.exists(backupDir)) {
						Files.createDirectories(backupDir);
						LOGGER.info("Created backup directory: {}", backupDir.toAbsolutePath());
					}
				} catch (IOException dirEx) {
					LOGGER.error("Failed to create backup directory: ", dirEx);
				}

				String backupName = "backup_v" + version + "_" + Date.from(Instant.now()).getTime() + ".cfg";
				Path backupPath = backupDir.resolve(backupName);

				try {
					Files.move(source, backupPath, StandardCopyOption.REPLACE_EXISTING);
					LOGGER.warn("Old configuration backed up at: {}", backupPath.getFileName());
				} catch (IOException moveEx) {
					LOGGER.error("Failed to back up old configuration: ", moveEx);
				}

				try {
					if (Files.exists(source)) {
						Files.delete(source);
						LOGGER.info("Deleted old configuration.");
					}
					PainScale.CFG.setDefaults(TOML);
					PainScale.CFG.create();
					LOGGER.info("New configuration created successfully with default values.");
				} catch (IOException ex) {
					LOGGER.error("Failed to create a fresh configuration file: ", ex);
				}
			}


		} catch (Exception e) {
			LOGGER.error("Error while initializing PainScale configuration: ", e);
		}

		base_attack_multiplier_per_level = PainScale.CFG.getDouble("Enemies.attack_multiplier_per_level");
		base_health_multiplier_per_level = PainScale.CFG.getDouble("Enemies.health_multiplier_per_level");
		start_attack_multiplier = PainScale.CFG.getDouble("Enemies.base_attack_multiplier");
		start_health_multiplier = PainScale.CFG.getDouble("Enemies.base_health_multiplier");

		setIsCatalystPresent(FabricLoader.getInstance().isModLoaded("catalyst-effects"));

		if (isCatalystPresent()) LOGGER.warn("Catalyst effects is present.");

		PainScaleItems.init();
		// CommandManager.registerCommand(new PainScaleCommand());

		EntityHelper.createEntity(MobSkeletonArmored.class, NamespaceID.getPermanent(MOD_ID, "skeleton_armored"), "skeleton_armored");
		EntityHelper.createEntity(MobPainScaleZombieArmored.class, NamespaceID.getPermanent(MOD_ID, "ps_zombie_armored"), "ps_zombie_armored");


		LOGGER.info("PainScale initialized.");

		DifficultySystem.initialize();
/*


// Nivel 1: Jugador


// Factor compuesto local que depende de factores del jugador y del mundo
		Factor globalDifficultyPlayer = new Factor("player.globalDifficulty", new Level(100, 5));
		globalDifficultyPlayer.addDependency(melee);
		globalDifficultyPlayer.addDependency(distance);
		globalDifficultyPlayer.addDependency(playerManager.get("world.difficulty"));
		playerManager.register(globalDifficultyPlayer);

// Simulación
		melee.addPoints(50);
		distance.addPoints(30);
		worldDifficulty.addPoints(20); // afecta automáticamente al compuesto
		System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		System.out.println(worldDifficulty.toString());
		System.out.println(globalDifficultyPlayer.toString());
		System.out.println(playerManager.toString());
*/
	}



	public void beforeGameStart() {
		try {
			if (TextureRegistry.guiSpriteAtlas != null) {
				TextureRegistry.initializeAllFiles(MOD_ID, TextureRegistry.guiSpriteAtlas, true);
				TextureRegistry.initializeAllFiles(MOD_ID, TextureRegistry.particleAtlas, true);
			}
		} catch (Throwable t) {
			LOGGER.warn("PainScale: Skipping early texture initialization (will load later)", t);
		}

	}


	@Override
	public void afterGameStart() {
		ArmorSets.initialize();
		new HudManager().onInitialize();
	}
}
