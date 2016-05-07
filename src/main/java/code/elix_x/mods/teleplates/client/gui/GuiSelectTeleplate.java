package code.elix_x.mods.teleplates.client.gui;

import java.util.ArrayList;
import java.util.List;

import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.config.ConfigurationManager;
import code.elix_x.mods.teleplates.net.TeleportToTeleplateMessage;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import code.elix_x.mods.teleplates.teleplates.Teleplate;
import code.elix_x.mods.teleplates.teleplates.Teleplate.EnumTeleplateMode;
import code.elix_x.mods.teleplates.teleplates.TeleplatesManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

public class GuiSelectTeleplate extends GuiScreen {

	private List<Teleplate> teleplates = new ArrayList<Teleplate>();

	private GuiTeleplatesList list;

	private int selected = -1;

	private GuiTextField password;

	private GuiButton teleport;

	public GuiSelectTeleplate(){
		TeleplatesManager manager = TeleplatesSavedData.getClient().getTeleplatesManager();
		for(Teleplate teleplate : manager.getAllTeleplates()){
			if(manager.isValid(teleplate.getId())){
				if(teleplate.getMode() == EnumTeleplateMode.PUBLIC){
					teleplates.add(teleplate);
				} else if(teleplate.getMode() == EnumTeleplateMode.PROTECTED){
					if(teleplate.isUsingList()){
						if(teleplate.isWhitelist() == teleplate.getList().contains(Minecraft.getMinecraft().thePlayer.getCommandSenderName())){
							teleplates.add(teleplate);
						}
					} else {
						teleplates.add(teleplate);
					}
				} else if(teleplate.getMode() == EnumTeleplateMode.PRIVATE){
					if(teleplate.getOwner().equals(EntityPlayer.func_146094_a(Minecraft.getMinecraft().thePlayer.getGameProfile()))){
						teleplates.add(teleplate);
					}
				}
			}
		}
	}

	@Override
	public void initGui(){
		super.initGui();

		buttonList.clear();

		list = new GuiTeleplatesList(Minecraft.getMinecraft(), width, height, 0, height - 100, 20);
		password = new GuiTextField(fontRendererObj, width / 2 - 128, height - 80, 256, 20);
		buttonList.add(teleport = new GuiButton(0, width / 2 - 128, height - 40, 256, 20, StatCollector.translateToLocal("teleplates.gui.select.teleport")));

		teleport.visible = selected != -1;

		if(selected != -1){			
			Teleplate teleplate = teleplates.get(selected);
			password.setVisible(teleplate.getMode() == EnumTeleplateMode.PROTECTED && !teleplate.isUsingList());
		} else {
			password.setVisible(false);
		}
	}

	@Override
	public void drawScreen(int x, int y, float f){
		super.drawScreen(x, y, f);
		list.drawScreen(x, y, f);
		password.drawTextBox();

		teleport.enabled = true;
		if(selected != -1){
			Teleplate teleplate = teleplates.get(selected);
			if(teleplate.getMode() == EnumTeleplateMode.PROTECTED && !teleplate.isUsingList()){
				teleport.enabled = teleplate.getPassword().equals(password.getText());
			}
		}

		teleport.drawButton(mc, x, y);
	}

	@Override
	protected void mouseClicked(int x, int y, int id){
		super.mouseClicked(x, y, id);

		list.func_148179_a(x, y, id);

		if(password.getVisible()) password.mouseClicked(x, y, id);
	}

	@Override
	protected void keyTyped(char ch, int i){
		if(password.getVisible()) password.textboxKeyTyped(ch, i);
	}

	@Override
	protected void actionPerformed(GuiButton button){
		if(button == teleport){
			TeleplatesBase.net.sendToServer(new TeleportToTeleplateMessage(teleplates.get(selected).getId()));
			Minecraft.getMinecraft().displayGuiScreen(null);
			Minecraft.getMinecraft().setIngameFocus();
		}
	}

	public class GuiTeleplatesList extends GuiListExtended {

		public GuiTeleplatesList(Minecraft minecraft, int width, int height, int top,int bottom, int slot) {
			super(minecraft, width, height, top, bottom, slot);
		}

		@Override
		public IGuiListEntry getListEntry(int i){
			return new TeleplateEntry(i);
		}

		@Override
		protected int getSize(){
			return teleplates.size();
		}

		@Override
		protected boolean isSelected(int i){
			return selected == i;
		}

		public class TeleplateEntry implements IGuiListEntry {

			private Teleplate teleplate;

			private String display;

			public TeleplateEntry(int i){
				teleplate = teleplates.get(i);
				display = teleplate.getName();
				boolean b = ConfigurationManager.forceDisplayCoordinatesInGui;
				for(Teleplate t : teleplates){
					if(t != teleplate && t.getName().equals(teleplate.getName())){
						b = true;
						break;
					}
				}
				if(b){
					DimBlockPos pos = teleplate.getPos();
					display += " (" + pos.getDimId() + ", " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
				}
			}

			@Override
			public void drawEntry(int slotId, int x, int y, int width, int height, Tessellator tessellator, int p_148279_7_, int p_148279_8_, boolean p_148279_9_){
				drawCenteredString(fontRendererObj, display, GuiSelectTeleplate.this.width / 2, y + 4, new RGBA(1f, 1f, 1f).argb());
			}

			@Override
			public boolean mousePressed(int id, int x, int y, int p_148278_4_, int p_148278_5_, int p_148278_6_){
				selected = id;
				teleport.visible = true;
				password.setVisible(teleplate.getMode() == EnumTeleplateMode.PROTECTED && !teleplate.isUsingList());
				return true;
			}

			@Override
			public void mouseReleased(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_){

			}

		}

	}

}
