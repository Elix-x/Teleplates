package code.elix_x.mods.teleplates.consomation.thaumcraft;

import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;

public class EssentiaStorage {

	protected Aspect aspect;
	protected int amount;
	protected int capacity;
	protected int maxReceive;
	protected int maxExtract;

	public EssentiaStorage(Aspect aspect, int capacity) {
		this(aspect, capacity, capacity, capacity);
	}

	public EssentiaStorage(Aspect aspect, int capacity, int maxTransfer) {
		this(aspect, capacity, maxTransfer, maxTransfer);
	}

	public EssentiaStorage(Aspect aspect, int capacity, int maxReceive, int maxExtract) {
		this.aspect = aspect;
		this.amount = 0;
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
	}

	public Aspect getAspect() {
		return aspect;
	}

	public void setAspect(Aspect aspect) {
		this.aspect = aspect;
	}

	public int getEssentiaAmount() {
		return amount;
	}

	public int getCapacity() {
		return capacity;
	}

	public int fill(Aspect aspect, int maxFill, boolean doFill) {
		if(this.aspect == aspect){
			int essentiaFilled = Math.min(capacity - amount, Math.min(this.maxReceive, maxFill));

			if (doFill) {
				amount += essentiaFilled;
			}
			return essentiaFilled;
		} else {
			return 0;
		}
	}

	public int drain(int maxDrain, boolean doDrain) {		
		int essentiaDrained = Math.min(amount, Math.min(this.maxExtract, maxDrain));

		if (doDrain) {
			amount -= essentiaDrained;
		}
		return essentiaDrained;
	}

	public EssentiaStorage readFromNBT(NBTTagCompound nbt) {
		aspect = Aspect.getAspect(nbt.getString("aspect"));
		amount = nbt.getInteger("amount");
		return this;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setString("aspect", aspect.getTag());
		nbt.setInteger("amount", amount);
		return nbt;
	}

	@Override
	public String toString() {
		return "EssentiaStorage [aspect=" + aspect.getName() + ", amount=" + amount + ", capacity=" + capacity + ", maxReceive=" + maxReceive + ", maxExtract=" + maxExtract + "]";
	}

}
