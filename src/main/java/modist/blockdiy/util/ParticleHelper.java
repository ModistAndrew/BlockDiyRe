package modist.blockdiy.util;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleHelper {

	public static void addBlockHitEffects(BlockPos pos, Direction side, World world) {
		Random random = new Random();
		BlockState blockstate = world.getBlockState(pos);
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		AxisAlignedBB axisalignedbb = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
		double d0 = (double) i + random.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - (double) 0.2F)
				+ (double) 0.1F + axisalignedbb.minX;
		double d1 = (double) j + random.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - (double) 0.2F)
				+ (double) 0.1F + axisalignedbb.minY;
		double d2 = (double) k + random.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - (double) 0.2F)
				+ (double) 0.1F + axisalignedbb.minZ;
		if (side == Direction.DOWN) {
			d1 = (double) j + axisalignedbb.minY - (double) 0.1F;
		}

		if (side == Direction.UP) {
			d1 = (double) j + axisalignedbb.maxY + (double) 0.1F;
		}

		if (side == Direction.NORTH) {
			d2 = (double) k + axisalignedbb.minZ - (double) 0.1F;
		}

		if (side == Direction.SOUTH) {
			d2 = (double) k + axisalignedbb.maxZ + (double) 0.1F;
		}

		if (side == Direction.WEST) {
			d0 = (double) i + axisalignedbb.minX - (double) 0.1F;
		}

		if (side == Direction.EAST) {
			d0 = (double) i + axisalignedbb.maxX + (double) 0.1F;
		}

		Minecraft.getInstance().particles
				.addEffect((new DiggingParticle(world, d0, d1, d2, 0.0D, 0.0D, 0.0D, blockstate)).setBlockPos(pos)
						.multiplyVelocity(0.2F).multiplyParticleScaleBy(0.6F));
	}
	
	 public static void addBlockHitEffects(BlockPos pos, net.minecraft.util.math.BlockRayTraceResult target, World world) {
	      BlockState state = world.getBlockState(pos);
	      if (!state.addHitEffects(world, target, Minecraft.getInstance().particles))
	         addBlockHitEffects(pos, target.getFace(), world);
	 }
	
	public static void addBlockDestroyEffects(BlockPos pos, BlockState state, World world) {
	         VoxelShape voxelshape = VoxelShapes.fullCube();
	         voxelshape.forEachBox((p_228348_3_, p_228348_5_, p_228348_7_, p_228348_9_, p_228348_11_, p_228348_13_) -> {
	            double d1 = Math.min(1.0D, p_228348_9_ - p_228348_3_);
	            double d2 = Math.min(1.0D, p_228348_11_ - p_228348_5_);
	            double d3 = Math.min(1.0D, p_228348_13_ - p_228348_7_);
	            int i = Math.max(2, MathHelper.ceil(d1 / 0.25D));
	            int j = Math.max(2, MathHelper.ceil(d2 / 0.25D));
	            int k = Math.max(2, MathHelper.ceil(d3 / 0.25D));
	            for(int l = 0; l < i; ++l) {
	               for(int i1 = 0; i1 < j; ++i1) {
	                  for(int j1 = 0; j1 < k; ++j1) {
	                     double d4 = ((double)l + 0.5D) / (double)i;
	                     double d5 = ((double)i1 + 0.5D) / (double)j;
	                     double d6 = ((double)j1 + 0.5D) / (double)k;
	                     double d7 = d4 * d1 + p_228348_3_;
	                     double d8 = d5 * d2 + p_228348_5_;
	                     double d9 = d6 * d3 + p_228348_7_;
	                     Minecraft.getInstance().particles.addEffect((new DiggingParticle(world, (double)pos.getX() + d7, (double)pos.getY() + d8, (double)pos.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, state)).setBlockPos(pos));
	                  }
	               }
	            }

	         });
	   }

}
