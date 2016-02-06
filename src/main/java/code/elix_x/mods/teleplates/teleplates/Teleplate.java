package code.elix_x.mods.teleplates.teleplates;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import code.elix_x.excore.utils.nbt.mbt.MBT;
import code.elix_x.excore.utils.pos.DimBlockPos;
import net.minecraft.nbt.NBTTagCompound;

public class Teleplate {

	public static final MBT mbt = new MBT();

	private int id;
	private String name;
	private DimBlockPos pos;

	private UUID owner;

	private EnumTeleplateMode mode;
	
	private String password;
	
	private boolean whitelist;
	private Set<UUID> list;

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
		this.password = password;
	}

	public Teleplate(int id, String name, DimBlockPos pos, UUID owner, boolean whitelist, Set<UUID> list) {
		this.id = id;
		this.name = name;
		this.pos = pos;
		this.owner = owner;
		this.mode = EnumTeleplateMode.PROTECTED;
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

	public Set<UUID> getList(){
		return list;
	}
	
	public void addToList(UUID uuid){
		list.add(uuid);
	}
	
	public void removeFromList(UUID uuid){
		list.remove(uuid);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		return (NBTTagCompound) mbt.toNBT(this);
	}

	public static Teleplate createFromNBT(NBTTagCompound nbt){
		return mbt.fromNBT(nbt, Teleplate.class);
	}

	@Override
	public String toString() {
		return "Teleplate [id=" + id + ", name=" + name + ", pos=" + pos + ", owner=" + owner + "]";
	}

	public enum EnumTeleplateMode {

		PUBLIC, PROTECTED, PRIVATE;

	}

}
