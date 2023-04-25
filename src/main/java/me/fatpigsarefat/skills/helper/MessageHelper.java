package me.fatpigsarefat.skills.helper;

import java.util.List;
import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.managers.FileManager;

public class MessageHelper {
    private final FileManager.Config config = PlayerSkills.getFileManager().getConfig("messages");

    private String getPrefix() {
        return this.config.get().getString("prefix") + " ";
    }

    public String getMessage(String key, String[] args) {
        String message = getPrefix() + this.config.get().getString(key);
        message = message.replace("&", "");
        if (args == null)
            return message;
        for (int i = 0; i < args.length; i++)
            message = message.replace("{" + i + "}", args[i]);
        return message;
    }

    public List<String> getMessageList(String key) {
        return this.config.get().getStringList(key);
    }
}
