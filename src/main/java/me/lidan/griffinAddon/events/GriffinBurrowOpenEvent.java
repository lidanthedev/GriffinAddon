package me.lidan.griffinAddon.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Objects;
import java.util.UUID;

/** Cancellable request emitted before the corresponding gameplay operation. */
public final class GriffinBurrowOpenEvent extends Event implements Cancellable {
    private boolean cancelled;
    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    private static final HandlerList HANDLERS = new HandlerList();
    private final UUID playerId;
    private final String rarity;
    private final String world;
    public GriffinBurrowOpenEvent(UUID playerId, String rarity, String world) {
        this.playerId = Objects.requireNonNull(playerId);
        this.rarity = Objects.requireNonNull(rarity);
        this.world = Objects.requireNonNull(world);
    }
    public UUID getPlayerId() { return playerId; }
    public String getRarity() { return rarity; }
    public String getWorld() { return world; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
