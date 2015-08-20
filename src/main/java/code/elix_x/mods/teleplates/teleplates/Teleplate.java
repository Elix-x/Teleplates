package code.elix_x.mods.teleplates.teleplates;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import code.elix_x.excore.utils.pos.DimBlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class Teleplate {

	private Map<UUID, Integer> permissions = new HashMap<UUID, Integer>();

	private final int id;
	private String name;
	private DimBlockPos pos;

	public Teleplate(int id, String name, DimBlockPos pos) {
		this.id = id;
		this.name = name;
		this.pos = pos;
	}

	public Teleplate(int id, String name, DimBlockPos pos, UUID owner) {
		this(id, name, pos);
		permissions.put(owner, 4);
	}

	public DimBlockPos getPos() {
		return pos;
	}

	public void setPos(DimBlockPos pos) {
		this.pos = pos;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void op(UUID asker, UUID player){
		if(isAdmin(asker) && !isOwner(player)){
			int i = permissions.containsKey(player) ? permissions.get(player) : 0;
			i++;
			i = i > 3 ? 0 : i;
			permissions.put(player, i);
		}
	}

	public void op(UUID asker, UUID player, int level){
		if(isAdmin(asker) && !isOwner(player)){
			permissions.put(player, level);
		}
	}

	public boolean isUser(UUID player) {
		return permissions.get(player) >= 1;
	}

	public boolean isModerator(UUID player) {
		return permissions.get(player) >= 2;
	}

	public boolean isAdmin(UUID player) {
		return permissions.get(player) >= 3;
	}

	public boolean isOwner(UUID player){
		return permissions.get(player) == 4;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		nbt = pos.writeToNBT(nbt);
		nbt.setInteger("id", id);
		nbt.setString("name", name);
		NBTTagList list = new NBTTagList();
		for(Entry<UUID, Integer> entry : permissions.entrySet()){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("uuid", entry.getKey().toString());
			tag.setInteger("level", entry.getValue());
			list.appendTag(tag);
		}
		nbt.setTag("permissions", list);
		return nbt;
	}

	public Teleplate readPermissionsFromNBT(NBTTagCompound nbt){
		permissions.clear();
		NBTTagList list = (NBTTagList) nbt.getTag("permissions");
		for(int i = 0; i < list.tagCount(); i++){
			NBTTagCompound tag = list.getCompoundTagAt(i);
			permissions.put(UUID.fromString(tag.getString("uuid")), tag.getInteger("level"));
		}
		return this;
	}

	public static Teleplate createFromNBT(NBTTagCompound nbt){
		return new Teleplate(nbt.getInteger("id"), nbt.getString("name"), DimBlockPos.createFromNBT(nbt)).readPermissionsFromNBT(nbt);
	}

	@Override
	public String toString() {
		return "Teleplate [id=" + id + ", name=" + name + ", pos=" + pos + ", permissions=" + permissions + "]";
	}

}
