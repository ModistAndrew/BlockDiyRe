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

	/*TER渲染
	 *client
	 *Pre：世界准备渲染TER（每一帧）
	 *Post：将渲染数据放入IRenderTypeBuffer准备渲染（每一帧）
	 */
	@Override
	public void render(DiyBlockTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn,
			IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		if(tileEntityIn.checkRenderPos()) { //如果有总模型
		World world = tileEntityIn.getWorld(); //从TileEntity中获取World实例
		BlockPos posmin = tileEntityIn.getRenderPos(true);
		BlockPos posmax = tileEntityIn.getRenderPos(false); //获取总模型坐标
		float sx = (float) 1 / (posmax.getX() - posmin.getX() + 1);
		float sy = (float) 1 / (posmax.getY() - posmin.getY() + 1);
		float sz = (float) 1 / (posmax.getZ() - posmin.getZ() + 1); //计算默认缩放比例
		matrixStackIn.push(); //将现有matrix（图形变换矩阵）复制一份压入，存储，仅修改顶层的matrix
		matrixStackIn.getLast().getMatrix().mul(tileEntityIn.getMatrix().toMatrix4f()); //先乘以总模型变换模型
		matrixStackIn.scale(sx, sy, sz); //再进行默认缩放
		RenderType.getBlockRenderTypes().forEach(type->{ //注意IRenderTypeBuffer中对于每个RenderType都有一个IVertexBuilder，需遍历RenderType
		IVertexBuilder buffer = bufferIn.getBuffer(type); //由RenderType从IRenderTypeBuffer中获取IVertexBuilder
		tileEntityIn.fluiddata.forEach((pos, data)->{ //遍历fluid分模型
		IFluidState state = data.state;
		matrixStackIn.translate(-posmin.getX(), -posmin.getY(), -posmin.getZ()); 
		//进行平移（具体数值为实验所得）
		if(RenderTypeLookup.canRenderInLayer(state, type)) { //如果该分模型有这种RenderType（在RenderTypeLookup中注册）
		TERHelper.getInstance().renderFluid(tileEntityIn.getPos(), matrixStackIn, pos, world, buffer, state, data.height, data.vec); //渲染fluid
		}
		matrixStackIn.translate(posmin.getX(), posmin.getY(), posmin.getZ()); //平移回来
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
		//1.用硬办法把piston解决了
		//2.把blockevent hack掉
		//3.改善update代码，只变一部分
		//4.设置client常加载方法
		
		
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
