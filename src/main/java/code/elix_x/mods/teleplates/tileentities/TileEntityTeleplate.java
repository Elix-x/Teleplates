package code.elix_x.mods.teleplates.tileentities;

import java.util.UUID;

import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.energy.EnergyManager;
import code.elix_x.mods.teleplates.teleplates.TeleplatesManager;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityTeleplate extends TileEntity implements IEnergyReceiver{

	private UUID owner;
	private int teleplate;

	public EnergyStorage storage = EnergyManager.getDefaultStorage();
	
	public TileEntityTeleplate() {

	}

	public void init(EntityPlayer player, String name){
		if(!worldObj.isRemote){
			owner = EntityPlayer.func_146094_a(player.getGameProfile());
			teleplate = TeleplatesManager.createTeleplate(player, name, new DimBlockPos(this));
			markDirty();
		} else {
			owner = EntityPlayer.func_146094_a(player.getGameProfile());
			markDirty();
		}
	}

	@Override
	public void setWorldObj(World world) {
		super.setWorldObj(world);
		if(owner != null){
			TeleplatesManager.validate(teleplate);
			TeleplatesManager.updateTeleplatePosition(this);
		}
	}

	public int getTeleplateId() {
		return teleplate;
	}

	public UUID getOwner(){
		return owner;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		storage.writeToNBT(nbt);
		nbt.setString("owner", owner.toString());
		nbt.setInteger("teleplate", teleplate);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		owner = UUID.fromString(nbt.getString("owner"));
		teleplate = nbt.getInteger("teleplate");
		storage.readFromNBT(nbt);
	}


	@Override
	public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z) {
		boolean b = super.shouldRefresh(oldBlock, newBlock, oldMeta, newMeta, world, x, y, z);
		if(b){
			TeleplatesManager.invalidate(teleplate);
		}
		return b;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return EnergyManager.canConnectEnergy(this, from);
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return EnergyManager.receiveEnergy(this, from, maxReceive, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return EnergyManager.getEnergyStored(this, from);
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return EnergyManager.getMaxEnergyStored(this, from);
	}
}
