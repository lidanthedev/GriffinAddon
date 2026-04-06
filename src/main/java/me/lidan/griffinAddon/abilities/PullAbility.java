package me.lidan.griffinAddon.abilities;

import com.google.gson.JsonObject;
import me.lidan.cavecrawlers.items.abilities.ClickAbility;
import me.lidan.cavecrawlers.items.abilities.ItemAbility;
import me.lidan.griffinAddon.GriffinAddon;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class PullAbility extends ClickAbility {
    private int pullDurationTicks = 60;
    private int maxRange = 30;
    private double pullRadius = 7.0;
    private double pullStrength = 0.18;
    private double maxPullSpeed = 1.2;

    public PullAbility() {
        super("Pull", "Pulls entities towards it", 100, 10000);
    }

    @Override
    protected boolean useAbility(PlayerEvent playerEvent) {
        Player player = playerEvent.getPlayer();
        RayTraceResult rayTraceResult = player.rayTraceBlocks(maxRange, FluidCollisionMode.NEVER);
        if (rayTraceResult == null) {
            return false;
        }

        Location pullCenter = rayTraceResult.getHitPosition().toLocation(player.getWorld());

        new BukkitRunnable() {
            private int elapsedTicks;

            @Override
            public void run() {
                if (!player.isOnline() || player.isDead() || elapsedTicks >= pullDurationTicks) {
                    cancel();
                    return;
                }

                for (Entity entity : player.getWorld().getNearbyEntities(
                        pullCenter,
                        pullRadius,
                        pullRadius,
                        pullRadius,
                        nearby -> nearby.isValid() && !nearby.isDead() && nearby instanceof Mob
                )) {
                    Vector toCenter = pullCenter.toVector().subtract(entity.getLocation().toVector());
                    double distanceSquared = toCenter.lengthSquared();
                    if (distanceSquared < 0.0001) {
                        continue;
                    }

                    double distance = Math.sqrt(distanceSquared);
                    double pullSpeed = Math.min(maxPullSpeed, pullStrength * distance);
                    Vector pullVelocity = toCenter.normalize().multiply(pullSpeed).add(entity.getVelocity().multiply(0.35));

                    if (pullVelocity.lengthSquared() > maxPullSpeed * maxPullSpeed) {
                        pullVelocity = pullVelocity.normalize().multiply(maxPullSpeed);
                    }

                    entity.setVelocity(pullVelocity);
                }

                elapsedTicks++;
            }
        }.runTaskTimer(GriffinAddon.getInstance(), 0L, 1L);

        return true;
    }

    @Override
    public ItemAbility buildAbilityWithSettings(JsonObject map) {
        PullAbility ability = (PullAbility) super.buildAbilityWithSettings(map);
        if (map.has("pullDurationTicks")) {
            ability.pullDurationTicks = map.get("pullDurationTicks").getAsInt();
        }
        if (map.has("maxRange")) {
            ability.maxRange = map.get("maxRange").getAsInt();
        }
        if (map.has("pullRadius")) {
            ability.pullRadius = map.get("pullRadius").getAsDouble();
        }
        if (map.has("pullStrength")) {
            ability.pullStrength = map.get("pullStrength").getAsDouble();
        }
        if (map.has("maxPullSpeed")) {
            ability.maxPullSpeed = map.get("maxPullSpeed").getAsDouble();
        }
        return ability;
    }
}
