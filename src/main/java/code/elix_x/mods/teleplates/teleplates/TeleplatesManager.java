package code.elix_x.mods.teleplates.teleplates;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import code.elix_x.mods.teleplates.teleplates.Teleplate.EnumTeleplateMode;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TeleplatesManager {

	private TeleplatesSavedData savedData;

	private Map<Integer, Teleplate> idTeleplateMap = new HashMap<Integer, Teleplate>();
	private Map<Integer, Boolean> idValidityMap = new HashMap<Integer, Boolean>();

	private int nextFreeId = 0;

	public TeleplatesManager(TeleplatesSavedData savedData){
		this.savedData = savedData;
	}

	public int createTeleplate(EntityPlayer player, String name, DimBlockPos pos){
		int id = getNextFreeTeleplateId();
		Teleplate teleplate = new Teleplate(id, name, pos, EntityPlayer.func_146094_a(player.getGameProfile()), EnumTeleplateMode.PRIVATE);
		idTeleplateMap.put(id, teleplate);
		idValidityMap.put(id, true);
		savedData.synchronizeWithAll();
		return id;
	}

	public Collection<Teleplate> getAllTeleplates(){
		return idTeleplateMap.values();
	}

	public Teleplate getTeleplate(int id){
		return idTeleplateMap.get(id);
	}

	public int getNextFreeTeleplateId(){
		while(true){
			nextFreeId++;
			int uuid = nextFreeId;
			if(getTeleplate(uuid) == null){
				return uuid;
			}
		}
	}

	public void setTeleplateSettings(Teleplate teleplate, EntityPlayerMP player){
		Teleplate old = getTeleplate(teleplate.getId());
		if(old != null && old.getOwner().equals(EntityPlayer.func_146094_a(player.getGameProfile()))){
			old.setName(teleplate.getName());
			old.setMode(teleplate.getMode());
			old.setUsingList(teleplate.isUsingList());
			old.setPassword(teleplate.getPassword());
			old.setWhitelist(teleplate.isWhitelist());
			old.setList(teleplate.getList());
			savedData.synchronizeWithAll();
		}
	}

	public  void updateTeleplatePosition(TileEntityTeleplate te){
		Teleplate teleplate = getTeleplate(te.getTeleplateId());
		if(teleplate != null){
			DimBlockPos pos = new DimBlockPos(te);
			if(!teleplate.getPos().equals(pos)){
				teleplate.setPos(pos);
				savedData.synchronizeWithAll();
			}
		}
	}

	public boolean isValid(int teleplate){
		return idValidityMap.get(teleplate);
	}

	public void invalidate(int teleplate){
		idValidityMap.put(teleplate, false);
		savedData.synchronizeWithAll();
	}

	public void validate(int teleplate){
		idValidityMap.put(teleplate, true);
		savedData.synchronizeWithAll();
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		nbt.setInteger("nextFreeId", nextFreeId);
		NBTTagList llist = new NBTTagList();
		for(Teleplate teleplate : idTeleplateMap.values()){
			llist.appendTag(teleplate.writeToNBT(new NBTTagCompound()));
		}
		nbt.setTag("idTeleplateMap", llist);

		NBTTagList lllist = new NBTTagList();
		for(Entry<Integer, Boolean> entry : idValidityMap.entrySet()){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("id", entry.getKey());
			tag.setBoolean("valid", entry.getValue());
			lllist.appendTag(tag);
		}
		nbt.setTag("idValidityMap", lllist);
		return nbt;
	}

	public void readFromNBT(NBTTagCompound nbt){		
		nextFreeId = nbt.getInteger("nextFreeId");
		NBTTagList llist = (NBTTagList) nbt.getTag("idTeleplateMap");
		for(int i = 0; i < llist.tagCount(); i++){
			NBTTagCompound tag = llist.getCompoundTagAt(i);
			idTeleplateMap.put(tag.getInteger("id"), Teleplate.createFromNBT(tag));
		}

		NBTTagList lllist = (NBTTagList) nbt.getTag("idValidityMap");
		for(int i = 0; i < lllist.tagCount(); i++){
			NBTTagCompound tag = lllist.getCompoundTagAt(i);
			idValidityMap.put(tag.getInteger("id"), tag.getBoolean("valid"));
		}
	}

}
