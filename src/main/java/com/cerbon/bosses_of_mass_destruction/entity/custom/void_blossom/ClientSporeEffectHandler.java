package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityEventHandler;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import net.minecraft.world.phys.Vec3;

public class ClientSporeEffectHandler implements IEntityEventHandler {
    private final VoidBlossomEntity entity;
    private final EventScheduler eventScheduler;

    private final ClientParticleBuilder projectileParticles = new ClientParticleBuilder(BMDParticles.OBSIDILITH_BURST.get())
            .color(BMDColors.GREEN)
            .colorVariation(0.4)
            .scale(0.5f)
            .brightness(BMDParticles.FULL_BRIGHT);

    public ClientSporeEffectHandler(VoidBlossomEntity entity, EventScheduler eventScheduler) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == VoidBlossomAttacks.sporeAttack)
            eventScheduler.addEvent(new TimedEvent(this::spawnParticles, 25, 15, () -> false));

    }

    private void spawnParticles(){
        Vec3 pos = entity.getEyePosition().add(RandomUtils.randVec().scale(3.0)).subtract(entity.getForward().scale(2.0));
        Vec3 vel = VecUtils.yAxis.scale(0.1);
        projectileParticles.build(pos, vel);
    }
}
