package code.elix_x.mods.teleplates.config;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import code.elix_x.mods.teleplates.TeleplatesBase;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;

public class ConfigurationManager {

	public static final Logger logger = LogManager.getLogger("Teleplates Config");

	//	public static File configFolder;
	public static File configFile;
	public static Configuration config;

	public static boolean debug = false;
	
	public static boolean forceDisplayCoordinatesInGui = false;
	
	public static boolean permissions = true;
	
	public static int rfUsageType = 0;
	public static int rfPerTransfer = 25000;
	public static int rfStrorage = 250000;

	public static EnumTeleplateRecipe teleplateRecipe = EnumTeleplateRecipe.TELEPADS;
	public static int teleplateRecipeResult = 1;
	
	public static EnumPortableTeleplateRecipe portableTeleplateRecipe = EnumPortableTeleplateRecipe.TELEPLATES_DEFAULT;
	public static int portableTeleplateRecipeResult = 1;

	public static void preInit(FMLPreInitializationEvent event){
		//		configFolder = new File(event.getModConfigurationDirectory(), "teleplates");
		//		configFolder.mkdirs();
		//		configFile = new File(configFolder, "main.cfg");
		configFile = new File(event.getModConfigurationDirectory(), "teleplates.cfg");
		try {
			configFile.createNewFile();
		} catch (IOException e) {
			logger.error("Caught exception while creating config file: ", e);
		}
		config = new Configuration(configFile);
		config.load();
		debug = config.getBoolean("debug", "debug", false, "Enables debug messages in console.");
		forceDisplayCoordinatesInGui = config.getBoolean("forceDisplayCoordinatesInGui", "GUI", false, "Force to display teleplate coordinates in teleportation gui.");
		permissions = config.getBoolean("permissions", "Permissions", true, "Enables or disables permissions system.\nIt's always deactivated in single player independently from this value.\nMay cause some bugs if activated on online mode false server.");
		rfUsageType = config.getInt("rfUsageType", "RF", 0, 0, 2, "Type of rf usage:\n0=No Rf Usage.\n1=Rf is stored per player in 5th dimension and used from there.\n2=Rf is stored per teleplate and when transfering double the transfer amount will be consumed from sending teleplate.");
		rfPerTransfer = config.getInt("RfPerTransfer", "RF", 25000, 0, Integer.MAX_VALUE, "Amount of rf teleplate will consume to transfer player to/from 5th dimension.");
		rfStrorage = config.getInt("RfStorage", "RF", rfPerTransfer * 10, rfPerTransfer * 2, Integer.MAX_VALUE, "Amount of rf stored in 5th dimensdion or teleplate (depends on rfUsageType) used to power teleplates.");
		teleplateRecipe = EnumTeleplateRecipe.valueOf(config.getString("teleplateRecipe", "Recipes", EnumTeleplateRecipe.TELEPADS.toString(), "Recipe to use for teleplate...\n" + EnumTeleplateRecipe.descAll(), EnumTeleplateRecipe.strValues()));
		teleplateRecipeResult = config.getInt("teleplateRecipeResultCount", "Recipes", 1, 1, 64, "Amount of teleplates as result of recipe");
		portableTeleplateRecipe = EnumPortableTeleplateRecipe.valueOf(config.getString("portableTeleplateRecipe", "Recipes", EnumPortableTeleplateRecipe.TELEPLATES_DEFAULT.toString(), "Recipe to use for portable teleplate...\n" + EnumPortableTeleplateRecipe.descAll(), EnumPortableTeleplateRecipe.strValues()));
		portableTeleplateRecipeResult = config.getInt("portableTeleplateRecipeResultCount", "Recipes", 1, 1, 64, "Amount of portable teleplates as result of recipe");
		config.save();

		logger.info("Successfully loaded settings...");
	}

	public static void init(FMLInitializationEvent event){
		if(teleplateRecipe != EnumTeleplateRecipe.NONE){
			GameRegistry.addRecipe(new ItemStack(TeleplatesBase.teleplate, teleplateRecipeResult), teleplateRecipe.getRecipe());
		}
		if(portableTeleplateRecipe != EnumPortableTeleplateRecipe.NONE){
			GameRegistry.addRecipe(new ItemStack(TeleplatesBase.portableTeleplate, portableTeleplateRecipeResult), portableTeleplateRecipe.getRecipe());
		}
	}

	public static void postInit(FMLPostInitializationEvent event){

	}

	public static boolean permissionsSystemActive(){
		return permissions && MinecraftServer.getServer().isDedicatedServer();
	}
	
	public static enum EnumTeleplateRecipe {

