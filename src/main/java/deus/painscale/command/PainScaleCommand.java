package deus.painscale.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import deus.painscale.api.IPainScalePlayer;
import deus.painscale.newsystem.Factor;
import deus.painscale.newsystem.Level;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.helpers.EntitySelector;

import java.util.List;

public class PainScaleCommand implements CommandManager.CommandRegistry {

	private static final List<String> FACTORS = List.of("melee", "survival", "distance");
	private static final List<String> TYPES = List.of("level", "points");
	private static final List<String> ACTIONS_WITH_AMOUNT = List.of("add", "sub");
	private static final List<String> ACTIONS_NO_AMOUNT = List.of("reset", "get");

	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> dispatcher) {

		ArgumentBuilderLiteral<Object> pain = ArgumentBuilderLiteral.literal("pain")
			.requires(source -> ((CommandSource) source).hasAdmin());

		ACTIONS_WITH_AMOUNT.forEach(action -> pain.then(
			ArgumentBuilderLiteral.literal(action)
				.then(
					ArgumentBuilderRequired.argument("amount", ArgumentTypeInteger.integer())
						.then(
							ArgumentBuilderRequired.argument("type", ArgumentStringEnum.stringEnum(TYPES))
								.then(
									ArgumentBuilderRequired.argument("factor", ArgumentStringEnum.stringEnum(FACTORS))
										.then(
											ArgumentBuilderRequired.argument("target", ArgumentTypeEntity.entities())
												.executes(c -> execute(c, action))
										)
								)
						)
				)
		));

		ACTIONS_NO_AMOUNT.forEach(action -> pain.then(
			ArgumentBuilderLiteral.literal(action)
				.then(
					ArgumentBuilderRequired.argument("type", ArgumentStringEnum.stringEnum(TYPES))
						.then(
							ArgumentBuilderRequired.argument("factor", ArgumentStringEnum.stringEnum(FACTORS))
								.then(
									ArgumentBuilderRequired.argument("target", ArgumentTypeEntity.entities())
										.executes(c -> execute(c, action))
								)
						)
				)
		));

		dispatcher.register((ArgumentBuilderLiteral<CommandSource>) (Object) pain);
	}

	private int execute(com.mojang.brigadier.context.CommandContext<Object> c, String action) throws CommandSyntaxException {
		int amount = ACTIONS_NO_AMOUNT.contains(action) ? 0 : ArgumentTypeInteger.getInteger(c, "amount");
		List<? extends Entity> targets = c.getArgument("target", EntitySelector.class).get((CommandSource) c.getSource());
		String factor = ArgumentStringEnum.getString(c, "factor");
		String type = ArgumentStringEnum.getString(c, "type");
		boolean isLevel = type.equals("level");

		for (Entity entity : targets) {
			if (entity instanceof Player player && entity instanceof IPainScalePlayer painplayer) {
				Level level = applyAction(painplayer, factor, action, isLevel, amount);
				player.sendMessage(action + " " + (ACTIONS_WITH_AMOUNT.contains(action) ? amount + " " : "") + type + " of " + factor + " executed");
				if (level != null) player.sendMessage(": " + level);
			}
		}

		return targets.size();
	}


	private Level applyAction(IPainScalePlayer player, String factor, String action, boolean isLevel, int amount) {
		Factor difficultyPart = switch (factor) {
			case "melee" -> player.ps$getPlayerDifficulty().melee;
			case "distance" -> player.ps$getPlayerDifficulty().distance;
			case "survival" -> player.ps$getPlayerDifficulty().survival;
			default -> null;
		};

		if (difficultyPart == null) return null;

		if (isLevel) {
			switch (action) {
				case "add" -> difficultyPart.getLevel().addLevel(amount);
				case "sub" -> difficultyPart.getLevel().subLevel(amount);
				case "reset" -> difficultyPart.getLevel().resetLevel();
				case "get" -> { return difficultyPart.getLevel(); }
			}
		} else {
			switch (action) {
				case "add" -> difficultyPart.getLevel().addPoints(amount);
				case "sub" -> difficultyPart.getLevel().subPoints(amount);
				case "reset" -> difficultyPart.getLevel().resetPoints();
				case "get" -> { return difficultyPart.getLevel(); }
			}
		}

		return null;
	}
}
