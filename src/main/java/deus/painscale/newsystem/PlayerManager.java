package deus.painscale.newsystem;

import deus.painscale.PainScale;
import deus.painscale.util.Signal;
import org.spongepowered.asm.mixin.Unique;

public class PlayerManager extends HierarchicalFactorManager {

	public final Factor melee;
	public final Factor distance;
	public final Factor survival;

	public final Factor attackFactor;
	public final Factor global;

	public PlayerManager() {
		super(DifficultySystem.worldManager);

		melee = createFactor("player.melee", 100, 1, 50, 1);
		distance = createFactor("player.distance", 100, 1, 50, 1);
		survival = createFactor("player.survival", 100, 1, 50, 1);

		this.register(melee);
		this.register(distance);
		this.register(survival);

		attackFactor = createFactor("player.attackFactor", 100, 1, 100, 1);
		attackFactor.addDependency(melee);
		attackFactor.addDependency(distance);

		global = createFactor("player.global", 100, 1, 100, 1);
		global.addDependency(attackFactor);
		global.addDependency(survival);

		DifficultySystem.worldDifficulty.addDependency(global);

		this.register(attackFactor);
		this.register(global);



		DifficultySystem.worldDifficulty.<Level>getEvent("changed").connect((s, v)->{
			PainScale.LOGGER.error(v.toString());
		});
	}
}
