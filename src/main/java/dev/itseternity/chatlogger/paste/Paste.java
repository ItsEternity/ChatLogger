package dev.itseternity.chatlogger.paste;

import dev.itseternity.chatlogger.chatlog.ChatLog;

import java.util.Set;

/**
 * Represents a method of pasting a player's chatlogs for viewing
 */
public interface Paste {

    /**
     * Pastes a player's chatlogs to be viewed
     *
     * @param logs the player's chatlogs
     *
     * @return a link to where the chatlogs can be viewed
     */
    String pasteLogs(String player, Set<ChatLog> logs);

}
