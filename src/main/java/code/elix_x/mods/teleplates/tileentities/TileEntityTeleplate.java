package code.elix_x.mods.teleplates.tileentities;

import java.util.UUID;

import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.consomation.IConsomationManager;
import code.elix_x.mods.teleplates.consomation.energy.EnergyConsomationManager;
import code.elix_x.mods.teleplates.consomation.fluid.FluidConsomationManager;
import code.elix_x.mods.teleplates.consomation.fluid.FluidStorage;
import code.elix_x.mods.teleplates.consomation.thaumcraft.EssentiaConsomationManager;
import code.elix_x.mods.teleplates.consomation.thaumcraft.EssentiaStorage;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import code.elix_x.mods.teleplates.teleplates.Teleplate;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.Optional.Method;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;

@InterfaceList({@Interface(modid = "Thaumcraft", iface = "thaumcraft.api.aspects.IEssentiaTransport"), @Interface(modid = "Thaumcraft", iface = "thaumcraft.api.aspects.IAspectContainer")})
public class TileEntityTeleplate extends TileEntity implements IEnergyReceiver, IFluidHandler, IEssentiaTransport, IAspectContainer {

	private int teleplate;

	public EnergyStorage energyStorage;
	public FluidStorage fluidStorage;

	private Object essentiaStorage;
	public int essentiaSuction = 120;

	public TileEntityTeleplate(){
		energyStorage = EnergyConsomationManager.getDefaultStorage();
		fluidStorage = FluidConsomationManager.getDefaultStorage();
		if(Loader.isModLoaded("Thaumcraft")) essentiaStorage = EssentiaConsomationManager.getDefaultStorage();
	}

	public void init(EntityPlayer player, String name){
		teleplate = TeleplatesSavedData.get(worldObj).getTeleplatesManager().createTeleplate(player, name, new DimBlockPos(this));
	}

	@Override
	public void setWorldObj(World world){
		super.setWorldObj(world);
		TeleplatesSavedData data = TeleplatesSavedData.get(world);		

		data.getTeleplatesManager().validate(teleplate);
		data.getTeleplatesManager().updateTeleplatePosition(this);
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
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("teleplate", teleplate);
		energyStorage.writeToNBT(nbt);
		fluidStorage.writeToNBT(nbt);
		if(Loader.isModLoaded("Thaumcraft")) writeEssentiaStorageToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		teleplate = nbt.getInteger("teleplate");
		energyStorage.readFromNBT(nbt);
		fluidStorage.readFromNBT(nbt);
		if(Loader.isModLoaded("Thaumcraft")) readEssentiaStorageFromNBT(nbt);
	}

