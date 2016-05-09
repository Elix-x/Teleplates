package code.elix_x.mods.teleplates.tileentity;

import net.minecraft.tileentity.TileEntity;

public interface ITeleplate<T extends TileEntity & ITeleplate> {

	public T getThis();

	public int getTeleplateId();

	public <I extends ITeleplateData> I getData(String name);

	public <I extends ITeleplateData> void setData(String name, I data);

}
