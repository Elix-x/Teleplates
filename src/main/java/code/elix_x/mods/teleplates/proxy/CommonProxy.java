package code.elix_x.mods.teleplates.proxy;

import net.minecraftforge.common.MinecraftForge;
import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.blocks.BlockTeleplate;
import code.elix_x.mods.teleplates.events.OnPlayerJoinEvent;
import code.elix_x.mods.teleplates.events.SaveLoadEvent;
import code.elix_x.mods.teleplates.net.SetTeleplateNameMessage;
import code.elix_x.mods.teleplates.net.SynchronizeTeleplatesMessage;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event){
		
	}

	public void init(FMLInitializationEvent event){
		
	}

	public void postInit(FMLPostInitializationEvent event){

	}
	
	public void displayGuiSetTeleplateName(DimBlockPos pos){
		
	}

	public void displatGuiSelectTeleplate() {
		
	}
}
