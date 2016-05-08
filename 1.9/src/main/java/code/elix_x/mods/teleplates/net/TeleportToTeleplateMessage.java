package code.elix_x.mods.teleplates.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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
