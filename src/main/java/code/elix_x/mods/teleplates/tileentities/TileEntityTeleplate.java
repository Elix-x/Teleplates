package code.elix_x.mods.teleplates.tileentities;

import java.util.UUID;

import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.consomation.energy.EnergyConsomationManager;
import code.elix_x.mods.teleplates.consomation.fluid.FluidConsomationManager;
import code.elix_x.mods.teleplates.consomation.fluid.FluidStorage;
import code.elix_x.mods.teleplates.consomation.thaumcraft.EssentiaConsomationManager;
import code.elix_x.mods.teleplates.consomation.thaumcraft.EssentiaStorage;
import code.elix_x.mods.teleplates.teleplates.TeleplatesManager;
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

	private UUID owner;
	private int teleplate;

	public EnergyStorage energyStorage = EnergyConsomationManager.INSTANCE.getDefaultStorage();
	public FluidStorage fluidStorage = FluidConsomationManager.INSTANCE.getDefaultStorage();

	private Object essentiaStorage;
	public int essentiaSuction = 120;

	public TileEntityTeleplate() {
			if(Loader.isModLoaded("Thaumcraft")) essentiaStorage = EssentiaConsomationManager.INSTANCE.getDefaultStorage();
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
		nbt.setString("owner", owner.toString());
		nbt.setInteger("teleplate", teleplate);
		energyStorage.writeToNBT(nbt);
		fluidStorage.writeToNBT(nbt);
		if(Loader.isModLoaded("Thaumcraft")) writeEssentiaStorageToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		owner = UUID.fromString(nbt.getString("owner"));
		teleplate = nbt.getInteger("teleplate");
		energyStorage.readFromNBT(nbt);
		fluidStorage.readFromNBT(nbt);
		if(Loader.isModLoaded("Thaumcraft")) readEssentiaStorageFromNBT(nbt);
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
	public void updateEntity() {
		if(Loader.isModLoaded("Thaumcraft")) thaumUpdate();
	}

	/*
	 * Energy
	 */

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return EnergyConsomationManager.INSTANCE.canConnectEnergy(this, from);
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return EnergyConsomationManager.INSTANCE.receiveEnergy(this, from, maxReceive, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return EnergyConsomationManager.INSTANCE.getEnergyStored(this, from);
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return EnergyConsomationManager.INSTANCE.getMaxEnergyStored(this, from);
	}

	/*
	 * Fluid
	 */

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return FluidConsomationManager.INSTANCE.canFill(this, from, fluid);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return FluidConsomationManager.INSTANCE.canDrain(this, from, fluid);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return FluidConsomationManager.INSTANCE.getTankInfo(this, from);
	}
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return FluidConsomationManager.INSTANCE.fill(this, from, resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return FluidConsomationManager.INSTANCE.drain(this, from, resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return FluidConsomationManager.INSTANCE.drain(this, from, maxDrain, doDrain);
	}

	/*
	 * Essentia
	 */
	
	@Method(modid = "Thaumcraft")
	public EssentiaStorage getEssentiaStorage(){
		return (EssentiaStorage) essentiaStorage;
	}

	@Method(modid = "Thaumcraft")
	private void thaumUpdate() {
		EssentiaConsomationManager.INSTANCE.thaumUpdate(this);
	}
	
	@Method(modid = "Thaumcraft")
	@Override
	public boolean isConnectable(ForgeDirection side) {
		return side == ForgeDirection.DOWN;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public boolean canInputFrom(ForgeDirection side) {
		return side == ForgeDirection.DOWN;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public boolean canOutputTo(ForgeDirection side) {
		return false;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public Aspect getSuctionType(ForgeDirection side) {
		return EssentiaConsomationManager.INSTANCE.getSuctionType(this, side);
	}
	
	@Method(modid = "Thaumcraft")
	@Override
	public int getSuctionAmount(ForgeDirection side) {
		return EssentiaConsomationManager.INSTANCE.getSuctionAmount(this, side);
	}
	
	@Method(modid = "Thaumcraft")
	@Override
	public void setSuction(Aspect aspect, int suction) {
		EssentiaConsomationManager.INSTANCE.setSuction(this, aspect, suction);
	}

	@Method(modid = "Thaumcraft")
	@Override
	public int takeEssentia(Aspect aspect, int amount, ForgeDirection side) {
		return EssentiaConsomationManager.INSTANCE.takeEssentia(this, aspect, amount, side);
	}

	@Method(modid = "Thaumcraft")
	@Override
	public int addEssentia(Aspect aspect, int amount, ForgeDirection side) {
		return EssentiaConsomationManager.INSTANCE.addEssentia(this, aspect, amount, side);
	}

	@Method(modid = "Thaumcraft")
	@Override
	public Aspect getEssentiaType(ForgeDirection side) {
		return EssentiaConsomationManager.INSTANCE.getEssentiaType(this, side);
	}

	@Method(modid = "Thaumcraft")
	@Override
	public int getEssentiaAmount(ForgeDirection side) {
		return EssentiaConsomationManager.INSTANCE.getEssentiaAmount(this, side);
	}

	@Method(modid = "Thaumcraft")
	@Override
	public int getMinimumSuction() {
		return 0;
	}

	@Method(modid = "Thaumcraft")
	@Override
	public boolean renderExtendedTube() {
		return false;
	}
	
	@Method(modid = "Thaumcraft")
	private void writeEssentiaStorageToNBT(NBTTagCompound nbt) {
		NBTTagCompound tag = new NBTTagCompound();
		getEssentiaStorage().writeToNBT(tag);
		tag.setInteger("suction", essentiaSuction);
		nbt.setTag("essentia", tag);
	}

	@Method(modid = "Thaumcraft")
	private void readEssentiaStorageFromNBT(NBTTagCompound nbt) {
		NBTTagCompound tag = nbt.getCompoundTag("essentia");
		getEssentiaStorage().readFromNBT(tag);
		essentiaSuction = tag.getInteger("suction");
	}
	
	/*
	 * Aspects
	 */

	@Method(modid = "Thaumcraft")
	@Override
	public AspectList getAspects() {
		return EssentiaConsomationManager.INSTANCE.getAspects(this);
	}

	@Method(modid = "Thaumcraft")
	@Override
	public void setAspects(AspectList aspects) {
		EssentiaConsomationManager.INSTANCE.setAspects(this, aspects);
	}

	@Method(modid = "Thaumcraft")
	@Override
	public boolean doesContainerAccept(Aspect aspect) {
		return EssentiaConsomationManager.INSTANCE.doesContainerAccept(this, aspect);
	}

	@Method(modid = "Thaumcraft")
	@Override
	public int addToContainer(Aspect aspect, int amount) {
		return EssentiaConsomationManager.INSTANCE.addToContainer(this, aspect, amount);
	}

	@Method(modid = "Thaumcraft")
	@Override
	public boolean takeFromContainer(Aspect aspect, int amount) {
		return EssentiaConsomationManager.INSTANCE.takeFromContainer(this, aspect, amount);
	}

	@Method(modid = "Thaumcraft")
	@Override
	public boolean takeFromContainer(AspectList aspects) {
		return EssentiaConsomationManager.INSTANCE.takeFromContainer(this, aspects);
	}

	@Method(modid = "Thaumcraft")
	@Override
	public boolean doesContainerContainAmount(Aspect aspect, int amount) {
		return EssentiaConsomationManager.INSTANCE.doesContainerContainAmount(this, aspect, amount);
	}

	@Method(modid = "Thaumcraft")
	@Override
	public boolean doesContainerContain(AspectList aspects) {
		return EssentiaConsomationManager.INSTANCE.doesContainerContain(this, aspects);
	}

	@Method(modid = "Thaumcraft")
	@Override
	public int containerContains(Aspect aspect) {
		return EssentiaConsomationManager.INSTANCE.containerContains(this, aspect);
	}

}
