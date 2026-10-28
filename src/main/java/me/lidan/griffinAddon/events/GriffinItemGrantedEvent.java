package me.lidan.griffinAddon.events;

import java.util.UUID;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/** Immutable observation after the corresponding addon operation. */
public final class GriffinItemGrantedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final UUID playerId;
    private final String itemId;
    private final int amount;
    public GriffinItemGrantedEvent(UUID playerId, String itemId, int amount) {
        this.playerId = java.util.Objects.requireNonNull(playerId);
        this.itemId = java.util.Objects.requireNonNull(itemId);
        this.amount = amount;
    }
    public UUID getPlayerId() { return playerId; }
    public String getItemId() { return itemId; }
    public int getAmount() { return amount; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
