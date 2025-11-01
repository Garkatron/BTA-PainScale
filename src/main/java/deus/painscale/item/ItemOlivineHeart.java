package deus.painscale.item;

import deus.painscale.PainScale;
import deus.painscale.api.IPainScalePlayer;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.world.World;
import sunsetsatellite.catalyst.CatalystEffects;
import sunsetsatellite.catalyst.effects.api.effect.Effect;
import sunsetsatellite.catalyst.effects.api.effect.Effects;
import sunsetsatellite.catalyst.effects.helper.HealthHelper;

import static deus.painscale.PainScale.isCatalystPresent;

public class ItemOlivineHeart extends Item {
	public ItemOlivineHeart(String name, String namespaceId, int id) {
		super(name, namespaceId, id);
	}

	@Override
	public ItemStack onUseItem(ItemStack itemstack, World world, Player player) {
		int extraHealth = HealthHelper.getExtraHealth(player);

		if (extraHealth >= 40) {
			return itemstack;
		}

		if (!itemstack.consumeItem(player)) return itemstack;

		if (isCatalystPresent()) {
			HealthHelper.addExtraHealth(player, 2);

			player.heal(player.getMaxHealth());

			PainScale.LOGGER.info("Applied catalyst effect");
			return itemstack;
		}
		else {
			((IPainScalePlayer) player).ps$setMaxHealth(player.getMaxHealth() + 2);
			PainScale.LOGGER.info("Applied PainScale extra health.");
		}

		player.sendMessage("Current health: " + player.getMaxHealth());
		return itemstack;
	}

}
