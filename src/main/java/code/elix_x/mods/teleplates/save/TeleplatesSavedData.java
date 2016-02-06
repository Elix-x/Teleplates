package code.elix_x.mods.teleplates.save;

import java.io.File;
import java.io.FileInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.consomation.ConsomationManager;
import code.elix_x.mods.teleplates.net.SynchronizeTeleplatesMessage;
import code.elix_x.mods.teleplates.teleplates.TeleplatesManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.event.world.WorldEvent.Load;

public class TeleplatesSavedData extends WorldSavedData {

	public static final Logger logger = LogManager.getLogger("Teleplates Saved Data");

	public static final String NAME = "Teleplates";

	public static TeleplatesSavedData get(World world){
		TeleplatesSavedData data = (TeleplatesSavedData) world.mapStorage.loadData(TeleplatesSavedData.class, NAME);
		if(data == null){
			data = new TeleplatesSavedData(NAME);
			world.mapStorage.setData(NAME, data);
		}
		return data;
	}

	public static TeleplatesSavedData get(){
		return get(MinecraftServer.getServer().getEntityWorld());
	}

	@SideOnly(Side.CLIENT)
	public static TeleplatesSavedData getClient(){
		return get(Minecraft.getMinecraft().theWorld);
	}

	private TeleplatesManager teleplatesManager = new TeleplatesManager(this);

	private ConsomationManager consomationManager = new ConsomationManager(this);

	public TeleplatesSavedData(String name){
		super(name);
	}

	public TeleplatesManager getTeleplatesManager(){
		return teleplatesManager;
	}

	public ConsomationManager getConsomationManager(){
		return consomationManager;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		teleplatesManager.readFromNBT(nbt);
		consomationManager.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt){
		teleplatesManager.writeToNBT(nbt);
		consomationManager.writeToNBT(nbt);
	}

	@Override
	public boolean isDirty() {
		return true;
	}

	public void synchronizeWith(EntityPlayerMP player){
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		TeleplatesBase.net.sendTo(new SynchronizeTeleplatesMessage(nbt), player);
	}

	public void synchronizeWithAll(){
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		TeleplatesBase.net.sendToAll(new SynchronizeTeleplatesMessage(nbt));
	}

	public static void load(Load event){
		if(event.world.provider.dimensionId == 0 && !event.world.isRemote){
			File teleplates = new File(event.world.getSaveHandler().getWorldDirectory(), "teleplates-move.dat");
			if(teleplates.exists()){
				try{
					NBTTagCompound nbt = CompressedStreamTools.readCompressed(new FileInputStream(teleplates));
					get(event.world).readFromNBT(nbt);
				} catch(Exception e){
					logger.error("Caught exception while reading move teleplates file: ", e);
				} finally {
					teleplates.delete();
				}
			}
		}
	}

}
