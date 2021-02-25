package modist.blockdiy.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;

public class AABBHelper {

	public static AxisAlignedBB offsetAndScale(AxisAlignedBB aabb, BlockPos offset, float x, float y, float z) {
		AxisAlignedBB aabb1 = aabb.offset(offset);
		return new AxisAlignedBB
				(aabb1.minX * x, aabb1.minY * y, aabb1.minZ * z, aabb1.maxX * x, aabb1.maxY * y, aabb1.maxZ * z);
	}
	
	public static List<AxisAlignedBB> generateAABBs
	(BlockPos posmin, Set<BlockPos> poses, World world, float x, float y, float z) {
		List<AxisAlignedBB> aabblist = new ArrayList<AxisAlignedBB>();
		poses.forEach(pos->{
		BlockState datastate = world.getBlockState(pos);
		datastate.getCollisionShape(world, pos, ISelectionContext.dummy()).toBoundingBoxList().forEach(aabb->
			aabblist.add(AABBHelper.offsetAndScale(aabb, 
					new BlockPos(pos.getX() - posmin.getX(), pos.getY() - posmin.getY(), pos.getZ() - posmin.getZ()), 
					x, y, z)));
		});
		return aabblist;
	}
	
	public static VoxelShape fromAABBList(List<AxisAlignedBB> aabbs){
		if(!aabbs.isEmpty()) {
		VoxelShape vox = VoxelShapes.create(aabbs.get(0));
		for(int i = 1; i<aabbs.size(); i++) {
		vox = VoxelShapes.combine(vox, VoxelShapes.create(aabbs.get(i)), IBooleanFunction.OR);
		}
		return vox.simplify();
		}
		return VoxelShapes.empty();
	}
}
