package me.lidan.griffinAddon.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Objects;
import java.util.UUID;

/** Immutable observation after the corresponding addon operation. */
public final class GriffinBurrowOpenedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final UUID playerId;
    private final String rarity;
    private final String outcome;
    private final String world;
    public GriffinBurrowOpenedEvent(UUID playerId, String rarity, String outcome, String world) {
        this.playerId = Objects.requireNonNull(playerId);
        this.rarity = Objects.requireNonNull(rarity);
        this.outcome = Objects.requireNonNull(outcome);
        this.world = Objects.requireNonNull(world);
    }
    public UUID getPlayerId() { return playerId; }
    public String getRarity() { return rarity; }
    public String getOutcome() { return outcome; }
    public String getWorld() { return world; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
