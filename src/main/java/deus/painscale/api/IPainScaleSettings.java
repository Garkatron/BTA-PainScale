package deus.painscale.api;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.OptionBoolean;
import net.minecraft.client.option.OptionInteger;
import net.minecraft.client.option.OptionRange;

public interface IPainScaleSettings
{
	OptionRange get_ms_delay_points();
	OptionRange get_ms_delay_level();
	OptionBoolean get_show_level();
	OptionBoolean get_show_points();
}
