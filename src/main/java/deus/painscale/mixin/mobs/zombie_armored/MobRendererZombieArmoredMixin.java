package deus.painscale.mixin.mobs.zombie_armored;

import deus.painscale.api.IPainScaleMobZombieArmored;
import net.minecraft.client.render.entity.MobRendererBiped;
import net.minecraft.client.render.entity.MobRendererZombieArmored;
import net.minecraft.client.render.model.ModelBiped;
import net.minecraft.client.render.model.ModelZombie;
import net.minecraft.core.entity.monster.MobZombieArmored;
import net.minecraft.core.item.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = MobRendererZombieArmored.class)
public class MobRendererZombieArmoredMixin extends MobRendererBiped<MobZombieArmored>  {

	@Final
	@Shadow(remap = false)
	private ModelZombie modelArmorChestplate;

	@Final
	@Shadow(remap = false)
	private ModelZombie modelArmor;

	public MobRendererZombieArmoredMixin(ModelBiped model, float shadowSize) {
		super(model, shadowSize);
	}

	@Shadow(remap = false)
	private void hideArmorPiece(int piece) {}


	/**
	 * @author Garkatron
	 * @reason Implement armors
	 */
	@Overwrite(remap = false)
	public boolean prepareArmor(MobZombieArmored entity, int layer, float partialTick) {

		ItemStack itemstack = ((IPainScaleMobZombieArmored)entity).ps$getInv().armorItemInSlot(3 - layer);
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
