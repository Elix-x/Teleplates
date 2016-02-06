package code.elix_x.mods.teleplates.events;

import code.elix_x.mods.teleplates.TeleplatesBase;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class BlockBreakEvent {

	public BlockBreakEvent() {

	}

	@SubscribeEvent
	public void breakBlock(BreakEvent event){
		if(event.block == TeleplatesBase.teleplate){
			if(!event.world.isRemote){
				event.setCanceled(true);
			}
		}
	}

}
