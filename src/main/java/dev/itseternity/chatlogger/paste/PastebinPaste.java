package dev.itseternity.chatlogger.paste;

import com.besaba.revonline.pastebinapi.Pastebin;
import com.besaba.revonline.pastebinapi.impl.factory.PastebinFactory;
import com.besaba.revonline.pastebinapi.paste.PasteBuilder;
import com.besaba.revonline.pastebinapi.paste.PasteExpire;
import com.besaba.revonline.pastebinapi.paste.PasteVisiblity;
import com.besaba.revonline.pastebinapi.response.Response;
import dev.itseternity.chatlogger.chatlog.ChatLog;
import dev.itseternity.chatlogger.utils.UtilTime;

import java.util.Comparator;
import java.util.Set;

public class PastebinPaste implements Paste {

    private final String username;
    private final String password;

    private final PastebinFactory factory = new PastebinFactory();
    private final Pastebin pastebin;

    private final String format;

    public PastebinPaste(String format, String username, String password, String devKey) {
        this.format = format;
        this.username = username;
        this.password = password;

        this.pastebin = factory.createPastebin(devKey);
    }

    @Override
    public String pasteLogs(String player, Set<ChatLog> logs) {
        Response<String> userLoginKeyResponse = pastebin.login(username, password);

        if (userLoginKeyResponse.hasError()) {
            return "Error Pasting - " + userLoginKeyResponse.getError();
        }

        String userKey = userLoginKeyResponse.get();
        PasteBuilder pasteBuilder = factory.createPaste();
        pasteBuilder.setTitle(player + " - Chat logs");
        pasteBuilder.setMachineFriendlyLanguage("text");
        pasteBuilder.setVisiblity(PasteVisiblity.Unlisted);
        pasteBuilder.setExpire(PasteExpire.OneHour);

        StringBuilder builder = new StringBuilder();
        logs.stream()
                .sorted(Comparator.comparingLong(ChatLog::getCreatedAt))
                .forEach(log -> {
                    String formattedMessage = format
                            .replace("%uuid%", log.getUuid().toString())
                            .replace("%player%", log.getName())
                            .replace("%server_name%", log.getServerName())
                            .replace("%log_type%", log.getLogType().name())
                            .replace("%timestamp%", UtilTime.formatTime(log.getCreatedAt()))
                            .replace("%message%", log.getMessage());

                    builder.append(formattedMessage).append("\n");
                });
        pasteBuilder.setRaw(builder.toString());

        com.besaba.revonline.pastebinapi.paste.Paste paste = pasteBuilder.build();
        Response<String> postResult = pastebin.post(paste, userKey);

        if (postResult.hasError()) {
            return "An error occured while posting - " + postResult.getError();
        }

        return postResult.get();
    }
}
