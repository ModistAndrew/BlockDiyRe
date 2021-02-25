package modist.blockdiy.common.block;

import modist.blockdiy.BlockDiy;
import modist.blockdiy.common.tileentity.DiyBlockTileEntity;
import modist.blockdiy.common.tileentity.TestTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;

public class BlockLoader {

	public static final Block DIY_BLOCK = new DiyBlockBlock();
	public static final TileEntityType<DiyBlockTileEntity> DIY_BLOCK_TILE_ENTITY = TileEntityType.Builder.create(DiyBlockTileEntity::new, BlockLoader.DIY_BLOCK).build(null);
	public static final Block TEST_BLOCK = new TestBlock();
	public static final TileEntityType<TestTileEntity> TEST_TILE_ENTITY = TileEntityType.Builder.create(TestTileEntity::new, BlockLoader.TEST_BLOCK).build(null);

	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		registerBlock(DIY_BLOCK, "diy_block", event);
		registerBlock(TEST_BLOCK, "test_block", event);
	}

	public static void registerBlockItems(final RegistryEvent.Register<Item> event) {
		registerBlockItem(DIY_BLOCK, event);
		registerBlockItem(TEST_BLOCK, event);
	}
	
	public static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
		registerTileEntity(DIY_BLOCK_TILE_ENTITY, "diy_block_tile_entity", event);
		registerTileEntity(TEST_TILE_ENTITY, "test_tile_entity", event);
	}

	private static void registerBlock(Block block, String name, final RegistryEvent.Register<Block> event) {
		block.setRegistryName(BlockDiy.MODID, name);
		event.getRegistry().register(block);

	}

	private static void registerBlockItem(Block block, final RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new BlockItem(block, new Properties()).setRegistryName(block.getRegistryName()));
	}
	
	private static void registerTileEntity(TileEntityType<?> tileentitytype, String name, final RegistryEvent.Register<TileEntityType<?>> event) {
		event.getRegistry().register(tileentitytype.setRegistryName(BlockDiy.MODID, name));
	}
	
}
