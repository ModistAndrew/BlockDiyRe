package modist.blockdiy;

import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import modist.blockdiy.network.NetworkLoader;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import net.minecraftforge.common.data.ForgeRecipeProvider;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("blockdiy")
public class BlockDiy {

	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "blockdiy";
	public static Stack<BlockPos> blockDiyTiles = new Stack<BlockPos>();

	public BlockDiy() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		NetworkLoader.registerMessages();
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		
	}

	private boolean test() {
		return true;
	}
	
	public static void log(Chunk chunk, boolean flag) {
		//if(flag) {
		LOGGER.info("qqqqq"+flag);
		//}
	}
	
	public static void log(boolean flag) {
		//if(flag) {
		LOGGER.info("qqqqq"+flag);
		//}
	}
	
	public static void log(World world, BlockPos pos, BlockState state) {
		LOGGER.info("qqqqqkkkk");
	}

}
