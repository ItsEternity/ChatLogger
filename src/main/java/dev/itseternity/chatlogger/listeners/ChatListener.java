package dev.itseternity.chatlogger.listeners;

import dev.itseternity.chatlogger.ChatLoggerPlugin;
import dev.itseternity.chatlogger.chatlog.ChatLog;
import dev.itseternity.chatlogger.chatlog.LogType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener implements Listener {

    private final ChatLoggerPlugin plugin;

    public ChatListener(ChatLoggerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        ChatLog chatLog = ChatLog.builder()
                .uuid(player.getUniqueId())
                .name(player.getName())
                .serverName(Bukkit.getServerName())
                .message(e.getMessage())
                .logType(LogType.CHAT)
                .createdAt(System.currentTimeMillis())
                .build();

        plugin.getChatStorage().addLog(chatLog);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        ChatLog chatLog = ChatLog.builder()
                .uuid(player.getUniqueId())
                .name(player.getName())
                .serverName(Bukkit.getServerName())
                .message(e.getMessage())
                .logType(LogType.COMMAND)
                .createdAt(System.currentTimeMillis())
                .build();

        plugin.getChatStorage().addLog(chatLog);
    }
}
