package modist.blockdiy.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class QuadHelper {

	//根据提供的matrix对quad进行变换
	public static BakedQuad generateQuad(BakedQuad quad, Matrix4fHelper matrix, BlockState state,
			World world, BlockPos pos) {
		int[] data = quad.getVertexData().clone(); //克隆quad中的数据
		for (int i = 0; i < 4; i++) { //遍历quad中的四个x，y，z
			float x = Float.intBitsToFloat(data[8 * i]);
			float y = Float.intBitsToFloat(data[8 * i + 1]);
			float z = Float.intBitsToFloat(data[8 * i + 2]); //quad中的x，y，z数据
			Vector4f vec = new Vector4f(x, y, z, 1); //生成位置向量（包含第四维度便于计算）
			matrix.transform(vec); //根据matrix对vec进行变换（矩阵乘法）		
			data[8 * i] = Float.floatToIntBits(vec.getX());
			data[8 * i + 1] = Float.floatToIntBits(vec.getY());
			data[8 * i + 2] = Float.floatToIntBits(vec.getZ()); //quad数据重新写入
		}
		int	index = -1; //若分模型quad不染色，则tintindex为-1，渲染时直接不染色
		if(quad.hasTintIndex()) {
		index = Minecraft.getInstance().getBlockColors().getColor(state, world, pos, quad.getTintIndex()); 
		//若分模型quad染色，此tintindex为color，此时渲染时调用注册的IBlockColor，又对应到color上去
		}
		return new BakedQuad(data, index, quad.getFace(), quad.func_187508_a(), true);
	}

	public static List<BakedQuad> generateQuads(List<BakedQuad> quads, Matrix4fHelper matrix,
			BlockState state, World world, BlockPos pos) {
		List<BakedQuad> quads1 = new ArrayList<BakedQuad>();
		quads.forEach(quad -> quads1.add(generateQuad(quad, matrix, state, world, pos)));
		return quads1;
	}
}
