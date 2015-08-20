package code.elix_x.mods.teleplates.energy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import code.elix_x.excore.utils.pos.BlockPos;
import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.config.ConfigurationManager;
import code.elix_x.mods.teleplates.items.ItemPortableTeleplate;
import code.elix_x.mods.teleplates.net.SaveSyncManager;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import cofh.api.energy.EnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

public class EnergyManager {

	public static final Logger logger = LogManager.getLogger("Teleplates Energy Manager");

	private static Map<UUID, EnergyStorage> storages = new HashMap<UUID, EnergyStorage>();

	public static EnergyStorage getDefaultStorage() {
		return new EnergyStorage(ConfigurationManager.rfStrorage);
	}

	public static EnergyStorage getStorage(UUID owner){
		EnergyStorage storage = storages.get(owner);
		if(storage == null){
			storage = getDefaultStorage();
			storages.put(owner, storage);
		}
		return storage;
	}

	public static boolean canConnectEnergy(TileEntityTeleplate teleplate, ForgeDirection from){
		return ConfigurationManager.rfUsageType > 0 && from == ForgeDirection.DOWN;
	}

	public static int receiveEnergy(TileEntityTeleplate teleplate, ForgeDirection from, int maxReceive, boolean simulate) {
		switch (ConfigurationManager.rfUsageType) {
		case 0:
			return 0;

		case 1:
			int i = getStorage(teleplate.getOwner()).receiveEnergy(maxReceive, simulate);
			if(i > 0) SaveSyncManager.synchronizeWithAll();
			return i;

		case 2:
			return teleplate.storage.receiveEnergy(maxReceive, simulate);

		default:
			return 0;
		}
	}

	public static int getEnergyStored(TileEntityTeleplate teleplate, ForgeDirection from) {
		switch (ConfigurationManager.rfUsageType) {
		case 0:
			return 0;

		case 1:
			return getStorage(teleplate.getOwner()).getEnergyStored();

		case 2:
			return teleplate.storage.getEnergyStored();

		default:
			return 0;
		}
	}

	public static int getMaxEnergyStored(TileEntityTeleplate teleplate, ForgeDirection from) {
		switch (ConfigurationManager.rfUsageType) {
		case 0:
			return 0;

		case 1:
			return getStorage(teleplate.getOwner()).getMaxEnergyStored();

		case 2:
			return teleplate.storage.getMaxEnergyStored();

		default:
			return 0;
		}
	}

	public static int receiveEnergy(ItemStack itemstack, int maxReceive, boolean simulate) {
		switch (ConfigurationManager.rfUsageType) {
		case 0:
			return 0;

		case 1:
			return 0;

		case 2:
			return ((ItemPortableTeleplate) itemstack.getItem()).receiveEnergy_(itemstack, maxReceive, simulate);

		default:
			return 0;
		}
	}

	public static int extractEnergy(ItemStack itemstack, int maxExtract, boolean simulate) {
		return 0;
	}

	public static int getEnergyStored(ItemStack itemstack) {
		switch (ConfigurationManager.rfUsageType) {
		case 0:
			return 0;

		case 1:
			return 0;

		case 2:
			return ((ItemPortableTeleplate) itemstack.getItem()).getEnergyStored_(itemstack);

		default:
			return 0;
		}
	}

	public static int getMaxEnergyStored(ItemStack itemstack) {
		switch (ConfigurationManager.rfUsageType) {
		case 0:
			return 0;

		case 1:
			return 0;

		case 2:
			return ((ItemPortableTeleplate) itemstack.getItem()).getMaxEnergyStored_(itemstack);

		default:
			return 0;
		}
	}

