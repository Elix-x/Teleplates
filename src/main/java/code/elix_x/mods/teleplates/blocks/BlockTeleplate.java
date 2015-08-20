package code.elix_x.mods.teleplates.blocks;

import java.util.Map.Entry;
import java.util.UUID;

import code.elix_x.excore.utils.pos.DimBlockPos;
import code.elix_x.mods.teleplates.TeleplatesBase;
import code.elix_x.mods.teleplates.config.ConfigurationManager;
import code.elix_x.mods.teleplates.events.OpPlayerTeleplateEvents;
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
import net.minecraft.world.World;

public class BlockTeleplate extends BlockContainer {

	public BlockTeleplate() {
		super(Material.iron);
		setBlockName("teleplate");
		setBlockBounds(0, 0, 0, 1, /*0.0625f*/ 0, 1);

		setCreativeTab(CreativeTabs.tabTransport);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityTeleplate();
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack) {
		if(entity instanceof EntityPlayer){
			((TileEntityTeleplate) world.getTileEntity(x, y, z)).init((EntityPlayer) entity, itemstack.getDisplayName());
			TeleplatesBase.proxy.displayGuiSetTeleplateName(new DimBlockPos(x, y, z, world.provider.dimensionId));
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int meta, float f1, float f2, float f3) {
		if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.paper){
			TeleplatesBase.proxy.displayGuiSetTeleplateName(new DimBlockPos(x, y, z, world.provider.dimensionId));
		} else {
			if(!world.isRemote && ConfigurationManager.permissionsSystemActive() && TeleplatesManager.isAdmin(((TileEntityTeleplate) world.getTileEntity(x, y, z)).getTeleplateId(), player)){
				if(OpPlayerTeleplateEvents.isSelecting(player)){
					Teleplate teleplate = TeleplatesManager.getTeleplate(((TileEntityTeleplate) world.getTileEntity(x, y, z)).getTeleplateId());
					for(Entry<UUID, Integer> entry : OpPlayerTeleplateEvents.getOps(player).entrySet()){
						teleplate.op(EntityPlayer.func_146094_a(player.getGameProfile()), entry.getKey(), entry.getValue());
					}
					TeleplatesManager.updateUsers();
					OpPlayerTeleplateEvents.setSelecting(player, false);
					player.addChatComponentMessage(new ChatComponentText("Succesfully loaded permissions!"));
				} else if(OpPlayerTeleplateEvents.canSelect(player)){
					OpPlayerTeleplateEvents.setSelecting(player, true);
					player.addChatComponentMessage(new ChatComponentText("Now go and hit with this stick ones taht you want to op. Then come back!"));
				}
			}
		}
		return false;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TeleplatesManager.invalidate(((TileEntityTeleplate) world.getTileEntity(x, y, z)).getTeleplateId());
		super.breakBlock(world, x, y, z, block, meta);
	}

	/*@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List boxes, Entity entity) {
		TileEntityTeleplate teleplate = (TileEntityTeleplate) world.getTileEntity(x, y, z);
//		TeleplatesManager.logger.info(teleplate.getTeleplateId());
		if(entity instanceof EntityPlayer){
			super.addCollisionBoxesToList(world, x, y, z, mask, boxes, entity);
		} else {
			addCollisionBoxesToListt(world, x, y, z, mask, boxes, entity);
		}
	}

	private void addCollisionBoxesToListt(World world, int x, int y, int z, AxisAlignedBB mask, List boxes, Entity entity) {
		AxisAlignedBB axisalignedbb1 = this.getCollisionBoundingBoxFromPooll(world, x, y, z);

        if (axisalignedbb1 != null && mask.intersectsWith(axisalignedbb1))
        {
            boxes.add(axisalignedbb1);
        }
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPooll(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return AxisAlignedBB.getBoundingBox((double)p_149668_2_, (double)p_149668_3_, (double)p_149668_4_, (double)p_149668_2_ + 1, (double)p_149668_3_ + 1, (double)p_149668_4_ + 1);
    }*/

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return -1;
	}
}
