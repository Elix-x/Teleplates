package code.elix_x.mods.teleplates.proxy;

import org.lwjgl.opengl.GL11;

import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.gui.GuiSelectTeleplate;
import code.elix_x.mods.teleplates.gui.GuiSetTeleplateSettings;
import code.elix_x.mods.teleplates.renderer.tileentity.TileEntityTeleplateRenderer;
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

public class ClientProxy extends CommonProxy {

	public void preInit(FMLPreInitializationEvent event){

	}

	public void init(FMLInitializationEvent event){
		final TileEntityTeleplateRenderer renderer = new TileEntityTeleplateRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTeleplate.class, renderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TeleplatesBase.teleplate), new IItemRenderer() {

			TileEntityTeleplate te = new TileEntityTeleplate();

			@Override
			public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
				return true;
			}

			@Override
			public boolean handleRenderType(ItemStack item, ItemRenderType type) {
				return true;
			}

			@Override
			public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
				if(type == IItemRenderer.ItemRenderType.ENTITY)		{
					GL11.glTranslatef(-0.5f, 0.0f, -0.5f);
				}
				renderer.renderTileEntityAt(te, 0, 0, 0, 0);
			}

		});
		MinecraftForgeClient.registerItemRenderer(TeleplatesBase.portableTeleplate, new IItemRenderer() {

			TileEntityTeleplate te = new TileEntityTeleplate();

			@Override
			public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
				return true;
			}

			@Override
			public boolean handleRenderType(ItemStack item, ItemRenderType type) {
				return true;
			}

			@Override
			public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
				if(type == IItemRenderer.ItemRenderType.ENTITY)		{
					GL11.glTranslatef(-0.5f, 0.0f, -0.5f);
				}
				if((type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.EQUIPPED) && TeleportationManager.isTeleporting(Minecraft.getMinecraft().thePlayer) && Minecraft.getMinecraft().thePlayer.getItemInUse() != null && Minecraft.getMinecraft().thePlayer.getItemInUse().getItem() == TeleplatesBase.portableTeleplate){
					GL11.glTranslated(-1, 1.75, -1);
					GL11.glRotatef(90f, 1, 0, 1);
				}
				renderer.renderTileEntityAt(te, 0, 0, 0, 0);
			}

		});
	}

	public void postInit(FMLPostInitializationEvent event){

	}

	@Override
	public void displayGuiSetTeleplateName(EntityPlayer player, DimBlockPos pos) {
		if(player == Minecraft.getMinecraft().thePlayer) Minecraft.getMinecraft().displayGuiScreen(new GuiSetTeleplateSettings(TeleplatesSavedData.getClient().getTeleplatesManager().getTeleplate(((TileEntityTeleplate) pos.getTileEntity()).getTeleplateId())));
	}

	@Override
	public void displayGuiSelectTeleplate(){
		Minecraft.getMinecraft().displayGuiScreen(new GuiSelectTeleplate());
	}

}
