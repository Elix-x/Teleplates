package code.elix_x.mods.teleplates.teleplates;

import java.util.HashMap;
import java.util.Map;

import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.net.CooldownChangeMessage;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;

public class TeleportationManager {

	public static final int DEFAULTCOOLDOWN = 100;

	private static Map<EntityPlayer, Integer> teleportCooldown = new HashMap<EntityPlayer, Integer>();

	public static boolean isTeleporting(EntityPlayer player){
		return teleportCooldown.containsKey(player);
	}

	public static int getCooldown(EntityPlayer player){
		return isTeleporting(player) ? teleportCooldown.get(player) : -1;
	}

	public static void setCooldown(EntityPlayer player, int cooldown){
		if(cooldown == -1) teleportCooldown.remove(player);
		else teleportCooldown.put(player, cooldown);
		if(!player.worldObj.isRemote) TeleplatesBase.net.sendTo(new CooldownChangeMessage(cooldown), (EntityPlayerMP) player);
	}

	public static void onPlayerUpdate(EntityPlayer player){
		if(player != null){
			if(!player.worldObj.isRemote){
				if(player.rotationPitch == 90 && ((player.worldObj.getBlockState(player.getPosition()).getBlock() == TeleplatesBase.teleplate && TeleplatesSavedData.get(player.worldObj).getConsomationManager().canTeleportFromTeleplate(player)) || (player.isActiveItemStackBlocking() && player.getHeldItem(EnumHand.MAIN_HAND) != null && player.getHeldItem(EnumHand.MAIN_HAND).getItem() == TeleplatesBase.portableTeleplate && TeleplatesSavedData.get(player.worldObj).getConsomationManager().canTeleportFromPortableTeleplate(player)))){
					if(!isTeleporting(player)){
						setCooldown(player, DEFAULTCOOLDOWN);
					} else if(getCooldown(player) == 0){
						TeleplatesSavedData.get(player.worldObj).getConsomationManager().onTransfer(player);
						setCooldown(player, -1);
					} else {
						player.rotationYaw += DEFAULTCOOLDOWN - teleportCooldown.get(player);
						setCooldown(player, getCooldown(player) - 1);
					}
				} else {
					setCooldown(player, -1);
				}
			} else {
				if(getCooldown(player) == 0){
					TeleplatesBase.proxy.displayGuiSelectTeleplate();
				}
			}

		}
	}

	public static void teleport(EntityPlayer player, int teleplateId){
		if(player != null){
			TeleplatesSavedData.get(player.worldObj).getConsomationManager().onTransfer(player);
			Teleplate teleplate = TeleplatesSavedData.get(player.worldObj).getTeleplatesManager().getTeleplate(teleplateId);
			if(player.worldObj.provider.getDimension() != teleplate.getPos().getDimId()){
				player.changeDimension(teleplate.getPos().getDimId());
			}
			player.rotationPitch = -90;
			player.setPositionAndUpdate(teleplate.getPos().getX() + 0.5, teleplate.getPos().getY(), teleplate.getPos().getZ() + 0.5);
			player.rotationPitch = -90;
		}
	}
}
