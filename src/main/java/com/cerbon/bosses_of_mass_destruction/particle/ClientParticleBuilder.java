package com.cerbon.bosses_of_mass_destruction.particle;

import com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities.RandomUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;
import java.util.function.Supplier;

public class ClientParticleBuilder {
    private ParticleOptions options;
    private Function<SimpleParticle, Vec3> getVel = null;
    private Function<SimpleParticle, Vec3> continuousPos = null;
    private Function<Float, Vec3> color = null;
    private Function<Float, Integer> brightness = null;
    private Function<Float, Float> scale = null;
    private Supplier<Integer> age = null;
    private Double colorVariation = null;
    private Function<SimpleParticle, Float> getRotation = null;

    public ClientParticleBuilder(ParticleOptions options){
        this.options = options;
    }

    public ClientParticleBuilder continuousRotation(Function<SimpleParticle, Float> rotation) {
        this.getRotation = rotation;
        return this;
    }

    public ClientParticleBuilder continuousVelocity(Function<SimpleParticle, Vec3> velocity) {
        this.getVel = velocity;
        return this;
    }

    public ClientParticleBuilder continuousPosition(Function<SimpleParticle, Vec3> positionProvider) {
        this.continuousPos = positionProvider;
        return this;
    }

    public ClientParticleBuilder color(Function<Float, Vec3> color) {
        this.color = color;
        return this;
    }

    public ClientParticleBuilder color(Vec3 color) {
        this.color = f -> color;
        return this;
    }

    public ClientParticleBuilder rotation(Float rotation) {
        this.getRotation = p -> rotation;
        return this;
    }

    public ClientParticleBuilder brightness(Integer brightness) {
        this.brightness = f -> brightness;
        return this;
    }

    public ClientParticleBuilder scale(Function<Float, Float> scale) {
        this.scale = scale;
        return this;
    }

    public ClientParticleBuilder scale(Float scale) {
        this.scale = f -> scale;
        return this;
    }

    public ClientParticleBuilder age(Supplier<Integer> age) {
        this.age = age;
        return this;
    }

    public ClientParticleBuilder age(Integer age) {
        this.age = () -> age;
        return this;
    }

    public ClientParticleBuilder age(Integer min, Integer max) {
        this.age = () -> RandomUtils.range(min, max);
        return this;
    }

    public ClientParticleBuilder colorVariation(Double variation) {
        this.colorVariation = variation;
        return this;
    }

    public void build(Vec3 pos, Vec3 vel) {
        Minecraft client = Minecraft.getInstance();
        Camera camera = client.gameRenderer.getMainCamera();

        if (camera.isInitialized()) {
            Particle particle = client.particleEngine.createParticle(options, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
            if (particle == null) {
                return;
            }

            if (scale != null) {
                particle.scale(scale.apply(0f));
            }
            if (color != null) {
                Vec3 clr = color.apply(0f);
                particle.setColor((float) clr.x, (float) clr.y, (float) clr.z);
            }
            if (age != null) {
                particle.setLifetime(age.get());
            }

            if (particle instanceof SimpleParticle simpleParticle) {
                if (brightness != null) {
                    simpleParticle.setBrightnessOverride(brightness);
                }
                if (color != null) {
                    simpleParticle.setColorOverride(color);
                }
                if (scale != null) {
                    simpleParticle.setScaleOverride(scale);
                }
                if (getVel != null) {
                    simpleParticle.setVelocityOverride(getVel);
                }
                if (continuousPos != null) {
                    simpleParticle.setPositionOverride(continuousPos);
                }
                if (colorVariation != null) {
                    simpleParticle.setColorVariation(colorVariation);
                }
                if (getRotation != null) {
                    simpleParticle.setRotationOverride(getRotation);
                }
            }
        }
    }
}
