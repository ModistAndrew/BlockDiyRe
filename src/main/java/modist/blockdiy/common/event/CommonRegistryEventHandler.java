package modist.blockdiy.common.event;

import modist.blockdiy.BlockDiy;
import modist.blockdiy.common.block.BlockLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Block.Properties;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=BlockDiy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonRegistryEventHandler {

	@SubscribeEvent(priority=EventPriority.HIGH)
	public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
		BlockLoader.registerBlocks(event);
		event.getRegistry().register(new Block(Properties.create(Material.ROCK).notSolid()) {
			@Override
			  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
			      return VoxelShapes.empty();
			   }
		}.setRegistryName(BlockDiy.MODID, "testblock"));
	}

	@SubscribeEvent
	public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
		BlockLoader.registerBlockItems(event);
	}
	
	@SubscribeEvent
	public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
		BlockLoader.registerTileEntities(event);
	}

}
