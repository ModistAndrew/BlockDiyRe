package modist.blockdiy.client.event;

import modist.blockdiy.BlockDiy;
import modist.blockdiy.client.block.model.DiyBlockBakedModel;
import modist.blockdiy.client.tileentity.DiyBlockTileEntityRenderer;
import modist.blockdiy.client.tileentity.TestTER;
import modist.blockdiy.common.block.BlockLoader;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlockDiy.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistryEventHandler {
	
	@SubscribeEvent
	public static void onModelBake(final ModelBakeEvent event) {
		event.getModelRegistry().put(new ModelResourceLocation(BlockLoader.DIY_BLOCK.getRegistryName(), ""),
				new DiyBlockBakedModel());
	}

	@SubscribeEvent
	public static void onTextureStitch(final TextureStitchEvent.Pre event) {
		if (event.getMap().getTextureLocation().equals(PlayerContainer.LOCATION_BLOCKS_TEXTURE)) {
			event.addSprite(DiyBlockBakedModel.DIY_BLOCK_TEXTURE);
		}
	}
	
	//注册diyblock的blockcolor
	@SubscribeEvent
	public static void blockColors(ColorHandlerEvent.Block event) {
		/*IBlockColor需提供根据BlockState，ILightReader（一般为World），BlockPos以及tintindex返回int颜色代码的方法（-1不染色）
		 *原版渲染时，若BakedQuad的tintindex为-1，则不染色；否则，传入BakeQuad的tintindex获取颜色代码染色
		 *原版中模型的每个BakedQuad（其实是一个长方体的6个BakedQuads）的tintindex可以设置，默认为-1不染色，否则一般设置为零
		 *事实上，BakedQuad的tintindex具体数值无关紧要，因为原版的IBlockColor一般不处理tintindex
		 *但在此，我们需将IBlockColor定义为返回颜色代码就是tintindex数值
		 *同时在diyblock模型的quads中塞入这样一个tintindex，它就是quads所属分模型的颜色代码（若quads不染色，则tintindex为-1，也不染色）
		 这样，渲染时就会传入BakeQuad的tintindex，为分模型颜色代码（或-1），返回的恰好是分模型颜色代码（或不染色），以正确染色*/
	    event.getBlockColors().register((p_getColor_1_, p_getColor_2_, p_getColor_3_, p_getColor_4_)->{
	    	return p_getColor_4_; //不管其他参数，返回与tintindex相同的颜色代码（或-1，但-1直接不渲染，无关紧要）
	    },
	    		BlockLoader.DIY_BLOCK);
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		ClientRegistry.bindTileEntityRenderer(BlockLoader.DIY_BLOCK_TILE_ENTITY, DiyBlockTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(BlockLoader.TEST_TILE_ENTITY, TestTER::new);
		RenderTypeLookup.setRenderLayer(BlockLoader.DIY_BLOCK, RenderType.getCutoutMipped());
	}
}
