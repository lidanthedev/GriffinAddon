package me.lidan.griffinAddon.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Objects;
import java.util.UUID;

/** Cancellable request emitted before the corresponding gameplay operation. */
public final class GriffinItemGrantEvent extends Event implements Cancellable {
    private boolean cancelled;
    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    private static final HandlerList HANDLERS = new HandlerList();
    private final UUID playerId;
    private final String itemId;
    private final int amount;
    public GriffinItemGrantEvent(UUID playerId, String itemId, int amount) {
        this.playerId = Objects.requireNonNull(playerId);
        this.itemId = Objects.requireNonNull(itemId);
        this.amount = amount;
    }
    public UUID getPlayerId() { return playerId; }
    public String getItemId() { return itemId; }
    public int getAmount() { return amount; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
