package modist.blockdiy.common.worldstorage;

import java.util.HashMap;

import modist.blockdiy.common.tileentity.DiyBlockTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

public class ModelWorldSavedData extends WorldSavedData {
    private static final String NAME = "ModelWorldSavedData";
    private HashMap<BlockPos, DiyBlockData> poses = new HashMap<BlockPos, DiyBlockData>();

    public ModelWorldSavedData() {
        super(NAME);
    }

    public void putPos(BlockPos tilepos, BlockPos pos1, BlockPos pos2) {
    	if(pos1!=null&&pos2!=null) {
    	poses.put(tilepos, new DiyBlockData(pos1, pos2));
    	}
    }
    
	public void removePos(BlockPos tilepos) {
		poses.remove(tilepos);
	}

	public void updateModelNoBoard(World world, BlockPos pos, BlockState state) {
		poses.keySet().forEach(tilepos->{
			if(poses.get(tilepos).completelyContains(pos) && world.getTileEntity(tilepos) instanceof DiyBlockTileEntity) {
				DiyBlockTileEntity diytile = (DiyBlockTileEntity)world.getTileEntity(tilepos);
				if(diytile.checkRenderPos()) {
				diytile.update(pos, state);				
				}
			}
		});
	}
	
	public void updateModel(World world, BlockPos pos, BlockState state) {
		poses.keySet().forEach(tilepos->{
			if(poses.get(tilepos).contains(pos) && world.getTileEntity(tilepos) instanceof DiyBlockTileEntity) {
				DiyBlockTileEntity diytile = (DiyBlockTileEntity)world.getTileEntity(tilepos);
				if(diytile.checkRenderPos()) {
				diytile.update(pos, state);				
				}
			}
		});
	}
	
	public void updateModel(World world, TileEntity tile, BlockPos pos, boolean add) {
		poses.keySet().forEach(tilepos->{
			if(poses.get(tilepos).completelyContains(pos) && world.getTileEntity(tilepos) instanceof DiyBlockTileEntity) {
				DiyBlockTileEntity diytile = (DiyBlockTileEntity)world.getTileEntity(tilepos);
				if(diytile.checkRenderPos()) {
				diytile.update(pos, tile, add);				
				}
			}
		});
	}
	
	/*public static void updatemodelc(World world, TileEntity tile, BlockPos pos, boolean add) {
			BlockPos tilepos = new BlockPos(0,4,0);
			if(world.getTileEntity(tilepos) instanceof DiyBlockTileEntity) {
				DiyBlockTileEntity diytile = (DiyBlockTileEntity)world.getTileEntity(tilepos);
				if(diytile.checkRenderPos()) {
				diytile.updatec(pos, tile, add);				
				}
			}
	}*/

    public static ModelWorldSavedData get(World worldIn) {   
    	if(worldIn instanceof ServerWorld) {
        ServerWorld world = (ServerWorld)worldIn;
        DimensionSavedDataManager storage = world.getSavedData();
        return storage.getOrCreate(() -> new ModelWorldSavedData(), NAME);
    	}
    	return null;
    }

	@Override
	public void read(CompoundNBT nbt) {		
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		return compound;
	}

}
