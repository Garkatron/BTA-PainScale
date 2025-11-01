package deus.painscale.item;

import deus.painscale.PainScale;
import deus.painscale.api.IPainScalePlayer;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import sunsetsatellite.catalyst.CatalystEffects;
import sunsetsatellite.catalyst.effects.api.effect.Effect;
import sunsetsatellite.catalyst.effects.api.effect.Effects;
import sunsetsatellite.catalyst.effects.helper.HealthHelper;

import static deus.painscale.PainScale.isCatalystPresent;

public class ItemOlivineHeart extends ItemFood {
	public ItemOlivineHeart(String name, String namespaceId, int id, int healAmount, int ticksPerHeal, boolean favouriteWolfMeat, int maxStackSize) {
		super(name, namespaceId, id, healAmount, ticksPerHeal, favouriteWolfMeat, maxStackSize);
	}

	@Override
	public ItemStack onUseItem(ItemStack itemstack, World world, Player entityplayer) {
		ItemStack i = super.onUseItem(itemstack, world, entityplayer);

		if (isCatalystPresent()) {
			PainScale.LOGGER.info("Applied catalyst effect.");
			// int extraHealth = HealthHelper.getExtraHealth(entityplayer);

			// if (!itemstack.consumeItem(entityplayer)) return itemstack;
			HealthHelper.addExtraHealth(entityplayer, 1);
			// entityplayer.heal(entityplayer.getMaxHealth());

			return itemstack;

		} else {
			PainScale.LOGGER.info("Applied Painscale extra health");

			((IPainScalePlayer)entityplayer).ps$setMaxHealth(entityplayer.getMaxHealth() + 2);

		}

		entityplayer.sendMessage("Current health: " + entityplayer.getMaxHealth());



		return i;
	}
}
