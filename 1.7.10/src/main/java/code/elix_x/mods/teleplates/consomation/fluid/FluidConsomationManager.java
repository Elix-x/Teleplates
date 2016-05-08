package code.elix_x.mods.teleplates.consomation.fluid;

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
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import cpw.mods.fml.common.Loader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

public class FluidConsomationManager implements IConsomationManager {

	public static final Logger logger = LogManager.getLogger("Teleplates Fluid Consomation Manager");

	public FluidConsomationManager(){

	}

	public static int fluidConsomationType = 0;
	public static int mbPerTransfer = 10000;
	public static int mbStorage = 100000;
	public static Fluid fluidToConsume = FluidRegistry.LAVA;

	private Map<UUID, FluidStorage> storages = new HashMap<UUID, FluidStorage>();

	public static FluidStorage getDefaultStorage(){
		return new FluidStorage(fluidToConsume, mbStorage);
	}

	public FluidStorage getFluidStorage(UUID owner){
		FluidStorage storage = storages.get(owner);
		if(storage == null){
			storage = getDefaultStorage();
			storages.put(owner, storage);
		}
		return storage;
	}

	public FluidStorage getFluidStorage(EntityPlayer owner){
		return getFluidStorage(EntityPlayer.func_146094_a(owner.getGameProfile()));
	}

	/*
	 * 
	 */

	public boolean canFill(TileEntityTeleplate teleplate, ForgeDirection from, Fluid fluid){
		return fluidConsomationType > 0 && from == ForgeDirection.DOWN;
	}

	public boolean canDrain(TileEntityTeleplate teleplate, ForgeDirection from, Fluid fluid){
		return false;
	}

	public FluidTankInfo[] getTankInfo(TileEntityTeleplate teleplate, ForgeDirection from){
		switch (fluidConsomationType) {
		case 0:
			return new FluidTankInfo[]{};
		case 1:
			return new FluidTankInfo[]{getFluidStorage(teleplate.getOwner()).getInfo()};
		case 2:
			return new FluidTankInfo[]{teleplate.fluidStorage.getInfo()};
		default:
			logger.warn("DEFAULT CASE!!! THIS SHOULD NOT HAPPEN!!!");
			return new FluidTankInfo[]{};
		}
	}

	public int fill(TileEntityTeleplate teleplate, ForgeDirection from, FluidStack resource, boolean doFill){
		switch (fluidConsomationType) {
		case 0:
			return 0;
		case 1:
			return getFluidStorage(teleplate.getOwner()).fill(resource, doFill);
		case 2:
			return teleplate.fluidStorage.fill(resource, doFill);
		default:
			logger.warn("DEFAULT CASE!!! THIS SHOULD NOT HAPPEN!!!");
			return 0;
		}
	}

	public FluidStack drain(TileEntityTeleplate teleplate, ForgeDirection from, FluidStack resource, boolean doDrain){
		return null;
	}

	public FluidStack drain(TileEntityTeleplate teleplate, ForgeDirection from, int maxDrain, boolean doDrain){
		return null;
	}

	/*
	 * 
	 */

	public FluidStack getFluid(ItemStack itemstack){
		switch (fluidConsomationType) {
		case 0:
			return null;
		case 1:
			return null;
		case 2:
			return ((ItemPortableTeleplate) itemstack.getItem()).getFluid_(itemstack);
		default:
			return null;
		}
	}

	public int getCapacity(ItemStack itemstack){
		switch (fluidConsomationType) {
		case 0:
			return 0;
		case 1:
			return 0;
		case 2:
			return ((ItemPortableTeleplate) itemstack.getItem()).getCapacity_(itemstack);
		default:
			return 0;
		}
	}

