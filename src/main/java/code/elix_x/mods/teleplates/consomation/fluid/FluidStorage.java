package code.elix_x.mods.teleplates.consomation.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

public class FluidStorage implements IFluidTank {

	protected FluidStack fluid;
	protected int capacity;
	protected int maxReceive;
	protected int maxExtract;

	public FluidStorage(Fluid fluid, int capacity) {
		this(fluid, capacity, capacity, capacity);
	}

	public FluidStorage(Fluid fluid, int capacity, int maxTransfer) {
		this(fluid, capacity, maxTransfer, maxTransfer);
	}

	public FluidStorage(Fluid fluid, int capacity, int maxReceive, int maxExtract) {
		this.fluid = new FluidStack(fluid, 0);
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
	}

	@Override
	public FluidStack getFluid() {
		return fluid;
	}

	public void setFluid(FluidStack fluid) {
		this.fluid = fluid;
	}

	@Override
	public int getFluidAmount() {
		return fluid.amount;
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(this);
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if(fluid.isFluidEqual(resource)){
			int fluidFilled = Math.min(capacity - getFluidAmount(), Math.min(this.maxReceive, resource.amount));

			if (doFill) {
				fluid.amount += fluidFilled;
			}
			return fluidFilled;
		} else {
			return 0;
		}
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		int fluidDrained = Math.min(fluid.amount, Math.min(this.maxExtract, maxDrain));

		if (doDrain) {
			fluid.amount -= fluidDrained;
		}
		return new FluidStack(fluid.getFluid(), fluidDrained);
	}

	public FluidStorage readFromNBT(NBTTagCompound nbt) {
		FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
		setFluid(fluid);
		return this;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		fluid.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public String toString() {
		return "FluidStorage [fluid=" + (fluid == null ? "null" : "FluidStack [fluid=" + fluid.getFluid().getName() + ", amount=" + fluid.amount + "]") + ", capacity=" + capacity + ", maxReceive=" + maxReceive + ", maxExtract=" + maxExtract + "]";
	}

}
