package deus.painscale.mixin.mobs.zombie_armored;

import deus.painscale.mechanics.ArmorSets;
import deus.painscale.api.IPainScaleMobMonster;
import deus.painscale.api.IPainScaleMobZombieArmored;
import deus.painscale.mobstuff.containers.MobContainerInventory;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.monster.MobZombie;
import net.minecraft.core.entity.monster.MobZombieArmored;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.*;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = MobZombieArmored.class)
public class MobZombieArmoredMixin extends MobZombie implements IPainScaleMobZombieArmored {

	@Unique
	public MobContainerInventory inventory;

	public MobZombieArmoredMixin(World world) {
		super(world);
	}


	@Inject(method = "<init>", at = @At("TAIL"), remap = false)
	public void afterInit(World world, CallbackInfo ci) {
		inventory = new MobContainerInventory((MobZombieArmored) (Object) this);
	}

	@Inject(method = "spawnInit", at = @At("TAIL"), remap = false)
	public void afterSpawnInit(CallbackInfo ci) {
		IPainScaleMobZombieArmored z = (IPainScaleMobZombieArmored) (Object) this;
		IPainScaleMobMonster z2 = (IPainScaleMobMonster) (Object) z;

		List<IArmorItem> set = ArmorSets.getRandomArmorSet(z2.ps$getDfLevel());

//		System.out.println("Spawn level: " + lvl + " | Selected armor set: " +
//			set.stream()
//				.map(a -> a == null ? "null" : a.asItem().getStatName())
//				.toList());

		for (IArmorItem armor : set) {
			if (armor == null) continue;
			Item item = armor.asItem();
			int slot = ((ItemArmor) item).getArmorPiece();
			inventory.armorInventory[slot] = new ItemStack((Item) armor);
		}
	}

	@Override
	public MobContainerInventory ps$getInv() {
		return inventory;
	}

	/**
	 * @author Garkatron
	 * @reason Break armor
	 */
	@Overwrite(remap = false)
	public boolean hurt(Entity attacker, int i, DamageType type) {
		int lastHealth = this.getHealth();
		boolean result = super.hurt(attacker, i, type);
		if (attacker instanceof Player) {
			if (inventory.armorInventory[0]!=null) {
				//((Player)attacker).sendMessage("tesT: " + inventory.armorInventory[0].getItemDamageForDisplay());
			}
		}
		// damageEntity(i, type);
		return false;
	}

	@Override
	protected void damageEntity(int damage, DamageType damageType) {
		float protection = 1.0F - this.inventory.getTotalProtectionAmount(damageType);
		protection = Math.max(protection, 0.01F);
		double d = (double)((float)damage * protection);
		int newDamage = (int)((double)this.random.nextFloat() > 0.5 ? Math.floor(d) : Math.ceil(d));
		int preventedDamage = damage - newDamage;
		if (damageType != null && damageType.shouldDamageArmor()) {
			int armorDamage = (int)Math.ceil((double)preventedDamage / 4.0);
			this.inventory.damageArmor(armorDamage);
		}

		super.damageEntity(newDamage, damageType);
	}


}
