package code.elix_x.mods.teleplates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import code.elix_x.mods.teleplates.blocks.BlockTeleplate;
import code.elix_x.mods.teleplates.config.ConfigurationManager;
import code.elix_x.mods.teleplates.events.BlockBreakEvent;
import code.elix_x.mods.teleplates.events.OnPlayerJoinEvent;
import code.elix_x.mods.teleplates.events.OnPlayerTickEvent;
import code.elix_x.mods.teleplates.events.OpPlayerTeleplateEvents;
import code.elix_x.mods.teleplates.events.SaveLoadEvent;
import code.elix_x.mods.teleplates.items.ItemPortableTeleplate;
import code.elix_x.mods.teleplates.net.SaveSyncManager;
import code.elix_x.mods.teleplates.net.SetTeleplateNameMessage;
import code.elix_x.mods.teleplates.net.SynchronizeTeleplatesMessage;
import code.elix_x.mods.teleplates.net.TeleportToTeleplateMessage;
import code.elix_x.mods.teleplates.proxy.CommonProxy;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = TeleplatesBase.MODID, name = TeleplatesBase.NAME, version = TeleplatesBase.VERSION, dependencies = "required-after:excore")
public class TeleplatesBase {
	
	public static final String MODID = "teleplates";
	public static final String NAME = "Teleplates";
	public static final String VERSION = "1.0.1";

	public static final Logger logger = LogManager.getLogger("Teleplates");
	
	@SidedProxy(clientSide = "code.elix_x.mods.teleplates.proxy.ClientProxy", serverSide = "code.elix_x.mods.teleplates.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static SimpleNetworkWrapper net;
	
	public static Block teleplate;
	
	public static Item portableTeleplate;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		net = NetworkRegistry.INSTANCE.newSimpleChannel("teleplates");
		net.registerMessage(SetTeleplateNameMessage.SetTeleplateNameMessageHandler.class, SetTeleplateNameMessage.class, 0, Side.SERVER);
		net.registerMessage(SynchronizeTeleplatesMessage.SynchronizeTeleplatesMessageHandler.class, SynchronizeTeleplatesMessage.class, 1, Side.CLIENT);
		net.registerMessage(TeleportToTeleplateMessage.TeleportToTeleplateMessageHandler.class, TeleportToTeleplateMessage.class, 2, Side.SERVER);
		
		teleplate = new BlockTeleplate();
		GameRegistry.registerBlock(teleplate, "teleplate");
		GameRegistry.registerTileEntity(TileEntityTeleplate.class, "teleplate");
		
		portableTeleplate = new ItemPortableTeleplate();
		GameRegistry.registerItem(portableTeleplate, "portableteleplate");
		
		ConfigurationManager.preInit(event);
		
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event){
		FMLInterModComms.sendMessage("appliedenergistics2", "whitelist-spatial", TileEntityTeleplate.class.getName());
		
		MinecraftForge.EVENT_BUS.register(new SaveLoadEvent());
		FMLCommonHandler.instance().bus().register(new OnPlayerJoinEvent());
		FMLCommonHandler.instance().bus().register(new OnPlayerTickEvent());
		
		MinecraftForge.EVENT_BUS.register(new BlockBreakEvent());
		MinecraftForge.EVENT_BUS.register(new OpPlayerTeleplateEvents());
		
		ConfigurationManager.init(event);
		
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		ConfigurationManager.postInit(event);
		
		proxy.postInit(event);
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event){
		SaveSyncManager.onServerStarting(event);
	}
	
	@EventHandler
	public void onServerStopping(FMLServerStoppingEvent event){
		SaveSyncManager.onServerStopping(event);
	}
	
	@EventHandler
	public void onServerStopped(FMLServerStoppedEvent event){
		SaveSyncManager.onServerStopped(event);
	}
}
