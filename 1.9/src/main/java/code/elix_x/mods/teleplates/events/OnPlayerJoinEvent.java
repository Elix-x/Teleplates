package code.elix_x.mods.teleplates.events;

import code.elix_x.mods.teleplates.config.ConfigurationManager;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class OnPlayerJoinEvent {

	@SubscribeEvent
	public void join(PlayerLoggedInEvent event){
		if(!event.player.worldObj.isRemote){
			ConfigurationManager.syncWith((EntityPlayerMP) event.player);
			TeleplatesSavedData.get(event.player.worldObj).synchronizeWith((EntityPlayerMP) event.player);
		}
	}

}
