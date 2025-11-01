package deus.painscale.mechanics;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deus.painscale.PainScale;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.item.IArmorItem;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.Items;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ArmorSets {
	public static final List<IArmorItem> defaultSet = new ArrayList<>();
	public static final NavigableMap<Integer, List<List<IArmorItem>>> SETS = new TreeMap<>();
	public static final HashMap<String, IArmorItem> armorList = new HashMap<>();
	private static final String CONFIG_DIRECTORY = FabricLoader.getInstance().getGameDir().toString() + "/config/";
	private static final Path CONFIG_PATH = Path.of(CONFIG_DIRECTORY + "/" + PainScale.MOD_ID + "_armor_sets.json");

	public static void initialize() {
		// Default armor set
		defaultSet.add(null);
		defaultSet.add(null);
		defaultSet.add(null);
		defaultSet.add((IArmorItem) Items.ARMOR_HELMET_LEATHER);

		// === Add all armor items to armorList ===
		addToArmorList(Items.ARMOR_BOOTS_CHAINMAIL);
		addToArmorList(Items.ARMOR_LEGGINGS_CHAINMAIL);
		addToArmorList(Items.ARMOR_CHESTPLATE_CHAINMAIL);
		addToArmorList(Items.ARMOR_HELMET_CHAINMAIL);

		addToArmorList(Items.ARMOR_BOOTS_LEATHER);
		addToArmorList(Items.ARMOR_LEGGINGS_LEATHER);
		addToArmorList(Items.ARMOR_CHESTPLATE_LEATHER);
		addToArmorList(Items.ARMOR_HELMET_LEATHER);

		addToArmorList(Items.ARMOR_BOOTS_IRON);
		addToArmorList(Items.ARMOR_LEGGINGS_IRON);
		addToArmorList(Items.ARMOR_CHESTPLATE_IRON);
		addToArmorList(Items.ARMOR_HELMET_IRON);

		addToArmorList(Items.ARMOR_BOOTS_GOLD);
		addToArmorList(Items.ARMOR_LEGGINGS_GOLD);
		addToArmorList(Items.ARMOR_CHESTPLATE_GOLD);
		addToArmorList(Items.ARMOR_HELMET_GOLD);

		addToArmorList(Items.ARMOR_BOOTS_DIAMOND);
		addToArmorList(Items.ARMOR_LEGGINGS_DIAMOND);
		addToArmorList(Items.ARMOR_CHESTPLATE_DIAMOND);
		addToArmorList(Items.ARMOR_HELMET_DIAMOND);

		addToArmorList(Items.ARMOR_BOOTS_STEEL);
		addToArmorList(Items.ARMOR_LEGGINGS_STEEL);
		addToArmorList(Items.ARMOR_CHESTPLATE_STEEL);
		addToArmorList(Items.ARMOR_HELMET_STEEL);



		if (Files.exists(CONFIG_PATH)) {
			try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
				PainScale.LOGGER.info("Loading armor sets from " + PainScale.MOD_ID + "_armor_sets.json");
				Gson gson = new Gson();

				Type type = new TypeToken<TreeMap<Integer, List<List<String>>>>(){}.getType();
				TreeMap<Integer, List<List<String>>> config = gson.fromJson(reader, type);

				config.forEach(
					(level,sets)->{
						List<String> cset = new ArrayList<>();
						sets.forEach(cset::addAll);
						register(level, cset);

					}
				);

			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			createArmorConfig();
		}

		// === Register armor tiers using armorList ===


	}

	private static void createArmorConfig() {
		PainScale.LOGGER.info("Config doesn't exists, Registering armor sets from " + PainScale.MOD_ID);

		// Tier 0 - Leather
		register(0,
			get("minecraft:item/armor_boots_leather"),
			get("minecraft:item/armor_leggings_leather"));
		register(0,
			get("minecraft:item/armor_chestplate_leather"),
			get("minecraft:item/armor_helmet_leather"));
		register(0,
			get("minecraft:item/armor_boots_leather"),
			get("minecraft:item/armor_leggings_leather"),
			get("minecraft:item/armor_chestplate_leather"),
			get("minecraft:item/armor_helmet_leather"));

		// Tier 15 - Leather + Iron
		register(15,
			get("minecraft:item/armor_boots_iron"),
			get("minecraft:item/armor_leggings_iron"),
			get("minecraft:item/armor_chestplate_leather"),
			get("minecraft:item/armor_helmet_leather"));
		register(15,
			get("minecraft:item/armor_boots_leather"),
			get("minecraft:item/armor_leggings_leather"),
			get("minecraft:item/armor_chestplate_iron"),
			get("minecraft:item/armor_helmet_iron"));

		// Tier 30 - Full Iron
		register(30,
			get("minecraft:item/armor_boots_iron"),
			get("minecraft:item/armor_leggings_iron"));
		register(30,
			get("minecraft:item/armor_chestplate_iron"),
			get("minecraft:item/armor_helmet_iron"));
		register(30,
			get("minecraft:item/armor_boots_iron"),
			get("minecraft:item/armor_leggings_iron"),
			get("minecraft:item/armor_chestplate_iron"),
			get("minecraft:item/armor_helmet_iron"));

		// Tier 45 - Iron + Gold
		register(45,
			get("minecraft:item/armor_boots_iron"),
			get("minecraft:item/armor_leggings_iron"),
			get("minecraft:item/armor_chestplate_gold"),
			get("minecraft:item/armor_helmet_gold"));
		register(45,
			get("minecraft:item/armor_boots_gold"),
			get("minecraft:item/armor_leggings_gold"),
			get("minecraft:item/armor_chestplate_iron"),
			get("minecraft:item/armor_helmet_iron"));

		// Tier 50 - Full Gold
		register(50,
			get("minecraft:item/armor_boots_gold"),
			get("minecraft:item/armor_leggings_gold"));
		register(50,
			get("minecraft:item/armor_chestplate_gold"),
			get("minecraft:item/armor_helmet_gold"));
		register(50,
			get("minecraft:item/armor_boots_gold"),
			get("minecraft:item/armor_leggings_gold"),
			get("minecraft:item/armor_chestplate_gold"),
			get("minecraft:item/armor_helmet_gold"));

		// Tier 55 - Gold + Diamond
		register(55,
			get("minecraft:item/armor_boots_gold"),
			get("minecraft:item/armor_leggings_gold"),
			get("minecraft:item/armor_chestplate_diamond"),
			get("minecraft:item/armor_helmet_diamond"));
		register(55,
			get("minecraft:item/armor_boots_diamond"),
			get("minecraft:item/armor_leggings_diamond"),
			get("minecraft:item/armor_chestplate_gold"),
			get("minecraft:item/armor_helmet_gold"));


		// Tier 60 - Full Diamond
		register(60,
			get("minecraft:item/armor_boots_diamond"),
			get("minecraft:item/armor_leggings_diamond"));
		register(60,
			get("minecraft:item/armor_chestplate_diamond"),
			get("minecraft:item/armor_helmet_diamond"));
		register(60,
			get("minecraft:item/armor_boots_diamond"),
			get("minecraft:item/armor_leggings_diamond"),
			get("minecraft:item/armor_chestplate_diamond"),
			get("minecraft:item/armor_helmet_diamond"));

		// Tier 75 - Diamond + Steel
		register(75,
			get("minecraft:item/armor_boots_diamond"),
			get("minecraft:item/armor_leggings_diamond"),
			get("minecraft:item/armor_chestplate_steel"),
			get("minecraft:item/armor_helmet_steel"));
		register(75,
			get("minecraft:item/armor_boots_steel"),
			get("minecraft:item/armor_leggings_steel"),
			get("minecraft:item/armor_chestplate_diamond"),
			get("minecraft:item/armor_helmet_diamond"));

		// Tier 90 - Full Steel
		register(90,
			get("minecraft:item/armor_boots_steel"),
			get("minecraft:item/armor_leggings_steel"));
		register(90,
			get("minecraft:item/armor_chestplate_steel"),
			get("minecraft:item/armor_helmet_steel"));
		register(90,
			get("minecraft:item/armor_boots_steel"),
			get("minecraft:item/armor_leggings_steel"),
			get("minecraft:item/armor_chestplate_steel"),
			get("minecraft:item/armor_helmet_steel"));

		// Tier 100 - Final Tier (Steel)
		register(100,
			get("minecraft:item/armor_boots_steel"),
			get("minecraft:item/armor_leggings_steel"),
			get("minecraft:item/armor_chestplate_steel"),
			get("minecraft:item/armor_helmet_steel"));


		TreeMap<Integer, List<List<String>>> stuff = SETS.entrySet().stream().collect(
			Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(innerList -> innerList.stream().map(iArmorItem -> iArmorItem.asItem().namespaceID.toString()).collect(Collectors.toList())).collect(Collectors.toList()), (a, b) -> b, TreeMap::new)
		);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(stuff);
		try (FileWriter fileWriter = new FileWriter(CONFIG_PATH.toFile())) {
			fileWriter.write(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Helper to register armor items in the armorList
	private static void addToArmorList(Item item) {
		armorList.put(item.namespaceID.toString(), (IArmorItem) item);
	}

	// Helper to retrieve from armorList
	private static IArmorItem get(String namespacedId) {
		return armorList.get(namespacedId);
	}


	public static void register(int sinceLvl, IArmorItem... set) {
		List<IArmorItem> armorSet = new ArrayList<>();

		for (IArmorItem item : set) {
			if (item == null) {
				PainScale.LOGGER.warn("Not an item");
			}
			armorSet.add(item);
		}

		SETS.computeIfAbsent(sinceLvl, k -> new ArrayList<>()).add(armorSet);
	}


	public static void register(Integer sinceLvl, List<String> namespaces) {
		List<IArmorItem> armorSet = new ArrayList<>();

		for (String namespace : namespaces) {
			IArmorItem armorItem = get(namespace);
			if (armorItem!=null) {
				armorSet.add(armorItem);
			}
		}

		SETS.computeIfAbsent(sinceLvl, k -> new ArrayList<>()).add(armorSet);
	}

	public static List<List<IArmorItem>> getArmorSetsForLevel(int lvl) {
		Integer key = SETS.floorKey(lvl);
		return key != null ? SETS.get(key) : List.of(defaultSet);
	}

	public static List<IArmorItem> getArmorSet(int lvl, int index) {
		List<List<IArmorItem>> sets = getArmorSetsForLevel(lvl);
		return index < sets.size() ? sets.get(index) : defaultSet;
	}

	public static List<IArmorItem> getRandomArmorSet(int lvl) {
		List<List<IArmorItem>> sets = getArmorSetsForLevel(lvl);
		return sets.get((int) (Math.random() * sets.size()));
	}
}
