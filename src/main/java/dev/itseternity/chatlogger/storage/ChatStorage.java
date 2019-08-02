package dev.itseternity.chatlogger.storage;

import dev.itseternity.chatlogger.chatlog.ChatLog;

import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ChatStorage {

    /** The cache of chat messages that need to be stored into the database **/
    private final Queue<ChatLog> logs = new ConcurrentLinkedQueue<>();

    public void addLog(ChatLog log) {
        logs.add(log);
    }

    Queue<ChatLog> getLogs() {
        return logs;
    }

    /**
     * Initialise the storage implementation
     */
    public abstract void init();

    /**
     * Store all the messages the are in the cache to the storage method
     */
    public abstract void storeMessages();

    /**
     * Gets all chatlogs for a player
     *
     * @param uuid the uuid of the player
     *
     * @return a set containing all chatlogs if none found it will return an empty set
     */
    public abstract CompletableFuture<Set<ChatLog>> getMessages(UUID uuid);

}
