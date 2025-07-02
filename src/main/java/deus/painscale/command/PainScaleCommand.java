package deus.painscale.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import deus.painscale.api.IPainScaleMobMonster;
import deus.painscale.api.IPainScalePlayer;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.monster.MobMonster;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntitySummon;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;

import java.util.List;

public class PainScaleCommand implements CommandManager.CommandRegistry {

	@Override
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			(ArgumentBuilderLiteral<CommandSource>) ArgumentBuilderLiteral.literal("painscale")
				.requires(source -> ((CommandSource) source).hasAdmin())

				.then((ArgumentBuilderLiteral) buildModifier("add", true))
				.then((ArgumentBuilderLiteral) buildModifier("sub", false))
				.then((ArgumentBuilderLiteral) buildSet())
				.then((ArgumentBuilderLiteral) buildReset())
				.then((ArgumentBuilderLiteral) buildGet())

				.then(
					ArgumentBuilderLiteral.literal("summon")
						.requires(source -> ((CommandSource) source).hasAdmin())
						.then(
							ArgumentBuilderRequired.argument("entity", ArgumentTypeEntitySummon.entity()).then(
								ArgumentBuilderRequired.argument("level", ArgumentTypeInteger.integer())
									.executes(c -> {
										CommandSource source = (CommandSource) c.getSource();
										Vec3 coordinates = source.getCoordinates(false);

										if (source.getSender() != null && coordinates != null) {
											Entity entity = summonEntityAt(
												c,
												coordinates.x,
												coordinates.y - (double) source.getSender().heightOffset,
												coordinates.z,
												0.0F,
												0.0F
											);
											if (entity instanceof MobMonster) {
												int level = c.getArgument("level", Integer.class);
												((IPainScaleMobMonster) entity).ps$setDfLevel(level);
												int result = ((IPainScaleMobMonster) entity).ps$getDfLevel();
												((CommandSource) c.getSource()).sendMessage("Spawned with level: " + result);

											}
											source.sendTranslatableMessage(
												"command.commands.summon.success_single_entity",
												CommandHelper.getEntityName(entity)
											);

											return 1;
										} else {
											throw CommandExceptions.notInWorld().create();
										}
									})
							)
						)
				)
		);
	}

	private ArgumentBuilderLiteral<CommandSource> buildModifier(String literal, boolean isAdd) {
		return (ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal(literal)
			.then(
				ArgumentBuilderLiteral.literal("levels")
					.then(
						ArgumentBuilderRequired.argument("targets", ArgumentTypeEntity.entities())
							.then(
								ArgumentBuilderRequired.argument("amount", ArgumentTypeInteger.integer())
									.executes(c -> {
										List<? extends Entity> entities = c.getArgument("targets", EntitySelector.class).get((CommandSource) c.getSource());
										int amount = c.getArgument("amount", Integer.class);
										int count = 0;
										CommandSource commandSource = (CommandSource) c.getSource();

										for (Entity entity : entities) {
											if (entity instanceof Player) {
												Player player = (Player) entity;
												if (isAdd) {
													((IPainScalePlayer) player).ps$addLevels(amount);
													commandSource.sendMessage("Now your level is " + ((IPainScalePlayer) player).ps$getDifficultyLevel());
												} else {
													((IPainScalePlayer) player).ps$subLevels(amount);
													commandSource.sendMessage("Now your level is " + ((IPainScalePlayer) player).ps$getDifficultyLevel());
												}
												count++;
											}
										}
										return count;
									})
							)
					)
			)
			.then(
				ArgumentBuilderLiteral.literal("points")
					.then(
						ArgumentBuilderRequired.argument("targets", ArgumentTypeEntity.entities())
							.then(
								ArgumentBuilderRequired.argument("amount", ArgumentTypeInteger.integer())
									.executes(c -> {
										List<? extends Entity> entities = c.getArgument("targets", EntitySelector.class).get((CommandSource) c.getSource());
										int amount = c.getArgument("amount", Integer.class);
										int count = 0;
										CommandSource commandSource = (CommandSource) c.getSource();

										for (Entity entity : entities) {
											if (entity instanceof Player) {
												Player player = (Player) entity;
												if (isAdd) {
													((IPainScalePlayer) player).ps$addPoints(amount);
													commandSource.sendMessage("Added " + amount + " points");
												} else {
													((IPainScalePlayer) player).ps$subPoints(amount);
													commandSource.sendMessage("Removed " + amount + " points");
												}
												count++;
											}
										}
										return count;
									})
							)
					)
			);
	}

	private ArgumentBuilderLiteral<CommandSource> buildGet() {
		return (ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("get")
			.then(
				ArgumentBuilderRequired.argument("targets", ArgumentTypeEntity.entities())
					.executes(c -> {
						List<? extends Entity> entities = c.getArgument("targets", EntitySelector.class).get((CommandSource) c.getSource());
						int count = 0;

						for (Entity entity : entities) {
							if (entity instanceof Player) {
								Player player = (Player) entity;
								IPainScalePlayer painPlayer = (IPainScalePlayer) player;

								int level = painPlayer.ps$getDifficultyLevel();
								int points = painPlayer.ps$getDifficultyPoints();
								int remainingPoints = painPlayer.ps$getRemainingPoints();

								CommandSource commandSource = (CommandSource) c.getSource();
								commandSource.sendMessage("[PainScale]");
								commandSource.sendMessage("> Level: " + level);
								commandSource.sendMessage("> Points: " + points);
								commandSource.sendMessage("> Remaining: " + remainingPoints);
								commandSource.sendMessage("> Health: " + ((Player) entity).getMaxHealth());
								count++;
							}
						}
						return count;
					})
			);
	}

	private static Entity summonEntityAt(CommandContext<Object> c, double x, double y, double z, float yaw, float pitch) {
		Class<? extends Entity> entityClass = (Class)c.getArgument("entity", Class.class);

		Entity entity;
		try {
			entity = (Entity)entityClass.getConstructor(World.class).newInstance(((CommandSource)c.getSource()).getWorld());
		} catch (Exception var12) {
			Exception e = var12;
			throw new RuntimeException(e);
		}

		entity.spawnInit();
		entity.moveTo(x, y, z, yaw, pitch);
		((CommandSource)c.getSource()).getWorld().entityJoinedWorld(entity);
		return entity;
	}

	private ArgumentBuilderLiteral<CommandSource> buildSet() {
		return (ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("set")
			.then(
				ArgumentBuilderLiteral.literal("level")
					.then(
						ArgumentBuilderRequired.argument("targets", ArgumentTypeEntity.entities())
							.then(
								ArgumentBuilderRequired.argument("levels", ArgumentTypeInteger.integer())
									.executes(c -> {
										CommandSource source = (CommandSource) c.getSource();

										List<? extends Entity> entities = c.getArgument("targets", EntitySelector.class).get((CommandSource) c.getSource());
										int amount = c.getArgument("levels", Integer.class);

										for (Entity entity : entities) {
											if (entity instanceof Player) {
												IPainScalePlayer player = (IPainScalePlayer) entity;
												player.ps$setDifficultyLevel(amount);
												player.ps$resetPoints();
												source.sendMessage("Pain Scale levels: " + ((IPainScalePlayer) entity).ps$getDifficultyLevel());
											}
										}

										return 1;
									})
							)
					)
			);
	}


	private ArgumentBuilderLiteral<CommandSource> buildReset() {
		return (ArgumentBuilderLiteral) ArgumentBuilderLiteral.literal("reset")
			.then(
				ArgumentBuilderRequired.argument("targets", ArgumentTypeEntity.entities())
					.executes(c -> {
						List<? extends Entity> entities = c.getArgument("targets", EntitySelector.class).get((CommandSource) c.getSource());
						CommandSource source = (CommandSource) c.getSource();
						int count = 0;

						for (Entity entity : entities) {
							if (entity instanceof Player) {
								IPainScalePlayer player = (IPainScalePlayer) entity;
								player.ps$setDifficultyLevel(0);
								player.ps$resetPoints();
								player.ps$resetLevels();
								player.ps$resetMultiplier();
								player.ps$setMaxHealth(20);
								source.sendMessage("Pain Scale reset: " + ((Player) entity).getDisplayName());
								count++;
							}
						}
						return count;
					})
			);
	}

}
