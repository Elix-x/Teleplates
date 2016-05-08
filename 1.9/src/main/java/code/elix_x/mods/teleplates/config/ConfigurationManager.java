package code.elix_x.mods.teleplates.config;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import code.elix_x.excore.utils.recipes.RecipeStringTranslator;
import code.elix_x.mods.teleplates.TeleplatesBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ConfigurationManager {

	public static final Logger logger = LogManager.getLogger("Teleplates Config");

	public static File configFile;
	public static Configuration config;

	public static boolean debug = false;

	public static boolean forceDisplayCoordinatesInGui = false;

	public static void preInit(FMLPreInitializationEvent event){
		configFile = new File(event.getModConfigurationDirectory(), "Teleplates.cfg");
		try {
			configFile.createNewFile();
		} catch (IOException e) {
			logger.error("Caught exception while creating config file: ", e);
		}
		config = new Configuration(configFile);
		config.load();
		debug = config.getBoolean("debug", "debug", false, "Enables debug messages in console.");
		forceDisplayCoordinatesInGui = config.getBoolean("forceDisplayCoordinatesInGui", "GUI", false, "Force to display teleplate coordinates in teleportation gui.");
		loadConsomationManagers();
		GameRegistry.addRecipe(RecipeStringTranslator.fromString(new ItemStack(TeleplatesBase.teleplate, config.getInt("resulting teleplates", "Recipes", 1, 1, 64, "Amount of teleplates as result of recipe.")), RecipeStringTranslator.validateFromConfig(config.getStringList("teleplate", "Recipes", RecipeStringTranslator.toString("GGG", "RER", "RIR", 'G', "blockGlass", 'R', "dustRedstone", 'E', Items.ENDER_PEARL, 'I', "blockIron"), "Configure recipe for teleplates."))));
		GameRegistry.addRecipe(RecipeStringTranslator.fromString(new ItemStack(TeleplatesBase.portableTeleplate, config.getInt("resulting portable teleplates", "Recipes", 1, 1, 64, "Amount of portable teleplates as result of recipe.")), RecipeStringTranslator.validateFromConfig(config.getStringList("portable teleplate", "Recipes", RecipeStringTranslator.toString("LGL", "GTG", "LGL", 'L', Items.LEATHER, 'G', "paneGlass", 'T', TeleplatesBase.teleplate), "Configure recipe for portable teleplates."))));
		TeleplatesBase.proxy.config(config);
		config.save();
	}

	private static void loadConsomationManagers(){

	}

	public static void init(FMLInitializationEvent event){

	}

	public static void postInit(FMLPostInitializationEvent event){

	}

}
