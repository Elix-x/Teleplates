package code.elix_x.mods.teleplates.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Throwables;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SyncConfigurationMessage implements IMessage {

	public File temp;

	public SyncConfigurationMessage(){
		try {
			temp = File.createTempFile("Teleplates#" + System.currentTimeMillis(), "cfg");
			temp.createNewFile();
		} catch(IOException e){
			Throwables.propagate(e);
		}
	}

	@Override
	public void fromBytes(ByteBuf buf){
		try {
			System.out.println("Read S");
			IOUtils.copy(new ByteBufInputStream(buf), new FileOutputStream(temp));
			System.out.println("Read F");
		} catch(IOException e){
			Throwables.propagate(e);
		}
	}

	@Override
	public void toBytes(ByteBuf buf){
		try {
			System.out.println("Write S");
			IOUtils.copy(new FileInputStream(temp), new ByteBufOutputStream(buf));
			System.out.println("Write F");
		} catch(IOException e){
			Throwables.propagate(e);
		}
	}

}
