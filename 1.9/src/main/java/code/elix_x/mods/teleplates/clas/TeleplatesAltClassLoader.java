package code.elix_x.mods.teleplates.clas;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.base.Throwables;

import code.elix_x.mods.teleplates.consumption.ConsumptionManager;
import code.elix_x.mods.teleplates.consumption.IConsumptionManager;
import code.elix_x.mods.teleplates.tileentity.ITeleplate;
import net.minecraft.tileentity.TileEntity;

public class TeleplatesAltClassLoader extends ClassLoader {

	public final ConsumptionManager manager;

	public TeleplatesAltClassLoader(ClassLoader parent, ConsumptionManager manager){
		super(parent);
		this.manager = manager;
	}

	public <T extends TileEntity & ITeleplate> Class<T> genTeleplateClass(String name){
		try {
			ClassNode node = new ClassNode();
			ClassReader reader = new ClassReader(getResourceAsStream(name.replace('.', '/') + ".class"));
			reader.accept(node, 0);

			for(IConsumptionManager m : manager.getActiveManagers()){
				Class<? extends ITeleplate> c = m.getInterfaceClass();
				if(c != null && c.isInterface()) node.interfaces.add(c.getName().replace('.', '/'));
			}

			ClassWriter writer = new ClassWriter(0);
			node.accept(writer);
			byte[] bytes = writer.toByteArray();
			return (Class<T>) defineClass(name, bytes, 0, bytes.length);
		} catch(Exception e){
			throw Throwables.propagate(e);
		}
	}

}
