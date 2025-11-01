package deus.painscale.item;

import deus.painscale.PainScale;
import net.minecraft.core.item.Item;
import turniplabs.halplibe.helper.ItemBuilder;

public class PainScaleItems {
	public static Item OLIVINE_HEART;
	private static final ItemBuilder itemBuilder = new ItemBuilder(PainScale.MOD_ID);

	public static void init() {
		OLIVINE_HEART = itemBuilder.build(new ItemOlivineHeart("olivine_heart", "painscale:item/olivine_heart", PainScale.CFG.getInt("IDs.item_id_olivine_heart"), 20, 1, false,1));
	}
}
