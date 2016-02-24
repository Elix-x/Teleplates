package code.elix_x.mods.teleplates.events;

import code.elix_x.excore.utils.pos.BlockPos;
import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class BlockBreakEvent {

	@SubscribeEvent
	public void breakBlock(BreakEvent event){
		if(event.block == TeleplatesBase.teleplate){
			if(event.getPlayer().getCurrentEquippedItem() != null && event.getPlayer().getCurrentEquippedItem().getItem() == Items.stick){
				TileEntityTeleplate teleplate = ((TileEntityTeleplate) new BlockPos(event.x, event.y, event.z).getTileEntity(event.world));
				if(!teleplate.isErrored() && !teleplate.getOwner().equals(EntityPlayer.func_146094_a(event.getPlayer().getGameProfile()))){
					event.setCanceled(true);
				}
			}
		}
	}

}
