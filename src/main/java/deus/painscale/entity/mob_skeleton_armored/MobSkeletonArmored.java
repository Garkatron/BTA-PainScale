package deus.painscale.entity.mob_skeleton_armored;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import deus.painscale.api.IPainScaleMobInventory;
import deus.painscale.api.IPainScaleMobMonster;
import deus.painscale.mechanics.ArmorSets;
import deus.painscale.mobstuff.containers.MobContainerInventory;
import net.minecraft.core.entity.monster.MobSkeleton;
import net.minecraft.core.item.IArmorItem;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemArmor;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MobSkeletonArmored extends MobSkeleton implements IPainScaleMobInventory {

	public MobContainerInventory inventory;

	public MobSkeletonArmored(World world) {
		super(world);
		inventory = new MobContainerInventory(this);
	}

	@Override
	public void spawnInit() {
		IPainScaleMobMonster z2 = (IPainScaleMobMonster) this;

		List<IArmorItem> set = ArmorSets.getRandomArmorSet(z2.ps$getDfLevel());

		for (IArmorItem armor : set) {
			if (armor == null) continue;
			Item item = armor.asItem();
			int slot = ((ItemArmor) item).getArmorPiece();
			inventory.armorInventory[slot] = new ItemStack((Item) armor);
		}
	}

	@Override
	public void addAdditionalSaveData(@NotNull CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.put("Inventory", this.inventory.writeToNBT(new ListTag()));
	}

	@Override
	public void readAdditionalSaveData(@NotNull CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		ListTag nbttaglist = tag.getList("Inventory");
		this.inventory.readFromNBT(nbttaglist);
	}

	@Override
	public MobContainerInventory ps$getInv() {
		return inventory;
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
