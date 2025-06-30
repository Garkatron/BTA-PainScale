package deus.painscale.mechanics;

import deus.painscale.PainScaleMod;
import net.minecraft.core.item.IArmorItem;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.Items;

import java.util.ArrayList;
import java.util.*;

public class ArmorSets {
	public static final NavigableMap<Integer, List<List<IArmorItem>>> SETS = new TreeMap<>();
	public static final List<IArmorItem> defaultSet = new ArrayList<>();

	public static void initialize() {
		defaultSet.add((IArmorItem) null);
		defaultSet.add((IArmorItem) null);
		defaultSet.add((IArmorItem) null);
		defaultSet.add((IArmorItem) Items.ARMOR_HELMET_CHAINMAIL);

		register(0, Items.ARMOR_BOOTS_LEATHER, Items.ARMOR_LEGGINGS_LEATHER);
		register(0, Items.ARMOR_CHESTPLATE_CHAINMAIL, Items.ARMOR_HELMET_CHAINMAIL);

		register(0, Items.ARMOR_BOOTS_CHAINMAIL, Items.ARMOR_LEGGINGS_CHAINMAIL);
		register(0, Items.ARMOR_CHESTPLATE_LEATHER, Items.ARMOR_HELMET_LEATHER);

		register(0, Items.ARMOR_BOOTS_LEATHER, Items.ARMOR_LEGGINGS_LEATHER, Items.ARMOR_CHESTPLATE_LEATHER, Items.ARMOR_HELMET_LEATHER);
		register(0, Items.ARMOR_BOOTS_CHAINMAIL, Items.ARMOR_LEGGINGS_CHAINMAIL, Items.ARMOR_CHESTPLATE_CHAINMAIL, Items.ARMOR_HELMET_CHAINMAIL);

		register(30, Items.ARMOR_BOOTS_CHAINMAIL, Items.ARMOR_LEGGINGS_CHAINMAIL);
		register(30, Items.ARMOR_CHESTPLATE_IRON, Items.ARMOR_HELMET_IRON);

		register(30, Items.ARMOR_BOOTS_IRON, Items.ARMOR_LEGGINGS_IRON);
		register(30, Items.ARMOR_CHESTPLATE_CHAINMAIL, Items.ARMOR_HELMET_CHAINMAIL);

		register(30, Items.ARMOR_BOOTS_CHAINMAIL, Items.ARMOR_LEGGINGS_CHAINMAIL, Items.ARMOR_CHESTPLATE_CHAINMAIL, Items.ARMOR_HELMET_CHAINMAIL);
		register(30, Items.ARMOR_BOOTS_IRON, Items.ARMOR_LEGGINGS_IRON, Items.ARMOR_CHESTPLATE_IRON, Items.ARMOR_HELMET_IRON);

		register(60, Items.ARMOR_BOOTS_IRON, Items.ARMOR_LEGGINGS_IRON);
		register(60, Items.ARMOR_CHESTPLATE_DIAMOND, Items.ARMOR_HELMET_DIAMOND);

		register(60, Items.ARMOR_BOOTS_DIAMOND, Items.ARMOR_LEGGINGS_DIAMOND);
		register(60, Items.ARMOR_CHESTPLATE_IRON, Items.ARMOR_HELMET_IRON);

		register(60, Items.ARMOR_BOOTS_IRON, Items.ARMOR_LEGGINGS_IRON, Items.ARMOR_CHESTPLATE_IRON, Items.ARMOR_HELMET_IRON);
		register(60, Items.ARMOR_BOOTS_DIAMOND, Items.ARMOR_LEGGINGS_DIAMOND, Items.ARMOR_CHESTPLATE_DIAMOND, Items.ARMOR_HELMET_DIAMOND);

		register(90, Items.ARMOR_BOOTS_DIAMOND, Items.ARMOR_LEGGINGS_DIAMOND);
		register(90, Items.ARMOR_CHESTPLATE_STEEL, Items.ARMOR_HELMET_STEEL);

		register(90, Items.ARMOR_BOOTS_STEEL, Items.ARMOR_LEGGINGS_STEEL);
		register(90, Items.ARMOR_CHESTPLATE_DIAMOND, Items.ARMOR_HELMET_DIAMOND);

		register(90, Items.ARMOR_BOOTS_DIAMOND, Items.ARMOR_LEGGINGS_DIAMOND, Items.ARMOR_CHESTPLATE_DIAMOND, Items.ARMOR_HELMET_DIAMOND);
		register(90, Items.ARMOR_BOOTS_STEEL, Items.ARMOR_LEGGINGS_STEEL, Items.ARMOR_CHESTPLATE_STEEL, Items.ARMOR_HELMET_STEEL);

		register(100, Items.ARMOR_BOOTS_STEEL, Items.ARMOR_LEGGINGS_STEEL, Items.ARMOR_CHESTPLATE_STEEL, Items.ARMOR_HELMET_STEEL);
	}

	public static void register(int sinceLvl, Item... set) {
		List<IArmorItem> armorSet = new ArrayList<>();

		for (Item item : set) {
			if (item == null) {
				PainScaleMod.LOGGER.warn("Not an item");
			} else if (!(item instanceof IArmorItem)) {
				throw new IllegalArgumentException("Item " + item + " does not implement IArmorItem!");
			}
			armorSet.add((IArmorItem) item);
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
