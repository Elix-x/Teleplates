package code.elix_x.mods.teleplates.consomation;

import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import code.elix_x.excore.utils.pos.BlockPos;
import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.config.ConfigurationManager;
import code.elix_x.mods.teleplates.items.ItemPortableTeleplate;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import cofh.api.energy.EnergyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import sun.util.resources.cldr.ur.CurrencyNames_ur;

public class ConsomationManager {

	private static List<IConsomationManager> currentlyActiveManagers = new ArrayList<IConsomationManager>();

	public static boolean canTeleportFromTeleplate(EntityPlayer player){
		boolean b = true;
		for(IConsomationManager manager : currentlyActiveManagers){
			b &= manager.canTeleportFromTeleplate(player);
		}
		return b;
	}

	public static boolean canTeleportFromPortableTeleplate(EntityPlayer player) {
		boolean b = true;
		for(IConsomationManager manager : currentlyActiveManagers){
			b &= manager.canTeleportFromPortableTeleplate(player);
		}
		return b;
	}

	public static void onTransfer(EntityPlayer player){
		for(IConsomationManager manager : currentlyActiveManagers){
			manager.onTransfer(player);
		}
	}

	public static NBTTagCompound writeToNBT(NBTTagCompound nbt){
		for(IConsomationManager manager : currentlyActiveManagers){
			nbt = manager.writeToNBT(nbt);
		}
		return nbt;
	}

	public static void readFromNBT(NBTTagCompound nbt){
		for(IConsomationManager manager : currentlyActiveManagers){
			manager.readFromNBT(nbt);
		}
	}

	public static void reset() {
		for(IConsomationManager manager : currentlyActiveManagers){
			manager.reset();
		}
	}

	public static void logDebugInfo() {

	}

	public static boolean isActive(IConsomationManager manager){
		return currentlyActiveManagers.contains(manager);
	}
	
	public static boolean isActive(Class<? extends IConsomationManager> manager){
		for(IConsomationManager managerr : currentlyActiveManagers){
			if(manager.isAssignableFrom(managerr.getClass())) return true;
		}
		return false;
	}

	public static void activate(List<IConsomationManager> managers, String[] currentActiveName) {
		for(IConsomationManager manager : managers){
			if(ArrayUtils.contains(currentActiveName, manager.getName())) currentlyActiveManagers.add(manager);
		}
	}

}
