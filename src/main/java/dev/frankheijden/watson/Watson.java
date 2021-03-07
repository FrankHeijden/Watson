package dev.frankheijden.watson;

import dev.frankheijden.watson.commands.CommandWatson;
import dev.frankheijden.watson.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Watson extends JavaPlugin {

    public static final String PLUGIN_CHANNEL = "watson:world";

    private Set<UUID> watsonPlayers;

    @Override
    public void onEnable() {
        super.onEnable();
        this.watsonPlayers = new HashSet<>();

        saveDefaultConfig();
        getServer().getMessenger().registerOutgoingPluginChannel(this, PLUGIN_CHANNEL);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        registerCommand("watsonserver", new CommandWatson(this));
    }

    private void registerCommand(String commandName, Object obj) {
        PluginCommand command = getCommand(commandName);
        if (command == null) return;
        if (obj instanceof CommandExecutor) command.setExecutor((CommandExecutor) obj);
        if (obj instanceof TabExecutor) command.setTabCompleter((TabExecutor) obj);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public Set<UUID> getWatsonPlayers() {
        return watsonPlayers;
    }

    public List<Player> getOnlineWatsonPlayers() {
        return watsonPlayers.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .filter(Player::isOnline)
                .collect(Collectors.toList());
    }

    public Optional<String> getRawMessage(String path) {
        String msg = getConfig().getString("messages." + path);
        if (msg == null || msg.isEmpty()) return Optional.empty();
        return Optional.of(msg);
    }

    public Optional<String> getMessage(String path) {
        return getRawMessage(path).map(this::color);
    }

    public String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public void send(CommandSender sender, String path) {
        getMessage(path).ifPresent(sender::sendMessage);
    }
}
