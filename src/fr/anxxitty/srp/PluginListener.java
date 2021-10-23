package fr.anxxitty.srp;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;

public class PluginListener implements Listener {

    private final MultiverseCore core;
    private final Plugin plugin;

    public PluginListener(MultiverseCore core,  Plugin plugin) {
        this.core = core;
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        String messageFromConfig = plugin.getConfig().getString("resetonstarting");

        try {
            if (messageFromConfig.equalsIgnoreCase("true")) {

                Player player = event.getPlayer();

                if (player.getWorld().getEnvironment() == World.Environment.THE_END)
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=minecraft:ender_dragon]");

                player.getInventory().clear();
                player.updateInventory();
                player.setHealth(20.0);
                player.setFoodLevel(20);
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
                player.setExp(0);
                player.setLevel(0);
                player.setSaturation(20);

                if (player.getWorld() != core.getMVWorldManager().getMVWorld("spworld")) {
                    Location location = core.getMVWorldManager().getMVWorld("spworld").getSpawnLocation();
                    location = core.getSafeTTeleporter().getSafeLocation(location);
                    player.teleport(location);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

}
