package deus.painscale.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import deus.painscale.PainScaleMod;
import deus.painscale.api.IPainScaleMobMonster;
import deus.painscale.api.IPainScalePlayer;
import deus.painscale.api.IPainScaleSpawnerMobs;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.monster.MobMonster;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.SpawnerMobs;
import net.minecraft.core.world.World;
import net.minecraft.core.world.config.spawning.SpawnerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static deus.painscale.PainScaleMod.DYNAMIC_DIFFICULTY;
import static deus.painscale.PainScaleMod.MP_NEARBY_PLAYER_AFFECTS;

@Mixin(value = SpawnerMobs.class, remap = false)
public class SpawnerMobsMixin implements IPainScaleSpawnerMobs {

	@Unique
	private static final int NEARBY_PLAYER_RADIUS = PainScaleMod.CFG.getInt("Multiplayer.nearby_player_radius");

	@Inject(
		method = "performSpawning",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/entity/Mob;moveTo(DDDFF)V"
		),
		remap = false
	)
	private static void applyPainScaleAttributes(
		World world,
		SpawnerConfig spawnerConfig,
		CallbackInfoReturnable<Integer> cir,
		@Local Mob mobToSpawn
	) {
		if (!world.getGameRuleValue(DYNAMIC_DIFFICULTY)) return;

		double averagePlayerLevelNearby = calculateAverageNearbyLevel(world.players);

		if (mobToSpawn instanceof MobMonster) {
			if (world.getGameRuleValue(MP_NEARBY_PLAYER_AFFECTS)) {
				IPainScaleMobMonster mob = (IPainScaleMobMonster) mobToSpawn;
				mob.ps$setAttackPowerMultiplier(Math.max(PainScaleMod.start_attack_multiplier, PainScaleMod.base_attack_multiplier_per_level * averagePlayerLevelNearby));
				mob.ps$setHealthMultiplier(Math.max(PainScaleMod.start_health_multiplier, PainScaleMod.base_health_multiplier_per_level * averagePlayerLevelNearby));
				mob.ps$setDfLevel((int) averagePlayerLevelNearby);
			}
			// PainScaleMod.LOGGER.error(String.valueOf(mob.ps$getAttackPower()));
		}
	}

	@Unique
	private static double calculateAverageNearbyLevel(List<Player> players) {
		Set<Player> countedPlayers = new HashSet<>();

		if (players.size() == 1) {
			Player solo = players.get(0);
			if (solo instanceof IPainScalePlayer) {
				return ((IPainScalePlayer) solo).ps$getDifficultyLevel();
			}
			return 0.0;
		}

		for (Player player : players) {
			for (Player other : players) {
				if (player == other) continue;

				double dx = player.x - other.x;
				double dz = player.z - other.z;
				double distanceSquared = dx * dx + dz * dz;

				if (distanceSquared <= NEARBY_PLAYER_RADIUS * NEARBY_PLAYER_RADIUS) {
					countedPlayers.add(player);
					countedPlayers.add(other);
				}
			}
		}

		if (countedPlayers.isEmpty()) {
			countedPlayers.addAll(players);
		}

		int totalLevel = 0;
		int count = 0;

		for (Player p : countedPlayers) {
			if (p instanceof IPainScalePlayer) {
				totalLevel += ((IPainScalePlayer) p).ps$getDifficultyLevel();
				count++;
			}
		}

		return count > 0 ? (double) totalLevel / count : 0.0;
	}

}
