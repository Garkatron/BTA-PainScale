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
			.addEntry("starting_item_id", 19000)
			.addEntry("starting_block_id", 11000);

		TOML.addCategory("Levels")
			.addEntry("min_df_points_to_grow_up", 10)
			.addEntry("min_df_points_multiplier", 50.0)
			.addEntry("min_df_level", 0)
			.addEntry("max_df_level", 100);

		TOML.addCategory("Enemies")
			.addEntry("armored_skeletons", true)
			.addEntry("monster_health_multiplier", true)
			.addEntry("monster_strength_multiplier", true)

			.addEntry("start_attack_multiplier", 0.05)
			.addEntry("base_attack_multiplier_per_level", 0.05)

			.addEntry("start_health_multiplier", 1.00)
			.addEntry("base_health_multiplier_per_level", 0.05);

		TOML.addCategory("Multiplayer")
			.addEntry("nearby_player_radius", 8);

		TOML.addCategory("Points")
			.addEntry("point_cost_multiplier_per_level", 50.0)
			.addEntry("gain_on_monster_attack", 1)
			.addEntry("gain_per_survived_day_per_level", 20)
			.addEntry("lose_on_death_per_level", 20)
			.addEntry("lose_when_killed_by_player_per_level", 10);

		CFG = new TomlConfigHandler(MOD_ID, TOML);

		DYNAMIC_DIFFICULTY = GameRules.register(new GameRuleBoolean("dynamicDifficulty", true));
		MORE_HEARTS = GameRules.register(new GameRuleBoolean("moreHearts", true));
		ARMORED_ZOMBIES_PLUS = GameRules.register(new GameRuleBoolean("armoredZombiesPlus", true));
		MP_NEARBY_PLAYER_AFFECTS = GameRules.register(new GameRuleBoolean("mpNearbyPlayerAffects", true));
		DAY_SURVIVED_MESSAGE = GameRules.register(new GameRuleBoolean("daySurvivedMessage", true));
	}

	@Override
	public void onInitialize() {

		base_attack_multiplier_per_level = PainScaleMod.CFG.getDouble("Enemies.start_attack_multiplier");
		base_health_multiplier_per_level = PainScaleMod.CFG.getDouble("Enemies.start_health_multiplier");
		start_attack_multiplier = PainScaleMod.CFG.getDouble("Enemies.base_attack_multiplier_per_level");
		start_health_multiplier = PainScaleMod.CFG.getDouble("Enemies.base_health_multiplier_per_level");

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
