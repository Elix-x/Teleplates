package code.elix_x.mods.teleplates;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Function;

import code.elix_x.excore.EXCore;
import code.elix_x.excore.utils.packets.SmartNetworkWrapper;
import code.elix_x.mods.teleplates.blocks.BlockTeleplate;
import code.elix_x.mods.teleplates.config.ConfigurationManager;
import code.elix_x.mods.teleplates.events.BlockBreakEvent;
import code.elix_x.mods.teleplates.events.OnPlayerJoinEvent;
import code.elix_x.mods.teleplates.events.OnPlayerTickEvent;
import code.elix_x.mods.teleplates.events.OpPlayerTeleplateEvents;
import code.elix_x.mods.teleplates.events.SaveLoadEvent;
import code.elix_x.mods.teleplates.items.ItemPortableTeleplate;
import code.elix_x.mods.teleplates.net.SetTeleplateSettingsMessage;
import code.elix_x.mods.teleplates.net.SynchronizeTeleplatesMessage;
import code.elix_x.mods.teleplates.net.TeleportToTeleplateMessage;
import code.elix_x.mods.teleplates.proxy.ITeleplatesProxy;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = TeleplatesBase.MODID, name = TeleplatesBase.NAME, version = TeleplatesBase.VERSION, dependencies = "required-after:" + EXCore.DEPENDENCY + ";after:ThermalFoundation;after:Thaumcraft;after:AWWayofTime", acceptedMinecraftVersions = EXCore.MCVERSION, acceptableSaveVersions = "[1.1,)")
public class TeleplatesBase {

	public static final String MODID = "teleplates";
	public static final String NAME = "Teleplates";
	public static final String VERSION = "1.2.4";

	public static final Logger logger = LogManager.getLogger(NAME);

	@SidedProxy(modId = MODID, clientSide = "code.elix_x.mods.teleplates.proxy.ClientProxy", serverSide = "code.elix_x.mods.teleplates.proxy.CommonProxy")
	public static ITeleplatesProxy proxy;

	public static SmartNetworkWrapper net;

	public static Block teleplate;

	public static Item portableTeleplate;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		net = new SmartNetworkWrapper(NAME);
		net.registerMessage1(new Function<Pair<SetTeleplateSettingsMessage, MessageContext>, Runnable>(){

			@Override
			public Runnable apply(final Pair<SetTeleplateSettingsMessage, MessageContext> pair){
				return new Runnable(){

					public void run(){
						SetTeleplateSettingsMessage message = pair.getKey();
						MessageContext context = pair.getValue();
						TeleplatesSavedData.get(context.getServerHandler().playerEntity.worldObj).getTeleplatesManager().setTeleplateSettings(message.teleplate, context.getServerHandler().playerEntity);
					}

				};
			}

		}, SetTeleplateSettingsMessage.class, Side.SERVER);
		net.registerMessage(new SynchronizeTeleplatesMessage.SynchronizeTeleplatesMessageHandler(), SynchronizeTeleplatesMessage.class, Side.CLIENT);
		net.registerMessage(new TeleportToTeleplateMessage.TeleportToTeleplateMessageHandler(), TeleportToTeleplateMessage.class, Side.SERVER);

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

}
