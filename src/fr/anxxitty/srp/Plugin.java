package fr.anxxitty.srp;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();
        saveDefaultConfig();

        String messageFromConfig = this.getConfig().getString("resetonstarting");

        MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        MultiverseNetherPortals netherPortals = (MultiverseNetherPortals) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-NetherPortals");

        MVWorldManager worldManager = core.getMVWorldManager();

        getCommand("reset").setExecutor(new Commands(core));
        getServer().getPluginManager().registerEvents(new PluginListener(core,  this), this);

        MultiverseWorld spworld = worldManager.getMVWorld("spworld");
        MultiverseWorld spnether = worldManager.getMVWorld("spnether");
        MultiverseWorld spend = worldManager.getMVWorld("spend");

        netherPortals.addWorldLink("spworld", "spnether", PortalType.NETHER);
        netherPortals.addWorldLink("spnether", "spworld", PortalType.NETHER);
        netherPortals.addWorldLink("spworld", "spend", PortalType.ENDER);
        netherPortals.addWorldLink("spend", "spworld", PortalType.ENDER);

        try {

            if (spworld == null || spnether == null || spend == null) {
                worldManager.addWorld("spworld", World.Environment.NORMAL, null, WorldType.NORMAL, true, null);
                String seed = String.valueOf(worldManager.getMVWorld("spworld").getSeed());
                worldManager.addWorld("spnether", World.Environment.NETHER, seed, WorldType.NORMAL, true, null);
                worldManager.addWorld("spend", World.Environment.THE_END, seed, WorldType.NORMAL, true, null);

                worldManager.getMVWorld("spnether").setRespawnToWorld("spworld");
                worldManager.getMVWorld("spend").setRespawnToWorld("spworld");

                Location originalLocation = worldManager.getMVWorld("spworld").getSpawnLocation();
                Location safeLocation = core.getSafeTTeleporter().getSafeLocation(originalLocation);
                worldManager.getMVWorld("spworld").setSpawnLocation(safeLocation);

            } else if (messageFromConfig.equalsIgnoreCase("true")) {
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
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("The 'resetonstarting' setting is not defined or is incorrect in the configuration. Considered as false");
        }

        worldManager.unloadWorld("world_nether");
        worldManager.unloadWorld("world_the_end");

        System.out.println("[SpeedRunPlugin] The plugin started successfully !");
    }

    @Override
    public void onDisable() {
        System.out.println("[SpeedRunPlugin] The plugin stopped successfully !");
    }

}
