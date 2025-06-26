package deus.painscale.mixin;

import deus.painscale.api.IMobMonster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(net.minecraft.core.entity.monster.MobMonster.class)
public class MobMonster implements IMobMonster {
	@Shadow
	protected int attackStrength;

	@Override
	public void ps$increaseAttackPower(int amount) {
		this.attackStrength += amount;
	}

	@Override
	public int ps$getBaseAttackPower() {
		return this.attackStrength;
	}
}
