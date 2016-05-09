package code.elix_x.mods.teleplates.consumption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

public class ConsumptionManager {

	public static final Logger logger = LogManager.getLogger("Teleplates Consommation Manager");

	private static BiMap<String, Class<? extends IConsumptionManager>> allManagers = HashBiMap.create();
	private static BiMap<Class<? extends IConsumptionManager>, String> allManagerss = allManagers.inverse();

	public static void register(Class<? extends IConsumptionManager> clas, String name){
		allManagers.put(name, clas);
	}

	public static Collection<String> getAllNames(){
		return allManagers.keySet();
	}

	public static Class<? extends IConsumptionManager> getManager(String name){
		return allManagers.get(name);
	}

	public static String getName(Class<? extends IConsumptionManager> manager){
		return allManagerss.get(manager);
	}

	public static String getName(IConsumptionManager manager){
		return getName(manager.getClass());
	}

	private TeleplatesSavedData savedData;

	private List<IConsumptionManager> activeManagers = new ArrayList<IConsumptionManager>();
	private Map<Class<? extends IConsumptionManager>, IConsumptionManager> activeManagersMap = new HashMap<Class<? extends IConsumptionManager>, IConsumptionManager>();

	public ConsumptionManager(TeleplatesSavedData savedData, Configuration config){
		this.savedData = savedData;

		String[] valid = allManagers.keySet().toArray(new String[0]);
		for(String s : config.getStringList("Consomation Managers", "Consomation", new String[]{}, "Consomation managers to use.\nConsomation managers: " + Arrays.toString(valid), valid)){
			Class<? extends IConsumptionManager> c = allManagers.get(s);
			if(c != null){
				try {
					IConsumptionManager m = c.newInstance();
					m.config(config.getCategory("Consomation." + s));
					activeManagers.add(m);
				} catch(Exception e){
					logger.error("Caught exception while loading consomation manager (" + s + "): ", e);
				}
			}
		}

		config.save();
	}

	public List<IConsumptionManager> getActiveManagers(){
		return activeManagers;
	}

	public boolean isManagerActive(Class<? extends IConsumptionManager> clas){
		return activeManagersMap.containsKey(clas);
	}

	public <T extends IConsumptionManager> T getActiveConsomationManager(Class<T> clas){
		return (T) activeManagersMap.get(clas);
	}

	public boolean canTeleportFromTeleplate(EntityPlayer player){
		boolean b = true;
		for(IConsumptionManager manager : activeManagers){
			b &= manager.canTeleportFromTeleplate(player);
		}
		return b;
	}

	public boolean canTeleportFromPortableTeleplate(EntityPlayer player){
		boolean b = true;
		for(IConsumptionManager manager : activeManagers){
			b &= manager.canTeleportFromPortableTeleplate(player);
		}
		return b;
	}

	public void onTransfer(EntityPlayer player){
		for(IConsumptionManager manager : activeManagers){
			manager.onTransfer(player);
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		for(IConsumptionManager manager : activeManagers){
			nbt = manager.writeToNBT(nbt);
		}
		return nbt;
	}

	public void readFromNBT(NBTTagCompound nbt){
		for(IConsumptionManager manager : activeManagers){
			manager.readFromNBT(nbt);
		}
	}

}