	@Override
	public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z){
		boolean b = super.shouldRefresh(oldBlock, newBlock, oldMeta, newMeta, world, x, y, z);
		if(b){
			TeleplatesSavedData.get(worldObj).getTeleplatesManager().invalidate(teleplate);
		}
		return b;
	}

	@Override
	public void updateEntity(){
		if(isErrored() && !worldObj.isRemote) worldObj.spawnParticle("angryVillager", xCoord + worldObj.rand.nextDouble(), yCoord + worldObj.rand.nextDouble(), zCoord + worldObj.rand.nextDouble(), 0, -0.05, 0);
		if(Loader.isModLoaded("Thaumcraft")) thaumUpdate();
	}

	public boolean isConsomationManagerActive(Class<? extends IConsomationManager> clas){
		return TeleplatesSavedData.get(worldObj).getConsomationManager().isManagerActive(clas);
	}

	public <T extends IConsomationManager> T getActiveConsomationManager(Class<T> clas){
		return TeleplatesSavedData.get(worldObj).getConsomationManager().getActiveConsomationManager(clas);
	}

	/*
	 * Energy
	 */

	public boolean isEnergyConsomationManagerActive(){
		return isConsomationManagerActive(EnergyConsomationManager.class);
	}

	public EnergyConsomationManager getActiveEnergyConsomationManager(){
		return getActiveConsomationManager(EnergyConsomationManager.class);
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from){
		return isEnergyConsomationManagerActive() ? getActiveEnergyConsomationManager().canConnectEnergy(this, from) : false;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate){
		return isEnergyConsomationManagerActive() ? getActiveEnergyConsomationManager().receiveEnergy(this, from, maxReceive, simulate) : 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from){
		return isEnergyConsomationManagerActive() ? getActiveEnergyConsomationManager().getEnergyStored(this, from) : 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from){
		return isEnergyConsomationManagerActive() ? getActiveEnergyConsomationManager().getMaxEnergyStored(this, from) : 0;
	}

	/*
	 * Fluid
	 */

	public boolean isFluidConsomationManagerActive(){
		return isConsomationManagerActive(FluidConsomationManager.class);
	}

	public FluidConsomationManager getActiveFluidConsomationManager(){
		return getActiveConsomationManager(FluidConsomationManager.class);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid){
		return isFluidConsomationManagerActive() ? getActiveFluidConsomationManager().canFill(this, from, fluid) : false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid){
		return isFluidConsomationManagerActive() ? getActiveFluidConsomationManager().canDrain(this, from, fluid) : false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from){
		return isFluidConsomationManagerActive() ? getActiveFluidConsomationManager().getTankInfo(this, from) : new FluidTankInfo[]{};
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill){
		return isFluidConsomationManagerActive() ? getActiveFluidConsomationManager().fill(this, from, resource, doFill) : 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain){
		return isFluidConsomationManagerActive() ? getActiveFluidConsomationManager().drain(this, from, resource, doDrain) : null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain){
		return isFluidConsomationManagerActive() ? getActiveFluidConsomationManager().drain(this, from, maxDrain, doDrain) : null;
	}

	/*
	 * Essentia
	 */

	@Method(modid = "Thaumcraft")
	public boolean isEssentiaConsomationManagerActive(){
		return isConsomationManagerActive(EssentiaConsomationManager.class);
	}

	@Method(modid = "Thaumcraft")
	public EssentiaConsomationManager getActiveEssentiaConsomationManager(){
		return getActiveConsomationManager(EssentiaConsomationManager.class);
	}

	@Method(modid = "Thaumcraft")
	public EssentiaStorage getEssentiaStorage(){
		return (EssentiaStorage) essentiaStorage;
	}

	@Method(modid = "Thaumcraft")
	private void thaumUpdate(){
		if(isEssentiaConsomationManagerActive()) getActiveEssentiaConsomationManager().thaumUpdate(this);
	}

	@Method(modid = "Thaumcraft")
	@Override
	public boolean isConnectable(ForgeDirection side){
		return side == ForgeDirection.DOWN;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public boolean canInputFrom(ForgeDirection side){
		return side == ForgeDirection.DOWN;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public boolean canOutputTo(ForgeDirection side){
		return false;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public Aspect getSuctionType(ForgeDirection side){
		return isEssentiaConsomationManagerActive() ? getActiveEssentiaConsomationManager().getSuctionType(this, side) : null;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public int getSuctionAmount(ForgeDirection side){
		return isEssentiaConsomationManagerActive() ? getActiveEssentiaConsomationManager().getSuctionAmount(this, side) : 0;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public void setSuction(Aspect aspect, int suction){
		if(isEssentiaConsomationManagerActive()) getActiveEssentiaConsomationManager().setSuction(this, aspect, suction);
	}

	@Method(modid = "Thaumcraft")
	@Override
	public int takeEssentia(Aspect aspect, int amount, ForgeDirection side){
		return isEssentiaConsomationManagerActive() ? getActiveEssentiaConsomationManager().takeEssentia(this, aspect, amount, side) : 0;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public int addEssentia(Aspect aspect, int amount, ForgeDirection side){
		return isEssentiaConsomationManagerActive() ? getActiveEssentiaConsomationManager().addEssentia(this, aspect, amount, side) : 0;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public Aspect getEssentiaType(ForgeDirection side){
		return isEssentiaConsomationManagerActive() ? getActiveEssentiaConsomationManager().getEssentiaType(this, side) : null;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public int getEssentiaAmount(ForgeDirection side){
		return isEssentiaConsomationManagerActive() ? getActiveEssentiaConsomationManager().getEssentiaAmount(this, side) : 0;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public int getMinimumSuction(){
		return 0;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public boolean renderExtendedTube(){
		return false;
	}

	@Method(modid = "Thaumcraft")
	private void writeEssentiaStorageToNBT(NBTTagCompound nbt){
		NBTTagCompound tag = new NBTTagCompound();
		getEssentiaStorage().writeToNBT(tag);
		tag.setInteger("suction", essentiaSuction);
		nbt.setTag("essentia", tag);
	}

	@Method(modid = "Thaumcraft")
	private void readEssentiaStorageFromNBT(NBTTagCompound nbt){
		NBTTagCompound tag = nbt.getCompoundTag("essentia");
		getEssentiaStorage().readFromNBT(tag);
		essentiaSuction = tag.getInteger("suction");
	}

	/*
	 * Aspects
	 */

	@Method(modid = "Thaumcraft")
	@Override
	public AspectList getAspects(){
		return isEssentiaConsomationManagerActive() ? getActiveEssentiaConsomationManager().getAspects(this) : new AspectList();
	}

	@Method(modid = "Thaumcraft")
	@Override
	public void setAspects(AspectList aspects){
		if(isEssentiaConsomationManagerActive()) getActiveEssentiaConsomationManager().setAspects(this, aspects);
	}

	@Method(modid = "Thaumcraft")
	@Override
	public boolean doesContainerAccept(Aspect aspect){
		return isEssentiaConsomationManagerActive() ? getActiveEssentiaConsomationManager().doesContainerAccept(this, aspect) : false;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public int addToContainer(Aspect aspect, int amount){
		return isEssentiaConsomationManagerActive() ? getActiveEssentiaConsomationManager().addToContainer(this, aspect, amount) : amount;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public boolean takeFromContainer(Aspect aspect, int amount){
		return isEssentiaConsomationManagerActive() ? getActiveEssentiaConsomationManager().takeFromContainer(this, aspect, amount) : false;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public boolean takeFromContainer(AspectList aspects){
		return isEssentiaConsomationManagerActive() ? getActiveEssentiaConsomationManager().takeFromContainer(this, aspects) : false;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public boolean doesContainerContainAmount(Aspect aspect, int amount){
		return isEssentiaConsomationManagerActive() ? getActiveEssentiaConsomationManager().doesContainerContainAmount(this, aspect, amount) : false;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public boolean doesContainerContain(AspectList aspects){
		return isEssentiaConsomationManagerActive() ? getActiveEssentiaConsomationManager().doesContainerContain(this, aspects) : false;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public int containerContains(Aspect aspect){
		return isEssentiaConsomationManagerActive() ? getActiveEssentiaConsomationManager().containerContains(this, aspect) : 0;
	}

}
