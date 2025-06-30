package deus.painscale.mobstuff.containers;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.IArmorItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.material.ArmorMaterial;
import net.minecraft.core.player.inventory.InventorySorter;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.DamageType;
import org.jetbrains.annotations.Nullable;


public class MobContainerInventory implements Container {
	public ItemStack[] armorInventory = new ItemStack[4];
	protected int currentItem = 0;
	private ItemStack heldItem;
	public boolean inventoryChanged = false;
	public ItemStack[] mainInventory = new ItemStack[36];
	public Mob mob;


	public MobContainerInventory(Mob mob) {
		this.mob = mob;
	}

	@Override
	public int getContainerSize() {
		return 10;
	}

	@Override
	public @Nullable ItemStack getItem(int index) {
		ItemStack[] aitemstack = this.mainInventory;
		if (index >= aitemstack.length) {
			index -= aitemstack.length;
			aitemstack = this.armorInventory;
		}

		return aitemstack[index];
	}

	@Override
	public @Nullable ItemStack removeItem(int index, int takeAmount) {
		ItemStack[] aitemstack = this.mainInventory;
		if (index >= this.mainInventory.length) {
			aitemstack = this.armorInventory;
			index -= this.mainInventory.length;
		}

		if (aitemstack[index] != null) {
			ItemStack itemstack1;
			if (aitemstack[index].stackSize <= takeAmount) {
				itemstack1 = aitemstack[index];
				aitemstack[index] = null;
				return itemstack1;
			} else {
				itemstack1 = aitemstack[index].splitStack(takeAmount);
				if (aitemstack[index].stackSize <= 0) {
					aitemstack[index] = null;
				}

				return itemstack1;
			}
		} else {
			return null;
		}
	}

	@Override

	public void setItem(int index, @Nullable ItemStack itemstack) {
		ItemStack[] aitemstack = this.mainInventory;
		if (index >= aitemstack.length) {
			index -= aitemstack.length;
			aitemstack = this.armorInventory;
		}

		aitemstack[index] = itemstack;
	}

	@Override
	public String getNameTranslationKey() {
		return "container.inventory.name";
	}


	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public void setChanged() {
		this.inventoryChanged = true;
	}


	@Override
	public boolean stillValid(Player player) {
		return false;
	}

	@Override
	public void sortContainer() {
		InventorySorter.sortInventory(this.mainInventory, 9, this.mainInventory.length - 1);
	}

	public ItemStack armorItemInSlot(int i) {
		return this.armorInventory[i];
	}

	public void damageArmor(int damage) {
		for(int j = 0; j < this.armorInventory.length; ++j) {
			if (this.armorInventory[j] != null && this.armorInventory[j].getItem() instanceof IArmorItem) {
				this.armorInventory[j].damageItem(damage, this.mob);
				if (this.armorInventory[j].stackSize <= 0) {
					this.armorInventory[j] = null;
				}
			}
		}

	}

	public float getTotalProtectionAmount(DamageType damageType) {
		float protectionPercentage = 0.0F;

		for(int i = 0; i < this.armorInventory.length; ++i) {
			ItemStack itemStack = this.armorInventory[i];
			if (itemStack != null && itemStack.getItem() instanceof IArmorItem) {
				IArmorItem armor = (IArmorItem)itemStack.getItem();
				if (armor.getArmorPiece() == i) {
					ArmorMaterial material = armor.getArmorMaterial();
					if (material != null) {
						protectionPercentage += material.getProtection(damageType) * armor.getArmorPieceProtectionPercentage();
					}
				}
			}
		}

		return protectionPercentage;
	}

	public ListTag writeToNBT(ListTag nbttaglist) {
		int j;
		CompoundTag nbttagcompound1;
		for(j = 0; j < this.mainInventory.length; ++j) {
			if (this.mainInventory[j] != null) {
				nbttagcompound1 = new CompoundTag();
				nbttagcompound1.putByte("Slot", (byte)j);
				this.mainInventory[j].writeToNBT(nbttagcompound1);
				nbttaglist.addTag(nbttagcompound1);
			}
		}

		for(j = 0; j < this.armorInventory.length; ++j) {
			if (this.armorInventory[j] != null) {
				nbttagcompound1 = new CompoundTag();
				nbttagcompound1.putByte("Slot", (byte)(j + 100));
				this.armorInventory[j].writeToNBT(nbttagcompound1);
				nbttaglist.addTag(nbttagcompound1);
			}
		}

		return nbttaglist;
	}
	public void readFromNBT(ListTag nbttaglist) {
		this.mainInventory = new ItemStack[36];
		this.armorInventory = new ItemStack[4];

		for(int i = 0; i < nbttaglist.tagCount(); ++i) {
			CompoundTag nbttagcompound = (CompoundTag)nbttaglist.tagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;
			ItemStack itemstack = ItemStack.readItemStackFromNbt(nbttagcompound);
			if (itemstack != null) {
				if (j >= 0 && j < this.mainInventory.length) {
					this.mainInventory[j] = itemstack;
				}

				if (j >= 100 && j < this.armorInventory.length + 100) {
					this.armorInventory[j - 100] = itemstack;
				}
			}
		}

	}}
