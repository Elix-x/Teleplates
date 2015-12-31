package code.elix_x.mods.teleplates.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import code.elix_x.excore.utils.recipes.RecipeStringTranslator;
import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.consomation.ConsomationManager;
import code.elix_x.mods.teleplates.consomation.IConsomationManager;
import code.elix_x.mods.teleplates.consomation.bloodmagic.LPConsomationManager;
import code.elix_x.mods.teleplates.consomation.energy.EnergyConsomationManager;
import code.elix_x.mods.teleplates.consomation.fluid.FluidConsomationManager;
import code.elix_x.mods.teleplates.consomation.holidays.HolidaysConsomationManager;
import code.elix_x.mods.teleplates.consomation.thaumcraft.EssentiaConsomationManager;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;

public class ConfigurationManager {

	public static final Logger logger = LogManager.getLogger("Teleplates Config");

	public static File configFile;
	public static Configuration config;

	public static boolean debug = false;

	public static boolean forceDisplayCoordinatesInGui = false;

	public static boolean permissions = true;

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
		permissions = config.getBoolean("permissions", "Permissions", true, "Enables or disables permissions system.\nIt's always deactivated in single player independently from this value.\nMay cause some bugs if activated on offline mode server.");
		GameRegistry.addRecipe(RecipeStringTranslator.fromString(new ItemStack(TeleplatesBase.teleplate, config.getInt("resulting teleplates", "Recipes", 1, 1, 64, "Amount of teleplates as result of recipe.")), RecipeStringTranslator.validateFromConfig(config.getStringList("teleplate", "Recipes", RecipeStringTranslator.toString("GGG", "RER", "RIR", 'G', "blockGlass", 'R', "dustRedstone", 'E', Items.ender_pearl, 'I', "blockIron"), "Configure recipe for teleplates."))));
		GameRegistry.addRecipe(RecipeStringTranslator.fromString(new ItemStack(TeleplatesBase.portableTeleplate, config.getInt("resulting portable teleplates", "Recipes", 1, 1, 64, "Amount of portable teleplates as result of recipe.")), RecipeStringTranslator.validateFromConfig(config.getStringList("portable teleplate", "Recipes", RecipeStringTranslator.toString("LGL", "GTG", "LGL", 'L', Items.leather, 'G', "paneGlass", 'T', TeleplatesBase.teleplate), "Configure recipe for portable teleplates."))));
		config.save();
	}

	private static void loadConsomationManagers() {
		List<IConsomationManager> managers = new ArrayList<IConsomationManager>();
		managers.add(EnergyConsomationManager.INSTANCE);
		managers.add(FluidConsomationManager.INSTANCE);
		if(Loader.isModLoaded("Thaumcraft")) managers.add(EssentiaConsomationManager.INSTANCE);
		if(Loader.isModLoaded("AWWayofTime")) managers.add(LPConsomationManager.INSTANCE);
		managers.add(HolidaysConsomationManager.INSTANCE);

		List<String> names = Lists.transform(managers, new Function<IConsomationManager, String>() {

			@Override
			public String apply(IConsomationManager manager) {
				return manager.getName();
			}

		});
		String[] currentActive = config.getStringList("consomationTypes", "consomation", new String[]{}, "Choose what teleplates will consume in order to work.\nValid consomation types: " + names, names.toArray(new String[0]));
		ConsomationManager.activate(managers, currentActive);
		for(IConsomationManager manager : managers){
			manager.config(config);
			if(!ConsomationManager.isActive(manager)){
				manager.deactivate();
			}
		}
	}

	public static void init(FMLInitializationEvent event){

	}

	public static void postInit(FMLPostInitializationEvent event){

	}

	public static boolean permissionsSystemActive(){
		return permissions && MinecraftServer.getServer().isDedicatedServer();
	}

}
