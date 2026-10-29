package me.lidan.griffinAddon.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Objects;
import java.util.UUID;

/** Immutable observation after the corresponding addon operation. */
public final class GriffinItemGrantedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final UUID playerId;
    private final String itemId;
    private final int amount;
    public GriffinItemGrantedEvent(UUID playerId, String itemId, int amount) {
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
