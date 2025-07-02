package deus.painscale.entity.mob_zombie_armored;

import deus.painscale.api.IPainScaleMobInventory;
import net.minecraft.client.render.entity.MobRendererBiped;
import net.minecraft.client.render.model.ModelBiped;
import net.minecraft.client.render.model.ModelZombie;
import net.minecraft.core.item.*;

public class MobRendererPainScaleZombieArmored extends MobRendererBiped<MobPainScaleZombieArmored> {

	private final ModelZombie modelArmorChestplate;
	private final ModelZombie modelArmor;

	public MobRendererPainScaleZombieArmored(ModelBiped model, float shadowSize) {
		super(model, shadowSize);
		this.modelBipedMain = model;
		this.modelArmorChestplate = new ModelZombie(1.0F);
		this.modelArmor = new ModelZombie(0.5F);
	}

	@Override
	public boolean prepareArmor(MobPainScaleZombieArmored entity, int layer, float partialTick) {
		ItemStack itemstack = ((IPainScaleMobInventory)entity).ps$getInv().armorItemInSlot(3 - layer);
		if (itemstack != null) {
			Item item = itemstack.getItem();
			IArmorItem armorItem;
			if (item instanceof IArmorItem && (armorItem = (IArmorItem)item).getArmorPiece() == 3 - layer) {
				ModelBiped modelBiped;

				if (item instanceof ItemQuiver) {
					return false;
				}

				if (item instanceof ItemQuiverEndless) {
					return false;
				}

				if (item == Items.ARMOR_BOOTS_ICESKATES) {
					return false;
				}

				if (armorItem.getArmorMaterial() != null) {
					this.bindTexture(String.format("/assets/%s/textures/armor/%s_%d.png", armorItem.getArmorMaterial().identifier.namespace(), armorItem.getArmorMaterial().identifier.value(), layer != 2 ? 1 : 2));
					modelBiped = layer != 2 ? this.modelArmorChestplate : this.modelArmor;
					modelBiped.head.visible = layer == 0;
					modelBiped.hair.visible = layer == 0;
					modelBiped.body.visible = layer == 1 || layer == 2;
					modelBiped.armRight.visible = layer == 1;
					modelBiped.armLeft.visible = layer == 1;
					modelBiped.legRight.visible = layer == 2 || layer == 3;
					modelBiped.legLeft.visible = layer == 2 || layer == 3;
					this.setArmorModel(modelBiped);
					return true;
				}
			}
		}

		return false;
	}
}
