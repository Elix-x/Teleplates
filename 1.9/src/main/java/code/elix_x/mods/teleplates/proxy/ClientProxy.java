package code.elix_x.mods.teleplates.proxy;

import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.client.gui.GuiSelectTeleplate;
import code.elix_x.mods.teleplates.client.gui.GuiSetTeleplateSettings;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import code.elix_x.mods.teleplates.tileentity.TileEntityTeleplate;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements ITeleplatesProxy {

	public static boolean forceDisplayCoordinatesInGui = false;
	public static int teleplateRendererVersion = 2;

	public void preInit(FMLPreInitializationEvent event){

	}

	public void init(FMLInitializationEvent event){
		OBJLoader.INSTANCE.addDomain(TeleplatesBase.MODID);
		//		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTeleplate.class, new TileEntityTeleplateRenderer());
		//		if(teleplateRendererVersion == 2) MinecraftForge.EVENT_BUS.register(new LastRenderWorldEvent());
	}

	public void postInit(FMLPostInitializationEvent event){

	}

	@Override
	public void config(Configuration config){
		forceDisplayCoordinatesInGui = config.getBoolean("forceDisplayCoordinatesInGui", "Client", false, "Force to display teleplate coordinates in teleportation gui.");
		teleplateRendererVersion = config.getInt("Teleplate Renderer Version", "Client", 2, 0, 2, "Version of renderer to render teleplates. Higher versions, may consume more FPS.\n0 - Like in 1.0, but static.\n1 - Like in 1.0\n2 - One that came in 1.2.4");
	}

	@Override
	public void displayGuiSetTeleplateName(EntityPlayer player, DimBlockPos pos){
		if(player == Minecraft.getMinecraft().thePlayer) Minecraft.getMinecraft().displayGuiScreen(new GuiSetTeleplateSettings(TeleplatesSavedData.get(Minecraft.getMinecraft().theWorld).getTeleplatesManager().getTeleplate(((TileEntityTeleplate) pos.getTileEntity(Minecraft.getMinecraft().theWorld)).getTeleplateId())));
	}

	@Override
	public void displayGuiSelectTeleplate(){
		Minecraft.getMinecraft().displayGuiScreen(new GuiSelectTeleplate());
	}

}
