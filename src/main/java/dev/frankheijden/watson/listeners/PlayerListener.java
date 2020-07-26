package dev.frankheijden.watson.listeners;

import dev.frankheijden.watson.Watson;
import dev.frankheijden.watson.entities.Action;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChannelEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.event.player.PlayerUnregisterChannelEvent;

import java.nio.charset.StandardCharsets;

public class PlayerListener implements Listener {

    private final Watson plugin;

    public PlayerListener(Watson plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerRegisterChannel(PlayerRegisterChannelEvent event) {
        handleChannelEvent(event, Action.REGISTER);
    }

    @EventHandler
    public void onPlayerUnregisterChannel(PlayerUnregisterChannelEvent event) {
        handleChannelEvent(event, Action.UNREGISTER);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getWatsonPlayers().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (plugin.getWatsonPlayers().contains(player.getUniqueId())) {
            sendWatsonWorld(player);
        }
    }

    private void handleChannelEvent(PlayerChannelEvent event, Action action) {
        if (!event.getChannel().equals(Watson.PLUGIN_CHANNEL)) return;

        Player player = event.getPlayer();
        switch (action) {
            case REGISTER:
                plugin.getWatsonPlayers().add(player.getUniqueId());
                plugin.getLogger().info("Player " + player.getDisplayName() + " registered their watson channel");
                sendWatsonWorld(player);
                break;
            case UNREGISTER:
                plugin.getWatsonPlayers().remove(player.getUniqueId());
                plugin.getLogger().info("Player " + player.getDisplayName() + " unregistered their watson channel");
                break;
        }
    }

    private void sendWatsonWorld(Player player) {
        if (!player.hasPermission("watson.register")) return;
        player.sendPluginMessage(plugin, Watson.PLUGIN_CHANNEL, player.getWorld().getName().getBytes(StandardCharsets.UTF_8));
    }
}
