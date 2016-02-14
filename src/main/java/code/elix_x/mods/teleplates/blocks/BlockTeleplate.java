package code.elix_x.mods.teleplates.blocks;

import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.names.TeleplatesRandomName;
import code.elix_x.mods.teleplates.save.TeleplatesSavedData;
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
import net.minecraft.world.World;

public class BlockTeleplate extends BlockContainer {

	public BlockTeleplate(){
		super(Material.iron);
		setBlockName("teleplate");
		setBlockBounds(0, 0, 0, 1, 0, 1);

		setBlockUnbreakable();

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
			((TileEntityTeleplate) world.getTileEntity(x, y, z)).init(player, itemstack.hasDisplayName() ? itemstack.getDisplayName() : TeleplatesRandomName.next(world.rand));
			TeleplatesBase.proxy.displayGuiSetTeleplateName(player, new DimBlockPos(x, y, z, world.provider.dimensionId));
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int meta, float f1, float f2, float f3){
		if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.paper){
			TeleplatesBase.proxy.displayGuiSetTeleplateName(player, new DimBlockPos(x, y, z, world.provider.dimensionId));
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
