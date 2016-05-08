package code.elix_x.mods.teleplates.net;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class SynchronizeTeleplatesMessage implements IMessage {

	public NBTTagCompound nbt;

	public SynchronizeTeleplatesMessage(){

	}

	public SynchronizeTeleplatesMessage(NBTTagCompound nbt){
		this.nbt = nbt;
	}

	@Override
	public void fromBytes(ByteBuf buf){
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf){
		ByteBufUtils.writeTag(buf, nbt);
	}

}
