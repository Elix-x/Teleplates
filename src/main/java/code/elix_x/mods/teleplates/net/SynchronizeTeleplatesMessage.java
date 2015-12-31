package code.elix_x.mods.teleplates.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import code.elix_x.mods.teleplates.teleplates.TeleplatesManager;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SynchronizeTeleplatesMessage implements IMessage{

	public NBTTagCompound nbt;
	
	public SynchronizeTeleplatesMessage() {
		
	}
	
	public SynchronizeTeleplatesMessage(NBTTagCompound nbt) {
		super();
		this.nbt = nbt;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
	}

	public static class SynchronizeTeleplatesMessageHandler implements IMessageHandler<SynchronizeTeleplatesMessage, IMessage>{

		@Override
		public IMessage onMessage(SynchronizeTeleplatesMessage message, MessageContext ctx) {
			/*TeleplatesManager.reset();
			TeleplatesManager.readFromNBT(message.nbt);*/
			SaveSyncManager.onSynchronize(message.nbt);
			return null;
		}
		
	}
	
}
