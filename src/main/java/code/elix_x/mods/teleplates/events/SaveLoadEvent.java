package code.elix_x.mods.teleplates.events;

import code.elix_x.mods.teleplates.net.SaveSyncManager;
import code.elix_x.mods.teleplates.teleplates.TeleplatesManager;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SaveLoadEvent {

	public SaveLoadEvent() {
		
	}
	
	@SubscribeEvent
	public void load(Load event){
		SaveSyncManager.load(event);
	}
	
	@SubscribeEvent
	public void save(Save event){
		SaveSyncManager.save(event);
	}
	
}
