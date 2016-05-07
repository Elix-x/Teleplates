package code.elix_x.mods.teleplates.proxy;

import org.lwjgl.opengl.GL11;

import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.client.events.LastRenderWorldEvent;
import code.elix_x.mods.teleplates.client.gui.GuiSelectTeleplate;
import code.elix_x.mods.teleplates.client.gui.GuiSetTeleplateSettings;
import code.elix_x.mods.teleplates.client.renderer.tileentity.TileEntityTeleplateRenderer;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import code.elix_x.mods.teleplates.teleplates.TeleportationManager;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

public class ClientProxy implements ITeleplatesProxy {

	public static int teleplateRendererVersion = 2;

	public void preInit(FMLPreInitializationEvent event){

	}

	public void init(FMLInitializationEvent event){
		final TileEntityTeleplateRenderer renderer = new TileEntityTeleplateRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTeleplate.class, renderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TeleplatesBase.teleplate), new IItemRenderer(){

			TileEntityTeleplate te = new TileEntityTeleplate();

			@Override
			public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper){
				return true;
			}

			@Override
			public boolean handleRenderType(ItemStack item, ItemRenderType type){
				return true;
			}

			@Override
			public void renderItem(ItemRenderType type, ItemStack item, Object... data){
				if(type == IItemRenderer.ItemRenderType.ENTITY){
					GL11.glTranslatef(-0.5f, 0.0f, -0.5f);
				}
				renderer.renderTileEntityAt(te, 0, 0, 0, 0, true, false);
			}

		});
		MinecraftForgeClient.registerItemRenderer(TeleplatesBase.portableTeleplate, new IItemRenderer(){

			TileEntityTeleplate te = new TileEntityTeleplate();

			@Override
			public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper){
				return true;
			}

			@Override
			public boolean handleRenderType(ItemStack item, ItemRenderType type){
				return true;
			}

			@Override
			public void renderItem(ItemRenderType type, ItemStack item, Object... data){
				if(type == IItemRenderer.ItemRenderType.ENTITY){
					GL11.glTranslatef(-0.5f, 0.0f, -0.5f);
				}
				if((type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.EQUIPPED) && TeleportationManager.isTeleporting(Minecraft.getMinecraft().thePlayer) && Minecraft.getMinecraft().thePlayer.getItemInUse() != null && Minecraft.getMinecraft().thePlayer.getItemInUse().getItem() == TeleplatesBase.portableTeleplate){
					GL11.glTranslated(-1, 1.75, -1);
					GL11.glRotatef(90f, 1, 0, 1);
				}
				renderer.renderTileEntityAt(te, 0, 0, 0, 0, true, true);
			}

		});

		if(teleplateRendererVersion == 2) MinecraftForge.EVENT_BUS.register(new LastRenderWorldEvent());
	}

	public void postInit(FMLPostInitializationEvent event){

	}

	@Override
	public void config(Configuration config){
		teleplateRendererVersion = config.getInt("Teleplate Renderer Version", "Client", 2, 0, 2, "Version of renderer to render teleplates. Higher versions, may consume more FPS.\n0 - Like in 1.0, but static.\n1 - Like in 1.0\n2 - One that came in 1.2.4");
	}

	@Override
	public void displayGuiSetTeleplateName(EntityPlayer player, DimBlockPos pos){
		if(player == Minecraft.getMinecraft().thePlayer) Minecraft.getMinecraft().displayGuiScreen(new GuiSetTeleplateSettings(TeleplatesSavedData.getClient().getTeleplatesManager().getTeleplate(((TileEntityTeleplate) pos.getTileEntity(Minecraft.getMinecraft().theWorld)).getTeleplateId())));
	}

	@Override
	public void displayGuiSelectTeleplate(){
		Minecraft.getMinecraft().displayGuiScreen(new GuiSelectTeleplate());
	}

}
