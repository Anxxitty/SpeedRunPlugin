package fr.anxxitty.srp;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

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

                if ((((Player) sender).getWorld().getEnvironment() == World.Environment.THE_END))
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=minecraft:ender_dragon]");

                worldManager.deleteWorld("spworld");
                worldManager.deleteWorld("spnether");
                worldManager.deleteWorld("spend");

                worldManager.addWorld("spworld", World.Environment.NORMAL, null, WorldType.NORMAL, true, null);
                String seed = String.valueOf(worldManager.getMVWorld("spworld").getSeed());
                worldManager.addWorld("spnether", World.Environment.NETHER, seed, WorldType.NORMAL, true, null);
                worldManager.addWorld("spend", World.Environment.THE_END, seed, WorldType.NORMAL, true, null);

                worldManager.getMVWorld("spnether").setRespawnToWorld("spworld");
                worldManager.getMVWorld("spend").setRespawnToWorld("spworld");

                Location originalLocation = worldManager.getMVWorld("spworld").getSpawnLocation();
                Location safeLocation = core.getSafeTTeleporter().getSafeLocation(originalLocation);
                worldManager.getMVWorld("spworld").setSpawnLocation(safeLocation);

                for (Player player : Bukkit.getOnlinePlayers()) {

                    player.sendMessage("§3You will reset your speedrun! §6/!\\ §4Please don't move during the generation, this may crash the server §6/!\\");

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
