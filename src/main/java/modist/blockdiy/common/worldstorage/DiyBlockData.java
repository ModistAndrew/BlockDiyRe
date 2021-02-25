package modist.blockdiy.common.worldstorage;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class DiyBlockData {
	public BlockPos pos1;
	public BlockPos pos2;
	public int minX;
	public int maxX;
	public int minY;
	public int maxY;
	public int minZ;
	public int maxZ;

	public DiyBlockData(BlockPos pos1, BlockPos pos2) {
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.minX = Math.min(pos1.getX(), pos2.getX());
		this.maxX = Math.max(pos1.getX(), pos2.getX());
		this.minY = Math.min(pos1.getY(), pos2.getY());
		this.maxY = Math.max(pos1.getY(), pos2.getY());
		this.minZ = Math.min(pos1.getZ(), pos2.getZ());
		this.maxZ = Math.max(pos1.getZ(), pos2.getZ());
	}

	public boolean contains(BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		return x >= this.minX - 1 && x <= this.maxX + 1 
				&& y >= this.minY - 1 && y <= this.maxY + 1
				&& z >= this.minZ - 1 && z <= this.maxZ + 1;//fluid fix
	}
	
	public boolean completelyContains(BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		return x >= this.minX && x <= this.maxX 
				&& y >= this.minY && y <= this.maxY
				&& z >= this.minZ && z <= this.maxZ;//check inside
	}

}
