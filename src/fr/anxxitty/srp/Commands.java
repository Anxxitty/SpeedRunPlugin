package fr.anxxitty.srp;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Iterator;

public class Commands implements CommandExecutor {

    private final MultiverseCore core;
    private final MVWorldManager worldManager;

    public Commands(MultiverseCore core) {
        this.core = core;
        this.worldManager = core.getMVWorldManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] strings) {
        if (sender instanceof Player) {

            if (cmd.getName().equalsIgnoreCase("reset")) {

                //Kills the dragon if the player is in the end because otherwise there's a bug with the bossbar
                //I don't like the use of dispatchCommand function so if you know an other way to do it, please let me know
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=minecraft:ender_dragon]");

                //Regenerates the worlds
                worldManager.deleteWorld("spworld");
                worldManager.deleteWorld("spnether");
                worldManager.deleteWorld("spend");

                worldManager.addWorld("spworld", World.Environment.NORMAL, null, WorldType.NORMAL, true, null);
                String seed = String.valueOf(worldManager.getMVWorld("spworld").getSeed());
                worldManager.addWorld("spnether", World.Environment.NETHER, seed, WorldType.NORMAL, true, null);
                worldManager.addWorld("spend", World.Environment.THE_END, seed, WorldType.NORMAL, true, null);

                worldManager.getMVWorld("spnether").setRespawnToWorld("spworld");
                worldManager.getMVWorld("spend").setRespawnToWorld("spworld");

                //Teleports the player at a safe location
                Location originalLocation = worldManager.getMVWorld("spworld").getSpawnLocation();
                Location safeLocation = core.getSafeTTeleporter().getSafeLocation(originalLocation);
                worldManager.getMVWorld("spworld").setSpawnLocation(safeLocation);

                for (Player player : Bukkit.getOnlinePlayers()) {

                    player.sendMessage("§3You will reset your speedrun! §6/!\\ §4Please don't move during the generation, this may crash the server §6/!\\");

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

                    player.sendMessage("§3Generation finished !");
                    player.sendMessage("§3Teleportation...");

                    player.teleport(safeLocation);

                    player.sendMessage("§3Teleported !");

                }
                return true;
            }
        }
        else {
            sender.sendMessage("Cannot reset from the console!");
        }
        return false;
    }
}
