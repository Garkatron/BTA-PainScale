package deus.painscale.mixin;

import deus.painscale.entity.mob_skeleton_armored.MobRendererSkeletonArmored;
import deus.painscale.entity.mob_skeleton_armored.MobSkeletonArmored;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.MobRendererZombieArmored;
import net.minecraft.client.render.model.ModelSkeleton;
import net.minecraft.client.render.model.ModelZombie;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turniplabs.halplibe.helper.ModelHelper;

import java.util.Map;

@Mixin(value = EntityRenderDispatcher.class, remap = false)
public interface IAEntityDispatcher {

	@Accessor("renderers")
	Map<Class<?>, EntityRenderer<?>> getRenderers();

}
