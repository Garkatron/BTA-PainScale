package deus.painscale.mixin;

import deus.painscale.PainScale;
import deus.painscale.api.IPainScaleSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(value = GameSettings.class, remap = false)
public class GameSettingsMixin implements IPainScaleSettings
{
	@Unique private final GameSettings mixinInst = (GameSettings)((Object)this);

	@Unique public final OptionRange ms_delay_level = new OptionRange(mixinInst, "painscale.msDelayLevel", 5000, 0, 9999);
	@Unique public final OptionRange ms_delay_points = new OptionRange(mixinInst, "painscale.msDelayPoints", 6000, 0, 9999);


	@Inject(method = "<init>", at=@At("TAIL"),remap = false)
	public void init(Minecraft minecraft, File file, CallbackInfo ci) {
		PainScale.OPTIONS = (IPainScaleSettings) ((Object)this);
	}

	@Override
	public OptionRange get_ms_delay_points() {
		return ms_delay_points;
	}

	@Override
	public OptionRange get_ms_delay_level() {
		return ms_delay_level;
	}
}
