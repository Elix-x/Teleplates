package code.elix_x.mods.teleplates.client.renderer.tileentity;

import java.util.List;

import org.lwjgl.opengl.GL11;

import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.proxy.ClientProxy;
import code.elix_x.mods.teleplates.teleplates.TeleportationManager;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.obj.ObjModelLoader;

public class TileEntityTeleplateRenderer extends TileEntitySpecialRenderer {

	public static final ResourceLocation teleplate = new ResourceLocation(TeleplatesBase.MODID, "textures/teleplate.png");
	public static final IModelCustom teleplateObj;

	private static int phase = 0;

	static {
		if(ClientProxy.teleplateRendererVersion == 2){
			teleplateObj = new ObjModelLoader().loadInstance(new ResourceLocation(TeleplatesBase.MODID, "models/teleplate.obj"));
		} else {
			teleplateObj = null;
		}
	}

	public TileEntityTeleplateRenderer(){

	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f){
		renderTileEntityAt((TileEntityTeleplate) tileentity, x, y, z, f, false, true);
	}

	public void renderTileEntityAt(TileEntityTeleplate tileentity, double x, double y, double z, float f, boolean item, boolean teleportAnimation){
		bindTexture(teleplate);
		GL11.glPushMatrix();

		GL11.glTranslated(x, y + 0.001, z);

		boolean animation = teleportAnimation && TeleportationManager.isTeleporting(Minecraft.getMinecraft().thePlayer) && ((tileentity.xCoord == Math.floor(Minecraft.getMinecraft().thePlayer.posX) && tileentity.yCoord == Math.floor(Minecraft.getMinecraft().thePlayer.posY - 1) && tileentity.zCoord == Math.floor(Minecraft.getMinecraft().thePlayer.posZ)) || (Minecraft.getMinecraft().thePlayer.isUsingItem() && Minecraft.getMinecraft().thePlayer.getHeldItem() != null && Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() == TeleplatesBase.portableTeleplate));

		switch (ClientProxy.teleplateRendererVersion){
		case 0:
		{			
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			if(animation){
				int cooldown = TeleportationManager.getCooldown(Minecraft.getMinecraft().thePlayer);
				double offset = (double) (TeleportationManager.DEFAULTCOOLDOWN - cooldown) / 100 * 2;
				double yOffest = Math.min(offset, Minecraft.getMinecraft().thePlayer.eyeHeight);
				double pngscale = ((y - cooldown) / 100) / 2;
				tessellator.addVertexWithUV(0 - offset, 0 + yOffest, 0 - offset, 0.5 + x / 10 - pngscale, 0.5 + z / 10 - pngscale);
				tessellator.addVertexWithUV(0 - offset, 0 + yOffest, 1 + offset, 0.5 + x / 10 - pngscale, 0.5 + z / 10 + pngscale);
				tessellator.addVertexWithUV(1 + offset, 0 + yOffest, 1 + offset, 0.5 + x / 10 + pngscale, 0.5 + z / 10 + pngscale);
				tessellator.addVertexWithUV(1 + offset, 0 + yOffest, 0 - offset, 0.5 + x / 10 + pngscale, 0.5 + z / 10 - pngscale);
			} else {
				tessellator.addVertexWithUV(0, 0, 0, 0, 0);
				tessellator.addVertexWithUV(0, 0, 1, 0, 1);
				tessellator.addVertexWithUV(1, 0, 1, 1, 1);
				tessellator.addVertexWithUV(1, 0, 0, 1, 0);
			}

			tessellator.draw();
		}
		break;

		case 1:
		{			
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			if(animation){
				int cooldown = TeleportationManager.getCooldown(Minecraft.getMinecraft().thePlayer);
				double offset = (double) (TeleportationManager.DEFAULTCOOLDOWN - cooldown) / 100 * 2;
				double yOffest = Math.min(offset, Minecraft.getMinecraft().thePlayer.eyeHeight);
				double pngscale = ((y - cooldown) / 100) / 2;
				tessellator.addVertexWithUV(0 - offset, 0 + yOffest, 0 - offset, 0.5 + x / 10 - pngscale, 0.5 + z / 10 - pngscale);
				tessellator.addVertexWithUV(0 - offset, 0 + yOffest, 1 + offset, 0.5 + x / 10 - pngscale, 0.5 + z / 10 + pngscale);
				tessellator.addVertexWithUV(1 + offset, 0 + yOffest, 1 + offset, 0.5 + x / 10 + pngscale, 0.5 + z / 10 + pngscale);
				tessellator.addVertexWithUV(1 + offset, 0 + yOffest, 0 - offset, 0.5 + x / 10 + pngscale, 0.5 + z / 10 - pngscale);
			} else {
				double scale = ((y - 5) / 10) / 2;
				tessellator.addVertexWithUV(0, 0, 0, 0.5 + x / -10 - scale, 0.5 + z / -10 - scale);
				tessellator.addVertexWithUV(0, 0, 1, 0.5 + x / -10 - scale, 0.5 + z / -10 + scale);
				tessellator.addVertexWithUV(1, 0, 1, 0.5 + x / -10 + scale, 0.5 + z / -10 + scale);
				tessellator.addVertexWithUV(1, 0, 0, 0.5 + x / -10 + scale, 0.5 + z / -10 - scale);
			}

			tessellator.draw();
		}
		break;

		case 2:
		{
			if(item){
				if(animation){
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					GL11.glTranslated(0.5, 0, 0.5);
					GL11.glScaled(0.5, 1, 0.5);
					int cooldown = TeleportationManager.getCooldown(Minecraft.getMinecraft().thePlayer);
					double offset = (double) (TeleportationManager.DEFAULTCOOLDOWN - cooldown) / 100 * 3;
					GL11.glTranslated(0, offset, 0);
					teleplateObj.renderAll();
					GL11.glEnable(GL11.GL_DEPTH_TEST);
				} else {
					GL11.glEnable(GL11.GL_STENCIL_TEST);
					GL11.glColorMask(false, false, false, false);
					GL11.glDepthMask(false);
					GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 255);
					GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

					GL11.glStencilMask(255);
					GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
					Tessellator tessellator = Tessellator.instance;
					tessellator.startDrawingQuads();
					tessellator.addVertex(0, 0, 0);
					tessellator.addVertex(0, 0, 1);
					tessellator.addVertex(1, 0, 1);
					tessellator.addVertex(1, 0, 0);
					tessellator.draw();
					GL11.glDepthMask(true);
					GL11.glColorMask(true, true, true, true);
					GL11.glStencilMask(0);

					GL11.glStencilFunc(GL11.GL_EQUAL, 1, 255);

					GL11.glTranslated(0.5, 0, 0.5);
					GL11.glScaled(0.5, 1, 0.5);

					GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

					teleplateObj.renderAll();

					GL11.glDisable(GL11.GL_STENCIL_TEST);
				}
			} else {
				switch (phase){
				case 0:
					break;
				case 1:
					renderStencil(tileentity, x, y, z, f, teleportAnimation);
					break;
				case 2:
					renderModel(tileentity, x, y, z, f, teleportAnimation);
					break;
				case 3:
					renderModelTeleport(tileentity, x, y, z, f, teleportAnimation);
					break;

				default:
					break;
				}
			}
		}
		break;

		default:
			break;
		}

