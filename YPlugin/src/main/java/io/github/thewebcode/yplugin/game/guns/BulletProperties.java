package io.github.thewebcode.yplugin.game.guns;

import io.github.thewebcode.yplugin.effect.ParticleEffect;
import io.github.thewebcode.yplugin.game.clause.BulletDamageEntityClause;
import io.github.thewebcode.yplugin.yml.Path;
import io.github.thewebcode.yplugin.yml.Skip;
import io.github.thewebcode.yplugin.yml.YamlConfig;

public class BulletProperties extends YamlConfig {
    @Path("bullet-damage")
    public double damage = 2;

    @Path("bullet-speed")
    public double speed = 6.5;

    @Path("bullet-spread")
    public double spread = 0.0;

    @Path("bullet-delay-ticks")
    public long delay = 2l;

    @Path("particles")
    private String effect;

    @Skip
    public BulletDamageEntityClause damageCondition;

    @Skip
    private Gun parent;

    public BulletProperties(double damage, double speed, double spread, long delay, ParticleEffect effect) {
        this.damage = damage;
        this.speed = speed;
        this.spread = spread;
        this.delay = delay;
        this.effect = effect.getName();
    }

    public BulletProperties(Gun gun) {
        this.parent = gun;
    }

    public BulletProperties() {
    }

    public BulletProperties damage(double damage) {
        this.damage = damage;
        return this;
    }

    public BulletProperties speed(double speed) {
        this.speed = speed;
        return this;
    }

    public BulletProperties spread(double spread) {
        this.spread = spread;
        return this;
    }

    public BulletProperties delayBetweenRounds(long ticks) {
        this.delay = ticks;
        return this;
    }

    public BulletProperties parent(Gun gun) {
        parent = gun;
        return this;
    }

    public BulletProperties effect(ParticleEffect effect) {
        this.effect = effect.getName();
        return this;
    }

    public BulletProperties damageCondition(BulletDamageEntityClause damageCondition) {
        this.damageCondition = damageCondition;
        return this;
    }

    public Gun parent() {
        return parent;
    }

    public boolean hasEffect() {
        return effect != null;
    }

    public ParticleEffect getEffect() {
        return ParticleEffect.getEffect(effect);
    }
}
