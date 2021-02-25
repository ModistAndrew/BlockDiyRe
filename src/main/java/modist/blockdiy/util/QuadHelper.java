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

	//�����ṩ��matrix��quad���б任
	public static BakedQuad generateQuad(BakedQuad quad, Matrix4fHelper matrix, BlockState state,
			World world, BlockPos pos) {
		int[] data = quad.getVertexData().clone(); //��¡quad�е�����
		for (int i = 0; i < 4; i++) { //����quad�е��ĸ�x��y��z
			float x = Float.intBitsToFloat(data[8 * i]);
			float y = Float.intBitsToFloat(data[8 * i + 1]);
			float z = Float.intBitsToFloat(data[8 * i + 2]); //quad�е�x��y��z����
			Vector4f vec = new Vector4f(x, y, z, 1); //����λ����������������ά�ȱ��ڼ��㣩
			matrix.transform(vec); //����matrix��vec���б任������˷���		
			data[8 * i] = Float.floatToIntBits(vec.getX());
			data[8 * i + 1] = Float.floatToIntBits(vec.getY());
			data[8 * i + 2] = Float.floatToIntBits(vec.getZ()); //quad��������д��
		}
		int	index = -1; //����ģ��quad��Ⱦɫ����tintindexΪ-1����Ⱦʱֱ�Ӳ�Ⱦɫ
		if(quad.hasTintIndex()) {
		index = Minecraft.getInstance().getBlockColors().getColor(state, world, pos, quad.getTintIndex()); 
		//����ģ��quadȾɫ����tintindexΪcolor����ʱ��Ⱦʱ����ע���IBlockColor���ֶ�Ӧ��color��ȥ
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
