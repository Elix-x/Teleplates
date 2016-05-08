package code.elix_x.mods.teleplates.blocks;

import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.names.TeleplatesRandomName;
import code.elix_x.mods.teleplates.tileentities.TileEntityTeleplate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTeleplate extends Block {

	public BlockTeleplate(){
		super(Material.IRON);
		setUnlocalizedName("teleplate");
		setCreativeTab(CreativeTabs.TRANSPORTATION);
	}

	@Override
	public boolean hasTileEntity(IBlockState state){
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state){
		return new TileEntityTeleplate();
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack itemstack){
		if(entity instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) entity;
			((TileEntityTeleplate) world.getTileEntity(pos)).init(player, itemstack.hasDisplayName() ? itemstack.getDisplayName() : TeleplatesRandomName.next(world.rand));
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack itemstack, EnumFacing side, float hitX, float hitY, float hitZ){
		if(itemstack != null && itemstack.getItem() == Items.PAPER){
			TeleplatesBase.proxy.displayGuiSetTeleplateName(player, new DimBlockPos(pos, world.provider.getDimension()));
		}
		return false;
	}

}
