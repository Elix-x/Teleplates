package code.elix_x.mods.teleplates.client.events;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LastRenderWorldEvent {

	@SubscribeEvent(priority = EventPriority.LOW)
	public void render(RenderWorldLastEvent event){
		//		TileEntityTeleplateRenderer.renderAllTeleplates(event.getContext().tileEntities);
	}

}
