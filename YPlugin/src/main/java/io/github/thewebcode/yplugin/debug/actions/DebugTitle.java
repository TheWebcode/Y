package io.github.thewebcode.yplugin.debug.actions;

import io.github.thewebcode.yplugin.chat.Title;
import io.github.thewebcode.yplugin.debug.DebugAction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DebugTitle implements DebugAction {
    @Override
    public void doAction(Player player, String... args) {
        Title title = new Title("Brandon is Awesome");
        title.setFadeOutTime(1);
        title.setStayTime(4);
        title.setSubtitleColor(ChatColor.YELLOW);
        title.setTitleColor(ChatColor.RED);
        title.setFadeInTime(1);

        title.broadcast();
    }

    @Override
    public String getActionName() {
        return "title_screen";
    }
}
