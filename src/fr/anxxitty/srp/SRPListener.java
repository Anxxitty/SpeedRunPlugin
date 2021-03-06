package fr.anxxitty.srp;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;

import java.util.Iterator;

public class SRPListener implements Listener {

    private final MultiverseCore core;
    private final SpeedRunPlugin SpeedRunPlugin;

    public SRPListener(MultiverseCore core, SpeedRunPlugin SpeedRunPlugin) {
        this.core = core;
        this.SpeedRunPlugin = SpeedRunPlugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        String resetonstarting = SpeedRunPlugin.getConfig().getString("resetonstarting");

        try {
            //resets player data only if the map is regenerated at server startup
            if (resetonstarting.equalsIgnoreCase("true")) {

                Player player = event.getPlayer();

                //Kills the dragon if the player is in the end because otherwise there's a bug with the bossbar
                //I don't like the usage of dispatchCommand function so if you know an other way to do it, please let me know
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=minecraft:ender_dragon]");

                //Removes all the data about the player (potion effect, health, inventory, advancements...)
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

                Iterator<Advancement> iterator = Bukkit.getServer().advancementIterator();
                while (iterator.hasNext())
                {
                    AdvancementProgress progress = player.getAdvancementProgress(iterator.next());
                    for (String criteria : progress.getAwardedCriteria())
                        progress.revokeCriteria(criteria);
                }

                if (player.getWorld() != this.core.getMVWorldManager().getMVWorld("spworld")) {
                    Location location = this.core.getMVWorldManager().getMVWorld("spworld").getSpawnLocation();
                    location = this.core.getSafeTTeleporter().getSafeLocation(location);
                    player.teleport(location);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

}
