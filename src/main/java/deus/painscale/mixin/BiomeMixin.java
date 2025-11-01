package deus.painscale.mixin;

import deus.painscale.PainScale;
import deus.painscale.entity.mob_skeleton_armored.MobSkeletonArmored;
import deus.painscale.entity.mob_zombie_armored.MobPainScaleZombieArmored;
import net.minecraft.core.entity.SpawnListEntry;
import net.minecraft.core.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = Biome.class, remap = false)
public abstract class BiomeMixin {

	@Shadow(remap = false)
	protected List<SpawnListEntry> spawnableMonsterList;

	@Inject(method = "<init>", at = @At("TAIL"), remap = false)
	private void onConstructed(String key, CallbackInfo ci) {
		if (PainScale.CFG.getBoolean("Enemies.enable_armored_skeletons")) {
			this.spawnableMonsterList.add(new SpawnListEntry(MobSkeletonArmored.class, 6));
		}
		this.spawnableMonsterList.add(new SpawnListEntry(MobPainScaleZombieArmored.class, 4));
		this.spawnableMonsterList.remove(3); // Bye Bye Mob Zombie Armored :) 😈
	}
}
