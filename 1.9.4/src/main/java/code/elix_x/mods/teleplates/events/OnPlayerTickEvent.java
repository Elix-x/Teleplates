package code.elix_x.mods.teleplates.events;

import code.elix_x.mods.teleplates.teleplates.TeleportationManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

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
