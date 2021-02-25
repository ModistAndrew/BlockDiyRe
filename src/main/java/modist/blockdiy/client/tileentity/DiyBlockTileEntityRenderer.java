package modist.blockdiy.client.tileentity;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import modist.blockdiy.BlockDiy;
import modist.blockdiy.common.tileentity.DiyBlockTileEntity;
import modist.blockdiy.util.TERHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelDataManager;

@OnlyIn(Dist.CLIENT)
public class DiyBlockTileEntityRenderer extends TileEntityRenderer<DiyBlockTileEntity> {

	public DiyBlockTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public boolean isGlobalRenderer(DiyBlockTileEntity te) {
		return true;
	}

	/*TER��Ⱦ
	 *client
	 *Pre������׼����ȾTER��ÿһ֡��
	 *Post������Ⱦ���ݷ���IRenderTypeBuffer׼����Ⱦ��ÿһ֡��
	 */
	@Override
	public void render(DiyBlockTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn,
			IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		if(tileEntityIn.checkRenderPos()) { //�������ģ��
		World world = tileEntityIn.getWorld(); //��TileEntity�л�ȡWorldʵ��
		BlockPos posmin = tileEntityIn.getRenderPos(true);
		BlockPos posmax = tileEntityIn.getRenderPos(false); //��ȡ��ģ������
		float sx = (float) 1 / (posmax.getX() - posmin.getX() + 1);
		float sy = (float) 1 / (posmax.getY() - posmin.getY() + 1);
		float sz = (float) 1 / (posmax.getZ() - posmin.getZ() + 1); //����Ĭ�����ű���
		matrixStackIn.push(); //������matrix��ͼ�α任���󣩸���һ��ѹ�룬�洢�����޸Ķ����matrix
		matrixStackIn.getLast().getMatrix().mul(tileEntityIn.getMatrix().toMatrix4f()); //�ȳ�����ģ�ͱ任ģ��
		matrixStackIn.scale(sx, sy, sz); //�ٽ���Ĭ������
		RenderType.getBlockRenderTypes().forEach(type->{ //ע��IRenderTypeBuffer�ж���ÿ��RenderType����һ��IVertexBuilder�������RenderType
		IVertexBuilder buffer = bufferIn.getBuffer(type); //��RenderType��IRenderTypeBuffer�л�ȡIVertexBuilder
		tileEntityIn.fluiddata.forEach((pos, data)->{ //����fluid��ģ��
		IFluidState state = data.state;
		matrixStackIn.translate(-posmin.getX(), -posmin.getY(), -posmin.getZ()); 
		//����ƽ�ƣ�������ֵΪʵ�����ã�
		if(RenderTypeLookup.canRenderInLayer(state, type)) { //����÷�ģ��������RenderType����RenderTypeLookup��ע�ᣩ
		TERHelper.getInstance().renderFluid(tileEntityIn.getPos(), matrixStackIn, pos, world, buffer, state, data.height, data.vec); //��Ⱦfluid
		}
		matrixStackIn.translate(posmin.getX(), posmin.getY(), posmin.getZ()); //ƽ�ƻ���
		});	
		
		
		/*BlockState state1 = world.getBlockState(new BlockPos(0,5,0));
		BlockPos pos1 = new BlockPos(0,5,0);
		matrixStackIn.translate(pos1.getX()-posmin.getX(), pos1.getY()-posmin.getY(), pos1.getZ()-posmin.getZ());
		if(RenderTypeLookup.canRenderInLayer(state1, type)) {
		TERHelper.getInstance().renderModelFlat(tileEntityIn.getBlockState(), tileEntityIn.getPos(), combinedLightIn, world, 
				Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state1), 
				state1, pos1, matrixStackIn, bufferIn.getBuffer(type), false, 
				rand, state1.getPositionRandom(pos1), combinedOverlayIn, ModelDataManager.getModelData(world, pos1));
		}
		matrixStackIn.translate(posmin.getX()-pos1.getX(), posmin.getY()-pos1.getY(), posmin.getZ()-pos1.getZ());
		
		
		
		BlockState state2 = world.getBlockState(new BlockPos(0,4,0));
		BlockPos pos2 = new BlockPos(0,4,0);
		matrixStackIn.translate(pos2.getX()-posmin.getX(), pos2.getY()-posmin.getY(), pos2.getZ()-posmin.getZ());
		if(RenderTypeLookup.canRenderInLayer(state2, type)) {
		TERHelper.getInstance().renderModelFlat(tileEntityIn.getBlockState(), tileEntityIn.getPos(), combinedLightIn, world, 
				Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state2), 
				state2, pos2, matrixStackIn, bufferIn.getBuffer(type), false, 
				rand, state2.getPositionRandom(pos2), combinedOverlayIn, ModelDataManager.getModelData(world, pos2));
		}
		matrixStackIn.translate(posmin.getX()-pos2.getX(), posmin.getY()-pos2.getY(), posmin.getZ()-pos2.getZ());*/
		//TODO
		//1.��Ӳ�취��piston�����
		//2.��blockevent hack��
		//3.����update���룬ֻ��һ����
		//4.����client�����ط���
		
		
		TERHelper.enableCache();
		Random rand = new Random();
		tileEntityIn.translucentdata.forEach((pos, data)->{ 
		BlockState state = data.state;
		matrixStackIn.translate(pos.getX()-posmin.getX(), pos.getY()-posmin.getY(), pos.getZ()-posmin.getZ());
		if(RenderTypeLookup.canRenderInLayer(state, type)) {
		TERHelper.getInstance().renderModelFlat(tileEntityIn.getBlockState(), tileEntityIn.getPos(), combinedLightIn, world, 
				Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state), 
				state, pos, matrixStackIn, bufferIn.getBuffer(type), false, 
				rand, state.getPositionRandom(pos), combinedOverlayIn, ModelDataManager.getModelData(world, pos));
		}
		matrixStackIn.translate(posmin.getX()-pos.getX(), posmin.getY()-pos.getY(), posmin.getZ()-pos.getZ());
		});		
		TERHelper.disableCache();		
		});		
		
		
		/*if(world.getTileEntity(new BlockPos(0,4,0))!=null) {
			TileEntity tile = world.getTileEntity(new BlockPos(0,4,0));
			BlockPos pos = tile.getPos();
			matrixStackIn.translate(pos.getX()-posmin.getX(), pos.getY()-posmin.getY(), pos.getZ()-posmin.getZ());
			TERHelper.getInstance().renderTER(tile, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
			matrixStackIn.translate(posmin.getX()-pos.getX(), posmin.getY()-pos.getY(), posmin.getZ()-pos.getZ());
		}
		
		if(world.getTileEntity(new BlockPos(0,5,0))!=null) {
			TileEntity tile = world.getTileEntity(new BlockPos(0,5,0));
			BlockPos pos = tile.getPos();
			matrixStackIn.translate(pos.getX()-posmin.getX(), pos.getY()-posmin.getY(), pos.getZ()-posmin.getZ());
			TERHelper.getInstance().renderTER(tile, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
			matrixStackIn.translate(posmin.getX()-pos.getX(), posmin.getY()-pos.getY(), posmin.getZ()-pos.getZ());
		}*/
		
		
		tileEntityIn.terdata.forEach((pos, data)->{
		TileEntity tile = data.te;
		matrixStackIn.translate(pos.getX()-posmin.getX(), pos.getY()-posmin.getY(), pos.getZ()-posmin.getZ());
		TERHelper.getInstance().renderTER(tile, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		matrixStackIn.translate(posmin.getX()-pos.getX(), posmin.getY()-pos.getY(), posmin.getZ()-pos.getZ());
		});
		matrixStackIn.pop();
		}
	}
}
