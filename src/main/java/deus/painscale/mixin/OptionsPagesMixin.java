package deus.painscale.mixin;

import deus.painscale.api.IPainScaleSettings;
import deus.painscale.item.PainScaleItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.IntegerOptionComponent;
import net.minecraft.client.gui.options.components.ToggleableOptionComponent;
import net.minecraft.client.gui.options.data.OptionsPage;
import net.minecraft.client.gui.options.data.OptionsPages;
import net.minecraft.client.option.GameSettings;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsPages.class)
public class OptionsPagesMixin {

	@Unique
	private static OptionsPage PAINSCALE;


	@Inject(method = "init", at = @At("TAIL"), remap = false)
	private static void customConfig(CallbackInfo ci) {
		GameSettings settings = Minecraft.getMinecraft().gameSettings;

		OptionsPage painScalePage = OptionsPages.register(
			new OptionsPage("gui.options.page.painscale.title",
				new ItemStack(PainScaleItems.OLIVINE_HEART))
		);

		painScalePage.withComponent(
			new ToggleableOptionComponent<>(((IPainScaleSettings)settings).get_ms_delay_level()));
		painScalePage.withComponent(
			new ToggleableOptionComponent<>(((IPainScaleSettings)settings).get_ms_delay_points()));
	}

}
