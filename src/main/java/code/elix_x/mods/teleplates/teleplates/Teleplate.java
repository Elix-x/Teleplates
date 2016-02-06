package code.elix_x.mods.teleplates.teleplates;

import java.util.UUID;

import code.elix_x.excore.utils.pos.DimBlockPos;
import net.minecraft.nbt.NBTTagCompound;

public class Teleplate {

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

	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		nbt = pos.writeToNBT(nbt);
		nbt.setInteger("id", id);
		nbt.setString("name", name);
		return nbt;
	}

	public static Teleplate createFromNBT(NBTTagCompound nbt){
		return new Teleplate(nbt.getInteger("id"), nbt.getString("name"), DimBlockPos.createFromNBT(nbt));
	}

	@Override
	public String toString() {
		return "Teleplate [id=" + id + ", name=" + name + ", pos=" + pos + "]";
	}

}
