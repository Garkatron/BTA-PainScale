package deus.painscale.gui;

import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.HudComponents;
import net.minecraft.client.gui.hud.component.layout.LayoutSnap;

public class HudManager
{
	public String initialSettingsString;

	public void onInitialize()
	{
		DifficultyMeter DIFFICULTY_METER = new DifficultyMeter("difficultymeter", 26, 16, new LayoutSnap(HudComponents.HOTBAR, ComponentAnchor.CENTER_RIGHT, ComponentAnchor.CENTER_LEFT));

		HudComponents.register(DIFFICULTY_METER);



		/*
		if (HudComponents.HEALTH_BAR.getLayout() instanceof LayoutSnap layout)
			layout.setParent(DIFFICULTY_METER);

		if (HudComponents.ARMOR_BAR.getLayout() instanceof LayoutSnap layout)
			layout.setParent(DIFFICULTY_METER);
*/
		initialSettingsString = HudComponents.INSTANCE.toSettingsString();
	}
}
