package code.elix_x.mods.teleplates.consomation;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

public interface IConsomationManager {

	public boolean canTeleportFromTeleplate(EntityPlayer player);

	public boolean canTeleportFromPortableTeleplate(EntityPlayer player);

	public void onTransfer(EntityPlayer player);

	public NBTTagCompound writeToNBT(NBTTagCompound nbt);

	public void readFromNBT(NBTTagCompound nbt);

	public void reset();

	public String getName();

	public void config(Configuration config);

	public void deactivate();

}
