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
	
	//ע��diyblock��blockcolor
	@SubscribeEvent
	public static void blockColors(ColorHandlerEvent.Block event) {
		/*IBlockColor���ṩ����BlockState��ILightReader��һ��ΪWorld����BlockPos�Լ�tintindex����int��ɫ����ķ�����-1��Ⱦɫ��
		 *ԭ����Ⱦʱ����BakedQuad��tintindexΪ-1����Ⱦɫ�����򣬴���BakeQuad��tintindex��ȡ��ɫ����Ⱦɫ
		 *ԭ����ģ�͵�ÿ��BakedQuad����ʵ��һ���������6��BakedQuads����tintindex�������ã�Ĭ��Ϊ-1��Ⱦɫ������һ������Ϊ��
		 *��ʵ�ϣ�BakedQuad��tintindex������ֵ�޹ؽ�Ҫ����Ϊԭ���IBlockColorһ�㲻����tintindex
		 *���ڴˣ������轫IBlockColor����Ϊ������ɫ�������tintindex��ֵ
		 *ͬʱ��diyblockģ�͵�quads����������һ��tintindex��������quads������ģ�͵���ɫ���루��quads��Ⱦɫ����tintindexΪ-1��Ҳ��Ⱦɫ��
		 ��������Ⱦʱ�ͻᴫ��BakeQuad��tintindex��Ϊ��ģ����ɫ���루��-1�������ص�ǡ���Ƿ�ģ����ɫ���루��Ⱦɫ��������ȷȾɫ*/
	    event.getBlockColors().register((p_getColor_1_, p_getColor_2_, p_getColor_3_, p_getColor_4_)->{
	    	return p_getColor_4_; //��������������������tintindex��ͬ����ɫ���루��-1����-1ֱ�Ӳ���Ⱦ���޹ؽ�Ҫ��
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
