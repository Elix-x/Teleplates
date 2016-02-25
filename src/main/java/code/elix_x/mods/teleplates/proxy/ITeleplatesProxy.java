package code.elix_x.mods.teleplates.proxy;

import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.excore.utils.proxy.IProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;

public interface ITeleplatesProxy extends IProxy {
	
	public void config(Configuration config);

	public void displayGuiSetTeleplateName(EntityPlayer player, DimBlockPos pos);

	public void displayGuiSelectTeleplate();

}
