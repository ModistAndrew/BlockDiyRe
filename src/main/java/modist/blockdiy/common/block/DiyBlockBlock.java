package modist.blockdiy.common.block;

import java.util.ArrayList;
import java.util.List;

import modist.blockdiy.common.tileentity.DiyBlockTileEntity;
import modist.blockdiy.util.AABBHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class DiyBlockBlock extends Block {

	public DiyBlockBlock() {
		super(Properties.create(Material.ROCK).hardnessAndResistance(3.0F,12.0F).notSolid());
	}

	/*@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return context.equals(ISelectionContext.dummy())?VoxelShapes.empty():VoxelShapes.fullCube();
	}*/

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		DiyBlockTileEntity tile = (DiyBlockTileEntity)worldIn.getTileEntity(pos);
		if(tile!=null) {
		return tile.shape;
		}
		return VoxelShapes.empty();
	}

	@Override
	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return false;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new DiyBlockTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if (!worldIn.isRemote) {
			TileEntity te = worldIn.getTileEntity(pos);
			if (te instanceof DiyBlockTileEntity) {
				DiyBlockTileEntity te1 = ((DiyBlockTileEntity) te);
				//te1.setRenderPos(new BlockPos(2, 4, 2), new BlockPos(9, 11, 9)); //注意不要套娃
				te1.setRenderPos(pos.add(-4,-4,-4), pos.add(-1,-1,-1)); //注意不要套娃
				//te1.translate(1F, 1F, 1F);
				//te1.scale(1.8F, 1.8F, 1.8F);
				//te1.rotate(90, 90, 45);
				te1.update();
			}
		}
		return ActionResultType.SUCCESS;
	}

}
