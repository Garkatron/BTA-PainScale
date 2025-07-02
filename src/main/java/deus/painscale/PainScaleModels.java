package deus.painscale;

import deus.painscale.entity.mob_skeleton_armored.MobRendererSkeletonArmored;
import deus.painscale.entity.mob_skeleton_armored.MobSkeletonArmored;
import deus.painscale.entity.mob_zombie_armored.MobPainScaleZombieArmored;
import deus.painscale.entity.mob_zombie_armored.MobRendererPainScaleZombieArmored;
import deus.painscale.item.PainScaleItems;
import deus.painscale.mixin.IAEntityDispatcher;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.model.ModelSkeleton;
import net.minecraft.client.render.model.ModelZombie;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.entity.Entity;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.ModelHelper;
import turniplabs.halplibe.util.ModelEntrypoint;

import static deus.painscale.PainScaleMod.MOD_ID;

public class PainScaleModels implements ModelEntrypoint {
	@Override
	public void initBlockModels(BlockModelDispatcher blockModelDispatcher) {

	}

	@Override
	public void initItemModels(ItemModelDispatcher itemModelDispatcher) {
		ModelHelper.setItemModel(PainScaleItems.OLIVINE_HEART,
			() -> {
				ItemModelStandard model = new ItemModelStandard(PainScaleItems.OLIVINE_HEART, MOD_ID);
				model.icon = TextureRegistry.getTexture(PainScaleItems.OLIVINE_HEART.namespaceID);
				return model;
			});
//		ModelHelper.setItemModel(PainScaleItems.ITEM_LVL_DOWN,
//			() -> {
//				ItemModelStandard model = new ItemModelStandard(PainScaleItems.ITEM_LVL_DOWN, MOD_ID);
//				model.icon = TextureRegistry.getTexture(PainScaleItems.ITEM_LVL_DOWN.namespaceID);
//				return model;
//			});
	}
	@Override
	public void initEntityModels(EntityRenderDispatcher dispatcher) {
		addEntityModel(dispatcher, MobSkeletonArmored.class, new MobRendererSkeletonArmored(new ModelSkeleton(), 0.5F));
		addEntityModel(dispatcher, MobPainScaleZombieArmored.class, new MobRendererPainScaleZombieArmored(new ModelZombie(), 0.5F));
	}

	public void addEntityModel(EntityRenderDispatcher dispatcher, @NotNull Class<? extends Entity> clazz, EntityRenderer<?> renderer){
		renderer.init(dispatcher);
		((IAEntityDispatcher)dispatcher).getRenderers().put(clazz, renderer);
	}

	@Override
	public void initTileEntityModels(TileEntityRenderDispatcher tileEntityRenderDispatcher) {

	}

	@Override
	public void initBlockColors(BlockColorDispatcher blockColorDispatcher) {

	}
}
