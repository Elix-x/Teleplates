package code.elix_x.mods.teleplates.net;

import code.elix_x.mods.teleplates.teleplates.Teleplate;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class SetTeleplateSettingsMessage implements IMessage {

	public Teleplate teleplate;

	public SetTeleplateSettingsMessage(){

	}

	public SetTeleplateSettingsMessage(Teleplate teleplate){
		this.teleplate = teleplate;
	}

	@Override
	public void fromBytes(ByteBuf buf){
		teleplate = Teleplate.createFromNBT(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf){
		ByteBufUtils.writeTag(buf, teleplate.writeToNBT(new NBTTagCompound()));
	}

}
