package code.elix_x.mods.teleplates.client.events;

import code.elix_x.mods.teleplates.client.renderer.tileentity.TileEntityTeleplateRenderer;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class LastRenderWorldEvent {

	@SubscribeEvent(priority = EventPriority.LOW)
	public void render(RenderWorldLastEvent event){
		TileEntityTeleplateRenderer.renderAllTeleplates(event.context.tileEntities);
	}

}
