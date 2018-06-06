/*
 * Pore(RT)
 * Copyright (c) 2014-2016, Lapis <https://github.com/LapisBlue>
 * Copyright (c) 2014-2016, Contributors
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package blue.lapis.pore.util;

import blue.lapis.pore.Pore;
import blue.lapis.pore.converter.vector.VectorConverter;
import blue.lapis.pore.impl.entity.*;
import com.google.common.base.Optional;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import org.spongepowered.api.entity.projectile.explosive.WitherSkull;
import org.spongepowered.api.entity.projectile.explosive.fireball.SmallFireball;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.entity.projectile.LaunchProjectileEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public final class ProjectileUtil {

    private ProjectileUtil() {
    }

    /**
     * Attempts to launch a projectile of a specified type from the given
     * {@link ProjectileSource}.
     *
     * @param source The Sponge {@link ProjectileSource} to launch from
     * @param projectile The Bukkit class of the projectile to launch
     * @param velocity A Bukkit {@link Vector} representing the initial velocity
     *     of the spawned projectile
     * @param <T> The Bukkit type of projectile to launch
     * @return An entity representing the launched projectile, or
     *     {@link Optional#absent()} if it was not launched
     * @throws UnsupportedOperationException If the projectile type cannot be
     *     matched to a class
     */
    @SuppressWarnings({"unchecked"})
    public static <T extends Projectile> java.util.Optional<T> launchProjectile(ProjectileSource source,
                                                                                Class<? extends T> projectile, @Nullable Vector velocity) throws UnsupportedOperationException {
        // I know this defeats the purpose of Optional, but I need a way to
        // differentiate between the projectile being straight invalid and it
        // just failing to launch
        java.util.Optional<T> entity = java.util.Optional.empty();
        if (projectile.isAssignableFrom(Arrow.class)) {
            entity = java.util.Optional.ofNullable((T) PoreArrow.of(source.launchProjectile(
                    org.spongepowered.api.entity.projectile.arrow.Arrow.class,
                    VectorConverter.create3d(Objects.requireNonNull(velocity))
            ).orElse(null)));
        } else if (projectile.isAssignableFrom(Egg.class)) {
            entity = java.util.Optional.ofNullable((T) PoreEgg.of(source.launchProjectile(
                    org.spongepowered.api.entity.projectile.Egg.class,
                    VectorConverter.create3d(Objects.requireNonNull(velocity))
            ).orElse(null)));
        } else if (projectile.isAssignableFrom(EnderPearl.class)) {
            entity = java.util.Optional.ofNullable((T) PoreEnderPearl.of(source.launchProjectile(
                    org.spongepowered.api.entity.projectile.EnderPearl.class,
                    VectorConverter.create3d(Objects.requireNonNull(velocity))
            ).orElse(null)));
        } else if (projectile.isAssignableFrom(Fireball.class)) {
            if (projectile.isAssignableFrom(LargeFireball.class)) {
                entity = java.util.Optional.ofNullable((T) PoreLargeFireball.of(source.launchProjectile(
                        org.spongepowered.api.entity.projectile.explosive.fireball.LargeFireball.class,
                        VectorConverter.create3d(Objects.requireNonNull(velocity))
                ).orElse(null)));
            } else if (projectile.isAssignableFrom(org.bukkit.entity.SmallFireball.class)) {
                entity = java.util.Optional.ofNullable((T) PoreSmallFireball.of(source.launchProjectile(
                        SmallFireball.class,
                        VectorConverter.create3d(Objects.requireNonNull(velocity))
                ).orElse(null)));
            } else if (projectile.isAssignableFrom(WitherSkull.class)) {
                entity = java.util.Optional.ofNullable((T) PoreWitherSkull.of(source.launchProjectile(
                        WitherSkull.class,
                        VectorConverter.create3d(Objects.requireNonNull(velocity))
                ).orElse(null)));
            } else {
                entity = java.util.Optional.ofNullable((T) PoreFireball.of(source.launchProjectile(
                        org.spongepowered.api.entity.projectile.explosive.fireball.Fireball.class,
                        VectorConverter.create3d(Objects.requireNonNull(velocity))
                ).orElse(null)));
            }
        } else if (projectile.isAssignableFrom(FishHook.class)) {
            entity = java.util.Optional.ofNullable((T) PoreFishHook.of(source.launchProjectile(
                    org.spongepowered.api.entity.projectile.FishHook.class,
                    VectorConverter.create3d(Objects.requireNonNull(velocity))
            ).orElse(null)));
        } else if (projectile.isAssignableFrom(Snowball.class)) {
            entity = java.util.Optional.ofNullable((T) PoreSnowball.of(source.launchProjectile(
                    org.spongepowered.api.entity.projectile.Snowball.class,
                    VectorConverter.create3d(Objects.requireNonNull(velocity))
            ).orElse(null)));
        } else if (projectile.isAssignableFrom(ThrownExpBottle.class)) {
            entity = java.util.Optional.ofNullable((T) PoreThrownExpBottle.of(source.launchProjectile(
                    org.spongepowered.api.entity.projectile.ThrownExpBottle.class,
                    VectorConverter.create3d(Objects.requireNonNull(velocity))
            ).orElse(null)));
        } else if (projectile.isAssignableFrom(ThrownPotion.class)) {
            entity = java.util.Optional.ofNullable((T) PoreThrownPotion.of(source.launchProjectile(
                    org.spongepowered.api.entity.projectile.ThrownPotion.class,
                    VectorConverter.create3d(Objects.requireNonNull(velocity))
            ).orElse(null)));
        }
        if (entity.orElse(null) == null) {
            throw new UnsupportedOperationException("Could not match projectile to type (is Pore up-to-date?)"); // lel
        }
        final T finalEntity = entity.get();// TODO
        Pore.getGame().getEventManager().post(new LaunchProjectileEvent() {
            @Nonnull
            @Override
            public org.spongepowered.api.entity.projectile.Projectile getTargetEntity() {
                return ((PoreProjectile) finalEntity).getHandle();
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public void setCancelled(boolean cancel) {

            }

            @Nonnull
            @Override
            public java.util.Optional<ProjectileSource> getSource() {
                return java.util.Optional.of(((PoreProjectile) finalEntity).getHandle().getShooter());
            }

            @Override
            public EventContext getContext() {
                return null;
            }

            @Nonnull
            @Override
            public Cause getCause() {
                return null; //TODO: cause
            }

            /*
            @Nonnull
            @Override
            public org.spongepowered.api.entity.projectile.Projectile getLaunchedProjectile() {
                return ((PoreProjectile) finalEntity).getHandle();
            }

            @Nonnull
            @Override
            public Entity getEntity() {
                return ((PoreProjectile) finalEntity).getHandle();
            }

            @Nonnull
            @Override
            public Game getGame() {
                return Pore.getGame();
            }

            @Nonnull
            @Override
            public CallbackList getCallbacks() {
                return null; //TODO: not sure exactly of what to return here
            }
            */
        });
        return entity;
    }

}
