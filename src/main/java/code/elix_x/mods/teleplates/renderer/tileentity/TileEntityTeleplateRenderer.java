package code.elix_x.mods.teleplates.renderer.tileentity;

import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.teleplates.TeleportationManager;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityTeleplateRenderer extends TileEntitySpecialRenderer{

	private static final ResourceLocation teleplate = new ResourceLocation(TeleplatesBase.MODID, "textures/teleplate.png");
	//	private static final ResourceLocation endSkyTexture = new ResourceLocation("textures/environment/end_sky.png");
	//	private static final ResourceLocation endPortalTexture = new ResourceLocation("textures/entity/end_portal.png");
	private static final Random random = new Random(31100L);
	FloatBuffer floatBuffer = GLAllocation.createDirectFloatBuffer(16);

	public TileEntityTeleplateRenderer() {

	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		renderTileEntityAt((TileEntityTeleplate) tileentity, x, y, z, f);
	}

	public void renderTileEntityAt(TileEntityTeleplate tileentity, double x, double y, double z, float f) {
		bindTexture(teleplate);

		GL11.glPushMatrix();
		GL11.glTranslated(x, y + 0.01, z);

		float f4 = 0.75F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		if(!TeleportationManager.isTeleporting(Minecraft.getMinecraft().thePlayer)){
			double scale = ((y - 5) / 10) / 2;
			tessellator.addVertexWithUV(0, 0, 0, 0.5 + x / 10 - scale, 0.5 + z / 10 - scale);
			tessellator.addVertexWithUV(0, 0, 1, 0.5 + x / 10 - scale, 0.5 + z / 10 + scale);
			tessellator.addVertexWithUV(1, 0, 1, 0.5 + x / 10 + scale, 0.5 + z / 10 + scale);
			tessellator.addVertexWithUV(1, 0, 0, 0.5 + x / 10 + scale, 0.5 + z / 10 - scale);
		} else {
			int cooldown = TeleportationManager.getCooldown(Minecraft.getMinecraft().thePlayer);
			double offset = (double) (TeleportationManager.DEFAULTCOOLDOWN - cooldown) / 100 * 2;
			double yOffest = Math.min(offset, Minecraft.getMinecraft().thePlayer.eyeHeight);
			double pngscale = ((y - cooldown) / 100) / 2;
			tessellator.addVertexWithUV(0 - offset, 0 + yOffest, 0 - offset, 0.5 + x / 10 - pngscale, 0.5 + z / 10 - pngscale);
			tessellator.addVertexWithUV(0 - offset, 0 + yOffest, 1 + offset, 0.5 + x / 10 - pngscale, 0.5 + z / 10 + pngscale);
			tessellator.addVertexWithUV(1 + offset, 0 + yOffest, 1 + offset, 0.5 + x / 10 + pngscale, 0.5 + z / 10 + pngscale);
			tessellator.addVertexWithUV(1 + offset, 0 + yOffest, 0 - offset, 0.5 + x / 10 + pngscale, 0.5 + z / 10 - pngscale);
		}

		tessellator.draw();
		GL11.glPopMatrix();
	}

	/*public void renderTileEntityAt(TileEntityTeleplate tileentity, double x, double y, double z, float f) {
		float f1 = (float)this.field_147501_a.field_147560_j;
		float f2 = (float)this.field_147501_a.field_147561_k;
		float f3 = (float)this.field_147501_a.field_147558_l;
		GL11.glDisable(GL11.GL_LIGHTING);
		random.setSeed(31100L);
		float f4 = 0.75F;

		GL11.glTranslatef(0, -(6.0f/8.0f), 0);
		GL11.glTranslatef(0, 0.001f, 0);

		for (int i = 0; i < 16; ++i)
		{
			GL11.glPushMatrix();
			float f5 = (float)(16 - i);
			float f6 = 0.0625F;
			float f7 = 1.0F / (f5 + 1.0F);

			bindTexture(teleplate);
			GL11.glEnable(GL11.GL_BLEND);

			float f8 = (float)(-(y + (double)f4));
			float f9 = f8 + ActiveRenderInfo.objectY;
			float f10 = f8 + f5 + ActiveRenderInfo.objectY;
			float f11 = f9 / f10;
			f11 += (float)(y + (double)f4);
			GL11.glTranslatef(f1, f11, f3);
			GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
			GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
			GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
			GL11.glTexGeni(GL11.GL_Q, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_EYE_LINEAR);
			GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, this.func_147525_a(1.0F, 0.0F, 0.0F, 0.0F));
			GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, this.func_147525_a(0.0F, 0.0F, 1.0F, 0.0F));
			GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE, this.func_147525_a(0.0F, 0.0F, 0.0F, 1.0F));
			GL11.glTexGen(GL11.GL_Q, GL11.GL_EYE_PLANE, this.func_147525_a(0.0F, 1.0F, 0.0F, 0.0F));
			GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, (float)(Minecraft.getSystemTime() % 700000L) / 700000.0F, 0.0F);
			GL11.glScalef(f6, f6, f6);
			GL11.glTranslatef(0.5F, 0.5F, 0.0F);
			GL11.glRotatef((float)(i * i * 4321 + i * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
			GL11.glTranslatef(-f1, -f3, -f2);
			f9 = f8 + ActiveRenderInfo.objectY;
			GL11.glTranslatef(ActiveRenderInfo.objectX * f5 / f9, ActiveRenderInfo.objectZ * f5 / f9, -f2);
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			f11 = random.nextFloat() * 0.5F + 0.1F;
			float f12 = random.nextFloat() * 0.5F + 0.4F;
			float f13 = random.nextFloat() * 0.5F + 0.5F;

			if (i == 0)
			{
				f13 = 1.0F;
				f12 = 1.0F;
				f11 = 1.0F;
			}

			tessellator.setColorRGBA_F(f11 * f7, f12 * f7, f13 * f7, 1.0F);
			tessellator.addVertex(x, y + (double)f4, z);
			tessellator.addVertex(x, y + (double)f4, z + 1.0D);
			tessellator.addVertex(x + 1.0D, y + (double)f4, z + 1.0D);
			tessellator.addVertex(x + 1.0D, y + (double)f4, z);
			tessellator.draw();
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	private FloatBuffer func_147525_a(float p_147525_1_, float p_147525_2_, float p_147525_3_, float p_147525_4_)
	{
		this.floatBuffer.clear();
		this.floatBuffer.put(p_147525_1_).put(p_147525_2_).put(p_147525_3_).put(p_147525_4_);
		this.floatBuffer.flip();
		return this.floatBuffer;
	}*/

	/*public void renderTileEntityAt(TileEntityTeleplate tileentity, double x, double y, double z, float f) {
		float f1 = (float)this.field_147501_a.field_147560_j;
		float f2 = (float)this.field_147501_a.field_147561_k;
		float f3 = (float)this.field_147501_a.field_147558_l;
		GL11.glDisable(GL11.GL_LIGHTING);
		random.setSeed(31100L);
		float f4 = 0.75F;

		GL11.glTranslatef(0, -(6.0f/8.0f), 0);
		GL11.glTranslatef(0, 0.001f, 0);

		for (int i = 0; i < 16; ++i)
		{
			GL11.glPushMatrix();
			float f5 = (float)(16 - i);
			float f6 = 0.0625F;
			float f7 = 1.0F / (f5 + 1.0F);

			if (i == 0)
			{
				this.bindTexture(endSkyTexture);
				f7 = 0.1F;
				f5 = 65.0F;
				f6 = 0.125F;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			}

			if (i == 1)
			{
				this.bindTexture(endPortalTexture);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
				f6 = 0.5F;
			}

			float f8 = (float)(-(y + (double)f4));
			float f9 = f8 + ActiveRenderInfo.objectY;
			float f10 = f8 + f5 + ActiveRenderInfo.objectY;
			float f11 = f9 / f10;
			f11 += (float)(y + (double)f4);
			GL11.glTranslatef(f1, f11, f3);
			GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
			GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
			GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
			GL11.glTexGeni(GL11.GL_Q, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_EYE_LINEAR);
			GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, this.func_147525_a(1.0F, 0.0F, 0.0F, 0.0F));
			GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, this.func_147525_a(0.0F, 0.0F, 1.0F, 0.0F));
			GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE, this.func_147525_a(0.0F, 0.0F, 0.0F, 1.0F));
			GL11.glTexGen(GL11.GL_Q, GL11.GL_EYE_PLANE, this.func_147525_a(0.0F, 1.0F, 0.0F, 0.0F));
			GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, (float)(Minecraft.getSystemTime() % 700000L) / 700000.0F, 0.0F);
			GL11.glScalef(f6, f6, f6);
			GL11.glTranslatef(0.5F, 0.5F, 0.0F);
			GL11.glRotatef((float)(i * i * 4321 + i * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
			GL11.glTranslatef(-f1, -f3, -f2);
			f9 = f8 + ActiveRenderInfo.objectY;
			GL11.glTranslatef(ActiveRenderInfo.objectX * f5 / f9, ActiveRenderInfo.objectZ * f5 / f9, -f2);
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			f11 = random.nextFloat() * 0.5F + 0.1F;
			float f12 = random.nextFloat() * 0.5F + 0.4F;
			float f13 = random.nextFloat() * 0.5F + 0.5F;

			if (i == 0)
			{
				f13 = 1.0F;
				f12 = 1.0F;
				f11 = 1.0F;
			}

			tessellator.setColorRGBA_F(f11 * f7, f12 * f7, f13 * f7, 1.0F);
			tessellator.addVertex(x, y + (double)f4, z);
			tessellator.addVertex(x, y + (double)f4, z + 1.0D);
			tessellator.addVertex(x + 1.0D, y + (double)f4, z + 1.0D);
			tessellator.addVertex(x + 1.0D, y + (double)f4, z);
			tessellator.draw();
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	private FloatBuffer func_147525_a(float p_147525_1_, float p_147525_2_, float p_147525_3_, float p_147525_4_)
	{
		this.floatBuffer.clear();
		this.floatBuffer.put(p_147525_1_).put(p_147525_2_).put(p_147525_3_).put(p_147525_4_);
		this.floatBuffer.flip();
		return this.floatBuffer;
	}*/

}
