package dev.itseternity.chatlogger.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.itseternity.chatlogger.ChatLoggerPlugin;
import dev.itseternity.chatlogger.utils.UUIDCallback;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class ChatLogCommand implements CommandExecutor {

    private final ChatLoggerPlugin plugin;

    public ChatLogCommand(ChatLoggerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessage("syntax-error"));
            return false;
        }

        sender.sendMessage(plugin.getMessage("loading-chatlogs")
                .replace("%player%", args[0]));

        getUUID(args[0], uuid -> {
            if (uuid == null) {
                sender.sendMessage(plugin.getMessage("cannot-find-uuid")
                        .replace("%player%", args[0]));
                return;
            }

            plugin.getChatStorage().getMessages(uuid).thenAcceptAsync(chatLogs -> {
                if (chatLogs.size() == 0) {
                    sender.sendMessage(plugin.getMessage("no-logs-found")
                            .replace("%player%", args[0]));
                    return;
                }

                String link = plugin.getPaste().pasteLogs(args[0], chatLogs);
                sender.sendMessage(plugin.getMessage("logs-link")
                        .replace("%player%", args[0])
                        .replace("%link%", link));
            });
        });
        return true;
    }

    private void getUUID(String name, UUIDCallback uuidCallback) {
        // Let's check if the player is online first before calling the API
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            uuidCallback.complete(player.getUniqueId());
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            URLConnection connection;
            try {
                connection = new URL("https://api.ashcon.app/mojang/v2/user/" + name).openConnection();
                connection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    Gson gson = new Gson();
                    JsonObject root = gson.fromJson(reader, JsonObject.class);

                    String uuid = root.get("uuid").getAsString();
                    Bukkit.getScheduler().runTask(plugin, () -> uuidCallback.complete(UUID.fromString(uuid)));
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
