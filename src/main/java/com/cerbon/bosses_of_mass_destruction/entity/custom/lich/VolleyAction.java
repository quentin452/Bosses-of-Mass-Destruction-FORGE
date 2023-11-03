package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.config.mob.LichConfig;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.util.ProjectileThrower;
import com.cerbon.bosses_of_mass_destruction.projectile.MagicMissileProjectile;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class VolleyAction implements IActionWithCooldown {
    public static final int missileThrowDelay = 46;
    public static final int missileThrowCooldown = 80;
    public static final int missileParticleSummonDelay = 16;

    private final LichEntity entity;
    private final LichConfig mobConfig;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;

    public VolleyAction(LichEntity entity, LichConfig mobConfig, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel) {
        this.entity = entity;
        this.mobConfig = mobConfig;
        this.eventScheduler = eventScheduler;
        this.shouldCancel = shouldCancel;
    }

    @Override
    public int perform() {
        LivingEntity target = entity.getTarget();
        if(!(target instanceof ServerPlayer)) return missileThrowCooldown;
        performVolley((ServerPlayer) target);
        return missileThrowCooldown;
    }

    public void performVolley(ServerPlayer target) {
        final Optional<MobEffect> missileMobEffect = Optional.ofNullable(ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.tryParse(mobConfig.missile.mobEffectId)));
        final int missileEffectDuration = mobConfig.missile.mobEffectDuration;
        final int missileEffectAmplifier = mobConfig.missile.mobEffectAmplifier;

        final Function<Vec3, ProjectileThrower> missileThrower = offset ->
                new ProjectileThrower(
                        () -> {
                            MagicMissileProjectile projectile = new MagicMissileProjectile(
                                    entity,
                                    entity.level()
                                    ,livingEntity -> missileMobEffect.ifPresent(effect -> livingEntity.addEffect(new MobEffectInstance(effect, missileEffectDuration, missileEffectAmplifier))),
                                    MinionAction.summonEntityType != null ? List.of(MinionAction.summonEntityType) : List.of());

                            MobUtils.setPos(projectile, MobUtils.eyePos(entity).add(offset));
                            return new ProjectileThrower.ProjectileData(projectile, 1.6f, 0f, 0.2);
                        });

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            Vec3 targetPos = target.getBoundingBox().getCenter();
                            for (Vec3 offset : getMissileLaunchOffsets(entity))
                                missileThrower.apply(offset).throwProjectile(targetPos.add(VecUtils.planeProject(offset, VecUtils.yAxis)));

                            BMDUtils.playSound(
                                    target.serverLevel(),
                                    entity.position(),
                                    BMDSounds.MISSILE_SHOOT.get(),
                                    SoundSource.HOSTILE,
                                    3.0f,
                                    64,
                                    null
                            );
                        },
                        missileThrowDelay,
                        1,
                        shouldCancel
                )
        );
    }

    public static List<Vec3> getMissileLaunchOffsets(Entity entity) {
        List<Vec3> offsets = new ArrayList<>();
        offsets.add(MathUtils.axisOffset(entity.getLookAngle(), VecUtils.yAxis.add(VecUtils.zAxis.multiply(2.0, 2.0, 2.0))));
        offsets.add(MathUtils.axisOffset(entity.getLookAngle(), VecUtils.yAxis.multiply(1.5, 1.5, 1.5).add(VecUtils.zAxis)));
        offsets.add(MathUtils.axisOffset(entity.getLookAngle(), VecUtils.yAxis.multiply(2.0, 2.0, 2.0)));
        offsets.add(MathUtils.axisOffset(entity.getLookAngle(), VecUtils.yAxis.multiply(1.5, 1.5, 1.5).add(VecUtils.negateServer(VecUtils.zAxis))));
        offsets.add(MathUtils.axisOffset(entity.getLookAngle(), VecUtils.yAxis.add(VecUtils.negateServer(VecUtils.zAxis).multiply(2.0, 2.0, 2.0))));
        return offsets;
    }
}
