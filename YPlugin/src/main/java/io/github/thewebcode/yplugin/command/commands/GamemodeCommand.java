package io.github.thewebcode.yplugin.command.commands;

import io.github.thewebcode.yplugin.Messages;
import io.github.thewebcode.yplugin.chat.Chat;
import io.github.thewebcode.yplugin.command.Arg;
import io.github.thewebcode.yplugin.command.Command;
import io.github.thewebcode.yplugin.permission.Perms;
import io.github.thewebcode.yplugin.player.Players;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GamemodeCommand {
    private static Map<String, GameMode> modeAliases = new HashMap<String, GameMode>() {{
        put("0", GameMode.SURVIVAL);
        put("s", GameMode.SURVIVAL);
        put("survival", GameMode.SURVIVAL);
        put("1", GameMode.CREATIVE);
        put("c", GameMode.CREATIVE);
        put("creative", GameMode.CREATIVE);
        put("build", GameMode.CREATIVE);
        put("2", GameMode.ADVENTURE);
        put("a", GameMode.ADVENTURE);
        put("adventure", GameMode.ADVENTURE);
        put("spectator", GameMode.SPECTATOR);
        put("3", GameMode.SPECTATOR);
        put("spec", GameMode.SPECTATOR);
    }};

    @Command(identifier = "gm", permissions = {Perms.COMMAND_GAMEMODE})
    public void gamemodeCommand(Player player, @Arg(name = "gamemode") String mode, @Arg(name = "target", def = "?sender") Player target) {
        if (mode == null) {
            Players.cycleGameMode(target);
            return;
        }

        mode = mode.toLowerCase();
        if (!modeAliases.containsKey(mode)) {
            Chat.message(player, Messages.invalidCommandUsage("gamemode"));
            return;
        }

        target.setGameMode(modeAliases.get(mode));
    }
}
