package code.elix_x.mods.teleplates.consomation.holidays;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import code.elix_x.mods.teleplates.consomation.IConsomationManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Configuration;

public class HolidaysConsomationManager implements IConsomationManager {

	public static Calendar calendar = Calendar.getInstance();

	public static List<Date> holidays = new ArrayList<Date>();

	static {
		holidays.add(new Date(0, 0, 1));
		holidays.add(new Date(0, 1, 14));
		holidays.add(new Date(0, 1, 19));
		holidays.add(new Date(0, 3, 1));
		holidays.add(new Date(0, 9, 31));
		holidays.add(new Date(0, 11, 24));
		holidays.add(new Date(0, 11, 25));
		holidays.add(new Date(0, 11, 31));

		holidays.add(new Date(116, 4, 1));
		holidays.add(new Date(117, 3, 16));
		holidays.add(new Date(118, 3, 8));
		holidays.add(new Date(119, 3, 28));
		holidays.add(new Date(120, 3, 19));
	}

	public HolidaysConsomationManager(){

	}

	@Override
	public boolean canTeleportFromTeleplate(EntityPlayer player){
		for(Date date : holidays){
			if(calendar.getTime().getMonth() == date.getMonth() && calendar.getTime().getDate() == date.getDate() && (date.getYear() == 0 || calendar.getTime().getYear() == date.getYear())) return true;
		}
		return false;
	}

	@Override
	public boolean canTeleportFromPortableTeleplate(EntityPlayer player){
		for(Date date : holidays){
			if(calendar.getTime().getMonth() == date.getMonth() && calendar.getTime().getDate() == date.getDate() && (date.getYear() == 0 || calendar.getTime().getYear() == date.getYear())) return true;
		}
		return false;
	}

	@Override
	public void onTransfer(EntityPlayer player){
		player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.BOLD + "" + EnumChatFormatting.YELLOW + StatCollector.translateToLocal("teleplates.happyholidays")));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){

	}

	public static void config(Configuration config){

	}

	@Override
	public String getName(){
		return "HOLIDAYS";
	}

}