	public static boolean canTeleportFromTeleplate(EntityPlayer player){
		if(ConfigurationManager.rfUsageType == 0){
			return true;
		} else {
			switch (ConfigurationManager.rfUsageType) {
			case 1:
				return getStorage(EntityPlayer.func_146094_a(player.getGameProfile())).getEnergyStored() >= ConfigurationManager.rfPerTransfer * 2;
			case 2:
				BlockPos pos;
				if(/*player.getClass().getName().contains("net.minecraft.client")*/player.worldObj.isRemote){
					pos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY - 1), (int) Math.floor(player.posZ));
				} else {
					pos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ));
				}
				TileEntityTeleplate teleplate = (TileEntityTeleplate) pos.getTileEntity(player.worldObj);
				return player.worldObj.isRemote || teleplate.storage.getEnergyStored() >= ConfigurationManager.rfPerTransfer * 2;
			default:
				logger.warn("DEFAULT CASE!!! THIS SHOULD NOT HAPPEN!!!");
				return false;
			}
		}
	}

	public static boolean canTeleportFromPortableTeleplate(EntityPlayer player) {
		if(ConfigurationManager.rfUsageType == 0){
			return true;
		} else {
			switch (ConfigurationManager.rfUsageType) {
			case 1:
				return getStorage(EntityPlayer.func_146094_a(player.getGameProfile())).getEnergyStored() >= ConfigurationManager.rfPerTransfer * 2;
			case 2:
				return ((ItemPortableTeleplate) player.getCurrentEquippedItem().getItem()).getEnergyStored(player.getCurrentEquippedItem()) >= ConfigurationManager.rfPerTransfer * 2;
			default:
				logger.warn("DEFAULT CASE!!! THIS SHOULD NOT HAPPEN!!!");
				return false;
			}
		}
	}

	public static void onTransfer(EntityPlayer player){
		if(ConfigurationManager.rfUsageType != 0){
			BlockPos pos;
			if(/*player.getClass().getName().contains("net.minecraft.client")*/player.worldObj.isRemote){
				pos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY - 1), (int) Math.floor(player.posZ));
			} else {
				pos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ));
			}
			if(pos.getTileEntity(player.worldObj) != null && pos.getTileEntity(player.worldObj) instanceof TileEntityTeleplate){
				TileEntityTeleplate teleplate = (TileEntityTeleplate) pos.getTileEntity(player.worldObj);
				switch (ConfigurationManager.rfUsageType) {
				case 1:
					getStorage(EntityPlayer.func_146094_a(player.getGameProfile())).extractEnergy(ConfigurationManager.rfPerTransfer, false);
				case 2:
					teleplate.storage.extractEnergy(ConfigurationManager.rfPerTransfer, false);
				default:

				}
			} else if(player.getItemInUse() != null && player.getItemInUse().getItem() == TeleplatesBase.portableTeleplate){
				switch (ConfigurationManager.rfUsageType) {
				case 1:
					getStorage(EntityPlayer.func_146094_a(player.getGameProfile())).extractEnergy(ConfigurationManager.rfPerTransfer, false);
				case 2:
					((ItemPortableTeleplate) player.getCurrentEquippedItem().getItem()).extractEnergy_(player.getCurrentEquippedItem(), ConfigurationManager.rfPerTransfer, false);
				default:

				}
			}
		}
	}

	public static NBTTagCompound writeToNBT(NBTTagCompound nbt){
		NBTTagList list = new NBTTagList();
		for(Entry<UUID, EnergyStorage> entry : storages.entrySet()){
			NBTTagCompound tag = new NBTTagCompound();
			tag = entry.getValue().writeToNBT(tag);
			tag.setString("uuid", entry.getKey().toString());
			list.appendTag(tag);
		}
		nbt.setTag("energy", list);
		return nbt;
	}

	public static void readFromNBT(NBTTagCompound nbt){
		reset();

		NBTTagList list = (NBTTagList) nbt.getTag("energy");
		for(int i = 0; i < list.tagCount(); i++){
			NBTTagCompound tag = list.getCompoundTagAt(i);
			storages.put(UUID.fromString(tag.getString("uuid")), getDefaultStorage().readFromNBT(tag));
		}
	}

	public static void reset(){
		storages.clear();
	}

	public static void logDebugInfo() {
		logger.debug("Energies: " + storages);
	}

}
