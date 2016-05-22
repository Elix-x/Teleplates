package code.elix_x.mods.teleplates.tileentity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.consumption.ConsumptionManager;
import code.elix_x.mods.teleplates.consumption.IConsumptionManager;
import code.elix_x.mods.teleplates.proxy.ClientProxy;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import code.elix_x.mods.teleplates.teleplates.Teleplate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityTeleplate extends TileEntity implements ITeleplate<TileEntityTeleplate>, ITickable {

	private int teleplate;

	private Map<String, ITeleplateData> data = new HashMap<String, ITeleplateData>();

	public TileEntityTeleplate(){

	}

	@Override
	public TileEntityTeleplate getThis(){
		return this;
	}

	@Override
	public <I extends ITeleplateData> I getData(String name){
		return (I) data.get(name);
	}

	@Override
	public void setData(String name, ITeleplateData data){
		this.data.put(name, data);
	}

	public void init(EntityPlayer player, String name){
		if(!worldObj.isRemote){
			teleplate = TeleplatesSavedData.get(worldObj).getTeleplatesManager().createTeleplate(player, name, new DimBlockPos(this));
			worldObj.markBlockRangeForRenderUpdate(pos, pos);
			markDirty();
		}
	}

	@Override
	public void setWorldObj(World world){
		super.setWorldObj(world);
		TeleplatesSavedData data = TeleplatesSavedData.get(world);

		for(IConsumptionManager manager : data.getConsumptionManager().getActiveManagers()) this.data.put(ConsumptionManager.getName(manager), manager.provideData(this));

		data.getTeleplatesManager().validate(teleplate);
		data.getTeleplatesManager().updateTeleplatePosition(this);
		worldObj.markBlockRangeForRenderUpdate(pos, pos);
	}

	public int getTeleplateId(){
		return teleplate;
	}

	public Teleplate getTeleplate(){
		return TeleplatesSavedData.get(worldObj).getTeleplatesManager().getTeleplate(teleplate);
	}

	public UUID getOwner(){
		return getTeleplate().getOwner();
	}

	public boolean isErrored(){
		return TeleplatesSavedData.get(worldObj).getTeleplatesManager().isErrored(teleplate);
	}

	@Override
	public void update(){
		for(ITeleplateData data : this.data.values()) data.update();
	}

	@Override
	public void validate(){
		super.validate();
		TeleplatesSavedData.get(worldObj).getTeleplatesManager().validate(teleplate);
	}

	@Override
	public void invalidate(){
		super.invalidate();
		TeleplatesSavedData.get(worldObj).getTeleplatesManager().invalidate(teleplate);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		boolean b = super.shouldRefresh(world, pos, oldState, newState);
		if(b){
			TeleplatesSavedData.get(worldObj).getTeleplatesManager().invalidate(teleplate);
		}
		return b;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRenderInPass(int pass){
		return ClientProxy.teleplateRendererVersion != 2 && pass == 0;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket(){
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new SPacketUpdateTileEntity(pos, 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet){
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("teleplate", teleplate);
		for(Entry<String, ITeleplateData> e : data.entrySet()){
			NBTBase tag = e.getValue().serializeNBT();
			if(tag != null) nbt.setTag(e.getKey(), tag);
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		teleplate = nbt.getInteger("teleplate");
		for(Entry<String, ITeleplateData> e : data.entrySet()){
			NBTBase tag = nbt.getTag(e.getKey());
			if(tag != null) e.getValue().deserializeNBT(tag);
		}
	}

	/*
	 * Consomation
	 */

	public boolean isConsomationManagerActive(Class<? extends IConsumptionManager> clas){
		return TeleplatesSavedData.get(worldObj).getConsumptionManager().isManagerActive(clas);
	}

	public <T extends IConsumptionManager> T getActiveConsomationManager(Class<T> clas){
		return TeleplatesSavedData.get(worldObj).getConsumptionManager().getActiveConsomationManager(clas);
	}

}
