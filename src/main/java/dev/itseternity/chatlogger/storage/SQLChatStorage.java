package dev.itseternity.chatlogger.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.itseternity.chatlogger.ChatLoggerPlugin;
import dev.itseternity.chatlogger.chatlog.ChatLog;
import dev.itseternity.chatlogger.chatlog.LogType;
import dev.itseternity.chatlogger.utils.DatabaseConstants;
import dev.itseternity.chatlogger.utils.SQLCredentials;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SQLChatStorage extends ChatStorage {

    private static final long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(15);
    private static final long LEAK_DETECTION = TimeUnit.SECONDS.toMillis(10);

    private final ChatLoggerPlugin plugin;
    private final SQLCredentials credentials;
    private HikariDataSource dataSource;

    public SQLChatStorage(ChatLoggerPlugin plugin, SQLCredentials credentials) {
        this.plugin = plugin;
        this.credentials = credentials;
    }

    @Override
    public void init() {
        HikariConfig config = generateConfig(credentials);
        dataSource = new HikariDataSource(config);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::setupTables);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (getLogs().size() <= 0) {
                return;
            }

            storeMessages();
        }, 20L * 60L, 20L * 60L);
    }

    @Override
    public void storeMessages() {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(DatabaseConstants.INSERT_CHATLOG_QUERY)) {
                plugin.getLogger().info("Attempting to save " + getLogs().size() + " messages...");
                ChatLog chatLog;
                while ((chatLog = getLogs().poll()) != null) {
                    statement.setString(1, chatLog.getUuid().toString());
                    statement.setString(2, chatLog.getName());
                    statement.setString(3, chatLog.getServerName());
                    statement.setString(4, chatLog.getLogType().name());
                    statement.setLong(5, chatLog.getCreatedAt());
                    statement.setString(6, chatLog.getMessage());

                    statement.addBatch();
                }

                int[] inserted = statement.executeBatch();
                plugin.getLogger().info("Added " + inserted.length + " messages to the database!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<Set<ChatLog>> getMessages(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            Set<ChatLog> logs = new HashSet<>();

            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(DatabaseConstants.SELECT_CHATLOG_UUID)) {
                    statement.setString(1, uuid.toString());

                    ResultSet result = statement.executeQuery();
                    while (result.next()) {
                        String username = result.getString(DatabaseConstants.USERNAME);
                        String serverName = result.getString(DatabaseConstants.SERVER_NAME);
                        LogType logType = LogType.valueOf(result.getString(DatabaseConstants.LOG_TYPE));
                        long createdAt = result.getLong(DatabaseConstants.CREATED_AT);
                        String message = result.getString(DatabaseConstants.MESSAGE);

                        ChatLog log = ChatLog.builder()
                                .uuid(uuid)
                                .name(username)
                                .serverName(serverName)
                                .logType(logType)
                                .createdAt(createdAt)
                                .message(message)
                                .build();

                        logs.add(log);
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return logs;
        });
    }

    private void setupTables() {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(DatabaseConstants.CREATE_TABLE_QUERY)) {
                statement.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HikariConfig generateConfig(SQLCredentials credentials) {
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("serverName", credentials.getHost());
        config.addDataSourceProperty("port", credentials.getPort());
        config.addDataSourceProperty("databaseName", credentials.getDatabase());
        config.addDataSourceProperty("user", credentials.getUsername());
        config.addDataSourceProperty("password", credentials.getPassword());

        // See: https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing
        config.setMaximumPoolSize(Math.max(Runtime.getRuntime().availableProcessors(), 4));
        config.setConnectionTimeout(CONNECTION_TIMEOUT);
        config.setLeakDetectionThreshold(LEAK_DETECTION);

        return config;
    }

}
