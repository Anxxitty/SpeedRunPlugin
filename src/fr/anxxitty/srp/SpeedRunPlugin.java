package fr.anxxitty.srp;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class SpeedRunPlugin extends JavaPlugin {

    private final static int requiresProtocol = 24;

    private MultiverseCore core;
    private MultiverseNetherPortals netherPortals;
    private SRPListener pluginListener;
    private Logger logger;
    private MVWorldManager worldManager;
    private MultiverseWorld spworld;
    private MultiverseWorld spnether;
    private MultiverseWorld spend;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.logger = Bukkit.getLogger();

        this.core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        this.netherPortals = (MultiverseNetherPortals) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-NetherPortals");

        if (this.core == null) {
            logger.info("Multiverse-Core not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (this.netherPortals == null) {
            logger.info("Multiverse-NetherPortals not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Check if the version of Multiverse-Core is correct
        if (this.core.getProtocolVersion() < requiresProtocol) {
            logger.severe("Your Multiverse-Core is OUT OF DATE");
            logger.severe("This version of SpeedRunPlugin requires Protocol Level: " + requiresProtocol);
            logger.severe("Your of Core Protocol Level is: " + this.core.getProtocolVersion());
            logger.severe("Grab an updated copy at: ");
            logger.severe("http://dev.bukkit.org/bukkit-plugins/multiverse-core/");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.worldManager = this.core.getMVWorldManager();
        this.pluginListener = new SRPListener(core,  this);

        this.spworld = worldManager.getMVWorld("spworld");
        this.spnether = worldManager.getMVWorld("spnether");
        this.spend = worldManager.getMVWorld("spend");

        //Link the nether and the end to the overworld
        this.addLinks();

        getCommand("reset").setExecutor(new Commands(this.core));
        getServer().getPluginManager().registerEvents(this.pluginListener, this);

        //Check if the worlds are created and regen them if needed
        try {
            String resetOnStarting = this.getConfig().getString("resetonstarting");

            if (resetOnStarting.equalsIgnoreCase("true") && !checkWorlds()) {
                this.regenWorlds();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            logger.info("The 'resetonstarting' setting is not defined or is incorrect in the configuration. Considered as false");
        }

        //Unload useless worlds
        worldManager.unloadWorld("world_nether");
        worldManager.unloadWorld("world_the_end");

        logger.info(" The plugin started successfully !");
    }

    private void addLinks() {
        netherPortals.addWorldLink("spworld", "spnether", PortalType.NETHER);
        netherPortals.addWorldLink("spnether", "spworld", PortalType.NETHER);
        netherPortals.addWorldLink("spworld", "spend", PortalType.ENDER);
        netherPortals.addWorldLink("spend", "spworld", PortalType.ENDER);
    }

    private void regenWorlds() {
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

    private boolean checkWorlds() {

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

            return true;
        }
        return false;

    }

    @Override
    public void onDisable() {
        logger.info(" The plugin stopped successfully !");
    }

}
