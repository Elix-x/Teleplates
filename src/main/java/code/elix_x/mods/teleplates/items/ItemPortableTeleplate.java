package code.elix_x.mods.teleplates.items;

import code.elix_x.mods.teleplates.config.ConfigurationManager;
import code.elix_x.mods.teleplates.energy.EnergyManager;
import code.elix_x.mods.teleplates.teleplates.TeleplatesManager;
import code.elix_x.mods.teleplates.teleplates.TeleportationManager;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.ItemEnergyContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemPortableTeleplate extends ItemEnergyContainer {

	public ItemPortableTeleplate() {
		super(ConfigurationManager.rfStrorage);
		setUnlocalizedName("portableteleplate");
		setCreativeTab(CreativeTabs.tabTransport);
	}

	@Override
	public int receiveEnergy(ItemStack itemstack, int maxReceive, boolean simulate) {
		return EnergyManager.receiveEnergy(itemstack, maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ItemStack itemstack, int maxExtract, boolean simulate) {
		return EnergyManager.extractEnergy(itemstack, maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(ItemStack itemstack) {
		return EnergyManager.getEnergyStored(itemstack);
	}

	@Override
	public int getMaxEnergyStored(ItemStack itemstack) {
		return EnergyManager.getMaxEnergyStored(itemstack);
	}

	public int receiveEnergy_(ItemStack itemstack, int maxReceive, boolean simulate) {
		return super.receiveEnergy(itemstack, maxReceive, simulate);
	}

	public int getEnergyStored_(ItemStack itemstack) {
		return super.getEnergyStored(itemstack);
	}

	public int getMaxEnergyStored_(ItemStack itemstack) {
		return super.getMaxEnergyStored(itemstack);
	}

	public int extractEnergy_(ItemStack itemstack, int maxExtract, boolean simulate) {
		return super.extractEnergy(itemstack, maxExtract, simulate);
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
		return ConfigurationManager.rfPerTransfer != 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getDurabilityForDisplay(ItemStack itemstack) {
		return 1.0 - (ConfigurationManager.rfUsageType == 1 ? (double) EnergyManager.getStorage(EntityPlayer.func_146094_a(Minecraft.getMinecraft().thePlayer.getGameProfile())).getEnergyStored() / (double) EnergyManager.getStorage(EntityPlayer.func_146094_a(Minecraft.getMinecraft().thePlayer.getGameProfile())).getMaxEnergyStored() : getEnergyStored_(itemstack) / (double) getMaxEnergyStored_(itemstack));
	}
}
