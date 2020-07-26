package dev.frankheijden.watson.commands;

import dev.frankheijden.watson.Watson;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandWatson implements CommandExecutor, TabExecutor {

    private final Watson plugin;

    public CommandWatson(Watson plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (!sender.hasPermission("watson.list")) {
                    plugin.send(sender, "no_permission");
                    return true;
                }

                StringBuilder sb = new StringBuilder(plugin.getRawMessage("list.prefix").orElse(""));
                String seperator = plugin.getRawMessage("list.seperator").orElse("");
                String allowed = plugin.getRawMessage("list.allowed").orElse("");
                String disallowed = plugin.getRawMessage("list.disallowed").orElse("");
                List<Player> players = plugin.getOnlineWatsonPlayers();
                for (int i = 0; i < players.size(); i++) {
                    if (i < players.size() - 1) {
                        sb.append(seperator);
                    }
                    Player player = players.get(i);
                    if (player.hasPermission("watson.register")) {
                        sb.append(allowed);
                    } else {
                        sb.append(disallowed);
                    }
                    sb.append(player.getPlayerListName());
                }
                sender.sendMessage(plugin.color(sb.toString()));
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("watson.reload")) {
                    plugin.send(sender, "no_permission");
                    return true;
                }

                plugin.reloadConfig();
                plugin.send(sender, "reload");
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            if (sender.hasPermission("watson.list")) list.add("list");
            if (sender.hasPermission("watson.reload")) list.add("reload");
            return StringUtil.copyPartialMatches(args[0], list, new ArrayList<>());
        }
        return Collections.emptyList();
    }
}
