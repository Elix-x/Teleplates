package code.elix_x.mods.teleplates.events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import code.elix_x.mods.teleplates.teleplates.TeleplatesManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class OpPlayerTeleplateEvents {

	public OpPlayerTeleplateEvents() {

	}

	@SubscribeEvent
	public void hitPlayer(LivingAttackEvent event){
		if(!event.entityLiving.worldObj.isRemote){
			if(event.entityLiving instanceof EntityPlayer && event.source instanceof EntityDamageSource && event.source.getEntity() instanceof EntityPlayer){
				if(isSelecting((EntityPlayer) event.source.getEntity())){
					addOrUpOp((EntityPlayer) event.source.getEntity(), (EntityPlayer) event.entityLiving);
					event.setCanceled(true);
				}
			}
		}
	}

	public static boolean canSelect(EntityPlayer player){
		return player != null && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.stick;
	}

	public static boolean isSelecting(EntityPlayer player){
		return canSelect(player) && player.getCurrentEquippedItem().hasTagCompound() && player.getCurrentEquippedItem().stackTagCompound.hasKey("ops");
	}

	public static void setSelecting(EntityPlayer player, boolean selecting){
		if(canSelect(player)){
			NBTTagCompound nbt = player.getCurrentEquippedItem().stackTagCompound;
			if(nbt == null){
				nbt = new NBTTagCompound();
			}
			if(selecting){
				if(!nbt.hasKey("ops")){
					nbt.setTag("ops", new NBTTagList());
				}
			} else {
				if(nbt.hasKey("ops")){
					nbt.removeTag("ops");
				}
			}
			player.getCurrentEquippedItem().stackTagCompound = nbt;
		}
	}

	public static int getOp(EntityPlayer admin, EntityPlayer op){
		if(isSelecting(admin)){
			String uuid = EntityPlayer.func_146094_a(op.getGameProfile()).toString();

			NBTTagList list = (NBTTagList) admin.getCurrentEquippedItem().stackTagCompound.getTag("ops");
			for(int i = 0; i < list.tagCount(); i++){
				NBTTagCompound tag = list.getCompoundTagAt(0);
				if(tag.getString("uuid").equals(uuid)){
					return tag.getInteger("level");
				}
			}
		}
		return 0;
	}

	public static void addOrUpOp(EntityPlayer admin, EntityPlayer op){
		if(isSelecting(admin)){
			NBTTagList list = (NBTTagList) admin.getCurrentEquippedItem().stackTagCompound.getTag("ops");
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("uuid", EntityPlayer.func_146094_a(op.getGameProfile()).toString());
			int i = getOp(admin, op);
			i++;
			i = i > 3 ? 0 : i;
			tag.setInteger("level", i);
			list.appendTag(tag);
			admin.getCurrentEquippedItem().stackTagCompound.setTag("ops", list);
			admin.addChatComponentMessage(new ChatComponentText(op.getDisplayName() + " is now " + getOpLevelName(i)));
		}
	}

	public static Map<UUID, Integer> getOps(EntityPlayer admin){
		HashMap<UUID, Integer> map = new HashMap<UUID, Integer>();
		if(isSelecting(admin)){
			NBTTagList list = (NBTTagList) admin.getCurrentEquippedItem().stackTagCompound.getTag("ops");
			for(int i = 0; i < list.tagCount(); i++){
				NBTTagCompound tag = list.getCompoundTagAt(0);
				map.put(UUID.fromString(tag.getString("uuid")), tag.getInteger("level"));
			}
		}
		return map;
	}

	private static String getOpLevelName(int i) {
		switch (i) {
		case 0:
			return "\"nobody\"";

		case 1:
			return "user";

		case 2:
			return "moderator";

		case 3:
			return "administrator";

		default:
			return "\"nobody\"";
		}
	}

}
