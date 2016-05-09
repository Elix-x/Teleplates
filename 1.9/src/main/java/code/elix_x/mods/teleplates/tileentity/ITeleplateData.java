package code.elix_x.mods.teleplates.tileentity;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.INBTSerializable;

public interface ITeleplateData<N extends NBTBase> extends ITickable, INBTSerializable<N> {

}
