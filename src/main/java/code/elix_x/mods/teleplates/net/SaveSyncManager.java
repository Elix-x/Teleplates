package code.elix_x.mods.teleplates.net;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.config.ConfigurationManager;
import code.elix_x.mods.teleplates.energy.EnergyManager;
import code.elix_x.mods.teleplates.teleplates.TeleplatesManager;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;

public class SaveSyncManager {

	public static final Logger logger = LogManager.getLogger("Teleplates Save Manager");
	
	public static void load(Load event){
		if(event.world.provider.dimensionId == 0 && !event.world.isRemote){
			reset();

			File teleplates = new File(event.world.getSaveHandler().getWorldDirectory(), "teleplates.dat");
			try {
				teleplates.createNewFile();
			} catch (IOException e) {
				logger.error("Caught exception while creating teleplates file: ", e);
			}

			try{
				NBTTagCompound nbt = CompressedStreamTools.read(teleplates);
				TeleplatesManager.readFromNBT(nbt);
				EnergyManager.readFromNBT(nbt);
				if(ConfigurationManager.debug){
					logger.debug("Loaded data from file: ");
					TeleplatesManager.logDebugInfo();
					EnergyManager.logDebugInfo();
				}
			} catch(Exception e){
				logger.error("Caught exception while reading teleplates file: ", e);
			}
		}
	}

	public static void save(Save event){
		if(event.world.provider.dimensionId == 0 && !event.world.isRemote){
			File teleplates = new File(event.world.getSaveHandler().getWorldDirectory(), "teleplates.dat");
			try {
				teleplates.createNewFile();
			} catch (IOException e) {
				logger.error("Caught exception while creating teleplates file: ", e);
			}		

			try {
				NBTTagCompound nbt = new NBTTagCompound();
				TeleplatesManager.writeToNBT(nbt);
				EnergyManager.writeToNBT(nbt);
				CompressedStreamTools.write(nbt, teleplates);
				if(ConfigurationManager.debug){
					logger.debug("Saved data to file: ");
					TeleplatesManager.logDebugInfo();
					EnergyManager.logDebugInfo();
				}
			} catch (IOException e) {
				logger.error("Caught exception while writing teleplates file: ", e);
			}
		}
	}
	
	public static void synchronizeWith(EntityPlayerMP player){
		if(ConfigurationManager.debug){
			logger.debug("Synchronizing " + player);
		}
		
		NBTTagCompound nbt = new NBTTagCompound();
		TeleplatesManager.writeToNBT(nbt);
		EnergyManager.writeToNBT(nbt);
		TeleplatesBase.net.sendTo(new SynchronizeTeleplatesMessage(nbt), player);
	}

	public static void synchronizeWithAll(){
		if(ConfigurationManager.debug){
			logger.debug("Synchronizing ALL");
		}
		
		NBTTagCompound nbt = new NBTTagCompound();
		TeleplatesManager.writeToNBT(nbt);
		EnergyManager.writeToNBT(nbt);
		TeleplatesBase.net.sendToAll(new SynchronizeTeleplatesMessage(nbt));
	}
	
	public static void onShynchronize(NBTTagCompound nbt){
		if(ConfigurationManager.debug){
			logger.debug("Synchronizing with server...");
		}
		
		reset();
		TeleplatesManager.readFromNBT(nbt);
		EnergyManager.readFromNBT(nbt);
		
		if(ConfigurationManager.debug){
			logger.debug("Synchronized with server");
			TeleplatesManager.logDebugInfo();
			EnergyManager.logDebugInfo();
		}
	}
	
	public static void onServerStarting(FMLServerStartingEvent event){

	}

	public static void onServerStopping(FMLServerStoppingEvent event){

	}

	public static void onServerStopped(FMLServerStoppedEvent event){
		reset();
	}
	
	public static void reset(){
		if(ConfigurationManager.debug){
			logger.debug("Resetting...");
			TeleplatesManager.logDebugInfo();
			EnergyManager.logDebugInfo();
		}
		TeleplatesManager.reset();
		EnergyManager.reset();
		if(ConfigurationManager.debug){
			logger.debug("Reseted");
			TeleplatesManager.logDebugInfo();
			EnergyManager.logDebugInfo();
		}
	}
	
}