	public int fill(ItemStack itemstack, FluidStack resource, boolean doFill){
		switch (fluidConsomationType) {
		case 0:
			return 0;
		case 1:
			return 0;
		case 2:
			return ((ItemPortableTeleplate) itemstack.getItem()).fill_(itemstack, resource, doFill);
		default:
			return 0;
		}
	}

	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain){
		return null;
	}

	/*
	 * 
	 */

	@Override
	public boolean canTeleportFromTeleplate(EntityPlayer player){
		switch(fluidConsomationType){
		case 0:
			return true;
		case 1:
			return getFluidStorage(player).getFluidAmount() >= mbPerTransfer * 2;
		case 2:
			BlockPos pos;
			if(player.worldObj.isRemote){
				pos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY - 1), (int) Math.floor(player.posZ));
			} else {
				pos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ));
			}
			TileEntityTeleplate teleplate = (TileEntityTeleplate) pos.getTileEntity(player.worldObj);
			return player.worldObj.isRemote || teleplate.fluidStorage.getFluidAmount() >= mbPerTransfer * 2;
		}
		return false;
	}

	@Override
	public boolean canTeleportFromPortableTeleplate(EntityPlayer player){
		switch(fluidConsomationType){
		case 0:
			return true;
		case 1:
			return getFluidStorage(player).getFluidAmount() >= mbPerTransfer * 2;
		case 2:
			return ((ItemPortableTeleplate) player.getCurrentEquippedItem().getItem()).getFluid(player.getCurrentEquippedItem()).amount >= mbPerTransfer * 2;
		default:
			logger.warn("DEFAULT CASE!!! THIS SHOULD NOT HAPPEN!!!");
			return false;
		}
	}

	@Override
	public void onTransfer(EntityPlayer player){
		if(fluidConsomationType != 0){
			BlockPos pos;
			if(player.worldObj.isRemote){
				pos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY - 1), (int) Math.floor(player.posZ));
			} else {
				pos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ));
			}
			if(pos.getTileEntity(player.worldObj) != null && pos.getTileEntity(player.worldObj) instanceof TileEntityTeleplate){
				TileEntityTeleplate teleplate = (TileEntityTeleplate) pos.getTileEntity(player.worldObj);
				switch (fluidConsomationType) {
				case 1:
					getFluidStorage(player).drain(mbPerTransfer, true);
				case 2:
					teleplate.fluidStorage.drain(mbPerTransfer, true);
				}
			} else if(player.getItemInUse() != null && player.getItemInUse().getItem() == TeleplatesBase.portableTeleplate){
				switch (fluidConsomationType) {
				case 1:
					getFluidStorage(player).drain(mbPerTransfer, true);
				case 2:
					((ItemPortableTeleplate) player.getCurrentEquippedItem().getItem()).drain_(player.getCurrentEquippedItem(), mbPerTransfer, true);
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		NBTTagList list = new NBTTagList();
		for(Entry<UUID, FluidStorage> entry : storages.entrySet()){
			NBTTagCompound tag = new NBTTagCompound();
			tag = entry.getValue().writeToNBT(tag);
			tag.setString("uuid", entry.getKey().toString());
			list.appendTag(tag);
		}
		nbt.setTag("fluid", list);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		storages.clear();

		NBTTagList list = (NBTTagList) nbt.getTag("fluid");
		for(int i = 0; i < list.tagCount(); i++){
			NBTTagCompound tag = list.getCompoundTagAt(i);
			storages.put(UUID.fromString(tag.getString("uuid")), getDefaultStorage().readFromNBT(tag));
		}
	}

	@Override
	public String getName(){
		return "FLUID";
	}

	public static void config(Configuration config) {
		fluidConsomationType = config.getInt("consomationType", "CONSOMATION-FLUID", 1, 0, 2, "Type of fluid usage:\n0=No fluid Usage.\n1=Fluid is stored per player in 5th dimension and used from there.\n2=Fluid is stored per teleplate and when transfering double the transfer amount will be consumed from sending teleplate.");
		mbPerTransfer = config.getInt("mbPerTransfer", "CONSOMATION-FLUID", 25000, 0, Integer.MAX_VALUE, "Amount of millibuckets teleplate will consume to transfer player to/from 5th dimension.");
		mbStorage = config.getInt("mbStorage", "CONSOMATION-FLUID", mbPerTransfer * 10, mbPerTransfer * 2, Integer.MAX_VALUE, "Amount of millibuckets stored in 5th dimensdion or teleplate (depends on mbUsageType) used to power teleplates.");
		fluidToConsume = FluidRegistry.getFluid(config.getString("fluidToConsume", "CONSOMATION-FLUID", Loader.isModLoaded("AWWayofTime") ? "Life Essence" : Loader.isModLoaded("ThermalFoundation") ? "ender" : FluidRegistry.LAVA.getName(), "Type of fluid that teleplates will consume."));
	}

}
