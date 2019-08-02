package dev.itseternity.chatlogger;

import dev.itseternity.chatlogger.commands.ChatLogCommand;
import dev.itseternity.chatlogger.listeners.ChatListener;
import dev.itseternity.chatlogger.paste.Paste;
import dev.itseternity.chatlogger.paste.PastebinPaste;
import dev.itseternity.chatlogger.storage.ChatStorage;
import dev.itseternity.chatlogger.storage.SQLChatStorage;
import dev.itseternity.chatlogger.utils.SQLCredentials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ChatLoggerPlugin extends JavaPlugin {

    private ChatStorage chatStorage;
    private Paste paste;

    @Override
    public void onEnable() {
        createConfig();

        SQLCredentials credentials = SQLCredentials.fromConfig(getConfig().getConfigurationSection("mysql"));
        chatStorage = new SQLChatStorage(this, credentials);
        chatStorage.init();

        paste = new PastebinPaste(
                getConfig().getString("log-format"),
                getConfig().getString("pastebin.username"),
                getConfig().getString("pastebin.password"),
                getConfig().getString("pastebin.dev-key")
        );

        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        getCommand("chatlog").setExecutor(new ChatLogCommand(this));
    }

    public String getMessage(String key) {
        String message = getConfig().getString("messages." + key);
        message = ChatColor.translateAlternateColorCodes('&', message);

        return message;
    }

    public Paste getPaste() {
        return paste;
    }

    public ChatStorage getChatStorage() {
        return chatStorage;
    }

    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("Config.yml not found, creating!");
                saveDefaultConfig();
            } else {
                getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
