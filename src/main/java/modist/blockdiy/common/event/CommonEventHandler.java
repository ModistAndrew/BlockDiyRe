package modist.blockdiy.common.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import modist.blockdiy.BlockDiy;
import modist.blockdiy.common.worldstorage.ModelWorldSavedData;
import modist.blockdiy.network.NetworkLoader;
import modist.blockdiy.network.PacketClientChunk;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.IPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk.CreateEntityType;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlockDiy.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEventHandler {
	
	

	//@SubscribeEvent
	public static void diyBlockHitParticle(final ClientTickEvent event) {
		if(Minecraft.getInstance().world!=null) {
		BlockDiy.LOGGER.info("a");
		BlockDiy.LOGGER.info(Minecraft.getInstance().world.getBlockState(new BlockPos(1, 4, 1)));
		BlockDiy.LOGGER.info(Minecraft.getInstance().world.getTileEntity(new BlockPos(1, 4, 1)));
		BlockDiy.LOGGER.info("b");
		BlockDiy.LOGGER.info(Minecraft.getInstance().world.getBlockState(new BlockPos(1, 5, 1)));
		BlockDiy.LOGGER.info(Minecraft.getInstance().world.getTileEntity(new BlockPos(1, 5, 1)));
		}
		//p a; p m; (p h); m a; p a
	}
	
	@SubscribeEvent
	public static void test(final RightClickBlock event) {
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		BlockDiy.LOGGER.info(world.getBlockState(pos).getShape(world, pos).toBoundingBoxList());
		//p a; p m; (p h); m a; p a
	}
	
	@SubscribeEvent
	public static void test(final ChunkEvent.Unload event) {
		if (event.getWorld().isRemote()&&event.getChunk().getPos().equals(new ChunkPos(0,0))) {
			NetworkLoader.INSTANCE.sendToServer(new PacketClientChunk(0,0));		
		}
	}
	
	/*
	//see world setblockstate
	public static void updateModel(World world, BlockPos pos, BlockState state) {
		BlockDiy.LOGGER.info("cccc");
		BlockDiy.LOGGER.info("ddddddddd");
	}*/
	
	//see serverworld notifyBlockUpdate
	public static void updateModel(ServerWorld world, BlockPos pos, BlockState state1, BlockState state2, int flag) {
		ModelWorldSavedData.get(world).updateModel(world, pos, state2);
	}
	
	//see addtileentity
	public static void addTile(World world, TileEntity tile) {	
		if(tile!=null) {
		if(!world.isRemote) {
		ModelWorldSavedData.get(world).updateModel(world, tile, tile.getPos(), true);
			}		
		}
	}
	
	//see removetileentity
	public static void removeTile(World world, TileEntity tile) {	
		if(tile!=null) {
		if(!world.isRemote) {
		ModelWorldSavedData.get(world).updateModel(world, tile, tile.getPos(), false);
		}		
		}
	}

}