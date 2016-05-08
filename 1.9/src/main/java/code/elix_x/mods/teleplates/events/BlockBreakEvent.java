package code.elix_x.mods.teleplates.events;

import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockBreakEvent {

	@SubscribeEvent
	public void breakBlock(BreakEvent event){
		if(event.getState().getBlock() == TeleplatesBase.teleplate){
			event.setCanceled(true);
			if(event.getPlayer().getHeldItem(EnumHand.MAIN_HAND) != null && event.getPlayer().getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.STICK){
				TileEntityTeleplate teleplate = (TileEntityTeleplate) event.getWorld().getTileEntity(event.getPos());
				if(teleplate.isErrored() || teleplate.getOwner().equals(EntityPlayer.getUUID(event.getPlayer().getGameProfile()))){
					event.setCanceled(false);
				}
			}
		}
	}

}
