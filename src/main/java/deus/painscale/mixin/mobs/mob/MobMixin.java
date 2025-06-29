package deus.painscale.mixin.mobs.mob;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Mob.class)
public class MobMixin {
	@Inject(method = "onDeath", at = @At("TAIL"), remap = false)
	public void addPointsOnDeath(Entity entityKilledBy, CallbackInfo ci) {
		System.out.println(entityKilledBy);
	}
}
