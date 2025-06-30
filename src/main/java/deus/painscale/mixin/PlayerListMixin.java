package deus.painscale.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import deus.painscale.PainScaleMod;
import deus.painscale.api.IPainScalePlayer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerList.class, remap = false)
public class PlayerListMixin {
	@Inject(method = "recreatePlayerEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/player/inventory/container/ContainerInventory;transferAllContents(Lnet/minecraft/core/player/inventory/container/ContainerInventory;)V"), remap = false)
	public void keepPsDfInfoMP(final PlayerServer previousPlayer, final int i, final CallbackInfoReturnable<PlayerServer> cir, @Local(name = "newPlayer") final PlayerServer newPlayer) {
		((IPainScalePlayer)newPlayer).ps$setPointsMultiplier(((IPainScalePlayer)previousPlayer).ps$getPointsMultiplier());
		((IPainScalePlayer)newPlayer).ps$setRemainingPoints(((IPainScalePlayer)previousPlayer).ps$getRemainingPoints());
		((IPainScalePlayer)newPlayer).ps$setDifficultyPoints(((IPainScalePlayer)previousPlayer).ps$getDifficultyPoints());
		((IPainScalePlayer)newPlayer).ps$setDifficultyLevels(((IPainScalePlayer)previousPlayer).ps$getDifficultyLevel());
		((IPainScalePlayer)newPlayer).ps$setMaxHealth(previousPlayer.getMaxHealth());
		
		double multiplier = PainScaleMod.CFG.getDouble("Points.point_cost_multiplier_per_level");
		int base = PainScaleMod.CFG.getInt("Points.lose_on_death");

		int pointsToLose = (int) (base * (((IPainScalePlayer)newPlayer).ps$getDifficultyLevel() * multiplier));

		((IPainScalePlayer)newPlayer).ps$subPoints(pointsToLose);


		newPlayer.sendMessage("You lost " + pointsToLose + " points...");
	}
}
