package code.elix_x.mods.teleplates.teleplates;

import java.util.Set;
import java.util.UUID;

import code.elix_x.excore.utils.nbt.mbt.MBT;
import code.elix_x.excore.utils.pos.DimBlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class Teleplate {

	public static final MBT mbt = new MBT();

	private int id;
	private String name;
	private DimBlockPos pos;

	private UUID owner;

	private EnumTeleplateMode mode;

	private boolean usingList;

	private String password;

	private boolean whitelist;
	private Set<String> list;

	private Teleplate(){

	}

	public Teleplate(int id, String name, DimBlockPos pos, UUID owner){
		this.id = id;
		this.name = name;
		this.pos = pos;
		this.owner = owner;
	}

	public Teleplate(int id, String name, DimBlockPos pos, UUID owner, EnumTeleplateMode mode){
		this.id = id;
		this.name = name;
		this.pos = pos;
		this.owner = owner;
		this.mode = mode;
	}

	public Teleplate(int id, String name, DimBlockPos pos, UUID owner, String password){
		this.id = id;
		this.name = name;
		this.pos = pos;
		this.owner = owner;
		this.mode = EnumTeleplateMode.PROTECTED;
		this.usingList = false;
		this.password = password;
	}

	public Teleplate(int id, String name, DimBlockPos pos, UUID owner, boolean whitelist, Set<String> list) {
		this.id = id;
		this.name = name;
		this.pos = pos;
		this.owner = owner;
		this.mode = EnumTeleplateMode.PROTECTED;
		this.usingList = true;
		this.whitelist = whitelist;
		this.list = list;
	}

	public DimBlockPos getPos(){
		return pos;
	}

	public void setPos(DimBlockPos pos){
		this.pos = pos;
	}

	public int getId(){
		return id;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public UUID getOwner(){
		return owner;
	}

	public EnumTeleplateMode getMode(){
		return mode;
	}

	public void setMode(EnumTeleplateMode mode){
		this.mode = mode;
	}

	public boolean isUsingList(){
		return usingList;
	}

	public void setUsingList(boolean usingList){
		this.usingList = usingList;
	}

	public String getPassword(){
		return password;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public boolean isWhitelist(){
		return whitelist;
	}

	public void setWhitelist(boolean whitelist){
		this.whitelist = whitelist;
	}

	public Set<String> getList(){
		return list;
	}

	public void setList(Set<String> list){
		this.list = list;
	}

	public void addToList(String uuid){
		list.add(uuid);
	}

	public void removeFromList(String uuid){
		list.remove(uuid);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		return (NBTTagCompound) mbt.toNBT(this);
	}

	public static Teleplate createFromNBT(NBTTagCompound nbt){
		return mbt.fromNBT(nbt, Teleplate.class);
	}
	@Deprecated
	public static Teleplate createFromNBTOld(NBTTagCompound nbt){
		return new Teleplate(nbt.getInteger("id"), nbt.getString("name"), DimBlockPos.createFromNBT(nbt), null).readPermissionsFromNBTOld(nbt);
	}

	@Deprecated
	public Teleplate readPermissionsFromNBTOld(NBTTagCompound nbt){
		NBTTagList list = (NBTTagList) nbt.getTag("permissions");
		for(int i = 0; i < list.tagCount(); i++){
			NBTTagCompound tag = list.getCompoundTagAt(i);
			if(tag.getInteger("level") == 4) owner = UUID.fromString(tag.getString("uuid"));
		}
		return this;
	}

	@Override
	public String toString() {
		return "Teleplate [id=" + id + ", name=" + name + ", pos=" + pos + ", owner=" + owner + "]";
	}

	public enum EnumTeleplateMode {

		PUBLIC, PROTECTED, PRIVATE;

	}

}
