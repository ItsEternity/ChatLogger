package dev.itseternity.chatlogger.utils;

import java.util.UUID;

@FunctionalInterface
public interface Callback {

    void complete(UUID uuid);

}
