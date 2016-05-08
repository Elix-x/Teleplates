package code.elix_x.mods.teleplates.consomation.bloodmagic;

import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import code.elix_x.mods.teleplates.consomation.IConsomationManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

public class LPConsomationManager implements IConsomationManager {

	public static int lpPerTransfer = 2500;

	public static boolean drainLifeWhenNotEnough = false;

	public LPConsomationManager(){

	}

	@Override
	public boolean canTeleportFromTeleplate(EntityPlayer player){
		return drainLifeWhenNotEnough || SoulNetworkHandler.getCurrentEssence(SoulNetworkHandler.getUsername(player)) >= lpPerTransfer * 2;
	}

	@Override
	public boolean canTeleportFromPortableTeleplate(EntityPlayer player){
		return drainLifeWhenNotEnough || SoulNetworkHandler.getCurrentEssence(SoulNetworkHandler.getUsername(player)) >= lpPerTransfer * 2;
	}

	@Override
	public void onTransfer(EntityPlayer player){
		if(drainLifeWhenNotEnough){
			SoulNetworkHandler.syphonAndDamageFromNetwork(SoulNetworkHandler.getUsername(player), player, lpPerTransfer);
		} else {
			SoulNetworkHandler.syphonFromNetwork(SoulNetworkHandler.getUsername(player), lpPerTransfer);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){

	}

	@Override
	public String getName(){
		return "LP";
	}

	public static void config(Configuration config){
		lpPerTransfer = config.getInt("LP Per Transfer", "CONSOMATION-LP", 2500, 1, Integer.MAX_VALUE, "Amount of LP consumed to transfer player to/from 5th dimension.");
		drainLifeWhenNotEnough = config.getBoolean("Drain Life When Not Enough LP", "CONSOMATION-LP", false, "Should teleplates drain life when not enough points are present in LP network.");
	}

}
