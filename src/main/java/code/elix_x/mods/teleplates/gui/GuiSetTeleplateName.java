package code.elix_x.mods.teleplates.gui;

import java.util.UUID;

import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.net.SetTeleplateNameMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

public class GuiSetTeleplateName extends GuiScreen {

//	private final int teleplate;
	
	private final DimBlockPos teleplate;
	
	protected int xSize = 128;
	protected int ySize = 48;
	
    protected int guiLeft;
    protected int guiTop;
    
    private GuiTextField text;

	public GuiSetTeleplateName(/*int teleplate*/ DimBlockPos teleplate) {
		this.teleplate = teleplate;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		this.buttonList.add(new GuiButton(0, guiLeft, guiTop + 32, 128, 16, I18n.format("teleplates.gui.confirm")));
		text = new GuiTextField(fontRendererObj, guiLeft, guiTop, 128, 16);
	}
	
	@Override
	public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
		super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
		text.drawTextBox();
	}

	@Override
	protected void keyTyped(char ch, int i) {
		super.keyTyped(ch, i);
		text.textboxKeyTyped(ch, i);
	}
	
	@Override
	protected void mouseClicked(int x, int y, int id) {
		super.mouseClicked(x, y, id);
		text.mouseClicked(x, y, id);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if(button.id == 0){
			TeleplatesBase.net.sendToServer(new SetTeleplateNameMessage(EntityPlayer.func_146094_a(Minecraft.getMinecraft().thePlayer.getGameProfile()), teleplate, text.getText()));
			this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
		}
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
}
