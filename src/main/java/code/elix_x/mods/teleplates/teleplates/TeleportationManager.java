package code.elix_x.mods.teleplates.teleplates;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;

public class TeleportationManager {

	public static final Logger logger = LogManager.getLogger("Teleportation Manager");

	public static final int DEFAULTCOOLDOWN = 100;

	private static Map<EntityPlayer, Integer> teleportCooldown = new HashMap<EntityPlayer, Integer>();

	public static boolean isTeleporting(EntityPlayer player){
		return teleportCooldown.containsKey(player);
	}

	public static int getCooldown(EntityPlayer player){
		return isTeleporting(player) ? teleportCooldown.get(player) : -1;
	}

	public static void onPlayerUpdate(EntityPlayer player){
		if(player != null){
			Block block;
			if(player.worldObj.isRemote){
				block = player.worldObj.getBlock((int) Math.floor(player.posX), (int) Math.floor(player.posY - 1), (int) Math.floor(player.posZ));
			} else {
				block = player.worldObj.getBlock((int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ));
			}
			if(player.rotationPitch == 90 && ((block == TeleplatesBase.teleplate && TeleplatesSavedData.get(player.worldObj).getConsomationManager().canTeleportFromTeleplate(player)) || (player.isUsingItem() && player.getHeldItem() != null && player.getHeldItem().getItem() == TeleplatesBase.portableTeleplate && TeleplatesSavedData.get(player.worldObj).getConsomationManager().canTeleportFromPortableTeleplate(player)))){
				if(!isTeleporting(player)){
					teleportCooldown.put(player, DEFAULTCOOLDOWN);
				} else if(getCooldown(player) == 0){
					processPlayerTeleportation(player);
					teleportCooldown.put(player, -1);
				} else {
					player.rotationYaw += DEFAULTCOOLDOWN - teleportCooldown.get(player);
					teleportCooldown.put(player, teleportCooldown.get(player) - 1);
				}
			} else {
				teleportCooldown.remove(player);
			}
		}
	}

	private static void processPlayerTeleportation(EntityPlayer player) {
		TeleplatesSavedData.get(player.worldObj).getConsomationManager().onTransfer(player);
		TeleplatesBase.proxy.displayGuiSelectTeleplate();
	}

	public static void teleport(EntityPlayer player, int teleplateId) {
		if(player != null){
			TeleplatesSavedData.get(player.worldObj).getConsomationManager().onTransfer(player);
			Teleplate teleplate = TeleplatesSavedData.get(player.worldObj).getTeleplatesManager().getTeleplate(teleplateId);
			if(player.worldObj.provider.dimensionId != teleplate.getPos().getDimId()){
				player.travelToDimension(teleplate.getPos().getDimId());
			}
			player.rotationPitch = -90;
			player.setPositionAndUpdate(teleplate.getPos().getX() + 0.5, teleplate.getPos().getY(), teleplate.getPos().getZ() + 0.5);
			player.rotationPitch = -90;
		}
	}
}
