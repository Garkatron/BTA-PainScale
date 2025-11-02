package deus.painscale.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import deus.painscale.PainScale;
import deus.painscale.api.IPainScalePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {

	@Shadow public PlayerLocal thePlayer;

	// @Inject(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/PlayerLocal;setGamemode(Lnet/minecraft/core/player/gamemode/Gamemode;)V"), remap = false)
	/*public void keepPsDfInfo(boolean multiplayer, int targetDimension, CallbackInfo ci, @Local Player previousPlayer) {
		((IPainScalePlayer)thePlayer).ps$setMaxHealth(previousPlayer.getMaxHealth());

		double multiplier = PainScale.CFG.getDouble("Points.level_cost_points_multiplier");
		int base = PainScale.CFG.getInt("Points.points_lost_on_death_per_level");

		// int pointsToLose = (int) (base * (((IPainScalePlayer)thePlayer).ps$getDifficultyLevel() * multiplier));

		// ((IPainScalePlayer)thePlayer).ps$subPoints(pointsToLose);

		// thePlayer.sendMessage("You lost " + pointsToLose + " points...");*/
	//int overflow = Math.max(0, points - maxPoints);}


}
