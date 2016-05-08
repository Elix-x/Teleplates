package code.elix_x.mods.teleplates.consomation.thaumcraft;

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
import cpw.mods.fml.common.Optional.Method;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;

public class EssentiaConsomationManager implements IConsomationManager {

	public static final Logger logger = LogManager.getLogger("Teleplates Essentia Consomation Manager");

	public EssentiaConsomationManager(){

	}

	public static int essentiaConsomationType = 0;
	public static int essentiaPerTransfer = 10;
	public static int essentiaStorage = 100;
	public static Aspect aspectToConsume;

	private Map<UUID, EssentiaStorage> storages = new HashMap<UUID, EssentiaStorage>();

	public static EssentiaStorage getDefaultStorage(){
		return new EssentiaStorage(aspectToConsume, essentiaStorage);
	}

	public EssentiaStorage getEssentiaStorage(UUID owner){
		EssentiaStorage storage = storages.get(owner);
		if(storage == null){
			storage = getDefaultStorage();
			storages.put(owner, storage);
		}
		return storage;
	}

	public EssentiaStorage getEssentiaStorage(EntityPlayer owner){
		return getEssentiaStorage(EntityPlayer.func_146094_a(owner.getGameProfile()));
	}

	/*
	 * 
	 */

	public Aspect getSuctionType(TileEntityTeleplate teleplate, ForgeDirection side){
		return aspectToConsume;
	}

	public int getSuctionAmount(TileEntityTeleplate teleplate, ForgeDirection side){
		return teleplate.essentiaSuction;
	}

	public void setSuction(TileEntityTeleplate teleplate, Aspect aspect, int suction){
		if(aspect == aspectToConsume) teleplate.essentiaSuction = suction;
	}

	public int takeEssentia(TileEntityTeleplate teleplate, Aspect aspect, int amount, ForgeDirection side){
		switch (essentiaConsomationType){
		case 0:
			return 0;
		case 1:
			return getEssentiaStorage(teleplate.getOwner()).drain(amount, true);
		case 2:
			return teleplate.getEssentiaStorage().drain(amount, true);
		default:
			logger.warn("DEFAULT CASE!!! THIS SHOULD NOT HAPPEN!!!");
			return 0;
		}
	}

	public int addEssentia(TileEntityTeleplate teleplate, Aspect aspect, int amount, ForgeDirection side){
		switch (essentiaConsomationType){
		case 0:
			return 0;
		case 1:
			return getEssentiaStorage(teleplate.getOwner()).fill(aspect, amount, true);
		case 2:
			return teleplate.getEssentiaStorage().fill(aspect, amount, true);
		default:
			logger.warn("DEFAULT CASE!!! THIS SHOULD NOT HAPPEN!!!");
			return 0;
		}
	}

	public Aspect getEssentiaType(TileEntityTeleplate teleplate, ForgeDirection side){
		return aspectToConsume;
	}

	public int getEssentiaAmount(TileEntityTeleplate teleplate, ForgeDirection side){
		switch (essentiaConsomationType){
		case 0:
			return 0;
		case 1:
			return getEssentiaStorage(teleplate.getOwner()).getEssentiaAmount();
		case 2:
			return teleplate.getEssentiaStorage().getEssentiaAmount();
		default:
			logger.warn("DEFAULT CASE!!! THIS SHOULD NOT HAPPEN!!!");
			return 0;
		}
	}

	public AspectList getAspects(TileEntityTeleplate teleplate){
		AspectList list = new AspectList();
		switch (essentiaConsomationType){
		case 0:
			break;
		case 1:
			list.add(aspectToConsume, getEssentiaStorage(teleplate.getOwner()).getEssentiaAmount());
			break;
		case 2:
			list.add(aspectToConsume, teleplate.getEssentiaStorage().getEssentiaAmount());
			break;
		default:
			logger.warn("DEFAULT CASE!!! THIS SHOULD NOT HAPPEN!!!");
		}
		return list;
	}

	public void setAspects(TileEntityTeleplate teleplate, AspectList aspects){

	}

	public boolean doesContainerAccept(TileEntityTeleplate teleplate, Aspect aspect){
		return aspect == aspectToConsume;
	}

	public int addToContainer(TileEntityTeleplate teleplate, Aspect aspect, int amount){
		return amount - addEssentia(teleplate, aspect, amount, ForgeDirection.DOWN);
	}

	public boolean takeFromContainer(TileEntityTeleplate teleplate, Aspect aspect, int amount){
		return false;
	}

	public boolean takeFromContainer(TileEntityTeleplate teleplate, AspectList aspects){
		return false;
	}

	public boolean doesContainerContainAmount(TileEntityTeleplate teleplate, Aspect aspect, int amount){
		return containerContains(teleplate, aspect) >= amount;
	}

	public boolean doesContainerContain(TileEntityTeleplate teleplate, AspectList aspects){
		boolean b = true;
		for(Aspect aspect : aspects.getAspects()){
			b &= doesContainerContainAmount(teleplate, aspect, aspects.getAmount(aspect));
		}
		return b;
	}

	public int containerContains(TileEntityTeleplate teleplate, Aspect aspect) {
		return aspect == aspectToConsume ? getEssentiaAmount(teleplate, ForgeDirection.DOWN) : 0;
	}

	/*
	 * 
	 */

