package code.elix_x.mods.teleplates.events;

import net.minecraft.entity.player.EntityPlayerMP;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import code.elix_x.mods.teleplates.teleplates.TeleplatesManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class OnPlayerJoinEvent {

	public OnPlayerJoinEvent() {
		
	}
	
	@SubscribeEvent
	public void join(PlayerLoggedInEvent event){
		if(!event.player.worldObj.isRemote) TeleplatesSavedData.get(event.player.worldObj).synchronizeWith((EntityPlayerMP) event.player);
	}
	
}
