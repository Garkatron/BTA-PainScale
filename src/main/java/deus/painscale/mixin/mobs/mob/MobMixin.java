package deus.painscale.mixin.mobs.mob;

import deus.painscale.PainScale;
import deus.painscale.api.IPainScaleMob;
import deus.painscale.api.IPainScalePlayer;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Mob.class, remap = false)
public class MobMixin implements IPainScaleMob {

	@Unique double dfPointsMultiplier = PainScale.CFG.getDouble("Points.points_per_level_multiplier_per_monster");

	@Inject(method = "onDeath", at = @At("TAIL"), remap = false)
	public void addPointsOnDeath(Entity entityKilledBy, CallbackInfo ci) {
		if (entityKilledBy instanceof Player player) {
			IPainScalePlayer p = (IPainScalePlayer) player;
			int points = (int) Math.max(PainScale.CFG.getInt("Points.base_points_per_monster"), dfPointsMultiplier * p.ps$getDifficultyLevel());
			p.ps$addPoints(points);
		}
	}


	@Override
	public double ps$getPointsMultiplier() {
		return dfPointsMultiplier;
	}
}
