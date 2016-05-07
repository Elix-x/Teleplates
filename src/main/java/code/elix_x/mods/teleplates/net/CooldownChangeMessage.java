package code.elix_x.mods.teleplates.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class CooldownChangeMessage implements IMessage {

	public int cooldown;

	public CooldownChangeMessage(){

	}

	public CooldownChangeMessage(int cooldown){
		this.cooldown = cooldown;
	}

	@Override
	public void fromBytes(ByteBuf buf){
		cooldown = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf){
		buf.writeInt(cooldown);
	}

}
