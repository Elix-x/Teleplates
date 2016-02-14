package code.elix_x.mods.teleplates.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import java.util.UUID;

import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import code.elix_x.mods.teleplates.teleplates.TeleplatesManager;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SetTeleplateNameMessage implements IMessage {

	private UUID caller;
	private DimBlockPos teleplate;
	private String newName;
	
	public SetTeleplateNameMessage(){
		
	}

	public SetTeleplateNameMessage(UUID caller, DimBlockPos teleplate, String newName){
		this.caller = caller;
		this.teleplate = teleplate;
		this.newName = newName;
	}

	@Override
	public void fromBytes(ByteBuf buf){
		NBTTagCompound nbt = ByteBufUtils.readTag(buf);
		teleplate = DimBlockPos.createFromNBT(nbt);
		caller = UUID.fromString(nbt.getString("caller"));
		newName = nbt.getString("newName");
	}

	@Override
	public void toBytes(ByteBuf buf){
		NBTTagCompound nbt = new NBTTagCompound();
		teleplate.writeToNBT(nbt);
		nbt.setString("caller", caller.toString());
		nbt.setString("newName", newName);
		ByteBufUtils.writeTag(buf, nbt);
	}
	
	public static class SetTeleplateNameMessageHandler implements IMessageHandler<SetTeleplateNameMessage, IMessage>{
		
		@Override
		public IMessage onMessage(SetTeleplateNameMessage message, MessageContext ctx){
			TeleplatesSavedData.get().getTeleplatesManager().tryChangeName(message.caller, ((TileEntityTeleplate) message.teleplate.getTileEntity()).getTeleplateId(), message.newName);
			return null;
		}
		
	}
	
}
