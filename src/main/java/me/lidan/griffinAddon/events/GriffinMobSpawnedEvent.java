package me.lidan.griffinAddon.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Objects;
import java.util.UUID;

/** Immutable observation after the corresponding addon operation. */
public final class GriffinMobSpawnedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final UUID playerId;
    private final UUID entityId;
    private final String mobId;
    private final String world;
    public GriffinMobSpawnedEvent(UUID playerId, UUID entityId, String mobId, String world) {
        this.playerId = Objects.requireNonNull(playerId);
        this.entityId = Objects.requireNonNull(entityId);
        this.mobId = Objects.requireNonNull(mobId);
        this.world = Objects.requireNonNull(world);
    }
    public UUID getPlayerId() { return playerId; }
    public UUID getEntityId() { return entityId; }
    public String getMobId() { return mobId; }
    public String getWorld() { return world; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
