package code.elix_x.mods.teleplates.events;

import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.config.ConfigurationManager;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import code.elix_x.mods.teleplates.teleplates.TeleplatesManager;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class BlockBreakEvent {

	public BlockBreakEvent() {

	}

	@SubscribeEvent
	public void breakBlock(BreakEvent event){
		if(event.block == TeleplatesBase.teleplate){
			if(!event.world.isRemote){
				if(ConfigurationManager.permissionsSystemActive() && !TeleplatesSavedData.get(event.world).getTeleplatesManager().isAdmin(((TileEntityTeleplate) event.world.getTileEntity(event.x, event.y, event.z)).getTeleplateId(), event.getPlayer())){
					event.setCanceled(true);
				}
			}
		}
	}

}
