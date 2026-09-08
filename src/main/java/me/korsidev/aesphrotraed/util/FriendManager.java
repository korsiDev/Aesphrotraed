package me.korsidev.aesphrotraed.util;

import java.util.HashMap;
import java.util.UUID;

public class FriendManager {
    private final HashMap<UUID, UUID> pendingRequests = new HashMap<>();

    public void sendRequest(UUID sender, UUID target) {
        pendingRequests.put(target, sender);
    }

    public UUID getIncomingRequest(UUID target) {
        return pendingRequests.get(target);
    }

    public void removeRequest(UUID target) {
        pendingRequests.remove(target);
    }

    public boolean hasPendingRequestFrom(UUID target, UUID sender) {
        return pendingRequests.containsKey(target) && pendingRequests.get(target).equals(sender);
    }
}
