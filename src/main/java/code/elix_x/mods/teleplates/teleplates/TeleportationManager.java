package code.elix_x.mods.teleplates.teleplates;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.config.ConfigurationManager;
import code.elix_x.mods.teleplates.energy.EnergyManager;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class TeleportationManager {

	public static final Logger logger = LogManager.getLogger("Teleportation Manager");

	public static final int DEFAULTCOOLDOWN = 100;

	private static Map<EntityPlayer, Integer> teleportCooldown = new HashMap<EntityPlayer, Integer>();

	public static boolean isTeleporting(EntityPlayer player){
		/*if(player != null){
			if(player.rotationPitch == -90){
				if(player.worldObj.getBlock((int) player.posX, (int) player.posY, (int) player.posZ) == TeleplatesBase.teleplate){
					return true;
				}
			}
		}
		return false;*/
		return teleportCooldown.containsKey(player);
	}

	public static int getCooldown(EntityPlayer player){
		return isTeleporting(player) ? teleportCooldown.get(player) : -1;
	}

	public static void onPlayerUpdate(EntityPlayer player){
		if(player != null){
			Block block;
			if(/*player.getClass().getName().contains("net.minecraft.client")*/player.worldObj.isRemote){
				block = player.worldObj.getBlock((int) Math.floor(player.posX), (int) Math.floor(player.posY - 1), (int) Math.floor(player.posZ));
			} else {
				block = player.worldObj.getBlock((int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ));
			}
			if(player.rotationPitch == 90 && ((block == TeleplatesBase.teleplate && EnergyManager.canTeleportFromTeleplate(player)) || (player.getItemInUse() != null && player.getItemInUse().getItem() == TeleplatesBase.portableTeleplate && EnergyManager.canTeleportFromPortableTeleplate(player)))){
				if(!isTeleporting(player)){
					teleportCooldown.put(player, DEFAULTCOOLDOWN);
				} else if(getCooldown(player) == 0){
					processPlayerTeleportation(player);
					teleportCooldown.put(player, -1);
				} else {
					player.rotationYaw -= DEFAULTCOOLDOWN - teleportCooldown.get(player);
					teleportCooldown.put(player, teleportCooldown.get(player) - 1);
				}
			} else {
				teleportCooldown.remove(player);
			}
		}
	}

	private static void processPlayerTeleportation(EntityPlayer player) {
		EnergyManager.onTransfer(player);
		TeleplatesBase.proxy.displatGuiSelectTeleplate();
	}

	public static void teleport(UUID playerUUID, int dimId, int teleplateId) {
		if(TeleplatesManager.getTeleplates(playerUUID).contains(teleplateId)){
			EntityPlayer player = MinecraftServer.getServer().worldServerForDimension(dimId).func_152378_a(playerUUID);
			if(player != null){
				EnergyManager.onTransfer(player);
				Teleplate teleplate = TeleplatesManager.getTeleplate(teleplateId);
				if(dimId != teleplate.getPos().getDimId()){
					player.travelToDimension(teleplate.getPos().getDimId());
				}
				player.rotationPitch = -90;
				player.setPositionAndUpdate(teleplate.getPos().getX() + 0.5, teleplate.getPos().getY(), teleplate.getPos().getZ() + 0.5);
				player.rotationPitch = -90;
			}
		}
	}
}