	public void thaumUpdate(TileEntityTeleplate teleplate){
		EssentiaStorage stor;
		switch(essentiaConsomationType){
		case 1:
			stor = getEssentiaStorage(teleplate.getOwner());
			break;
		case 2:
			stor = teleplate.getEssentiaStorage();
			break;
		default:
			return;
		}
		if(stor.getEssentiaAmount() < essentiaStorage){
			TileEntity te = teleplate.getWorldObj().getTileEntity(teleplate.xCoord, teleplate.yCoord - 1, teleplate.zCoord);
			if(te instanceof IEssentiaTransport){
				IEssentiaTransport es = (IEssentiaTransport) te;
				if(es.isConnectable(ForgeDirection.UP) && es.canOutputTo(ForgeDirection.UP)){
					stor.amount += es.takeEssentia(aspectToConsume, stor.fill(aspectToConsume, essentiaStorage, false), ForgeDirection.UP);
				}
			}
		}
	}

	/*
	 * 
	 */

	@Override
	public boolean canTeleportFromTeleplate(EntityPlayer player){
		switch(essentiaConsomationType){
		case 0:
			return true;
		case 1:
			return getEssentiaStorage(player).getEssentiaAmount() >= essentiaPerTransfer * 2;
		case 2:
			BlockPos pos;
			if(player.worldObj.isRemote){
				pos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY - 1), (int) Math.floor(player.posZ));
			} else {
				pos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ));
			}
			TileEntityTeleplate teleplate = (TileEntityTeleplate) pos.getTileEntity(player.worldObj);
			return player.worldObj.isRemote || teleplate.getEssentiaStorage().getEssentiaAmount() >= essentiaPerTransfer * 2;
		}
		return false;
	}

	@Override
	public boolean canTeleportFromPortableTeleplate(EntityPlayer player){
		switch(essentiaConsomationType){
		case 0:
			return true;
		case 1:
			return getEssentiaStorage(player).getEssentiaAmount() >= essentiaPerTransfer * 2;
		case 2:
			//			return ((ItemPortableTeleplate) player.getCurrentEquippedItem().getItem()).getFluid(player.getCurrentEquippedItem()).amount >= essentiaPerTransfer * 2;
			return false;
		default:
			logger.warn("DEFAULT CASE!!! THIS SHOULD NOT HAPPEN!!!");
			return false;
		}
	}

	@Override
	public void onTransfer(EntityPlayer player){
		if(essentiaConsomationType != 0){
			BlockPos pos;
			if(player.worldObj.isRemote){
				pos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY - 1), (int) Math.floor(player.posZ));
			} else {
				pos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ));
			}
			if(pos.getTileEntity(player.worldObj) != null && pos.getTileEntity(player.worldObj) instanceof TileEntityTeleplate){
				TileEntityTeleplate teleplate = (TileEntityTeleplate) pos.getTileEntity(player.worldObj);
				switch (essentiaConsomationType) {
				case 1:
					getEssentiaStorage(player).drain(essentiaPerTransfer, true);
				case 2:
					teleplate.getEssentiaStorage().drain(essentiaPerTransfer, true);
				}
			} else if(player.getItemInUse() != null && player.getItemInUse().getItem() == TeleplatesBase.portableTeleplate){
				switch (essentiaConsomationType) {
				case 1:
					getEssentiaStorage(player).drain(essentiaPerTransfer, true);
				case 2:
					//					((ItemPortableTeleplate) player.getCurrentEquippedItem().getItem()).drain_(player.getCurrentEquippedItem(), essentiaPerTransfer, true);
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		NBTTagList list = new NBTTagList();
		for(Entry<UUID, EssentiaStorage> entry : storages.entrySet()){
			if(entry != null && entry.getKey() != null && entry.getValue() != null){
				NBTTagCompound tag = new NBTTagCompound();
				tag = entry.getValue().writeToNBT(tag);
				tag.setString("uuid", entry.getKey().toString());
				list.appendTag(tag);
			}
		}
		nbt.setTag("essentia", list);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		storages.clear();

		NBTTagList list = (NBTTagList) nbt.getTag("essentia");
		for(int i = 0; i < list.tagCount(); i++){
			NBTTagCompound tag = list.getCompoundTagAt(i);
			storages.put(UUID.fromString(tag.getString("uuid")), getDefaultStorage().readFromNBT(tag));
		}
	}

	@Override
	public String getName(){
		return "ESSENTIA";
	}

	public static void config(Configuration config){
		essentiaConsomationType = config.getInt("consomationType", "CONSOMATION-ESSENTIA", 1, 0, 2, "Type of essentia usage:\n0=No essentia Usage.\n1=Essentia is stored per player in 5th dimension and used from there.\n2=Essentia is stored per teleplate and when transfering double the transfer amount will be consumed from sending teleplate.");
		essentiaPerTransfer = config.getInt("essentiaPerTransfer", "CONSOMATION-ESSENTIA", 25, 0, Integer.MAX_VALUE, "Amount of essentia teleplate will consume to transfer player to/from 5th dimension.");
		essentiaStorage = config.getInt("essentiaStorage", "CONSOMATION-ESSENTIA", essentiaPerTransfer * 10, essentiaPerTransfer * 2, Integer.MAX_VALUE, "Amount of essentia stored in 5th dimension or teleplate (depends on essentiaUsageType) used to power teleplates.");
		aspectToConsume = Aspect.getAspect(config.getString("aspectToConsume", "CONSOMATION-ESSENTIA", Aspect.TRAVEL.getTag(), "Aspect that teleplates will consume."));
	}

}
