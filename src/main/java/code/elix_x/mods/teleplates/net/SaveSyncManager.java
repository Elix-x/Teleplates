package code.elix_x.mods.teleplates.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.config.ConfigurationManager;
import code.elix_x.mods.teleplates.consomation.ConsomationManager;
import code.elix_x.mods.teleplates.consomation.energy.EnergyConsomationManager;
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
			if(teleplates.exists()){
				try{
					NBTTagCompound nbt = CompressedStreamTools.read(teleplates);
					TeleplatesManager.readFromNBT(nbt);
					ConsomationManager.readFromNBT(nbt);
					if(ConfigurationManager.debug){
						logger.debug("Loaded data from file: ");
						TeleplatesManager.logDebugInfo();
						ConsomationManager.logDebugInfo();
					}
				} catch(Exception e){
					logger.error("Caught exception while reading old teleplates file: ", e);
				} finally {
					teleplates.delete();
				}
			} else {
				teleplates = new File(event.world.getSaveHandler().getWorldDirectory(), "teleplates-move.dat");
				try {
					teleplates.createNewFile();
				} catch (IOException e) {
					logger.error("Caught exception while creating teleplates file: ", e);
				}

				try{
					NBTTagCompound nbt = CompressedStreamTools.readCompressed(new FileInputStream(teleplates));
					TeleplatesManager.readFromNBT(nbt);
					ConsomationManager.readFromNBT(nbt);
					if(ConfigurationManager.debug){
						logger.debug("Loaded data from file: ");
						TeleplatesManager.logDebugInfo();
						ConsomationManager.logDebugInfo();
					}
				} catch(Exception e){
					logger.error("Caught exception while reading teleplates file: ", e);
				}
			}
		}
	}

	public static void save(Save event){
		if(event.world.provider.dimensionId == 0 && !event.world.isRemote){
			File teleplates = new File(event.world.getSaveHandler().getWorldDirectory(), "teleplates-move.dat");
			try {
				teleplates.createNewFile();
			} catch (IOException e) {
				logger.error("Caught exception while creating teleplates file: ", e);
			}		

			try {
				NBTTagCompound nbt = new NBTTagCompound();
				TeleplatesManager.writeToNBT(nbt);
				ConsomationManager.writeToNBT(nbt);
				CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(teleplates));
				if(ConfigurationManager.debug){
					logger.debug("Saved data to file: ");
					TeleplatesManager.logDebugInfo();
					ConsomationManager.logDebugInfo();
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
		ConsomationManager.writeToNBT(nbt);
		TeleplatesBase.net.sendTo(new SynchronizeTeleplatesMessage(nbt), player);
	}

	public static void synchronizeWithAll(){
		if(ConfigurationManager.debug){
			logger.debug("Synchronizing ALL");
		}

		NBTTagCompound nbt = new NBTTagCompound();
		TeleplatesManager.writeToNBT(nbt);
		ConsomationManager.writeToNBT(nbt);
		TeleplatesBase.net.sendToAll(new SynchronizeTeleplatesMessage(nbt));
	}

	public static void onSynchronize(NBTTagCompound nbt){
		if(ConfigurationManager.debug){
			logger.debug("Synchronizing with server...");
		}

		reset();
		TeleplatesManager.readFromNBT(nbt);
		ConsomationManager.readFromNBT(nbt);

		if(ConfigurationManager.debug){
			logger.debug("Synchronized with server");
			TeleplatesManager.logDebugInfo();
			ConsomationManager.logDebugInfo();
		}
	}

	public static void onServerStopped(FMLServerStoppedEvent event){
		reset();
	}

	public static void reset(){
		if(ConfigurationManager.debug){
			logger.debug("Resetting...");
			TeleplatesManager.logDebugInfo();
			ConsomationManager.logDebugInfo();
		}
		TeleplatesManager.reset();
		ConsomationManager.reset();
		if(ConfigurationManager.debug){
			logger.debug("Reseted");
			TeleplatesManager.logDebugInfo();
			ConsomationManager.logDebugInfo();
		}
	}

}
