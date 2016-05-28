package code.elix_x.mods.teleplates.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class TeleportToTeleplateMessage implements IMessage {

	public int teleplate;
	public String password;

	public TeleportToTeleplateMessage(){

	}

	public TeleportToTeleplateMessage(int teleplateId, String password){
		this.teleplate = teleplateId;
		this.password = password;
	}

	@Override
	public void fromBytes(ByteBuf buf){
		teleplate = buf.readInt();
		password = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf){
		buf.writeInt(teleplate);
		ByteBufUtils.writeUTF8String(buf, password);
	}

}
