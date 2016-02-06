package code.elix_x.mods.teleplates.consomation.energy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import code.elix_x.excore.utils.pos.BlockPos;
import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.consomation.IConsomationManager;
import code.elix_x.mods.teleplates.items.ItemPortableTeleplate;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import cofh.api.energy.EnergyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;

public class EnergyConsomationManager implements IConsomationManager {

	public static final Logger logger = LogManager.getLogger("Teleplates Energy Consomation Manager");

	public static int rfUsageType = 1;
	public static int rfPerTransfer = 25000;
	public static int rfStorage = 250000;

	private Map<UUID, EnergyStorage> storages = new HashMap<UUID, EnergyStorage>();
	
	public EnergyConsomationManager(){
		
	}
	
	public static EnergyStorage getDefaultStorage() {
		return new EnergyStorage(rfStorage);
	}

	public EnergyStorage getEnergyStorage(UUID owner){
		EnergyStorage storage = storages.get(owner);
		if(storage == null){
			storage = getDefaultStorage();
			storages.put(owner, storage);
		}
		return storage;
	}

	public EnergyStorage getEnergyStorage(EntityPlayer owner){
		return getEnergyStorage(EntityPlayer.func_146094_a(owner.getGameProfile()));
	}

	public boolean canConnectEnergy(TileEntityTeleplate teleplate, ForgeDirection from){
		return rfUsageType > 0 && from == ForgeDirection.DOWN;
	}

	public int receiveEnergy(TileEntityTeleplate teleplate, ForgeDirection from, int maxReceive, boolean simulate) {
		switch (rfUsageType) {
		case 0:
			return 0;

		case 1:
			int i = getEnergyStorage(teleplate.getOwner()).receiveEnergy(maxReceive, simulate);
//			if(i > 0) TeleplatesSavedData.synchronizeWithAll();
			return i;

		case 2:
			return teleplate.energyStorage.receiveEnergy(maxReceive, simulate);

		default:
			return 0;
		}
	}

	public int getEnergyStored(TileEntityTeleplate teleplate, ForgeDirection from) {
		switch (rfUsageType) {
		case 0:
			return 0;

		case 1:
			return getEnergyStorage(teleplate.getOwner()).getEnergyStored();

		case 2:
			return teleplate.energyStorage.getEnergyStored();

		default:
			return 0;
		}
	}

	public int getMaxEnergyStored(TileEntityTeleplate teleplate, ForgeDirection from) {
		switch (rfUsageType) {
		case 0:
			return 0;

		case 1:
			return getEnergyStorage(teleplate.getOwner()).getMaxEnergyStored();

		case 2:
			return teleplate.energyStorage.getMaxEnergyStored();

		default:
			return 0;
		}
	}

	public int receiveEnergy(ItemStack itemstack, int maxReceive, boolean simulate) {
		switch (rfUsageType) {
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

	public int extractEnergy(ItemStack itemstack, int maxExtract, boolean simulate) {
		return 0;
	}

	public int getEnergyStored(ItemStack itemstack) {
		switch (rfUsageType) {
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

	public int getMaxEnergyStored(ItemStack itemstack) {
		switch (rfUsageType) {
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

	public boolean canTeleportFromTeleplate(EntityPlayer player){
		if(rfUsageType == 0){
			return true;
		} else {
			switch (rfUsageType) {
			case 1:
				return getEnergyStorage(player).getEnergyStored() >= rfPerTransfer * 2;
			case 2:
				BlockPos pos;
				if(player.worldObj.isRemote){
					pos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY - 1), (int) Math.floor(player.posZ));
				} else {
					pos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ));
				}
				TileEntityTeleplate teleplate = (TileEntityTeleplate) pos.getTileEntity(player.worldObj);
				return player.worldObj.isRemote || teleplate.energyStorage.getEnergyStored() >= rfPerTransfer * 2;
			default:
				logger.warn("DEFAULT CASE!!! THIS SHOULD NOT HAPPEN!!!");
				return false;
			}
		}
	}

	public boolean canTeleportFromPortableTeleplate(EntityPlayer player) {
		if(rfUsageType == 0){
			return true;
		} else {
			switch (rfUsageType) {
			case 1:
				return getEnergyStorage(player).getEnergyStored() >= rfPerTransfer * 2;
			case 2:
				return ((ItemPortableTeleplate) player.getCurrentEquippedItem().getItem()).getEnergyStored(player.getCurrentEquippedItem()) >= rfPerTransfer * 2;
			default:
				logger.warn("DEFAULT CASE!!! THIS SHOULD NOT HAPPEN!!!");
				return false;
			}
		}
	}

	public void onTransfer(EntityPlayer player){
		if(rfUsageType != 0){
			BlockPos pos;
			if(player.worldObj.isRemote){
				pos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY - 1), (int) Math.floor(player.posZ));
			} else {
				pos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ));
			}
			if(pos.getTileEntity(player.worldObj) != null && pos.getTileEntity(player.worldObj) instanceof TileEntityTeleplate){
				TileEntityTeleplate teleplate = (TileEntityTeleplate) pos.getTileEntity(player.worldObj);
				switch (rfUsageType) {
				case 1:
					getEnergyStorage(player).extractEnergy(rfPerTransfer, false);
				case 2:
					teleplate.energyStorage.extractEnergy(rfPerTransfer, false);
				}
			} else if(player.getItemInUse() != null && player.getItemInUse().getItem() == TeleplatesBase.portableTeleplate){
				switch (rfUsageType) {
				case 1:
					getEnergyStorage(player).extractEnergy(rfPerTransfer, false);
				case 2:
					((ItemPortableTeleplate) player.getCurrentEquippedItem().getItem()).extractEnergy_(player.getCurrentEquippedItem(), rfPerTransfer, false);
				}
			}
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
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

	public void readFromNBT(NBTTagCompound nbt){
		storages.clear();

		NBTTagList list = (NBTTagList) nbt.getTag("energy");
		for(int i = 0; i < list.tagCount(); i++){
			NBTTagCompound tag = list.getCompoundTagAt(i);
			storages.put(UUID.fromString(tag.getString("uuid")), getDefaultStorage().readFromNBT(tag));
		}
	}

	@Override
	public String getName(){
		return "ENERGY-RF";
	}

	public static void config(Configuration config){
		rfUsageType = config.getInt("consomationType", "CONSOMATION-ENERGY-RF", 1, 0, 2, "Type of rf usage:\n0=No Rf Usage.\n1=Rf is stored per player in 5th dimension and used from there.\n2=Rf is stored per teleplate and when transfering double the transfer amount will be consumed from sending teleplate.");
		rfPerTransfer = config.getInt("RfPerTransfer", "CONSOMATION-ENERGY-RF", 25000, 0, Integer.MAX_VALUE, "Amount of rf teleplate will consume to transfer player to/from 5th dimension.");
		rfStorage = config.getInt("RfStorage", "CONSOMATION-ENERGY-RF", rfPerTransfer * 10, rfPerTransfer * 2, Integer.MAX_VALUE, "Amount of rf stored in 5th dimensdion or teleplate (depends on rfUsageType) used to power teleplates.");
	}

}