		NONE(),
		TELEPADS("GGG", "RER", "RIR", 'G', Blocks.glass, 'R', Items.redstone, 'E', Items.ender_pearl, 'I', Blocks.iron_block),
		TELEPLATES_EASY("GGG", "DED", "III", 'G', Blocks.glass_pane, 'D', Items.diamond, 'E', Items.ender_pearl, 'I', Blocks.heavy_weighted_pressure_plate),
		TELEPLATES_NORMAL("GGG", "DED", "III", 'G', Blocks.glass, 'D', Items.emerald, 'E', Items.ender_pearl, 'I', Blocks.iron_block),
		TELEPLATES_HARD("GGG", "DED", "III", 'G', Blocks.glass, 'D', Items.ender_pearl, 'E', Items.nether_star, 'I', Blocks.iron_block);

		private Object[] recipe;

		private EnumTeleplateRecipe(Object... recipe) {
			this.recipe = recipe;
		}

		public Object[] getRecipe(){
			return recipe;
		}
		
		public String desc(){
			String s = super.toString() + "{";
			for(Object o : recipe){
				String ss;
				if(o instanceof Item){
					ss = Item.itemRegistry.getNameForObject(o);
				} else if(o instanceof Block){
					ss = Block.blockRegistry.getNameForObject(o);
				} else if(o instanceof ItemStack){
					ss = Item.itemRegistry.getNameForObject(((ItemStack) o).getItem()) + ":" + ((ItemStack) o).getItemDamage();
				} else {
					ss = o.toString();
				}
				s += ss + ", ";
			}
			if(s.lastIndexOf(',') != -1){
				s = s.substring(0, s.lastIndexOf(','));
			}
			s += "}";
			return s;
		}

		public static String[] strValues() {
			String[] str = new String[values().length];
			for(int i = 0; i < str.length; i++){
				str[i] = values()[i].name();
			}
			return str;
		}
		
		public static String descAll(){
			String str = "";
			for(EnumTeleplateRecipe rec : values()){
				str += rec.desc() + "\n";
			}
			return str;
		}
	}

	public static enum EnumPortableTeleplateRecipe {

		NONE(),
		LEATHERED_TELEPADS("GGG", "RER", "LIL", 'G', Blocks.glass, 'R', Items.redstone, 'E', Items.ender_pearl, 'I', Blocks.iron_block, 'L', Items.leather),
		LEATHERED_TELEPLATES_EASY("GGG", "DED", "LIL", 'G', Blocks.glass_pane, 'D', Items.diamond, 'E', Items.ender_pearl, 'I', Blocks.heavy_weighted_pressure_plate, 'L', Items.leather),
		LEATHERED_TELEPLATES_NORMAL("GGG", "DED", "LIL", 'G', Blocks.glass, 'D', Items.emerald, 'E', Items.ender_pearl, 'I', Blocks.iron_block, 'L', Items.leather),
		LEATHERED_TELEPLATES_HARD("GGG", "DED", "LIL", 'G', Blocks.glass, 'D', Items.ender_pearl, 'E', Items.nether_star, 'I', Blocks.iron_block, 'L', Items.leather),
		TELEPLATES_DEFAULT("LGL", "GTG", "LGL", 'L', Items.leather, 'G', Blocks.glass_pane, 'T', TeleplatesBase.teleplate);

		private Object[] recipe;

		private EnumPortableTeleplateRecipe(Object... recipe) {
			this.recipe = recipe;
		}

		public Object[] getRecipe(){
			return recipe;
		}

		public String desc(){
			String s = super.toString() + "{";
			for(Object o : recipe){
				String ss;
				if(o instanceof Item){
					ss = Item.itemRegistry.getNameForObject(o);
				} else if(o instanceof Block){
					ss = Block.blockRegistry.getNameForObject(o);
				} else if(o instanceof ItemStack){
					ss = Item.itemRegistry.getNameForObject(((ItemStack) o).getItem()) + ":" + ((ItemStack) o).getItemDamage();
				} else {
					ss = o.toString();
				}
				s += ss + ", ";
			}
			if(s.lastIndexOf(',') != -1){
				s = s.substring(0, s.lastIndexOf(','));
			}
			s += "}";
			return s;
		}
		
		public static String[] strValues() {
			String[] str = new String[values().length];
			for(int i = 0; i < str.length; i++){
				str[i] = values()[i].name();
			}
			return str;
		}
		
		public static String descAll(){
			String str = "";
			for(EnumPortableTeleplateRecipe rec : values()){
				str += rec.desc() + "\n";
			}
			return str;
		}
	}

}
