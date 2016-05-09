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
import code.elix_x.mods.teleplates.events.WorldLoadEvents;
import code.elix_x.mods.teleplates.net.CooldownChangeMessage;
import code.elix_x.mods.teleplates.net.SetTeleplateSettingsMessage;
import code.elix_x.mods.teleplates.net.SynchronizeTeleplatesMessage;
import code.elix_x.mods.teleplates.net.TeleportToTeleplateMessage;
import code.elix_x.mods.teleplates.proxy.ITeleplatesProxy;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import code.elix_x.mods.teleplates.teleplates.TeleportationManager;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = TeleplatesBase.MODID, name = TeleplatesBase.NAME, version = TeleplatesBase.VERSION, dependencies = "required-after:" + EXCore.DEPENDENCY, acceptedMinecraftVersions = EXCore.MCVERSION)
public class TeleplatesBase {

	public static final String MODID = "teleplates";
	public static final String NAME = "Teleplates";
	public static final String VERSION = "1.2.7";

	public static final Logger logger = LogManager.getLogger(NAME);

	@SidedProxy(modId = MODID, clientSide = "code.elix_x.mods.teleplates.proxy.ClientProxy", serverSide = "code.elix_x.mods.teleplates.proxy.CommonProxy")
	public static ITeleplatesProxy proxy;

	public static SmartNetworkWrapper net;

	public static Block teleplate;

	//TODO
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
		net.registerMessage1(new Function<Pair<TeleportToTeleplateMessage, MessageContext>, Runnable>(){

			@Override
			public Runnable apply(final Pair<TeleportToTeleplateMessage, MessageContext> pair){
				return new Runnable(){

					@Override
					public void run(){
						TeleportationManager.teleport(pair.getRight().getServerHandler().playerEntity, pair.getLeft().teleplate);
					}

				};
			}

		}, TeleportToTeleplateMessage.class, Side.SERVER);
		net.registerMessage3(new Function<SynchronizeTeleplatesMessage, Runnable>(){

			@Override
			public Runnable apply(final SynchronizeTeleplatesMessage message){
				return new Runnable(){

					@Override
					public void run(){
						TeleplatesSavedData.get(Minecraft.getMinecraft().theWorld).readFromNBT(message.nbt);
					}

				};
			}

		}, SynchronizeTeleplatesMessage.class, Side.CLIENT);
		net.registerMessage3(new Function<CooldownChangeMessage, Runnable>(){

			@Override
			public Runnable apply(final CooldownChangeMessage message){
				return new Runnable(){

					@Override
					public void run(){
						TeleportationManager.setCooldown(Minecraft.getMinecraft().thePlayer, message.cooldown);
					}

				};
			}

		}, CooldownChangeMessage.class, Side.CLIENT);

		teleplate = new BlockTeleplate().setRegistryName(MODID, "teleplate");
		GameRegistry.register(teleplate);

		ConfigurationManager.preInit(event);

		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(new OnPlayerJoinEvent());
		MinecraftForge.EVENT_BUS.register(new OnPlayerTickEvent());
		MinecraftForge.EVENT_BUS.register(new BlockBreakEvent());
		MinecraftForge.EVENT_BUS.register(new WorldLoadEvents());

		ConfigurationManager.init(event);

		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		ConfigurationManager.postInit(event);

		proxy.postInit(event);
	}

}
