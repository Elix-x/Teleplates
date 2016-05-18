package code.elix_x.mods.teleplates.config;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import code.elix_x.excore.utils.recipes.RecipeStringTranslator;
import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.net.SyncConfigurationMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ConfigurationManager {

	public static final Logger logger = LogManager.getLogger("Teleplates Config");

	public static File configFile;
	public static Configuration config;

	public static LinkedBlockingQueue<Configuration> clientConfigQueue = new LinkedBlockingQueue<>(1);

	public static void preInit(FMLPreInitializationEvent event){
		configFile = new File(event.getModConfigurationDirectory(), "Teleplates.cfg");
		try {
			configFile.createNewFile();
		} catch (IOException e) {
			logger.error("Caught exception while creating config file: ", e);
		}
		config = new Configuration(configFile);
		config.load();
		loadFromConfig(config);
		TeleplatesBase.proxy.config(config);
		config.save();
	}

	private static Configuration loadFromConfig(Configuration config){
		loadConsumptionManagers();
		GameRegistry.addRecipe(RecipeStringTranslator.fromString(new ItemStack(TeleplatesBase.teleplate, config.getInt("resulting teleplates", "Recipes", 1, 1, 64, "Amount of teleplates as result of recipe.")), RecipeStringTranslator.validateFromConfig(config.getStringList("teleplate", "Recipes", RecipeStringTranslator.toString("GGG", "RER", "RIR", 'G', "blockGlass", 'R', "dustRedstone", 'E', Items.ENDER_PEARL, 'I', "blockIron"), "Configure recipe for teleplates."))));
		//				GameRegistry.addRecipe(RecipeStringTranslator.fromString(new ItemStack(TeleplatesBase.portableTeleplate, config.getInt("resulting portable teleplates", "Recipes", 1, 1, 64, "Amount of portable teleplates as result of recipe.")), RecipeStringTranslator.validateFromConfig(config.getStringList("portable teleplate", "Recipes", RecipeStringTranslator.toString("LGL", "GTG", "LGL", 'L', Items.LEATHER, 'G', "paneGlass", 'T', TeleplatesBase.teleplate), "Configure recipe for portable teleplates."))));
		return config;
	}

	private static void loadConsumptionManagers(){

	}

	public static void init(FMLInitializationEvent event){

	}

	public static void postInit(FMLPostInitializationEvent event){

	}

	public static Configuration config(World world){
		if(world.isRemote){
			try {
				logger.info("Waiting for config.");
				Configuration config = clientConfigQueue.take();
				try {
					logger.info("Received config: " + FileUtils.readFileToString(config.getConfigFile()));
				} catch (IOException e){
					e.printStackTrace();
				}
				return loadFromConfig(config);
			} catch(InterruptedException e){
				ConfigurationManager.logger.error("Caught exception while receiving config file: ", e);
				return ConfigurationManager.config;
			}
		} else {
			Configuration config = ConfigurationManager.config;

			File worldDir = world.getSaveHandler().getWorldDirectory();
			if(worldDir != null){
				File c = new File(worldDir, "Teleplates.cfg");
				if(c.exists()){
					config = new Configuration(c);
					config.load();
				}
			}

			return loadFromConfig(config);
		}
	}

	public static void syncWith(EntityPlayerMP player){
		Configuration config = config(player.worldObj);
		SyncConfigurationMessage message = new SyncConfigurationMessage();
		Configuration c = new Configuration(message.temp);
		c.copyCategoryProps(config, null);
		c.save();
		TeleplatesBase.net.sendTo(message, player);
		try {
			logger.info("Sent config: " + FileUtils.readFileToString(message.temp).replace("\n", ""));
		} catch(IOException e){
			e.printStackTrace();
		}
	}

}
