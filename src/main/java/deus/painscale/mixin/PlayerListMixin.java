package deus.painscale.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import deus.painscale.PainScale;
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

		double multiplier = PainScale.CFG.getDouble("Points.level_cost_points_multiplier");
		int base = PainScale.CFG.getInt("Points.points_lost_on_death_per_level");

		int pointsToLose = (int) (base * (((IPainScalePlayer)newPlayer).ps$getDifficultyLevel() * multiplier));

		if (((IPainScalePlayer)previousPlayer).ps$wasKilledByPlayer()) {
			int c = (int) (PainScale.CFG.getInt("Points.points_lost_when_killed_by_player_per_level") * multiplier);
			pointsToLose += c;
		}

		((IPainScalePlayer)newPlayer).ps$subPoints(pointsToLose);

		newPlayer.sendMessage("You lost " + pointsToLose + " points...");
	}
}
