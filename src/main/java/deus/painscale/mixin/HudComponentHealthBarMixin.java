package deus.painscale.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.client.gui.hud.component.HudComponentHealthBar;
import net.minecraft.client.gui.hud.component.HudComponentMovable;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.item.ItemBucketIceCream;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.gamemode.Gamemode;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;
import java.util.Random;

@Mixin(HudComponentHealthBar.class)
public class HudComponentHealthBarMixin extends HudComponentMovable {
	private final Random random = new Random();

	public HudComponentHealthBarMixin(String key, int xSize, int ySize, Layout layout) {
		super(key, xSize, ySize, layout);
	}

	public boolean isVisible(Minecraft mc) {
		return mc.playerController.canHurtPlayer() && !mc.thePlayer.getGamemode().isPlayerInvulnerable() && mc.gameSettings.immersiveMode.drawHotbar();
	}

	/**
	 * @author Garkatron
	 * @reason Draw more hearts
	 * @implNote The same method as the original but allow more hearts. Note: I used AI because I was too lazy to do the calculations myself.
	 */
	@Overwrite(remap = false)
	public void render(Minecraft mc, HudIngame hud, int xSizeScreen, int ySizeScreen, float partialTick) {
		int x = this.getLayout().getComponentX(mc, this, xSizeScreen);
		int y = this.getLayout().getComponentY(mc, this, ySizeScreen);
		GL11.glColor4f(1.0F, 0.0F, 1.0F, 1.0F);
		GL11.glDisable(3042);

		boolean heartsFlash = mc.thePlayer.heartsFlashTime / 3 % 2 == 1;
		if (mc.thePlayer.heartsFlashTime < 10) {
			heartsFlash = false;
		}

		int health = mc.thePlayer.getHealth();
		int maxHealth = mc.thePlayer.getMaxHealth();
		int prevHealth = mc.thePlayer.prevHealth;

		this.random.setSeed((long) hud.updateCounter * 312871L);
		boolean isHardcore = mc.thePlayer.getGamemode() == Gamemode.hardcore;

		// First pass: Draw containers for the first row
		for (int i = 0; i < Math.min(maxHealth, 10); ++i) {
			int xHeart = x + i * 8;
			int yHeart = y;

			if (health <= 4) {
				yHeart += this.random.nextInt(2);
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			hud.drawGuiIcon(xHeart, yHeart, 9, 9, !heartsFlash ?
				TextureRegistry.getTexture("minecraft:gui/hud/heart/container") :
				TextureRegistry.getTexture("minecraft:gui/hud/heart/container_blinking"));
		}

		// Second pass: Draw hearts for each row, overlapping on the first row's positions
		for (int i = 0; i < maxHealth; ++i) {
			int row = i / 10; // Current row (0 for first 10 hearts, 1 for next 10, etc.)
			int heartInRow = i % 10; // Position within the row (0 to 9)
			int xHeart = x + heartInRow * 8; // X position maps to first row's heart positions
			int yHeart = y; // Same Y position for all hearts

			if (health <= 4) {
				yHeart += this.random.nextInt(2);
			}

			// Flashing (prevHealth)
			if (heartsFlash) {
				if (i * 2 + 1 < prevHealth) {
					hud.drawGuiIcon(xHeart, yHeart, 9, 9, TextureRegistry.getTexture("minecraft:gui/hud/heart/" + (isHardcore ? "hardcore_" : "") + "full_blinking"));
				}

				if (i * 2 + 1 == prevHealth) {
					hud.drawGuiIcon(xHeart, yHeart, 9, 9, TextureRegistry.getTexture("minecraft:gui/hud/heart/" + (isHardcore ? "hardcore_" : "") + "half_blinking"));
				}
			}

			// Color based on each row
			float[] color = rngColor(row);
			GL11.glColor4f(color[0], color[1], color[2], 1.0F);

			// Current health
			if (i * 2 + 1 < health) {
				hud.drawGuiIcon(xHeart, yHeart, 9, 9, TextureRegistry.getTexture("painscale:gui/hud/heart/" + (isHardcore ? "hardcore_" : "") + "full"));
			}

			if (i * 2 + 1 == health) {
				hud.drawGuiIcon(xHeart, yHeart, 9, 9, TextureRegistry.getTexture("painscale:gui/hud/heart/" + (isHardcore ? "hardcore_" : "") + "half"));
			}

			// Preview healing
			if (mc.thePlayer.inventory.getCurrentItem() != null && (mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemFood || mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBucketIceCream) && (Boolean)mc.gameSettings.foodHealthRegenOverlay.value) {
				int healing;
				if (mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemFood) {
					healing = ((ItemFood)mc.thePlayer.inventory.getCurrentItem().getItem()).getHealAmount();
				} else {
					healing = ((ItemBucketIceCream)mc.thePlayer.inventory.getCurrentItem().getItem()).getHealAmount();
				}

				if (i * 2 + 1 >= health) {
					if (i * 2 + 1 == health) {
						hud.drawGuiIcon(xHeart, yHeart, 9, 9, TextureRegistry.getTexture("minecraft:gui/hud/heart/" + (isHardcore ? "hardcore_" : "") + "preview_half_right"));
					} else if (i * 2 + 1 < health + healing) {
						hud.drawGuiIcon(xHeart, yHeart, 9, 9, TextureRegistry.getTexture("minecraft:gui/hud/heart/" + (isHardcore ? "hardcore_" : "") + "preview_full"));
					} else if (i * 2 + 1 == health + healing) {
						hud.drawGuiIcon(xHeart, yHeart, 9, 9, TextureRegistry.getTexture("minecraft:gui/hud/heart/" + (isHardcore ? "hardcore_" : "") + "preview_half"));
					}
				}
			}

			// Reset color to white for next container or heart
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	private float[] rngColor(int row) {
		// Definir múltiples colores para la interpolación
		float[][] colors = {
			{1.0f, 0.0f, 0.0f}, // Rojo
			{0.0f, 0.0f, 1.0f},  // Azul
			{0.0f, 1.0f, 0.0f}, // Verde
			{1.0f, 1.0f, 0.0f} // Amarillo

		};

		// Normalizar la fila para que esté entre 0 y 1
		float t = Math.min((float) row / 10.0f, 1.0f);
		// Escalar t para mapear a los segmentos de color
		float segmentCount = colors.length - 1; // Número de segmentos entre colores
		float scaledT = t * segmentCount;
		int startIndex = (int) Math.floor(scaledT);
		if (startIndex >= segmentCount) {
			startIndex = (int) (segmentCount - 1); // Último color si t está en el límite
		}
		float localT = scaledT - startIndex; // Fracción dentro del segmento

		// Obtener los colores de inicio y fin para el segmento actual
		float[] startColor = colors[startIndex];
		float[] endColor = colors[(int) Math.min(startIndex + 1, segmentCount)];

		// Interpolación lineal
		float r = startColor[0] + localT * (endColor[0] - startColor[0]);
		float g = startColor[1] + localT * (endColor[1] - startColor[1]);
		float b = startColor[2] + localT * (endColor[2] - startColor[2]);

		return new float[]{r, g, b};
	}


	@Override
	public void renderPreview(Minecraft mc, Gui gui, Layout layout, int xSizeScreen, int ySizeScreen) {
		int x = layout.getComponentX(mc, this, xSizeScreen);
		int y = layout.getComponentY(mc, this, ySizeScreen);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(3042);
		int health = 11;

		for(int i = 0; i < 10; ++i) {
			int xHeart = x + i * 8;
			gui.drawGuiIcon(xHeart, y, 9, 9, TextureRegistry.getTexture("minecraft:gui/hud/heart/container"));
			if (i * 2 + 1 < health) {
				gui.drawGuiIcon(xHeart, y, 9, 9, TextureRegistry.getTexture("minecraft:gui/hud/heart/full"));
			}

			if (i * 2 + 1 == health) {
				gui.drawGuiIcon(xHeart, y, 9, 9, TextureRegistry.getTexture("minecraft:gui/hud/heart/half"));
			}
		}
	}
}
