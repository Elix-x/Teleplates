package code.elix_x.mods.teleplates.net;

import java.util.UUID;

import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import code.elix_x.mods.teleplates.teleplates.Teleplate;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class SetTeleplateNameMessage implements IMessage {

	private UUID caller;
	private Teleplate teleplate;
	
	public SetTeleplateNameMessage(){
		
	}

	public SetTeleplateNameMessage(UUID caller, Teleplate teleplate, String newName){
		this.caller = caller;
		this.teleplate = teleplate;
	}

	@Override
	public void fromBytes(ByteBuf buf){
		NBTTagCompound nbt = ByteBufUtils.readTag(buf);
		teleplate = Teleplate.createFromNBT(nbt);
		caller = UUID.fromString(nbt.getString("caller"));
	}

	@Override
	public void toBytes(ByteBuf buf){
		NBTTagCompound nbt = new NBTTagCompound();
		teleplate.writeToNBT(nbt);
		nbt.setString("caller", caller.toString());
		ByteBufUtils.writeTag(buf, nbt);
	}
	
	public static class SetTeleplateNameMessageHandler implements IMessageHandler<SetTeleplateNameMessage, IMessage>{
		
		@Override
		public IMessage onMessage(SetTeleplateNameMessage message, MessageContext ctx){
//			TeleplatesSavedData.get().getTeleplatesManager().tryChangeName(message.caller, ((TileEntityTeleplate) message.teleplate.getTileEntity()).getTeleplateId(), message.newName);
			return null;
		}
		
	}
	
}
