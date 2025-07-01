package deus.painscale.item;

import deus.painscale.api.IPainScalePlayer;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;

public class ItemOlivineHeart extends ItemFood {
	public ItemOlivineHeart(String name, String namespaceId, int id, int healAmount, int ticksPerHeal, boolean favouriteWolfMeat, int maxStackSize) {
		super(name, namespaceId, id, healAmount, ticksPerHeal, favouriteWolfMeat, maxStackSize);
	}

	@Override
	public ItemStack onUseItem(ItemStack itemstack, World world, Player entityplayer) {
		ItemStack i = super.onUseItem(itemstack, world, entityplayer);
		((IPainScalePlayer)entityplayer).ps$setMaxHealth(entityplayer.getMaxHealth() + 2);
		entityplayer.sendMessage("Current health: " + entityplayer.getMaxHealth());
		i.consumeItem(entityplayer);
		return i;
	}
}
