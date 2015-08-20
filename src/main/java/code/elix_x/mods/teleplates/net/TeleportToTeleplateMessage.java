package code.elix_x.mods.teleplates.net;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import code.elix_x.mods.teleplates.teleplates.TeleportationManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class TeleportToTeleplateMessage implements IMessage{

	private UUID player;
	private int dimId;
	private int teleplate;
	
	public TeleportToTeleplateMessage() {
		
	}
	
	public TeleportToTeleplateMessage(EntityPlayer player, int dimId, int teleplateId) {
		this.player = EntityPlayer.func_146094_a(player.getGameProfile());
		this.dimId = dimId;
		this.teleplate = teleplateId;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound nbt = ByteBufUtils.readTag(buf);
		player = UUID.fromString(nbt.getString("player"));
		dimId = nbt.getInteger("dimId");
		teleplate = nbt.getInteger("teleplate");
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("player", player.toString());
		nbt.setInteger("dimId", dimId);
		nbt.setInteger("teleplate", teleplate);
		ByteBufUtils.writeTag(buf, nbt);
	}
	
	public static class TeleportToTeleplateMessageHandler implements IMessageHandler<TeleportToTeleplateMessage, IMessage>{

		@Override
		public IMessage onMessage(TeleportToTeleplateMessage message, MessageContext ctx) {
			TeleportationManager.teleport(message.player, message.dimId, message.teleplate);
			return null;
		}
		
		
		
	}

}
