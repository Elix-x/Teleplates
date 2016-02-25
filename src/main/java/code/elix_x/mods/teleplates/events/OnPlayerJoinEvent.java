package code.elix_x.mods.teleplates.events;

import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraft.entity.player.EntityPlayerMP;

public class OnPlayerJoinEvent {

	@SubscribeEvent
	public void join(PlayerLoggedInEvent event){
		if(!event.player.worldObj.isRemote) TeleplatesSavedData.get(event.player.worldObj).synchronizeWith((EntityPlayerMP) event.player);
	}

}