		GL11.glPopMatrix();
	}

	public void renderStencil(TileEntityTeleplate tileentity, double x, double y, double z, float f, boolean teleportAnimation){
		GL11.glPushMatrix();

		if(!(teleportAnimation && TeleportationManager.isTeleporting(Minecraft.getMinecraft().thePlayer) && ((tileentity.xCoord == Math.floor(Minecraft.getMinecraft().thePlayer.posX) && tileentity.yCoord == Math.floor(Minecraft.getMinecraft().thePlayer.posY - 1) && tileentity.zCoord == Math.floor(Minecraft.getMinecraft().thePlayer.posZ)) || (Minecraft.getMinecraft().thePlayer.isUsingItem() && Minecraft.getMinecraft().thePlayer.getHeldItem() != null && Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() == TeleplatesBase.portableTeleplate)))){
			Tessellator tessellator = Tessellator.instance;
			tessellator.setColorOpaque(255, 0, 0);
			tessellator.startDrawingQuads();
			tessellator.addVertex(0, 0, 0);
			tessellator.addVertex(0, 0, 1);
			tessellator.addVertex(1, 0, 1);
			tessellator.addVertex(1, 0, 0);
			tessellator.draw();
		}

		GL11.glPopMatrix();
	}

	public void renderModel(TileEntityTeleplate tileentity, double x, double y, double z, float f, boolean teleportAnimation){
		bindTexture(teleplate);
		GL11.glPushMatrix();

		GL11.glTranslated(0.5, 0, 0.5);
		GL11.glScaled(0.5, 1, 0.5);

		teleplateObj.renderAll();

		GL11.glPopMatrix();
	}

	public void renderModelTeleport(TileEntityTeleplate tileentity, double x, double y, double z, float f, boolean teleportAnimation){
		if(teleportAnimation && TeleportationManager.isTeleporting(Minecraft.getMinecraft().thePlayer) && ((tileentity.xCoord == Math.floor(Minecraft.getMinecraft().thePlayer.posX) && tileentity.yCoord == Math.floor(Minecraft.getMinecraft().thePlayer.posY - 1) && tileentity.zCoord == Math.floor(Minecraft.getMinecraft().thePlayer.posZ)) || (Minecraft.getMinecraft().thePlayer.isUsingItem() && Minecraft.getMinecraft().thePlayer.getHeldItem() != null && Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() == TeleplatesBase.portableTeleplate))){
			bindTexture(teleplate);
			GL11.glPushMatrix();

			GL11.glTranslated(0.5, 0, 0.5);
			GL11.glScaled(0.5, 1, 0.5);

			int cooldown = TeleportationManager.getCooldown(Minecraft.getMinecraft().thePlayer);
			double offset = (double) (TeleportationManager.DEFAULTCOOLDOWN - cooldown) / 100 * 3;
			GL11.glTranslated(0, offset, 0);

			teleplateObj.renderAll();

			GL11.glPopMatrix();
		}
	}

	public static void renderAllTeleplates(List<TileEntity> tes){
		phase = 0;

		GL11.glPushMatrix();

		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glColorMask(false, false, false, false);
		GL11.glDepthMask(false);
		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 255);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

		GL11.glStencilMask(255);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

		phase = 1;
		for(TileEntity te : tes){
			if(te instanceof TileEntityTeleplate) TileEntityRendererDispatcher.instance.renderTileEntity(te, 0);
		}

		GL11.glDepthMask(true);
		GL11.glColorMask(true, true, true, true);
		GL11.glStencilMask(0);

		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 255);

		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

		phase = 2;
		for(TileEntity te : tes){
			if(te instanceof TileEntityTeleplate) TileEntityRendererDispatcher.instance.renderTileEntity(te, 0);
		}

		GL11.glDisable(GL11.GL_STENCIL_TEST);

		phase = 3;
		for(TileEntity te : tes){
			if(te instanceof TileEntityTeleplate) TileEntityRendererDispatcher.instance.renderTileEntity(te, 0);
		}

		GL11.glPopMatrix();

		phase = 0;
	}

}
