package ltd.icecold.orangeengine.utils.math;

import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface IExtraDamage {
	float getBonusDamage(LivingEntity attacker, LivingEntity target, float arg);
}