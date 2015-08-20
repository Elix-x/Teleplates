package code.elix_x.mods.teleplates.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;
import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.config.ConfigurationManager;
import code.elix_x.mods.teleplates.net.TeleportToTeleplateMessage;
import code.elix_x.mods.teleplates.teleplates.Teleplate;
import code.elix_x.mods.teleplates.teleplates.TeleplatesManager;
import code.elix_x.mods.teleplates.teleplates.TeleportationManager;

public class GuiTeleplatesList extends GuiListExtended{

	private List<Teleplate> teleplates = new ArrayList<Teleplate>();

	public GuiTeleplatesList(GuiSelectTeleplate gui) {
		super(Minecraft.getMinecraft(), gui.width, gui.height, 0, gui.height, 20);

		for(int id : TeleplatesManager.getTeleplates(Minecraft.getMinecraft().thePlayer)){
			if(/*!TeleplatesManager.getTeleplate(id).getName().equals("Teleplate") &&*/TeleplatesManager.isValid(id)){
				teleplates.add(TeleplatesManager.getTeleplate(id));
			}
		}
	}

	@Override
	public IGuiListEntry getListEntry(int i) {
		return new TeleplateEntry(i);
	}

	@Override
	protected int getSize() {
		return teleplates.size();
	}

	public class TeleplateEntry implements IGuiListEntry{

		private Teleplate teleplate;
		private GuiButton teleport;

		public TeleplateEntry(int i) {
			//			teleplate = TeleplatesManager.getTeleplates(Minecraft.getMinecraft().thePlayer).
			teleplate = teleplates.get(i);
			String s = teleplate.getName();
			boolean b = ConfigurationManager.forceDisplayCoordinatesInGui;
			for(Teleplate t : teleplates){
				if(t != teleplate && t.getName().equals(teleplate.getName())){
					b = true;
					break;
				}
			}
			if(b){
				DimBlockPos pos = teleplate.getPos();
				s += " (" + pos.getDimId() + ", " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
			}
			teleport = new GuiButton(0, 0, 0, 128, 18, s);
		}

		@Override
		public void drawEntry(int slotId, int x, int y, int width, int height, Tessellator tessellator, int p_148279_7_, int p_148279_8_, boolean p_148279_9_) {
			teleport.xPosition = x + 64;
			teleport.yPosition = y + 1;
			teleport.drawButton(Minecraft.getMinecraft(), p_148279_7_, p_148279_8_);
		}

		@Override
		public boolean mousePressed(int x, int y, int id, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
			//			System.out.println("Pressing!");
			//			if(teleport.mousePressed(Minecraft.getMinecraft(), x, y)){
			//				System.out.println("Whoosh!");
//			TeleportationManager.teleport(EntityPlayer.func_146094_a(Minecraft.getMinecraft().thePlayer.getGameProfile()), Minecraft.getMinecraft().thePlayer.worldObj.provider.dimensionId, teleplate.getId());
			TeleplatesBase.net.sendToServer(new TeleportToTeleplateMessage(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().thePlayer.worldObj.provider.dimensionId, teleplate.getId()));
			Minecraft.getMinecraft().displayGuiScreen(null);
			Minecraft.getMinecraft().setIngameFocus();
			//			}
			return false;
		}

		@Override
		public void mouseReleased(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_) {

		}

	}


}
