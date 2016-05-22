package code.elix_x.mods.teleplates.events;

import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldLoadEvents {

	@SubscribeEvent
	public void load(Load event){
		TeleplatesSavedData.get(event.getWorld());
	}

}
