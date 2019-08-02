package dev.itseternity.chatlogger.chatlog;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ChatLog {

    private final UUID uuid;
    private final String name;
    private final String serverName;
    private final String message;
    private final LogType logType;
    private final long createdAt;

}
