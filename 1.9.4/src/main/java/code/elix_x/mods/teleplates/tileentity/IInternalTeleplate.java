package code.elix_x.mods.teleplates.tileentity;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public interface IInternalTeleplate<T extends TileEntity & ITeleplate> extends ITeleplate<T> {

	public int getTeleplateId();

	public UUID getOwner();

	public boolean isErrored();

	public void init(EntityPlayer player, String name);

}
