package code.elix_x.mods.teleplates.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class TeleportToTeleplateMessage implements IMessage {

	public int teleplate;

	public TeleportToTeleplateMessage(){

	}

	public TeleportToTeleplateMessage(int teleplateId){
		this.teleplate = teleplateId;
	}

	@Override
	public void fromBytes(ByteBuf buf){
		teleplate = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf){
		buf.writeInt(teleplate);
	}

}
