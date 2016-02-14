package code.elix_x.mods.teleplates.gui;

import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;

import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.mods.teleplates.teleplates.Teleplate;
import code.elix_x.mods.teleplates.teleplates.Teleplate.EnumTeleplateMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.StatCollector;

public class GuiSetTeleplateSettings extends GuiScreen {

	private final Teleplate teleplate;

	protected int xSize = 128;
	protected int ySize = 80;

	protected int guiLeft;
	protected int guiTop;

	private String name;

	private GuiTextField textFieldName;

	private EnumTeleplateMode mode;

	private GuiButton buttonModePublic;
	private GuiButton buttonModeProtected;
	private GuiButton buttonModePrivate;

	private boolean usingList;
	private GuiButton buttonUsingList;

	private String password;
	private GuiTextField textFieldPassword;

	private boolean whitelist;
	private GuiButton buttonWhitelist;

	private GuiPlayerList listList;
	private Set<UUID> list;
	private UUID[] llist;

	public GuiSetTeleplateSettings(Teleplate teleplate){
		this.teleplate = teleplate;

		name = teleplate.getName();

		mode = teleplate.getMode();

		usingList = teleplate.isUsingList();

		password = teleplate.getPassword();

		whitelist = teleplate.isWhitelist();
		list = teleplate.getList() != null ? Sets.newHashSet(teleplate.getList()) : null;
	}

	@Override
	public void initGui(){
		super.initGui();
		buttonList.clear();

		if(mode == EnumTeleplateMode.PROTECTED){
			if(usingList){
				ySize = 192;
			} else {
				ySize = 132;
			}
		} else {
			ySize = 76;
		}

		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;

		textFieldName = new GuiTextField(fontRendererObj, guiLeft, guiTop, 128, 20);
		textFieldName.setText(name);
		this.buttonList.add(buttonModePublic = new GuiButton(1, guiLeft, guiTop + 28, 40, 20, StatCollector.translateToLocal("teleplates.gui.mode.public")){

			@Override
			public int getHoverState(boolean b){
				return mode == EnumTeleplateMode.PUBLIC ? 0 : b ? 2 : 1;
			}

		});
		this.buttonList.add(buttonModeProtected = new GuiButton(2, guiLeft + 44, guiTop + 28, 40, 20, StatCollector.translateToLocal("teleplates.gui.mode.protected")){

			@Override
			public int getHoverState(boolean b){
				return mode == EnumTeleplateMode.PROTECTED ? 0 : b ? 2 : 1;
			}

		});
		this.buttonList.add(buttonModePrivate = new GuiButton(3, guiLeft + 88, guiTop + 28, 40, 20, StatCollector.translateToLocal("teleplates.gui.mode.private")){

			@Override
			public int getHoverState(boolean b){
				return mode == EnumTeleplateMode.PRIVATE ? 0 : b ? 2 : 1;
			}

		});

		this.buttonList.add(buttonUsingList = new GuiButton(4, guiLeft, guiTop + 56, 128, 20, StatCollector.translateToLocal(usingList ? "teleplates.gui.mode.protected.list" : "teleplates.gui.mode.protected.password")));
		buttonUsingList.visible = mode == EnumTeleplateMode.PROTECTED;

		textFieldPassword = new GuiTextField(fontRendererObj, guiLeft, guiTop + 84, 128, 20);
		if(password != null) textFieldPassword.setText(password);

		this.buttonList.add(buttonWhitelist = new GuiButton(5, guiLeft, guiTop + 84, 128, 20, StatCollector.translateToLocal(whitelist ? "teleplates.gui.mode.protected.list.whitelist" : "teleplates.gui.mode.protected.list.blacklist")));
		buttonWhitelist.visible = mode == EnumTeleplateMode.PROTECTED && usingList;

		listList = new GuiPlayerList(mc, 128, 48, guiTop + 112, guiTop + 164, 16);	
		listList.left = guiLeft;
		listList.right = guiLeft + 128;

		llist = list != null ? list.toArray(new UUID[0]) : new UUID[0];

		this.buttonList.add(new GuiButton(0, guiLeft, guiTop + ySize - 20, 128, 20, StatCollector.translateToLocal("teleplates.gui.confirm")));
	}

	@Override
	public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_){
		if(mode == EnumTeleplateMode.PROTECTED){
			if(usingList){
				listList.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
			} else {
				textFieldPassword.drawTextBox();
			}
		}
		super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
		textFieldName.drawTextBox();
	}

	@Override
	protected void keyTyped(char ch, int i){
		super.keyTyped(ch, i);
		textFieldName.textboxKeyTyped(ch, i);
		if(mode == EnumTeleplateMode.PROTECTED){
			if(!usingList){
				textFieldPassword.textboxKeyTyped(ch, i);
			}
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int id){
		super.mouseClicked(x, y, id);
		textFieldName.mouseClicked(x, y, id);
		if(mode == EnumTeleplateMode.PROTECTED){
			if(usingList){
				listList.func_148179_a(x, y, id);
			} else {
				textFieldPassword.drawTextBox();
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button){
		if(button.id == 0){
			//			TeleplatesBase.net.sendToServer(new SetTeleplateNameMessage(EntityPlayer.func_146094_a(Minecraft.getMinecraft().thePlayer.getGameProfile()), teleplate, text.getText()));
			this.mc.displayGuiScreen((GuiScreen) null);
			this.mc.setIngameFocus();
		}

		if(button == buttonModePublic){
			mode = EnumTeleplateMode.PUBLIC;
			initGui();
		}
		if(button == buttonModeProtected){
			mode = EnumTeleplateMode.PROTECTED;
			initGui();
		}
		if(button == buttonModePrivate){
			mode = EnumTeleplateMode.PRIVATE;
			initGui();
		}

		if(button == buttonUsingList){
			usingList = !usingList;
			initGui();
		}

		if(button == buttonWhitelist){
			whitelist = !whitelist;
			initGui();
		}
	}

	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}

	public class GuiPlayerList extends GuiListExtended {

		public GuiPlayerList(Minecraft minecraft, int width, int height, int top, int bottom, int slotHeight){
			super(minecraft, width, height, top, bottom, slotHeight);
		}

		@Override
		public IGuiListEntry getListEntry(int i){
			return new IGuiListEntry(){

				@Override
				public void mouseReleased(int i, int id, int x, int y, int relX, int relY){

				}

				@Override
				public boolean mousePressed(int i, int x, int y, int id, int relX, int relY){
					if(list == null || isCtrlKeyDown()){
						list.remove(llist[i]);
						initGui();
						return true;
					}
					return false;
				}

				@Override
				public void drawEntry(int i, int x, int y, int relX, int relY, Tessellator tessellator, int p_148279_7_, int p_148279_8_, boolean p_148279_9_){
					if(list != null) drawString(fontRendererObj, llist[i].toString(), x, y + 16, new RGBA(1, 1, 1).argb());
				}

			};
		}

		@Override
		protected int getSize(){
			return list != null ? list.size() : 1;
		}

	}

}
