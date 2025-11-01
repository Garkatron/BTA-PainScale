package deus.painscale.gui;

import deus.painscale.PainScale;
import deus.painscale.api.IPainScalePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.HudComponentMovable;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutSnap;
import org.lwjgl.opengl.GL11;

import java.awt.*;


public class DifficultyMeter extends HudComponentMovable {
	private final int width = 26;
	private final int height = 16;

	private long previousTime = 0;
	private int lastDifficulty = -1;

	public DifficultyMeter(String key, int xSize, int ySize, LayoutSnap layout) {
		super(key, xSize, ySize, layout);
		previousTime = System.currentTimeMillis();

	}

	@Override
	public boolean isVisible(Minecraft mc) {
		return PainScale.OPTIONS.get_show_level().value && mc.thePlayer != null;
	}


	private void renderBar(Minecraft mc, Gui gui, int x, int y, float amount, float partialTick) {
		amount = Math.max(0, Math.min(amount, 1));

		String text = (int)(amount * 100) + "l";

		int textWidth = mc.font.getStringWidth(text);
		int textHeight = mc.font.fontHeight;

		int textX = x + (width - textWidth) / 2;
		int textY = y + (height - textHeight) / 2;

		gui.drawStringNoShadow(mc.font, text, textX, textY, Color.WHITE.getRGB());
	}


	@Override
	public int getAnchorY(ComponentAnchor anchor) {
		return this.isVisible(Minecraft.getMinecraft()) ? super.getAnchorY(anchor) : 0;
	}

	@Override
	public void render(Minecraft mc, HudIngame gui, int xSizeScreen, int ySizeScreen, float partialTick) {
		if (mc.thePlayer == null) {
			return;
		}

		long currentTime = System.currentTimeMillis();
		long elapsed = currentTime - previousTime;

		IPainScalePlayer player = (IPainScalePlayer) mc.thePlayer;
		int difficulty = player.ps$getDifficultyLevel();

		if (difficulty != lastDifficulty) {
			previousTime = System.currentTimeMillis();
			lastDifficulty = difficulty;
			elapsed = 0;
		}

		if (elapsed > PainScale.OPTIONS.get_ms_delay_level().value) {
			return;
		}

		int x = this.getLayout().getComponentX(mc, this, xSizeScreen);
		int y = this.getLayout().getComponentY(mc, this, ySizeScreen);

		renderBar(mc, gui, x + 2, y, difficulty / 100f, partialTick);
	}


	@Override
	public void renderPreview(Minecraft mc, Gui gui, Layout layout, int xSizeScreen, int ySizeScreen) {
		int x = layout.getComponentX(mc, this, xSizeScreen);
		int y = layout.getComponentY(mc, this, ySizeScreen);
		renderBar(mc, gui, x, y, 1.0f, 0.0f);
	}

	private void setColor(Color color) {
		GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
	}

	public Color lerpColor(Color a, Color b, float t) {
		return new Color(
			(int) (a.getRed() + (b.getRed() - a.getRed()) * t),
			(int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
			(int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t),
			(int) (a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t));
	}

	public float lerp(float a, float b, float t) {
		return a + (b - a) * t;
	}
}
