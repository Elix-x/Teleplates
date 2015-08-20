package code.elix_x.mods.teleplates.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class GuiSelectTeleplate extends GuiScreen {

	private GuiTeleplatesList list;

	public GuiSelectTeleplate() {

	}

	@Override
	public void initGui() {
		super.initGui();
		list = new GuiTeleplatesList(this);
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);
		if(list != null){
			list.drawScreen(x, y, f);
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int id) {
		list.func_148179_a(x, y, id);
	}

	@Override
	protected void keyTyped(char ch, int i) {
		
	}
	
}
