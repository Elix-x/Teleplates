package code.elix_x.mods.teleplates.events;

import code.elix_x.mods.teleplates.teleplates.TeleportationManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class OnPlayerTickEvent {

	public OnPlayerTickEvent() {
		
	}
	
	@SubscribeEvent
	public void tick(PlayerTickEvent event){
		if(event.phase == Phase.START){
			TeleportationManager.onPlayerUpdate(event.player);
		}
	}
	
}
