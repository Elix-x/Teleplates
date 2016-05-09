package code.elix_x.mods.teleplates.consumption;

import code.elix_x.mods.teleplates.tileentity.ITeleplate;
import code.elix_x.mods.teleplates.tileentity.ITeleplateData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.config.ConfigCategory;

public interface IConsumptionManager {

	public Class<? extends ITeleplate> getInterfaceClass();

	public <T extends TileEntity & ITeleplate> ITeleplateData provideData(T t);

	public boolean canTeleportFromTeleplate(EntityPlayer player);

	public boolean canTeleportFromPortableTeleplate(EntityPlayer player);

	public void onTransfer(EntityPlayer player);

	public NBTTagCompound writeToNBT(NBTTagCompound nbt);

	public void readFromNBT(NBTTagCompound nbt);

	public void config(ConfigCategory config);

}
