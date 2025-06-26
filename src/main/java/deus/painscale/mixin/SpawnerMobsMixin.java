package deus.painscale.mixin;

import deus.painscale.PainScaleMod;
import deus.painscale.api.IMobMonster;
import deus.painscale.api.ISpawnerMobs;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.SpawnListEntry;
import net.minecraft.core.enums.MobCategory;
import net.minecraft.core.world.SpawnerMobs;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.core.world.chunk.ChunkPosition;
import net.minecraft.core.world.config.spawning.SpawnerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(value = SpawnerMobs.class)
public class SpawnerMobsMixin implements ISpawnerMobs {

	@Inject(
		method = "performSpawning",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/entity/Mob;moveTo(DDDFF)V"
		),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private static void captureMobToSpawn(World world, SpawnerConfig spawnerConfig, CallbackInfoReturnable<Integer> cir, int totalSpawned, ChunkCoordinates spawnPoint, ChunkCoordinate[] spawnChunks, MobCategory[] var5, int var6, int var7, MobCategory creatureType, Iterator var9, ChunkCoordinate chunk, boolean checkSpawnDist, int blockX, int blockZ, Biome biome, List spawnableList, int totalSpawnRarity, SpawnListEntry spawnListEntry, int calculatedSpawnChance, ChunkPosition pos, int x, int y, int z, int countSpawned, byte range, int i, int ix, int iy, int iz, int spawnAttempt, double dx, double dy, double dz, Mob mobToSpawn, Exception e, double var38, double var40, double var42) {
		IMobMonster mob = (IMobMonster) mobToSpawn;
		mob.ps$increaseAttackPower((int) Math.ceil(mob.ps$getBaseAttackPower()*PainScaleMod.CFG.getDouble("base_attack_multiplier_per_level")));
	}


}
