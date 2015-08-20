package code.elix_x.mods.teleplates.teleplates;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.config.ConfigurationManager;
import code.elix_x.mods.teleplates.net.SaveSyncManager;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TeleplatesManager {

	public static final Logger logger = LogManager.getLogger("Teleplates Manager");

	private static Multimap<UUID, Integer> playerIdsMap = HashMultimap.create();
	private static Map<Integer, Teleplate> idTeleplateMap = new HashMap<Integer, Teleplate>();
	private static Map<Integer, Boolean> idValidityMap = new HashMap<Integer, Boolean>();

	private static int nextFreeId = 0;

	public static int createTeleplate(EntityPlayer player, String name, DimBlockPos pos){
		int id = getNextFreeTeleplateId();
		Teleplate teleplate = new Teleplate(id, name, pos, EntityPlayer.func_146094_a(player.getGameProfile()));	
		playerIdsMap.put(EntityPlayer.func_146094_a(player.getGameProfile()), id);	
		idTeleplateMap.put(id, teleplate);
		idValidityMap.put(id, true);
		SaveSyncManager.synchronizeWithAll();
		return id;
	}

	public static Teleplate getTeleplate(int id){
		return idTeleplateMap.get(id);
	}

	public static Collection<Integer> getTeleplates(UUID playerId){
		return playerIdsMap.get(playerId);
	}

	public static Collection<Integer> getTeleplates(EntityPlayer player){
		return getTeleplates(EntityPlayer.func_146094_a(player.getGameProfile()));
	}

	public static int getNextFreeTeleplateId(){
		while(true){
			nextFreeId++;
			int uuid = nextFreeId;
			if(getTeleplate(uuid) == null){
				return uuid;
			}
		}
	}
	
	public static boolean isUser(int teleplate, EntityPlayer player) {
		return isUser(teleplate, EntityPlayer.func_146094_a(player.getGameProfile()));
	}

	public static boolean isUser(int teleplate, UUID player) {
		return idTeleplateMap.get(teleplate).isUser(player);
	}

	public static boolean isModerator(int teleplate, EntityPlayer player) {
		return isModerator(teleplate, EntityPlayer.func_146094_a(player.getGameProfile()));
	}

	public static boolean isModerator(int teleplate, UUID player) {
		return idTeleplateMap.get(teleplate).isModerator(player);
	}
	
	public static boolean isAdmin(int teleplate, EntityPlayer player) {
		return isAdmin(teleplate, EntityPlayer.func_146094_a(player.getGameProfile()));
	}

	public static boolean isAdmin(int teleplate, UUID player) {
		return idTeleplateMap.get(teleplate).isAdmin(player);
	}

	public static void tryChangeName(UUID caller, int teleplate, String newName) {
		if((!ConfigurationManager.permissionsSystemActive()) || (ConfigurationManager.permissionsSystemActive() && isModerator(teleplate, caller))){
			getTeleplate(teleplate).setName(newName);
			SaveSyncManager.synchronizeWithAll();
		}
	}

	public static void updateTeleplatePosition(TileEntityTeleplate te){
		Teleplate teleplate = getTeleplate(te.getTeleplateId());
		if(teleplate != null){
			DimBlockPos pos = new DimBlockPos(te);
			if(!teleplate.getPos().equals(pos)){
				teleplate.setPos(pos);
				SaveSyncManager.synchronizeWithAll();
			}
		}
	}
	
	public static void updateUsers(){
		for(Teleplate teleplate : idTeleplateMap.values()){
			for(UUID uuid : playerIdsMap.keySet()){
				if(teleplate.isUser(uuid)){
					playerIdsMap.put(uuid, teleplate.getId());
				} else {
					playerIdsMap.remove(uuid, teleplate.getId());
				}
			}
		}
	}

	public static NBTTagCompound writeToNBT(NBTTagCompound nbt){
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

	public static void readFromNBT(NBTTagCompound nbt){
		reset();
		
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

	public static void reset() {
		nextFreeId = 0;
		playerIdsMap.clear();
		idTeleplateMap.clear();
	}

	public static void invalidate(int teleplate) {
		idValidityMap.put(teleplate, false);
		SaveSyncManager.synchronizeWithAll();
	}
	
	public static void validate(int teleplate){
		idValidityMap.put(teleplate, true);
		SaveSyncManager.synchronizeWithAll();
	}

	public static boolean isValid(int teleplate) {
		return idValidityMap.get(teleplate);
	}

	public static void logDebugInfo() {
		logger.debug("Next free Id: " + nextFreeId);
		logger.debug("Player ids: " + playerIdsMap);
		logger.debug("Id teleplate: " + idTeleplateMap);
		logger.debug("Id validity: " + idValidityMap);
	}
}
