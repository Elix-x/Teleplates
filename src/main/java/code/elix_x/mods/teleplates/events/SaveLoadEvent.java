package code.elix_x.mods.teleplates.events;

import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent.Load;

public class SaveLoadEvent {

	@SubscribeEvent
	public void load(Load event){
		TeleplatesSavedData.load(event);
	}

}
