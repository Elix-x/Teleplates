package code.elix_x.mods.teleplates.teleplates;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TeleplatesManager {

	private TeleplatesSavedData savedData;

	private Multimap<UUID, Integer> playerIdsMap = HashMultimap.create();
	private Map<Integer, Teleplate> idTeleplateMap = new HashMap<Integer, Teleplate>();
	private Map<Integer, Boolean> idValidityMap = new HashMap<Integer, Boolean>();

	private int nextFreeId = 0;

	public TeleplatesManager(TeleplatesSavedData savedData){
		this.savedData = savedData;
	}

	public int createTeleplate(EntityPlayer player, String name, DimBlockPos pos){
		int id = getNextFreeTeleplateId();
		Teleplate teleplate = new Teleplate(id, name, pos, EntityPlayer.func_146094_a(player.getGameProfile()));	
		playerIdsMap.put(EntityPlayer.func_146094_a(player.getGameProfile()), id);	
		idTeleplateMap.put(id, teleplate);
		idValidityMap.put(id, true);
		savedData.synchronizeWithAll();
		return id;
	}

	public Teleplate getTeleplate(int id){
		return idTeleplateMap.get(id);
	}

	public Collection<Integer> getTeleplates(UUID playerId){
		return playerIdsMap.get(playerId);
	}

	public Collection<Integer> getTeleplates(EntityPlayer player){
		return getTeleplates(EntityPlayer.func_146094_a(player.getGameProfile()));
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

	public void tryChangeName(UUID caller, int teleplate, String newName){
		//		if((!ConfigurationManager.permissionsSystemActive()) || (ConfigurationManager.permissionsSystemActive() && isModerator(teleplate, caller))){
		getTeleplate(teleplate).setName(newName);
		savedData.synchronizeWithAll();
		//		}
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

	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		nbt.setInteger("nextFreeId", nextFreeId);
		NBTTagList list = new NBTTagList();
		for(Entry<UUID, Integer> entry : playerIdsMap.entries()){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("player", entry.getKey().toString());
			tag.setInteger("teleplate", entry.getValue());
			list.appendTag(tag);
		}
		nbt.setTag("playerTeleplatesMap", list);

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
		NBTTagList list = (NBTTagList) nbt.getTag("playerTeleplatesMap");
		for(int i = 0; i < list.tagCount(); i++){
			NBTTagCompound tag = list.getCompoundTagAt(i);
			playerIdsMap.put(UUID.fromString(tag.getString("player")), tag.getInteger("teleplate"));
		}

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

	public void invalidate(int teleplate) {
		idValidityMap.put(teleplate, false);
		savedData.synchronizeWithAll();
	}

	public void validate(int teleplate){
		idValidityMap.put(teleplate, true);
		savedData.synchronizeWithAll();
	}

	public boolean isValid(int teleplate){
		return idValidityMap.get(teleplate);
	}

}
