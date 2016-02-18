package code.elix_x.mods.teleplates.consomation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

public class ConsomationManager {

	public static final Logger logger = LogManager.getLogger("Teleplates Consomation Manager");

	private static Map<String, Class<? extends IConsomationManager>> allManagers = new HashMap<String, Class<? extends IConsomationManager>>();

	private static List<Class<? extends IConsomationManager>> activeManagers = new ArrayList<Class<? extends IConsomationManager>>();

	public static void register(Class<? extends IConsomationManager> clas, String name){
		allManagers.put(name, clas);
	}

	public static Collection<String> getAllNames(){
		return allManagers.keySet();
	}

	public static void activate(String... names) {
		activeManagers.clear();
		for(String name : names){
			activeManagers.add(allManagers.get(name));
		}
	}

	public static boolean isActive(Class<? extends IConsomationManager> manager){
		return activeManagers.contains(manager);
	}

	public static void config(Configuration config){
		for(Class<? extends IConsomationManager> clas : allManagers.values()){
			try {
				clas.getMethod("config", Configuration.class).invoke(null, config);
			} catch (Exception e){
				logger.error("Caught exception while configuring consomation manager (" + clas.getName() + "), it will not be configured:", e);
			}
		}
	}



	private TeleplatesSavedData savedData;

	private List<IConsomationManager> currentlyActiveManagers = new ArrayList<IConsomationManager>();
	private Map<Class<? extends IConsomationManager>, IConsomationManager> currentlyActiveManagersMap = new HashMap<Class<? extends IConsomationManager>, IConsomationManager>();

	public ConsomationManager(TeleplatesSavedData savedData) {
		this.savedData = savedData;

		for(Class<? extends IConsomationManager> clas : activeManagers){
			if(clas != null){
				try {
					IConsomationManager manager = clas.newInstance();
					currentlyActiveManagers.add(manager);
					currentlyActiveManagersMap.put(clas, manager);
				} catch (Exception e){
					logger.error("Caught exception while instantiating consomation manager (" + clas.getName() + "), it will not be added:", e);
				}
			}
		}
	}

	public boolean isManagerActive(Class<? extends IConsomationManager> clas){
		return currentlyActiveManagersMap.containsKey(clas);
	}

	public <T extends IConsomationManager> T getActiveConsomationManager(Class<T> clas){
		return (T) currentlyActiveManagersMap.get(clas);
	}

	public boolean canTeleportFromTeleplate(EntityPlayer player){
		boolean b = true;
		for(IConsomationManager manager : currentlyActiveManagers){
			b &= manager.canTeleportFromTeleplate(player);
		}
		return b;
	}

	public boolean canTeleportFromPortableTeleplate(EntityPlayer player) {
		boolean b = true;
		for(IConsomationManager manager : currentlyActiveManagers){
			b &= manager.canTeleportFromPortableTeleplate(player);
		}
		return b;
	}

	public void onTransfer(EntityPlayer player){
		for(IConsomationManager manager : currentlyActiveManagers){
			manager.onTransfer(player);
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		for(IConsomationManager manager : currentlyActiveManagers){
			nbt = manager.writeToNBT(nbt);
		}
		return nbt;
	}

	public void readFromNBT(NBTTagCompound nbt){
		for(IConsomationManager manager : currentlyActiveManagers){
			manager.readFromNBT(nbt);
		}
	}

}
