package modist.blockdiy.common.tileentity;

import modist.blockdiy.common.block.BlockLoader;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

public class TestTileEntity extends TileEntity {
	public TestTileEntity() {
		super(BlockLoader.TEST_TILE_ENTITY);
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		return super.write(compound);
	}
}