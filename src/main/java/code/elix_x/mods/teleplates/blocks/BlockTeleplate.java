package code.elix_x.mods.teleplates.blocks;

import java.util.Map.Entry;
import java.util.UUID;

import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.config.ConfigurationManager;
import code.elix_x.mods.teleplates.events.OpPlayerTeleplateEvents;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
import code.elix_x.mods.teleplates.teleplates.Teleplate;
import code.elix_x.mods.teleplates.teleplates.TeleplatesManager;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class BlockTeleplate extends BlockContainer {

	public BlockTeleplate(){
		super(Material.iron);
		setBlockName("teleplate");
		setBlockBounds(0, 0, 0, 1, 0, 1);

		setCreativeTab(CreativeTabs.tabTransport);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityTeleplate();
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack){
		if(entity instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) entity;
			((TileEntityTeleplate) world.getTileEntity(x, y, z)).init(player, itemstack.getDisplayName());
			TeleplatesBase.proxy.displayGuiSetTeleplateName(player, new DimBlockPos(x, y, z, world.provider.dimensionId));
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int meta, float f1, float f2, float f3){
		if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.paper){
			TeleplatesBase.proxy.displayGuiSetTeleplateName(player, new DimBlockPos(x, y, z, world.provider.dimensionId));
		} else {
			if(!world.isRemote && ConfigurationManager.permissionsSystemActive() && TeleplatesSavedData.get(world).getTeleplatesManager().isAdmin(((TileEntityTeleplate) world.getTileEntity(x, y, z)).getTeleplateId(), player)){
				if(OpPlayerTeleplateEvents.isSelecting(player)){
					Teleplate teleplate = TeleplatesSavedData.get(world).getTeleplatesManager().getTeleplate(((TileEntityTeleplate) world.getTileEntity(x, y, z)).getTeleplateId());
					for(Entry<UUID, Integer> entry : OpPlayerTeleplateEvents.getOps(player).entrySet()){
						teleplate.op(EntityPlayer.func_146094_a(player.getGameProfile()), entry.getKey(), entry.getValue());
					}
					TeleplatesSavedData.get(world).getTeleplatesManager().updateUsers();
					OpPlayerTeleplateEvents.setSelecting(player, false);
					player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("teleplates.permission.success")));
				} else if(OpPlayerTeleplateEvents.canSelect(player)){
					OpPlayerTeleplateEvents.setSelecting(player, true);
					player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("teleplates.permissions.stick")));
				}
			}
		}
		return false;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta){
		TeleplatesSavedData.get(world).getTeleplatesManager().invalidate(((TileEntityTeleplate) world.getTileEntity(x, y, z)).getTeleplateId());
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}

	@Override
	public int getRenderType(){
		return -1;
	}
}
