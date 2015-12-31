package code.elix_x.mods.teleplates.items;

import org.apache.commons.lang3.ArrayUtils;

import code.elix_x.excore.utils.math.AdvancedMathUtils;
import code.elix_x.mods.teleplates.consomation.ConsomationManager;
import code.elix_x.mods.teleplates.consomation.energy.EnergyConsomationManager;
import code.elix_x.mods.teleplates.consomation.fluid.FluidConsomationManager;
import code.elix_x.mods.teleplates.teleplates.TeleportationManager;
import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public class ItemPortableTeleplate extends Item implements IEnergyContainerItem, IFluidContainerItem {

	protected int energyCapacity;
	protected int energyMaxReceive;
	protected int energyMaxExtract;

	protected int fluidCapacity;

	public ItemPortableTeleplate() {
		setUnlocalizedName("portableteleplate");
		setCreativeTab(CreativeTabs.tabTransport);
		setMaxStackSize(1);
		energyCapacity = energyMaxReceive = energyMaxExtract = EnergyConsomationManager.INSTANCE.rfStorage;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		player.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		return itemstack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return TeleportationManager.DEFAULTCOOLDOWN;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.none;
	}

	@Override
	public boolean showDurabilityBar(ItemStack itemstack) {
		return (ConsomationManager.isActive(EnergyConsomationManager.class) && EnergyConsomationManager.INSTANCE.rfUsageType != 0) || (ConsomationManager.isActive(FluidConsomationManager.class) && FluidConsomationManager.INSTANCE.fluidConsomationType != 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getDurabilityForDisplay(ItemStack itemstack) {
		if(!showDurabilityBar(itemstack)) return 0;
		
		double[] ds = new double[]{};
		if(EnergyConsomationManager.INSTANCE.rfUsageType != 0 ) ds = ArrayUtils.add(ds, EnergyConsomationManager.INSTANCE.rfUsageType == 1 ? (double) EnergyConsomationManager.INSTANCE.getEnergyStorage(Minecraft.getMinecraft().thePlayer).getEnergyStored() / (double) EnergyConsomationManager.INSTANCE.getEnergyStorage(Minecraft.getMinecraft().thePlayer).getMaxEnergyStored() : getEnergyStored_(itemstack) / (double) getMaxEnergyStored_(itemstack));
		if(FluidConsomationManager.INSTANCE.fluidConsomationType != 0 ) ds = ArrayUtils.add(ds, FluidConsomationManager.INSTANCE.fluidConsomationType == 1 ? (double) FluidConsomationManager.INSTANCE.getFluidStorage(Minecraft.getMinecraft().thePlayer).getFluidAmount() / (double) FluidConsomationManager.INSTANCE.getFluidStorage(Minecraft.getMinecraft().thePlayer).getCapacity() : getFluid_(itemstack).amount);
		return 1 - AdvancedMathUtils.average(ds);
	}

	/*
	 * Energy
	 */

	@Override
	public int receiveEnergy(ItemStack itemstack, int maxReceive, boolean simulate) {
		return EnergyConsomationManager.INSTANCE.receiveEnergy(itemstack, maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ItemStack itemstack, int maxExtract, boolean simulate) {
		return EnergyConsomationManager.INSTANCE.extractEnergy(itemstack, maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(ItemStack itemstack) {
		return EnergyConsomationManager.INSTANCE.getEnergyStored(itemstack);
	}

	@Override
	public int getMaxEnergyStored(ItemStack itemstack) {
		return EnergyConsomationManager.INSTANCE.getMaxEnergyStored(itemstack);
	}

	public int receiveEnergy_(ItemStack container, int maxReceive, boolean simulate) {

		if (container.stackTagCompound == null) {
			container.stackTagCompound = new NBTTagCompound();
		}
		int energy = container.stackTagCompound.getInteger("Energy");
		int energyReceived = Math.min(energyCapacity - energy, Math.min(this.energyMaxReceive, maxReceive));

		if (!simulate) {
			energy += energyReceived;
			container.stackTagCompound.setInteger("Energy", energy);
		}
		return energyReceived;
	}

	public int extractEnergy_(ItemStack container, int maxExtract, boolean simulate) {

		if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Energy")) {
			return 0;
		}
		int energy = container.stackTagCompound.getInteger("Energy");
		int energyExtracted = Math.min(energy, Math.min(this.energyMaxExtract, maxExtract));

		if (!simulate) {
			energy -= energyExtracted;
			container.stackTagCompound.setInteger("Energy", energy);
		}
		return energyExtracted;
	}

	public int getEnergyStored_(ItemStack container) {

		if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Energy")) {
			return 0;
		}
		return container.stackTagCompound.getInteger("Energy");
	}

	public int getMaxEnergyStored_(ItemStack container) {
		return energyCapacity;
	}

	/*
	 * Fluid
	 */

	@Override
	public FluidStack getFluid(ItemStack container) {
		return FluidConsomationManager.INSTANCE.getFluid(container);
	}

	@Override
	public int getCapacity(ItemStack container) {
		return FluidConsomationManager.INSTANCE.getCapacity(container);
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill) {
		return FluidConsomationManager.INSTANCE.fill(container, resource, doFill);
	}

	@Override
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
		return FluidConsomationManager.INSTANCE.drain(container, maxDrain, doDrain);
	}

	public FluidStack getFluid_(ItemStack container){
		if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Fluid"))
		{
			return null;
		}
		return FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("Fluid"));
	}

	public int getCapacity_(ItemStack container){
		return fluidCapacity;
	}

	public int fill_(ItemStack container, FluidStack resource, boolean doFill){
		if (resource == null)
		{
			return 0;
		}

		if (!doFill)
		{
			if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Fluid"))
			{
				return Math.min(fluidCapacity, resource.amount);
			}

			FluidStack stack = FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("Fluid"));

			if (stack == null)
			{
				return Math.min(fluidCapacity, resource.amount);
			}

			if (!stack.isFluidEqual(resource))
			{
				return 0;
			}

			return Math.min(fluidCapacity - stack.amount, resource.amount);
		}

		if (container.stackTagCompound == null)
		{
			container.stackTagCompound = new NBTTagCompound();
		}

		if (!container.stackTagCompound.hasKey("Fluid"))
		{
			NBTTagCompound fluidTag = resource.writeToNBT(new NBTTagCompound());

			if (fluidCapacity < resource.amount)
			{
				fluidTag.setInteger("Amount", fluidCapacity);
				container.stackTagCompound.setTag("Fluid", fluidTag);
				return fluidCapacity;
			}

			container.stackTagCompound.setTag("Fluid", fluidTag);
			return resource.amount;
		}

		NBTTagCompound fluidTag = container.stackTagCompound.getCompoundTag("Fluid");
		FluidStack stack = FluidStack.loadFluidStackFromNBT(fluidTag);

		if (!stack.isFluidEqual(resource))
		{
			return 0;
		}

		int filled = fluidCapacity - stack.amount;
		if (resource.amount < filled)
		{
			stack.amount += resource.amount;
			filled = resource.amount;
		}
		else
		{
			stack.amount = fluidCapacity;
		}

		container.stackTagCompound.setTag("Fluid", stack.writeToNBT(fluidTag));
		return filled;
	}

	public FluidStack drain_(ItemStack container, int maxDrain, boolean doDrain){
		if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Fluid"))
		{
			return null;
		}

		FluidStack stack = FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("Fluid"));
		if (stack == null)
		{
			return null;
		}

		int currentAmount = stack.amount;
		stack.amount = Math.min(stack.amount, maxDrain);
		if (doDrain)
		{
			if (currentAmount == stack.amount)
			{
				container.stackTagCompound.removeTag("Fluid");

				if (container.stackTagCompound.hasNoTags())
				{
					container.stackTagCompound = null;
				}
				return stack;
			}

			NBTTagCompound fluidTag = container.stackTagCompound.getCompoundTag("Fluid");
			fluidTag.setInteger("Amount", currentAmount - stack.amount);
			container.stackTagCompound.setTag("Fluid", fluidTag);
		}
		return stack;
	}

}
