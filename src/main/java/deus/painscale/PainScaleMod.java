package deus.painscale;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;


public class PainScaleMod implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint {
	public static final String MOD_ID = "painscale";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final TomlConfigHandler CFG;
	private static final Toml TOML = new Toml("Customize your pain :)");

	static {
		TOML.addCategory("IDs")
			.addEntry("starting_item_id", 19000)
			.addEntry("starting_block_id", 11000);

		TOML.addCategory("Levels")
			.addEntry("min_df_points_to_grow_up", 10)
			.addEntry("min_df_points_multiplier", 0.15)
			.addEntry("min_df_level", 0)
			.addEntry("max_df_level", 100);

		TOML.addCategory("Enemies")
			.addEntry("base_attack_multiplier_per_level", 0.15);


		CFG = new TomlConfigHandler(MOD_ID, TOML);
	}

	@Override
	public void onInitialize() {
		LOGGER.info("PainScale initialized.");
	}

	@Override
	public void onRecipesReady() {

	}

	@Override
	public void initNamespaces() {

	}

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {

	}
}
