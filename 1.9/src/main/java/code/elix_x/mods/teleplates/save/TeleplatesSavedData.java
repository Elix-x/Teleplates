package code.elix_x.mods.teleplates.save;

import java.io.File;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import code.elix_x.excore.utils.reflection.AdvancedReflectionHelper.AConstructor;
import code.elix_x.excore.utils.reflection.AdvancedReflectionHelper.AField;
import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.clas.TeleplatesAltClassLoader;
import code.elix_x.mods.teleplates.config.ConfigurationManager;
import code.elix_x.mods.teleplates.consumption.ConsumptionManager;
import code.elix_x.mods.teleplates.net.SynchronizeTeleplatesMessage;
import code.elix_x.mods.teleplates.teleplates.TeleplatesManager;
import code.elix_x.mods.teleplates.tileentity.ITeleplate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.config.Configuration;

public class TeleplatesSavedData<T extends TileEntity & ITeleplate> extends WorldSavedData {

	public static final Logger logger = LogManager.getLogger("Teleplates Saved Data");

	public static final String NAME = "Teleplates";

	private static final AField<TileEntity, Map<String, Class <? extends TileEntity>>> nameToClassMap = new AField(TileEntity.class, "nameToClassMap", "field_145855_i");
	private static final AField<TileEntity, Map<Class<? extends TileEntity>, String>> classToNameMap = new AField(TileEntity.class, "classToNameMap", "field_145853_j");

	public static TeleplatesSavedData get(World world){
		TeleplatesSavedData data = (TeleplatesSavedData) world.getMapStorage().loadData(TeleplatesSavedData.class, NAME);
		if(data == null){
			data = new TeleplatesSavedData(NAME);
			world.getMapStorage().setData(NAME, data);
		}
		if(data.clas == null){
			data.consumptionManager = new ConsumptionManager(data, ConfigurationManager.config(world));

			data.clas = new TeleplatesAltClassLoader(TeleplatesSavedData.class.getClassLoader(), data.consumptionManager).genTeleplateClass("code.elix_x.mods.teleplates.tileentity.TileEntityTeleplate");
			data.constr = new AConstructor(data.clas);

			nameToClassMap.get(null).remove("Teleplate");

			TileEntity.addMapping(data.clas, "Teleplate");
		}
		return data;
	}

	public Class<T> clas;

	private AConstructor<T> constr;

	private TeleplatesManager teleplatesManager = new TeleplatesManager(this);

	private ConsumptionManager consumptionManager;

	public TeleplatesSavedData(String name){
		super(name);
	}

	public T newInstance(){
		return constr.newInstance();
	}

	public TeleplatesManager getTeleplatesManager(){
		return teleplatesManager;
	}

	public ConsumptionManager getConsumptionManager(){
		return consumptionManager;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		teleplatesManager.readFromNBT(nbt);
		consumptionManager.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt){
		teleplatesManager.writeToNBT(nbt);
		consumptionManager.writeToNBT(nbt);
	}

	@Override
	public boolean isDirty(){
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

}
