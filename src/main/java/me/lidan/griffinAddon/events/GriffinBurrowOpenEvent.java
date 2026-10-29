package me.lidan.griffinAddon.events;

import java.util.UUID;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/** Cancellable request emitted before the corresponding gameplay operation. */
public final class GriffinBurrowOpenEvent extends Event implements org.bukkit.event.Cancellable {
    private boolean cancelled;
    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    private static final HandlerList HANDLERS = new HandlerList();
    private final UUID playerId;
    private final String rarity;
    private final String world;
    public GriffinBurrowOpenEvent(UUID playerId, String rarity, String world) {
        this.playerId = java.util.Objects.requireNonNull(playerId);
        this.rarity = java.util.Objects.requireNonNull(rarity);
        this.world = java.util.Objects.requireNonNull(world);
    }
    public UUID getPlayerId() { return playerId; }
    public String getRarity() { return rarity; }
    public String getWorld() { return world; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
