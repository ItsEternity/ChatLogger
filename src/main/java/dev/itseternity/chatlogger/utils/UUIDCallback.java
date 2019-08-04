package dev.itseternity.chatlogger.utils;

import java.util.UUID;

@FunctionalInterface
public interface UUIDCallback {

    void complete(UUID uuid);

}
