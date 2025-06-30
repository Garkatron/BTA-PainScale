package deus.painscale.mixin.mobs.mob;

import net.minecraft.core.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Mob.class, remap = false)
public class MobMixin {
//	@Inject(method = "onDeath", at = @At("TAIL"), remap = false)
//	public void addPointsOnDeath(Entity entityKilledBy, CallbackInfo ci) {
//		if (entityKilledBy instanceof Player) {
//
//		}
//		System.out.println("KILLED BY: " + entityKilledBy);
//	}
}
