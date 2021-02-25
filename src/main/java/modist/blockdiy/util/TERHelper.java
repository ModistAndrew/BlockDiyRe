package modist.blockdiy.util;

import java.util.BitSet;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import modist.blockdiy.BlockDiy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ILightReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TERHelper {

	private static final TERHelper INSTANCE = new TERHelper(Minecraft.getInstance().getBlockColors());
	private TextureAtlasSprite atlasSpriteWaterOverlay;
	private final BlockColors blockcolors;
	private static final ThreadLocal<TERHelper.Cache> CACHE_COMBINED_LIGHT = ThreadLocal.withInitial(() -> {
		return new TERHelper.Cache();
	});

	public TERHelper(BlockColors blockColorsIn) {
		this.blockcolors = blockColorsIn;
		this.atlasSpriteWaterOverlay = ModelBakery.LOCATION_WATER_OVERLAY.getSprite();
	}

	public static TERHelper getInstance() {
		return INSTANCE;
	}

	public void renderTER(TileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn,
			IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		if (tileEntityIn != null && TileEntityRendererDispatcher.instance.getRenderer(tileEntityIn) != null) {
			// BlockDiy.LOGGER.info("sadewf" +
			// TileEntityRendererDispatcher.instance.getRenderer(tileEntityIn));
			TileEntityRendererDispatcher.instance.getRenderer(tileEntityIn).render(tileEntityIn, partialTicks,
					matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		}
	}

	public void renderFluid(BlockPos lightPosIn, MatrixStack matrixStackIn, BlockPos posIn, ILightReader lightReaderIn,
			IVertexBuilder vertexBuilderIn, IFluidState fluidStateIn, Vector4f height, Vec3d vec3d) {
		/*boolean flag = fluidStateIn.isTagged(FluidTags.LAVA);
	      TextureAtlasSprite[] atextureatlassprite = net.minecraftforge.client.ForgeHooksClient.getFluidSprites(lightReaderIn, posIn, fluidStateIn);
	      int i = fluidStateIn.getFluid().getAttributes().getColor(lightReaderIn, posIn);
	      float alpha = (float)(i >> 24 & 255) / 255.0F;
	      float f = (float)(i >> 16 & 255) / 255.0F;
	      float f1 = (float)(i >> 8 & 255) / 255.0F;
	      float f2 = (float)(i & 255) / 255.0F;
	      boolean flag1 = !isAdjacentFluidSameAs(lightReaderIn, posIn, Direction.UP, fluidStateIn);
	      boolean flag2 = !isAdjacentFluidSameAs(lightReaderIn, posIn, Direction.DOWN, fluidStateIn) && !isNeighbourSideCovered(lightReaderIn, posIn, Direction.DOWN, 0.8888889F);
	      boolean flag3 = !isAdjacentFluidSameAs(lightReaderIn, posIn, Direction.NORTH, fluidStateIn);
	      boolean flag4 = !isAdjacentFluidSameAs(lightReaderIn, posIn, Direction.SOUTH, fluidStateIn);
	      boolean flag5 = !isAdjacentFluidSameAs(lightReaderIn, posIn, Direction.WEST, fluidStateIn);
	      boolean flag6 = !isAdjacentFluidSameAs(lightReaderIn, posIn, Direction.EAST, fluidStateIn);
	      if (!flag1 && !flag2 && !flag6 && !flag5 && !flag3 && !flag4) {
	         return false;
	      } else {
	         boolean flag7 = false;
	         float f3 = 0.5F;
	         float f4 = 1.0F;
	         float f5 = 0.8F;
	         float f6 = 0.6F;
	         float f7 = this.getFluidHeight(lightReaderIn, posIn, fluidStateIn.getFluid());
	         float f8 = this.getFluidHeight(lightReaderIn, posIn.south(), fluidStateIn.getFluid());
	         float f9 = this.getFluidHeight(lightReaderIn, posIn.east().south(), fluidStateIn.getFluid());
	         float f10 = this.getFluidHeight(lightReaderIn, posIn.east(), fluidStateIn.getFluid());
	         double d0 = (double)(posIn.getX() & 15);
	         double d1 = (double)(posIn.getY() & 15);
	         double d2 = (double)(posIn.getZ() & 15);
	         float f11 = 0.001F;
	         float f12 = flag2 ? 0.001F : 0.0F;
	         if (flag1 && !isNeighbourSideCovered(lightReaderIn, posIn, Direction.UP, Math.min(Math.min(f7, f8), Math.min(f9, f10)))) {
	            flag7 = true;
	            f7 -= 0.001F;
	            f8 -= 0.001F;
	            f9 -= 0.001F;
	            f10 -= 0.001F;
	            Vec3d vec3d = fluidStateIn.getFlow(lightReaderIn, posIn);
	            float f13;
	            float f14;
	            float f15;
	            float f16;
	            float f17;
	            float f18;
	            float f19;
	            float f20;
	            if (vec3d.x == 0.0D && vec3d.z == 0.0D) {
	               TextureAtlasSprite textureatlassprite1 = atextureatlassprite[0];
	               f13 = textureatlassprite1.getInterpolatedU(0.0D);
	               f17 = textureatlassprite1.getInterpolatedV(0.0D);
	               f14 = f13;
	               f18 = textureatlassprite1.getInterpolatedV(16.0D);
	               f15 = textureatlassprite1.getInterpolatedU(16.0D);
	               f19 = f18;
	               f16 = f15;
	               f20 = f17;
	            } else {
	               TextureAtlasSprite textureatlassprite = atextureatlassprite[1];
	               float f21 = (float)MathHelper.atan2(vec3d.z, vec3d.x) - ((float)Math.PI / 2F);
	               float f22 = MathHelper.sin(f21) * 0.25F;
	               float f23 = MathHelper.cos(f21) * 0.25F;
	               float f24 = 8.0F;
	               f13 = textureatlassprite.getInterpolatedU((double)(8.0F + (-f23 - f22) * 16.0F));
	               f17 = textureatlassprite.getInterpolatedV((double)(8.0F + (-f23 + f22) * 16.0F));
	               f14 = textureatlassprite.getInterpolatedU((double)(8.0F + (-f23 + f22) * 16.0F));
	               f18 = textureatlassprite.getInterpolatedV((double)(8.0F + (f23 + f22) * 16.0F));
	               f15 = textureatlassprite.getInterpolatedU((double)(8.0F + (f23 + f22) * 16.0F));
	               f19 = textureatlassprite.getInterpolatedV((double)(8.0F + (f23 - f22) * 16.0F));
	               f16 = textureatlassprite.getInterpolatedU((double)(8.0F + (f23 - f22) * 16.0F));
	               f20 = textureatlassprite.getInterpolatedV((double)(8.0F + (-f23 - f22) * 16.0F));
	            }

	            float f43 = (f13 + f14 + f15 + f16) / 4.0F;
	            float f44 = (f17 + f18 + f19 + f20) / 4.0F;
	            float f45 = (float)atextureatlassprite[0].getWidth() / (atextureatlassprite[0].getMaxU() - atextureatlassprite[0].getMinU());
	            float f46 = (float)atextureatlassprite[0].getHeight() / (atextureatlassprite[0].getMaxV() - atextureatlassprite[0].getMinV());
	            float f47 = 4.0F / Math.max(f46, f45);
	            f13 = MathHelper.lerp(f47, f13, f43);
	            f14 = MathHelper.lerp(f47, f14, f43);
	            f15 = MathHelper.lerp(f47, f15, f43);
	            f16 = MathHelper.lerp(f47, f16, f43);
	            f17 = MathHelper.lerp(f47, f17, f44);
	            f18 = MathHelper.lerp(f47, f18, f44);
	            f19 = MathHelper.lerp(f47, f19, f44);
	            f20 = MathHelper.lerp(f47, f20, f44);
	            int j = this.getCombinedAverageLight(lightReaderIn, posIn);
	            float f25 = 1.0F * f;
	            float f26 = 1.0F * f1;
	            float f27 = 1.0F * f2;
	            this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 0.0D, d1 + (double)f7, d2 + 0.0D, f25, f26, f27, alpha, f13, f17, j);
	            this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 0.0D, d1 + (double)f8, d2 + 1.0D, f25, f26, f27, alpha, f14, f18, j);
	            this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 1.0D, d1 + (double)f9, d2 + 1.0D, f25, f26, f27, alpha, f15, f19, j);
	            this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 1.0D, d1 + (double)f10, d2 + 0.0D, f25, f26, f27, alpha, f16, f20, j);
	            if (fluidStateIn.shouldRenderSides(lightReaderIn, posIn.up())) {
	               this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 0.0D, d1 + (double)f7, d2 + 0.0D, f25, f26, f27, alpha, f13, f17, j);
	               this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 1.0D, d1 + (double)f10, d2 + 0.0D, f25, f26, f27, alpha, f16, f20, j);
	               this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 1.0D, d1 + (double)f9, d2 + 1.0D, f25, f26, f27, alpha, f15, f19, j);
	               this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 0.0D, d1 + (double)f8, d2 + 1.0D, f25, f26, f27, alpha, f14, f18, j);
	            }
	         }

	         if (flag2) {
	            float f34 = atextureatlassprite[0].getMinU();
	            float f35 = atextureatlassprite[0].getMaxU();
	            float f37 = atextureatlassprite[0].getMinV();
	            float f39 = atextureatlassprite[0].getMaxV();
	            int i1 = this.getCombinedAverageLight(lightReaderIn, posIn.down());
	            float f40 = 0.5F * f;
	            float f41 = 0.5F * f1;
	            float f42 = 0.5F * f2;
	            this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0, d1 + (double)f12, d2 + 1.0D, f40, f41, f42, alpha, f34, f39, i1);
	            this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0, d1 + (double)f12, d2, f40, f41, f42, alpha, f34, f37, i1);
	            this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 1.0D, d1 + (double)f12, d2, f40, f41, f42, alpha, f35, f37, i1);
	            this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 1.0D, d1 + (double)f12, d2 + 1.0D, f40, f41, f42, alpha, f35, f39, i1);
	            flag7 = true;
	         }

	         for(int l = 0; l < 4; ++l) {
	            float f36;
	            float f38;
	            double d3;
	            double d4;
	            double d5;
	            double d6;
	            Direction direction;
	            boolean flag8;
	            if (l == 0) {
	               f36 = f7;
	               f38 = f10;
	               d3 = d0;
	               d5 = d0 + 1.0D;
	               d4 = d2 + (double)0.001F;
	               d6 = d2 + (double)0.001F;
	               direction = Direction.NORTH;
	               flag8 = flag3;
	            } else if (l == 1) {
	               f36 = f9;
	               f38 = f8;
	               d3 = d0 + 1.0D;
	               d5 = d0;
	               d4 = d2 + 1.0D - (double)0.001F;
	               d6 = d2 + 1.0D - (double)0.001F;
	               direction = Direction.SOUTH;
	               flag8 = flag4;
	            } else if (l == 2) {
	               f36 = f8;
	               f38 = f7;
	               d3 = d0 + (double)0.001F;
	               d5 = d0 + (double)0.001F;
	               d4 = d2 + 1.0D;
	               d6 = d2;
	               direction = Direction.WEST;
	               flag8 = flag5;
	            } else {
	               f36 = f10;
	               f38 = f9;
	               d3 = d0 + 1.0D - (double)0.001F;
	               d5 = d0 + 1.0D - (double)0.001F;
	               d4 = d2;
	               d6 = d2 + 1.0D;
	               direction = Direction.EAST;
	               flag8 = flag6;
	            }

	            if (flag8 && !isNeighbourSideCovered(lightReaderIn, posIn, direction, Math.max(f36, f38))) {
	               flag7 = true;
	               BlockPos blockpos = posIn.offset(direction);
	               TextureAtlasSprite textureatlassprite2 = atextureatlassprite[1];
	               if (!flag) {
	                  Block block = lightReaderIn.getBlockState(blockpos).getBlock();
	                  if (block == Blocks.GLASS || block instanceof StainedGlassBlock) {
	                     textureatlassprite2 = this.atlasSpriteWaterOverlay;
	                  }
	               }

	               float f48 = textureatlassprite2.getInterpolatedU(0.0D);
	               float f49 = textureatlassprite2.getInterpolatedU(8.0D);
	               float f50 = textureatlassprite2.getInterpolatedV((double)((1.0F - f36) * 16.0F * 0.5F));
	               float f28 = textureatlassprite2.getInterpolatedV((double)((1.0F - f38) * 16.0F * 0.5F));
	               float f29 = textureatlassprite2.getInterpolatedV(8.0D);
	               int k = this.getCombinedAverageLight(lightReaderIn, blockpos);
	               float f30 = l < 2 ? 0.8F : 0.6F;
	               float f31 = 1.0F * f30 * f;
	               float f32 = 1.0F * f30 * f1;
	               float f33 = 1.0F * f30 * f2;
	               this.vertexVanilla(matrixStackIn, vertexBuilderIn, d3, d1 + (double)f36, d4, f31, f32, f33, alpha, f48, f50, k);
	               this.vertexVanilla(matrixStackIn, vertexBuilderIn, d5, d1 + (double)f38, d6, f31, f32, f33, alpha, f49, f28, k);
	               this.vertexVanilla(matrixStackIn, vertexBuilderIn, d5, d1 + (double)f12, d6, f31, f32, f33, alpha, f49, f29, k);
	               this.vertexVanilla(matrixStackIn, vertexBuilderIn, d3, d1 + (double)f12, d4, f31, f32, f33, alpha, f48, f29, k);
	               if (textureatlassprite2 != this.atlasSpriteWaterOverlay) {
	                  this.vertexVanilla(matrixStackIn, vertexBuilderIn, d3, d1 + (double)f12, d4, f31, f32, f33, alpha, f48, f29, k);
	                  this.vertexVanilla(matrixStackIn, vertexBuilderIn, d5, d1 + (double)f12, d6, f31, f32, f33, alpha, f49, f29, k);
	                  this.vertexVanilla(matrixStackIn, vertexBuilderIn, d5, d1 + (double)f38, d6, f31, f32, f33, alpha, f49, f28, k);
	                  this.vertexVanilla(matrixStackIn, vertexBuilderIn, d3, d1 + (double)f36, d4, f31, f32, f33, alpha, f48, f50, k);
	               }
	            }
	         }

	         return flag7;
	      }*/
		
		
		boolean flag = fluidStateIn.isTagged(FluidTags.LAVA);
		TextureAtlasSprite[] atextureatlassprite = net.minecraftforge.client.ForgeHooksClient
				.getFluidSprites(lightReaderIn, posIn, fluidStateIn);
		int i = fluidStateIn.getFluid().getAttributes().getColor(lightReaderIn, posIn);
		float alpha = (float) (i >> 24 & 255) / 255.0F;
		float f = (float) (i >> 16 & 255) / 255.0F;
		float f1 = (float) (i >> 8 & 255) / 255.0F;
		float f2 = (float) (i & 255) / 255.0F;
		boolean flag1 = !isAdjacentFluidSameAs(lightReaderIn, posIn, Direction.UP, fluidStateIn);
		boolean flag2 = !isAdjacentFluidSameAs(lightReaderIn, posIn, Direction.DOWN, fluidStateIn)
				&& !isNeighbourSideCovered(lightReaderIn, posIn, Direction.DOWN, 0.8888889F);
		boolean flag3 = !isAdjacentFluidSameAs(lightReaderIn, posIn, Direction.NORTH, fluidStateIn);
		boolean flag4 = !isAdjacentFluidSameAs(lightReaderIn, posIn, Direction.SOUTH, fluidStateIn);
		boolean flag5 = !isAdjacentFluidSameAs(lightReaderIn, posIn, Direction.WEST, fluidStateIn);
		boolean flag6 = !isAdjacentFluidSameAs(lightReaderIn, posIn, Direction.EAST, fluidStateIn);
		if (!flag1 && !flag2 && !flag6 && !flag5 && !flag3 && !flag4) {
			return;
		} else {
			float f7 = height.getX();
			float f8 = height.getY();
			float f9 = height.getZ();
			float f10 = height.getW();
			double d0 = (double) (posIn.getX() & 15);
			double d1 = (double) (posIn.getY() & 15);
			double d2 = (double) (posIn.getZ() & 15);
			float f12 = flag2 ? 0.001F : 0.0F;
			if (flag1 && !isNeighbourSideCovered(lightReaderIn, posIn, Direction.UP,
					Math.min(Math.min(f7, f8), Math.min(f9, f10)))) {
				f7 -= 0.001F;
				f8 -= 0.001F;
				f9 -= 0.001F;
				f10 -= 0.001F;
				float f13;
				float f14;
				float f15;
				float f16;
				float f17;
				float f18;
				float f19;
				float f20;
				if (vec3d.x == 0.0D && vec3d.z == 0.0D) {
					TextureAtlasSprite textureatlassprite1 = atextureatlassprite[0];
					f13 = textureatlassprite1.getInterpolatedU(0.0D);
					f17 = textureatlassprite1.getInterpolatedV(0.0D);
					f14 = f13;
					f18 = textureatlassprite1.getInterpolatedV(16.0D);
					f15 = textureatlassprite1.getInterpolatedU(16.0D);
					f19 = f18;
					f16 = f15;
					f20 = f17;
				} else {
					TextureAtlasSprite textureatlassprite = atextureatlassprite[1];
					float f21 = (float) MathHelper.atan2(vec3d.z, vec3d.x) - ((float) Math.PI / 2F);
					float f22 = MathHelper.sin(f21) * 0.25F;
					float f23 = MathHelper.cos(f21) * 0.25F;
					f13 = textureatlassprite.getInterpolatedU((double) (8.0F + (-f23 - f22) * 16.0F));
					f17 = textureatlassprite.getInterpolatedV((double) (8.0F + (-f23 + f22) * 16.0F));
					f14 = textureatlassprite.getInterpolatedU((double) (8.0F + (-f23 + f22) * 16.0F));
					f18 = textureatlassprite.getInterpolatedV((double) (8.0F + (f23 + f22) * 16.0F));
					f15 = textureatlassprite.getInterpolatedU((double) (8.0F + (f23 + f22) * 16.0F));
					f19 = textureatlassprite.getInterpolatedV((double) (8.0F + (f23 - f22) * 16.0F));
					f16 = textureatlassprite.getInterpolatedU((double) (8.0F + (f23 - f22) * 16.0F));
					f20 = textureatlassprite.getInterpolatedV((double) (8.0F + (-f23 - f22) * 16.0F));
				}

				float f43 = (f13 + f14 + f15 + f16) / 4.0F;
				float f44 = (f17 + f18 + f19 + f20) / 4.0F;
				float f45 = (float) atextureatlassprite[0].getWidth()
						/ (atextureatlassprite[0].getMaxU() - atextureatlassprite[0].getMinU());
				float f46 = (float) atextureatlassprite[0].getHeight()
						/ (atextureatlassprite[0].getMaxV() - atextureatlassprite[0].getMinV());
				float f47 = 4.0F / Math.max(f46, f45);
				f13 = MathHelper.lerp(f47, f13, f43);
				f14 = MathHelper.lerp(f47, f14, f43);
				f15 = MathHelper.lerp(f47, f15, f43);
				f16 = MathHelper.lerp(f47, f16, f43);
				f17 = MathHelper.lerp(f47, f17, f44);
				f18 = MathHelper.lerp(f47, f18, f44);
				f19 = MathHelper.lerp(f47, f19, f44);
				f20 = MathHelper.lerp(f47, f20, f44);
				int j = this.getCombinedAverageLight(lightReaderIn, lightPosIn);
				float f25 = 1.0F * f;
				float f26 = 1.0F * f1;
				float f27 = 1.0F * f2;
				this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 0.0D, d1 + (double) f7, d2 + 0.0D, f25, f26,
						f27, alpha, f13, f17, j);
				this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 0.0D, d1 + (double) f8, d2 + 1.0D, f25, f26,
						f27, alpha, f14, f18, j);
				this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 1.0D, d1 + (double) f9, d2 + 1.0D, f25, f26,
						f27, alpha, f15, f19, j);
				this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 1.0D, d1 + (double) f10, d2 + 0.0D, f25, f26,
						f27, alpha, f16, f20, j);
				if ( /*fluidStateIn.shouldRenderSides(lightReaderIn, posIn.up())*/ true) {
					this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 0.0D, d1 + (double) f7, d2 + 0.0D, f25, f26,
							f27, alpha, f13, f17, j);
					this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 1.0D, d1 + (double) f10, d2 + 0.0D, f25,
							f26, f27, alpha, f16, f20, j);
					this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 1.0D, d1 + (double) f9, d2 + 1.0D, f25, f26,
							f27, alpha, f15, f19, j);
					this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 0.0D, d1 + (double) f8, d2 + 1.0D, f25, f26,
							f27, alpha, f14, f18, j);
				}
			}

			if (flag2) {
				float f34 = atextureatlassprite[0].getMinU();
				float f35 = atextureatlassprite[0].getMaxU();
				float f37 = atextureatlassprite[0].getMinV();
				float f39 = atextureatlassprite[0].getMaxV();
				int i1 = this.getCombinedAverageLight(lightReaderIn, lightPosIn.down());
				float f40 = 0.5F * f;
				float f41 = 0.5F * f1;
				float f42 = 0.5F * f2;
				this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0, d1 + (double) f12, d2 + 1.0D, f40, f41, f42,
						alpha, f34, f39, i1);
				this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0, d1 + (double) f12, d2, f40, f41, f42, alpha, f34,
						f37, i1);
				this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 1.0D, d1 + (double) f12, d2, f40, f41, f42,
						alpha, f35, f37, i1);
				this.vertexVanilla(matrixStackIn, vertexBuilderIn, d0 + 1.0D, d1 + (double) f12, d2 + 1.0D, f40, f41,
						f42, alpha, f35, f39, i1);
			}

			for (int l = 0; l < 4; ++l) {
				float f36;
				float f38;
				double d3;
				double d4;
				double d5;
				double d6;
				Direction direction;
				boolean flag8;
				if (l == 0) {
					f36 = f7;
					f38 = f10;
					d3 = d0;
					d5 = d0 + 1.0D;
					d4 = d2 + (double) 0.001F;
					d6 = d2 + (double) 0.001F;
					direction = Direction.NORTH;
					flag8 = flag3;
				} else if (l == 1) {
					f36 = f9;
					f38 = f8;
					d3 = d0 + 1.0D;
					d5 = d0;
					d4 = d2 + 1.0D - (double) 0.001F;
					d6 = d2 + 1.0D - (double) 0.001F;
					direction = Direction.SOUTH;
					flag8 = flag4;
				} else if (l == 2) {
					f36 = f8;
					f38 = f7;
					d3 = d0 + (double) 0.001F;
					d5 = d0 + (double) 0.001F;
					d4 = d2 + 1.0D;
					d6 = d2;
					direction = Direction.WEST;
					flag8 = flag5;
				} else {
					f36 = f10;
					f38 = f9;
					d3 = d0 + 1.0D - (double) 0.001F;
					d5 = d0 + 1.0D - (double) 0.001F;
					d4 = d2;
					d6 = d2 + 1.0D;
					direction = Direction.EAST;
					flag8 = flag6;
				}

				if (flag8 && !isNeighbourSideCovered(lightReaderIn, posIn, direction, Math.max(f36, f38))) {
					BlockPos blockpos = posIn.offset(direction);
					TextureAtlasSprite textureatlassprite2 = atextureatlassprite[1];
					if (!flag) {
						Block block = lightReaderIn.getBlockState(blockpos).getBlock();
						if (/* block == Blocks.GLASS || block instanceof StainedGlassBlock*/ false) {
							textureatlassprite2 = this.atlasSpriteWaterOverlay;
						}
					}

					float f48 = textureatlassprite2.getInterpolatedU(0.0D);
					float f49 = textureatlassprite2.getInterpolatedU(8.0D);
					float f50 = textureatlassprite2.getInterpolatedV((double) ((1.0F - f36) * 16.0F * 0.5F));
					float f28 = textureatlassprite2.getInterpolatedV((double) ((1.0F - f38) * 16.0F * 0.5F));
					float f29 = textureatlassprite2.getInterpolatedV(8.0D);
					int k = this.getCombinedAverageLight(lightReaderIn, lightPosIn.offset(direction));
					float f30 = l < 2 ? 0.8F : 0.6F;
					float f31 = 1.0F * f30 * f;
					float f32 = 1.0F * f30 * f1;
					float f33 = 1.0F * f30 * f2;
					this.vertexVanilla(matrixStackIn, vertexBuilderIn, d3, d1 + (double) f36, d4, f31, f32, f33, alpha,
							f48, f50, k);
					this.vertexVanilla(matrixStackIn, vertexBuilderIn, d5, d1 + (double) f38, d6, f31, f32, f33, alpha,
							f49, f28, k);
					this.vertexVanilla(matrixStackIn, vertexBuilderIn, d5, d1 + (double) f12, d6, f31, f32, f33, alpha,
							f49, f29, k);
					this.vertexVanilla(matrixStackIn, vertexBuilderIn, d3, d1 + (double) f12, d4, f31, f32, f33, alpha,
							f48, f29, k);
					if (textureatlassprite2 != this.atlasSpriteWaterOverlay) {
						this.vertexVanilla(matrixStackIn, vertexBuilderIn, d3, d1 + (double) f12, d4, f31, f32, f33,
								alpha, f48, f29, k);
						this.vertexVanilla(matrixStackIn, vertexBuilderIn, d5, d1 + (double) f12, d6, f31, f32, f33,
								alpha, f49, f29, k);
						this.vertexVanilla(matrixStackIn, vertexBuilderIn, d5, d1 + (double) f38, d6, f31, f32, f33,
								alpha, f49, f28, k);
						this.vertexVanilla(matrixStackIn, vertexBuilderIn, d3, d1 + (double) f36, d4, f31, f32, f33,
								alpha, f48, f50, k);
					}
				}
			}
			return;
		}
		
	}

	private boolean isAdjacentFluidSameAs(IBlockReader worldIn, BlockPos pos, Direction side, IFluidState state) {
		/*
		 * BlockPos blockpos = pos.offset(side); IFluidState ifluidstate =
		 * worldIn.getFluidState(blockpos); return
		 * ifluidstate.getFluid().isEquivalentTo(state.getFluid());
		 */
		return false;
	}

	private boolean isNeighbourSideCovered(IBlockReader reader, BlockPos pos, Direction face, float heightIn) {
		/*
		 * BlockPos blockpos = pos.offset(face); BlockState blockstate =
		 * reader.getBlockState(blockpos); if (blockstate.isSolid()) { VoxelShape
		 * voxelshape = VoxelShapes.create(0.0D, 0.0D, 0.0D, 1.0D, heightIn, 1.0D);
		 * VoxelShape voxelshape1 = blockstate.getRenderShape(reader, blockpos); return
		 * VoxelShapes.isCubeSideCovered(voxelshape, voxelshape1, face); } else { return
		 * false; }
		 */
		return false;
	}

	private void vertexVanilla(MatrixStack matrixStackIn, IVertexBuilder vertexBuilderIn, double x, double y, double z,
			float red, float green, float blue, float alpha, float u, float v, int packedLight) {
		vertexBuilderIn.pos(matrixStackIn.getLast().getMatrix(), (float) x, (float) y, (float) z)
				.color(red, green, blue, alpha).tex(u, v).lightmap(packedLight).normal(0.0F, 1.0F, 0.0F).endVertex();
	}

	private int getCombinedAverageLight(ILightReader lightReaderIn, BlockPos posIn) {
		int i = WorldRenderer.getCombinedLight(lightReaderIn, posIn);
		int j = WorldRenderer.getCombinedLight(lightReaderIn, posIn.up());
		int k = i & 255;
		int l = j & 255;
		int i1 = i >> 16 & 255;
		int j1 = j >> 16 & 255;
		return (k > l ? k : l) | (i1 > j1 ? i1 : j1) << 16;
	}

	private float getFluidHeight(IBlockReader reader, BlockPos pos, Fluid fluidIn) {
		int i = 0;
		float f = 0.0F;

		for (int j = 0; j < 4; ++j) {
			BlockPos blockpos = pos.add(-(j & 1), 0, -(j >> 1 & 1));
			if (reader.getFluidState(blockpos.up()).getFluid().isEquivalentTo(fluidIn)) {
				return 1.0F;
			}

			IFluidState ifluidstate = reader.getFluidState(blockpos);
			if (ifluidstate.getFluid().isEquivalentTo(fluidIn)) {
				float f1 = ifluidstate.getActualHeight(reader, blockpos);
				if (f1 >= 0.8F) {
					f += f1 * 10.0F;
					i += 10;
				} else {
					f += f1;
					++i;
				}
			} else if (!reader.getBlockState(blockpos).getMaterial().isSolid()) {
				++i;
			}
		}

		return f / i;
	}

	public boolean renderModel(ILightReader worldIn, IBakedModel modelIn, BlockState stateIn, BlockPos posIn,
			MatrixStack matrixIn, IVertexBuilder buffer, boolean checkSides, Random randomIn, long rand,
			int combinedOverlayIn, net.minecraftforge.client.model.data.IModelData modelData) {
		boolean flag = Minecraft.isAmbientOcclusionEnabled() && stateIn.getLightValue(worldIn, posIn) == 0
				&& modelIn.isAmbientOcclusion();
		Vec3d vec3d = stateIn.getOffset(worldIn, posIn);
		matrixIn.translate(vec3d.x, vec3d.y, vec3d.z);
		modelData = modelIn.getModelData(worldIn, posIn, stateIn, modelData);

		try {
			return flag
					? this.renderModelSmooth(worldIn, modelIn, stateIn, posIn, matrixIn, buffer, checkSides, randomIn,
							rand, combinedOverlayIn, modelData)
					: this.renderModelFlat(null, null, 0, worldIn, modelIn, stateIn, posIn, matrixIn, buffer, checkSides, randomIn,
							rand, combinedOverlayIn, modelData);
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block model");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Block model being tesselated");
			CrashReportCategory.addBlockInfo(crashreportcategory, posIn, stateIn);
			crashreportcategory.addDetail("Using AO", flag);
			throw new ReportedException(crashreport);
		}
	}

	private boolean renderModelSmooth(ILightReader worldIn, IBakedModel modelIn, BlockState stateIn, BlockPos posIn,
			MatrixStack matrixStackIn, IVertexBuilder buffer, boolean checkSides, Random randomIn, long rand,
			int combinedOverlayIn, net.minecraftforge.client.model.data.IModelData modelData) {
		boolean flag = false;
		float[] afloat = new float[Direction.values().length * 2];
		BitSet bitset = new BitSet(3);
		TERHelper.AmbientOcclusionFace TERHelper$ambientocclusionface = new TERHelper.AmbientOcclusionFace();

		for (Direction direction : Direction.values()) {
			randomIn.setSeed(rand);
			List<BakedQuad> list = modelIn.getQuads(stateIn, direction, randomIn, modelData);
			if (!list.isEmpty() && (!checkSides || Block.shouldSideBeRendered(stateIn, worldIn, posIn, direction))) {
				this.renderQuadsSmooth(worldIn, stateIn, posIn, matrixStackIn, buffer, list, afloat, bitset,
						TERHelper$ambientocclusionface, combinedOverlayIn);
				flag = true;
			}
		}

		randomIn.setSeed(rand);
		List<BakedQuad> list1 = modelIn.getQuads(stateIn, (Direction) null, randomIn, modelData);
		if (!list1.isEmpty()) {
			this.renderQuadsSmooth(worldIn, stateIn, posIn, matrixStackIn, buffer, list1, afloat, bitset,
					TERHelper$ambientocclusionface, combinedOverlayIn);
			flag = true;
		}

		return flag;
	}

	public boolean renderModelFlat(BlockState lightStateIn, BlockPos lightPosIn, int light, ILightReader worldIn,
			IBakedModel modelIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixStackIn, IVertexBuilder buffer,
			boolean checkSides, Random randomIn, long rand, int combinedOverlayIn,
			net.minecraftforge.client.model.data.IModelData modelData) {
		boolean flag = false;
		BitSet bitset = new BitSet(3);
		int i = WorldRenderer.getPackedLightmapCoords(worldIn, lightStateIn, lightPosIn);// .offset(direction));
		
		for (Direction direction : Direction.values()) {
			randomIn.setSeed(rand);
			List<BakedQuad> list = modelIn.getQuads(stateIn, direction, randomIn, modelData);
			if (!list.isEmpty() && (!checkSides || Block.shouldSideBeRendered(stateIn, worldIn, posIn, direction))) {	
				this.renderQuadsFlat(worldIn, stateIn, posIn, i, combinedOverlayIn, false, matrixStackIn, buffer, list,
						bitset);
				flag = true;
			}
		}

		randomIn.setSeed(rand);
		List<BakedQuad> list1 = modelIn.getQuads(stateIn, (Direction) null, randomIn, modelData);
		if (!list1.isEmpty()) {
			//this.renderQuadsFlat(worldIn, stateIn, posIn, -1, combinedOverlayIn, true, matrixStackIn, buffer, list1, bitset);
			this.renderQuadsFlat(worldIn, stateIn, posIn, i, combinedOverlayIn, false, matrixStackIn, buffer, list1, bitset);
			flag = true;
		}

		return flag;
	}

	private void renderQuadsSmooth(ILightReader blockAccessIn, BlockState stateIn, BlockPos posIn,
			MatrixStack matrixStackIn, IVertexBuilder buffer, List<BakedQuad> list, float[] quadBounds, BitSet bitSet,
			TERHelper.AmbientOcclusionFace aoFace, int combinedOverlayIn) {
		for (BakedQuad bakedquad : list) {
			this.fillQuadBounds(blockAccessIn, stateIn, posIn, bakedquad.getVertexData(), bakedquad.getFace(),
					quadBounds, bitSet);
			aoFace.updateVertexBrightness(blockAccessIn, stateIn, posIn, bakedquad.getFace(), quadBounds, bitSet);
			this.renderQuadSmooth(blockAccessIn, stateIn, posIn, buffer, matrixStackIn.getLast(), bakedquad,
					aoFace.vertexColorMultiplier[0], aoFace.vertexColorMultiplier[1], aoFace.vertexColorMultiplier[2],
					aoFace.vertexColorMultiplier[3], aoFace.vertexBrightness[0], aoFace.vertexBrightness[1],
					aoFace.vertexBrightness[2], aoFace.vertexBrightness[3], combinedOverlayIn);
		}

	}

	private void renderQuadSmooth(ILightReader blockAccessIn, BlockState stateIn, BlockPos posIn, IVertexBuilder buffer,
			MatrixStack.Entry matrixEntry, BakedQuad quadIn, float colorMul0, float colorMul1, float colorMul2,
			float colorMul3, int brightness0, int brightness1, int brightness2, int brightness3,
			int combinedOverlayIn) {
		float f;
		float f1;
		float f2;
		if (quadIn.hasTintIndex()) {
			int i = blockcolors.getColor(stateIn, blockAccessIn, posIn, quadIn.getTintIndex());
			f = (float) (i >> 16 & 255) / 255.0F;
			f1 = (float) (i >> 8 & 255) / 255.0F;
			f2 = (float) (i & 255) / 255.0F;
		} else {
			f = 1.0F;
			f1 = 1.0F;
			f2 = 1.0F;
		}
		if (quadIn.shouldApplyDiffuseLighting()) {
			float l = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(quadIn.getFace());
			f *= l;
			f1 *= l;
			f2 *= l;
		}

		buffer.addQuad(matrixEntry, quadIn, new float[] { colorMul0, colorMul1, colorMul2, colorMul3 }, f, f1, f2,
				new int[] { brightness0, brightness1, brightness2, brightness3 }, combinedOverlayIn, true);
	}

	private void fillQuadBounds(ILightReader blockReaderIn, BlockState stateIn, BlockPos posIn, int[] vertexData,
			Direction face, @Nullable float[] quadBounds, BitSet boundsFlags) {
		float f = 32.0F;
		float f1 = 32.0F;
		float f2 = 32.0F;
		float f3 = -32.0F;
		float f4 = -32.0F;
		float f5 = -32.0F;

		for (int i = 0; i < 4; ++i) {
			float f6 = Float.intBitsToFloat(vertexData[i * 8]);
			float f7 = Float.intBitsToFloat(vertexData[i * 8 + 1]);
			float f8 = Float.intBitsToFloat(vertexData[i * 8 + 2]);
			f = Math.min(f, f6);
			f1 = Math.min(f1, f7);
			f2 = Math.min(f2, f8);
			f3 = Math.max(f3, f6);
			f4 = Math.max(f4, f7);
			f5 = Math.max(f5, f8);
		}

		if (quadBounds != null) {
			quadBounds[Direction.WEST.getIndex()] = f;
			quadBounds[Direction.EAST.getIndex()] = f3;
			quadBounds[Direction.DOWN.getIndex()] = f1;
			quadBounds[Direction.UP.getIndex()] = f4;
			quadBounds[Direction.NORTH.getIndex()] = f2;
			quadBounds[Direction.SOUTH.getIndex()] = f5;
			int j = Direction.values().length;
			quadBounds[Direction.WEST.getIndex() + j] = 1.0F - f;
			quadBounds[Direction.EAST.getIndex() + j] = 1.0F - f3;
			quadBounds[Direction.DOWN.getIndex() + j] = 1.0F - f1;
			quadBounds[Direction.UP.getIndex() + j] = 1.0F - f4;
			quadBounds[Direction.NORTH.getIndex() + j] = 1.0F - f2;
			quadBounds[Direction.SOUTH.getIndex() + j] = 1.0F - f5;
		}

		switch (face) {
		case DOWN:
			boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
			boundsFlags.set(0, f1 == f4 && (f1 < 1.0E-4F || stateIn.isCollisionShapeOpaque(blockReaderIn, posIn)));
			break;
		case UP:
			boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
			boundsFlags.set(0, f1 == f4 && (f4 > 0.9999F || stateIn.isCollisionShapeOpaque(blockReaderIn, posIn)));
			break;
		case NORTH:
			boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
			boundsFlags.set(0, f2 == f5 && (f2 < 1.0E-4F || stateIn.isCollisionShapeOpaque(blockReaderIn, posIn)));
			break;
		case SOUTH:
			boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
			boundsFlags.set(0, f2 == f5 && (f5 > 0.9999F || stateIn.isCollisionShapeOpaque(blockReaderIn, posIn)));
			break;
		case WEST:
			boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
			boundsFlags.set(0, f == f3 && (f < 1.0E-4F || stateIn.isCollisionShapeOpaque(blockReaderIn, posIn)));
			break;
		case EAST:
			boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
			boundsFlags.set(0, f == f3 && (f3 > 0.9999F || stateIn.isCollisionShapeOpaque(blockReaderIn, posIn)));
		}

	}

	private void renderQuadsFlat(ILightReader blockAccessIn, BlockState stateIn, BlockPos posIn, int brightnessIn,
			int combinedOverlayIn, boolean ownBrightness, MatrixStack matrixStackIn, IVertexBuilder buffer,
			List<BakedQuad> list, BitSet bitSet) {
		for (BakedQuad bakedquad : list) {
			if (ownBrightness) {
				this.fillQuadBounds(blockAccessIn, stateIn, posIn, bakedquad.getVertexData(), bakedquad.getFace(),
						(float[]) null, bitSet);
				BlockPos blockpos = bitSet.get(0) ? posIn.offset(bakedquad.getFace()) : posIn;
				brightnessIn = WorldRenderer.getPackedLightmapCoords(blockAccessIn, stateIn, blockpos);
			}

			this.renderQuadSmooth(blockAccessIn, stateIn, posIn, buffer, matrixStackIn.getLast(), bakedquad, 1.0F, 1.0F,
					1.0F, 1.0F, brightnessIn, brightnessIn, brightnessIn, brightnessIn, combinedOverlayIn);
		}

	}

	public static void enableCache() {
		CACHE_COMBINED_LIGHT.get().enable();
	}

	public static void disableCache() {
		CACHE_COMBINED_LIGHT.get().disable();
	}

	@OnlyIn(Dist.CLIENT)
	class AmbientOcclusionFace {
		private final float[] vertexColorMultiplier = new float[4];
		private final int[] vertexBrightness = new int[4];

		public AmbientOcclusionFace() {
		}

		public void updateVertexBrightness(ILightReader worldIn, BlockState state, BlockPos centerPos,
				Direction directionIn, float[] faceShape, BitSet shapeState) {
			BlockPos blockpos = shapeState.get(0) ? centerPos.offset(directionIn) : centerPos;
			TERHelper.NeighborInfo TERHelper$neighborinfo = TERHelper.NeighborInfo.getNeighbourInfo(directionIn);
			BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
			TERHelper.Cache TERHelper$cache = TERHelper.CACHE_COMBINED_LIGHT.get();
			blockpos$mutable.setPos(blockpos).move(TERHelper$neighborinfo.corners[0]);
			BlockState blockstate = worldIn.getBlockState(blockpos$mutable);
			int i = TERHelper$cache.getPackedLight(blockstate, worldIn, blockpos$mutable);
			float f = TERHelper$cache.getBrightness(blockstate, worldIn, blockpos$mutable);
			blockpos$mutable.setPos(blockpos).move(TERHelper$neighborinfo.corners[1]);
			BlockState blockstate1 = worldIn.getBlockState(blockpos$mutable);
			int j = TERHelper$cache.getPackedLight(blockstate1, worldIn, blockpos$mutable);
			float f1 = TERHelper$cache.getBrightness(blockstate1, worldIn, blockpos$mutable);
			blockpos$mutable.setPos(blockpos).move(TERHelper$neighborinfo.corners[2]);
			BlockState blockstate2 = worldIn.getBlockState(blockpos$mutable);
			int k = TERHelper$cache.getPackedLight(blockstate2, worldIn, blockpos$mutable);
			float f2 = TERHelper$cache.getBrightness(blockstate2, worldIn, blockpos$mutable);
			blockpos$mutable.setPos(blockpos).move(TERHelper$neighborinfo.corners[3]);
			BlockState blockstate3 = worldIn.getBlockState(blockpos$mutable);
			int l = TERHelper$cache.getPackedLight(blockstate3, worldIn, blockpos$mutable);
			float f3 = TERHelper$cache.getBrightness(blockstate3, worldIn, blockpos$mutable);
			blockpos$mutable.setPos(blockpos).move(TERHelper$neighborinfo.corners[0]).move(directionIn);
			boolean flag = worldIn.getBlockState(blockpos$mutable).getOpacity(worldIn, blockpos$mutable) == 0;
			blockpos$mutable.setPos(blockpos).move(TERHelper$neighborinfo.corners[1]).move(directionIn);
			boolean flag1 = worldIn.getBlockState(blockpos$mutable).getOpacity(worldIn, blockpos$mutable) == 0;
			blockpos$mutable.setPos(blockpos).move(TERHelper$neighborinfo.corners[2]).move(directionIn);
			boolean flag2 = worldIn.getBlockState(blockpos$mutable).getOpacity(worldIn, blockpos$mutable) == 0;
			blockpos$mutable.setPos(blockpos).move(TERHelper$neighborinfo.corners[3]).move(directionIn);
			boolean flag3 = worldIn.getBlockState(blockpos$mutable).getOpacity(worldIn, blockpos$mutable) == 0;
			float f4;
			int i1;
			if (!flag2 && !flag) {
				f4 = f;
				i1 = i;
			} else {
				blockpos$mutable.setPos(blockpos).move(TERHelper$neighborinfo.corners[0])
						.move(TERHelper$neighborinfo.corners[2]);
				BlockState blockstate4 = worldIn.getBlockState(blockpos$mutable);
				f4 = TERHelper$cache.getBrightness(blockstate4, worldIn, blockpos$mutable);
				i1 = TERHelper$cache.getPackedLight(blockstate4, worldIn, blockpos$mutable);
			}

			float f5;
			int j1;
			if (!flag3 && !flag) {
				f5 = f;
				j1 = i;
			} else {
				blockpos$mutable.setPos(blockpos).move(TERHelper$neighborinfo.corners[0])
						.move(TERHelper$neighborinfo.corners[3]);
				BlockState blockstate6 = worldIn.getBlockState(blockpos$mutable);
				f5 = TERHelper$cache.getBrightness(blockstate6, worldIn, blockpos$mutable);
				j1 = TERHelper$cache.getPackedLight(blockstate6, worldIn, blockpos$mutable);
			}

			float f6;
			int k1;
			if (!flag2 && !flag1) {
				f6 = f;
				k1 = i;
			} else {
				blockpos$mutable.setPos(blockpos).move(TERHelper$neighborinfo.corners[1])
						.move(TERHelper$neighborinfo.corners[2]);
				BlockState blockstate7 = worldIn.getBlockState(blockpos$mutable);
				f6 = TERHelper$cache.getBrightness(blockstate7, worldIn, blockpos$mutable);
				k1 = TERHelper$cache.getPackedLight(blockstate7, worldIn, blockpos$mutable);
			}

			float f7;
			int l1;
			if (!flag3 && !flag1) {
				f7 = f;
				l1 = i;
			} else {
				blockpos$mutable.setPos(blockpos).move(TERHelper$neighborinfo.corners[1])
						.move(TERHelper$neighborinfo.corners[3]);
				BlockState blockstate8 = worldIn.getBlockState(blockpos$mutable);
				f7 = TERHelper$cache.getBrightness(blockstate8, worldIn, blockpos$mutable);
				l1 = TERHelper$cache.getPackedLight(blockstate8, worldIn, blockpos$mutable);
			}

			int i3 = TERHelper$cache.getPackedLight(state, worldIn, centerPos);
			blockpos$mutable.setPos(centerPos).move(directionIn);
			BlockState blockstate5 = worldIn.getBlockState(blockpos$mutable);
			if (shapeState.get(0) || !blockstate5.isOpaqueCube(worldIn, blockpos$mutable)) {
				i3 = TERHelper$cache.getPackedLight(blockstate5, worldIn, blockpos$mutable);
			}

			float f8 = shapeState.get(0)
					? TERHelper$cache.getBrightness(worldIn.getBlockState(blockpos), worldIn, blockpos)
					: TERHelper$cache.getBrightness(worldIn.getBlockState(centerPos), worldIn, centerPos);
			TERHelper.VertexTranslations TERHelper$vertextranslations = TERHelper.VertexTranslations
					.getVertexTranslations(directionIn);
			if (shapeState.get(1) && TERHelper$neighborinfo.doNonCubicWeight) {
				float f29 = (f3 + f + f5 + f8) * 0.25F;
				float f30 = (f2 + f + f4 + f8) * 0.25F;
				float f31 = (f2 + f1 + f6 + f8) * 0.25F;
				float f32 = (f3 + f1 + f7 + f8) * 0.25F;
				float f13 = faceShape[TERHelper$neighborinfo.vert0Weights[0].shape]
						* faceShape[TERHelper$neighborinfo.vert0Weights[1].shape];
				float f14 = faceShape[TERHelper$neighborinfo.vert0Weights[2].shape]
						* faceShape[TERHelper$neighborinfo.vert0Weights[3].shape];
				float f15 = faceShape[TERHelper$neighborinfo.vert0Weights[4].shape]
						* faceShape[TERHelper$neighborinfo.vert0Weights[5].shape];
				float f16 = faceShape[TERHelper$neighborinfo.vert0Weights[6].shape]
						* faceShape[TERHelper$neighborinfo.vert0Weights[7].shape];
				float f17 = faceShape[TERHelper$neighborinfo.vert1Weights[0].shape]
						* faceShape[TERHelper$neighborinfo.vert1Weights[1].shape];
				float f18 = faceShape[TERHelper$neighborinfo.vert1Weights[2].shape]
						* faceShape[TERHelper$neighborinfo.vert1Weights[3].shape];
				float f19 = faceShape[TERHelper$neighborinfo.vert1Weights[4].shape]
						* faceShape[TERHelper$neighborinfo.vert1Weights[5].shape];
				float f20 = faceShape[TERHelper$neighborinfo.vert1Weights[6].shape]
						* faceShape[TERHelper$neighborinfo.vert1Weights[7].shape];
				float f21 = faceShape[TERHelper$neighborinfo.vert2Weights[0].shape]
						* faceShape[TERHelper$neighborinfo.vert2Weights[1].shape];
				float f22 = faceShape[TERHelper$neighborinfo.vert2Weights[2].shape]
						* faceShape[TERHelper$neighborinfo.vert2Weights[3].shape];
				float f23 = faceShape[TERHelper$neighborinfo.vert2Weights[4].shape]
						* faceShape[TERHelper$neighborinfo.vert2Weights[5].shape];
				float f24 = faceShape[TERHelper$neighborinfo.vert2Weights[6].shape]
						* faceShape[TERHelper$neighborinfo.vert2Weights[7].shape];
				float f25 = faceShape[TERHelper$neighborinfo.vert3Weights[0].shape]
						* faceShape[TERHelper$neighborinfo.vert3Weights[1].shape];
				float f26 = faceShape[TERHelper$neighborinfo.vert3Weights[2].shape]
						* faceShape[TERHelper$neighborinfo.vert3Weights[3].shape];
				float f27 = faceShape[TERHelper$neighborinfo.vert3Weights[4].shape]
						* faceShape[TERHelper$neighborinfo.vert3Weights[5].shape];
				float f28 = faceShape[TERHelper$neighborinfo.vert3Weights[6].shape]
						* faceShape[TERHelper$neighborinfo.vert3Weights[7].shape];
				this.vertexColorMultiplier[TERHelper$vertextranslations.vert0] = f29 * f13 + f30 * f14 + f31 * f15
						+ f32 * f16;
				this.vertexColorMultiplier[TERHelper$vertextranslations.vert1] = f29 * f17 + f30 * f18 + f31 * f19
						+ f32 * f20;
				this.vertexColorMultiplier[TERHelper$vertextranslations.vert2] = f29 * f21 + f30 * f22 + f31 * f23
						+ f32 * f24;
				this.vertexColorMultiplier[TERHelper$vertextranslations.vert3] = f29 * f25 + f30 * f26 + f31 * f27
						+ f32 * f28;
				int i2 = this.getAoBrightness(l, i, j1, i3);
				int j2 = this.getAoBrightness(k, i, i1, i3);
				int k2 = this.getAoBrightness(k, j, k1, i3);
				int l2 = this.getAoBrightness(l, j, l1, i3);
				this.vertexBrightness[TERHelper$vertextranslations.vert0] = this.getVertexBrightness(i2, j2, k2, l2,
						f13, f14, f15, f16);
				this.vertexBrightness[TERHelper$vertextranslations.vert1] = this.getVertexBrightness(i2, j2, k2, l2,
						f17, f18, f19, f20);
				this.vertexBrightness[TERHelper$vertextranslations.vert2] = this.getVertexBrightness(i2, j2, k2, l2,
						f21, f22, f23, f24);
				this.vertexBrightness[TERHelper$vertextranslations.vert3] = this.getVertexBrightness(i2, j2, k2, l2,
						f25, f26, f27, f28);
			} else {
				float f9 = (f3 + f + f5 + f8) * 0.25F;
				float f10 = (f2 + f + f4 + f8) * 0.25F;
				float f11 = (f2 + f1 + f6 + f8) * 0.25F;
				float f12 = (f3 + f1 + f7 + f8) * 0.25F;
				this.vertexBrightness[TERHelper$vertextranslations.vert0] = this.getAoBrightness(l, i, j1, i3);
				this.vertexBrightness[TERHelper$vertextranslations.vert1] = this.getAoBrightness(k, i, i1, i3);
				this.vertexBrightness[TERHelper$vertextranslations.vert2] = this.getAoBrightness(k, j, k1, i3);
				this.vertexBrightness[TERHelper$vertextranslations.vert3] = this.getAoBrightness(l, j, l1, i3);
				this.vertexColorMultiplier[TERHelper$vertextranslations.vert0] = f9;
				this.vertexColorMultiplier[TERHelper$vertextranslations.vert1] = f10;
				this.vertexColorMultiplier[TERHelper$vertextranslations.vert2] = f11;
				this.vertexColorMultiplier[TERHelper$vertextranslations.vert3] = f12;
			}

		}

		private int getAoBrightness(int br1, int br2, int br3, int br4) {
			if (br1 == 0) {
				br1 = br4;
			}

			if (br2 == 0) {
				br2 = br4;
			}

			if (br3 == 0) {
				br3 = br4;
			}

			return br1 + br2 + br3 + br4 >> 2 & 16711935;
		}

		private int getVertexBrightness(int b1, int b2, int b3, int b4, float w1, float w2, float w3, float w4) {
			int i = (int) ((float) (b1 >> 16 & 255) * w1 + (float) (b2 >> 16 & 255) * w2 + (float) (b3 >> 16 & 255) * w3
					+ (float) (b4 >> 16 & 255) * w4) & 255;
			int j = (int) ((float) (b1 & 255) * w1 + (float) (b2 & 255) * w2 + (float) (b3 & 255) * w3
					+ (float) (b4 & 255) * w4) & 255;
			return i << 16 | j;
		}
	}

	@OnlyIn(Dist.CLIENT)
	static class Cache {
		private boolean enabled;
		private final Long2IntLinkedOpenHashMap packedLightCache = Util.make(() -> {
			Long2IntLinkedOpenHashMap long2intlinkedopenhashmap = new Long2IntLinkedOpenHashMap(100, 0.25F) {

				private static final long serialVersionUID = 1L;

				protected void rehash(int p_rehash_1_) {
				}
			};
			long2intlinkedopenhashmap.defaultReturnValue(Integer.MAX_VALUE);
			return long2intlinkedopenhashmap;
		});
		private final Long2FloatLinkedOpenHashMap brightnessCache = Util.make(() -> {
			Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = new Long2FloatLinkedOpenHashMap(100, 0.25F) {

				private static final long serialVersionUID = 1L;

				protected void rehash(int p_rehash_1_) {
				}
			};
			long2floatlinkedopenhashmap.defaultReturnValue(Float.NaN);
			return long2floatlinkedopenhashmap;
		});

		private Cache() {
		}

		public void enable() {
			this.enabled = true;
		}

		public void disable() {
			this.enabled = false;
			this.packedLightCache.clear();
			this.brightnessCache.clear();
		}

		public int getPackedLight(BlockState blockStateIn, ILightReader lightReaderIn, BlockPos blockPosIn) {
			long i = blockPosIn.toLong();
			if (this.enabled) {
				int j = this.packedLightCache.get(i);
				if (j != Integer.MAX_VALUE) {
					return j;
				}
			}

			int k = WorldRenderer.getPackedLightmapCoords(lightReaderIn, blockStateIn, blockPosIn);
			if (this.enabled) {
				if (this.packedLightCache.size() == 100) {
					this.packedLightCache.removeFirstInt();
				}

				this.packedLightCache.put(i, k);
			}

			return k;
		}

		public float getBrightness(BlockState blockStateIn, ILightReader lightReaderIn, BlockPos blockPosIn) {
			long i = blockPosIn.toLong();
			if (this.enabled) {
				float f = this.brightnessCache.get(i);
				if (!Float.isNaN(f)) {
					return f;
				}
			}

			float f1 = blockStateIn.getAmbientOcclusionLightValue(lightReaderIn, blockPosIn);
			if (this.enabled) {
				if (this.brightnessCache.size() == 100) {
					this.brightnessCache.removeFirstFloat();
				}

				this.brightnessCache.put(i, f1);
			}

			return f1;
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static enum NeighborInfo {
		DOWN(new Direction[] { Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH }, 0.5F, true,
				new TERHelper.Orientation[] { TERHelper.Orientation.FLIP_WEST, TERHelper.Orientation.SOUTH,
						TERHelper.Orientation.FLIP_WEST, TERHelper.Orientation.FLIP_SOUTH, TERHelper.Orientation.WEST,
						TERHelper.Orientation.FLIP_SOUTH, TERHelper.Orientation.WEST, TERHelper.Orientation.SOUTH },
				new TERHelper.Orientation[] { TERHelper.Orientation.FLIP_WEST, TERHelper.Orientation.NORTH,
						TERHelper.Orientation.FLIP_WEST, TERHelper.Orientation.FLIP_NORTH, TERHelper.Orientation.WEST,
						TERHelper.Orientation.FLIP_NORTH, TERHelper.Orientation.WEST, TERHelper.Orientation.NORTH },
				new TERHelper.Orientation[] { TERHelper.Orientation.FLIP_EAST, TERHelper.Orientation.NORTH,
						TERHelper.Orientation.FLIP_EAST, TERHelper.Orientation.FLIP_NORTH, TERHelper.Orientation.EAST,
						TERHelper.Orientation.FLIP_NORTH, TERHelper.Orientation.EAST, TERHelper.Orientation.NORTH },
				new TERHelper.Orientation[] { TERHelper.Orientation.FLIP_EAST, TERHelper.Orientation.SOUTH,
						TERHelper.Orientation.FLIP_EAST, TERHelper.Orientation.FLIP_SOUTH, TERHelper.Orientation.EAST,
						TERHelper.Orientation.FLIP_SOUTH, TERHelper.Orientation.EAST, TERHelper.Orientation.SOUTH }),
		UP(new Direction[] { Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH }, 1.0F, true,
				new TERHelper.Orientation[] { TERHelper.Orientation.EAST, TERHelper.Orientation.SOUTH,
						TERHelper.Orientation.EAST, TERHelper.Orientation.FLIP_SOUTH, TERHelper.Orientation.FLIP_EAST,
						TERHelper.Orientation.FLIP_SOUTH, TERHelper.Orientation.FLIP_EAST,
						TERHelper.Orientation.SOUTH },
				new TERHelper.Orientation[] { TERHelper.Orientation.EAST, TERHelper.Orientation.NORTH,
						TERHelper.Orientation.EAST, TERHelper.Orientation.FLIP_NORTH, TERHelper.Orientation.FLIP_EAST,
						TERHelper.Orientation.FLIP_NORTH, TERHelper.Orientation.FLIP_EAST,
						TERHelper.Orientation.NORTH },
				new TERHelper.Orientation[] { TERHelper.Orientation.WEST, TERHelper.Orientation.NORTH,
						TERHelper.Orientation.WEST, TERHelper.Orientation.FLIP_NORTH, TERHelper.Orientation.FLIP_WEST,
						TERHelper.Orientation.FLIP_NORTH, TERHelper.Orientation.FLIP_WEST,
						TERHelper.Orientation.NORTH },
				new TERHelper.Orientation[] { TERHelper.Orientation.WEST, TERHelper.Orientation.SOUTH,
						TERHelper.Orientation.WEST, TERHelper.Orientation.FLIP_SOUTH, TERHelper.Orientation.FLIP_WEST,
						TERHelper.Orientation.FLIP_SOUTH, TERHelper.Orientation.FLIP_WEST,
						TERHelper.Orientation.SOUTH }),
		NORTH(new Direction[] { Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST }, 0.8F, true,
				new TERHelper.Orientation[] { TERHelper.Orientation.UP, TERHelper.Orientation.FLIP_WEST,
						TERHelper.Orientation.UP, TERHelper.Orientation.WEST, TERHelper.Orientation.FLIP_UP,
						TERHelper.Orientation.WEST, TERHelper.Orientation.FLIP_UP, TERHelper.Orientation.FLIP_WEST },
				new TERHelper.Orientation[] { TERHelper.Orientation.UP, TERHelper.Orientation.FLIP_EAST,
						TERHelper.Orientation.UP, TERHelper.Orientation.EAST, TERHelper.Orientation.FLIP_UP,
						TERHelper.Orientation.EAST, TERHelper.Orientation.FLIP_UP, TERHelper.Orientation.FLIP_EAST },
				new TERHelper.Orientation[] { TERHelper.Orientation.DOWN, TERHelper.Orientation.FLIP_EAST,
						TERHelper.Orientation.DOWN, TERHelper.Orientation.EAST, TERHelper.Orientation.FLIP_DOWN,
						TERHelper.Orientation.EAST, TERHelper.Orientation.FLIP_DOWN, TERHelper.Orientation.FLIP_EAST },
				new TERHelper.Orientation[] { TERHelper.Orientation.DOWN, TERHelper.Orientation.FLIP_WEST,
						TERHelper.Orientation.DOWN, TERHelper.Orientation.WEST, TERHelper.Orientation.FLIP_DOWN,
						TERHelper.Orientation.WEST, TERHelper.Orientation.FLIP_DOWN, TERHelper.Orientation.FLIP_WEST }),
		SOUTH(new Direction[] { Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP }, 0.8F, true,
				new TERHelper.Orientation[] { TERHelper.Orientation.UP, TERHelper.Orientation.FLIP_WEST,
						TERHelper.Orientation.FLIP_UP, TERHelper.Orientation.FLIP_WEST, TERHelper.Orientation.FLIP_UP,
						TERHelper.Orientation.WEST, TERHelper.Orientation.UP, TERHelper.Orientation.WEST },
				new TERHelper.Orientation[] { TERHelper.Orientation.DOWN, TERHelper.Orientation.FLIP_WEST,
						TERHelper.Orientation.FLIP_DOWN, TERHelper.Orientation.FLIP_WEST,
						TERHelper.Orientation.FLIP_DOWN, TERHelper.Orientation.WEST, TERHelper.Orientation.DOWN,
						TERHelper.Orientation.WEST },
				new TERHelper.Orientation[] { TERHelper.Orientation.DOWN, TERHelper.Orientation.FLIP_EAST,
						TERHelper.Orientation.FLIP_DOWN, TERHelper.Orientation.FLIP_EAST,
						TERHelper.Orientation.FLIP_DOWN, TERHelper.Orientation.EAST, TERHelper.Orientation.DOWN,
						TERHelper.Orientation.EAST },
				new TERHelper.Orientation[] { TERHelper.Orientation.UP, TERHelper.Orientation.FLIP_EAST,
						TERHelper.Orientation.FLIP_UP, TERHelper.Orientation.FLIP_EAST, TERHelper.Orientation.FLIP_UP,
						TERHelper.Orientation.EAST, TERHelper.Orientation.UP, TERHelper.Orientation.EAST }),
		WEST(new Direction[] { Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH }, 0.6F, true,
				new TERHelper.Orientation[] { TERHelper.Orientation.UP, TERHelper.Orientation.SOUTH,
						TERHelper.Orientation.UP, TERHelper.Orientation.FLIP_SOUTH, TERHelper.Orientation.FLIP_UP,
						TERHelper.Orientation.FLIP_SOUTH, TERHelper.Orientation.FLIP_UP, TERHelper.Orientation.SOUTH },
				new TERHelper.Orientation[] { TERHelper.Orientation.UP, TERHelper.Orientation.NORTH,
						TERHelper.Orientation.UP, TERHelper.Orientation.FLIP_NORTH, TERHelper.Orientation.FLIP_UP,
						TERHelper.Orientation.FLIP_NORTH, TERHelper.Orientation.FLIP_UP, TERHelper.Orientation.NORTH },
				new TERHelper.Orientation[] { TERHelper.Orientation.DOWN, TERHelper.Orientation.NORTH,
						TERHelper.Orientation.DOWN, TERHelper.Orientation.FLIP_NORTH, TERHelper.Orientation.FLIP_DOWN,
						TERHelper.Orientation.FLIP_NORTH, TERHelper.Orientation.FLIP_DOWN,
						TERHelper.Orientation.NORTH },
				new TERHelper.Orientation[] { TERHelper.Orientation.DOWN, TERHelper.Orientation.SOUTH,
						TERHelper.Orientation.DOWN, TERHelper.Orientation.FLIP_SOUTH, TERHelper.Orientation.FLIP_DOWN,
						TERHelper.Orientation.FLIP_SOUTH, TERHelper.Orientation.FLIP_DOWN,
						TERHelper.Orientation.SOUTH }),
		EAST(new Direction[] { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH }, 0.6F, true,
				new TERHelper.Orientation[] { TERHelper.Orientation.FLIP_DOWN, TERHelper.Orientation.SOUTH,
						TERHelper.Orientation.FLIP_DOWN, TERHelper.Orientation.FLIP_SOUTH, TERHelper.Orientation.DOWN,
						TERHelper.Orientation.FLIP_SOUTH, TERHelper.Orientation.DOWN, TERHelper.Orientation.SOUTH },
				new TERHelper.Orientation[] { TERHelper.Orientation.FLIP_DOWN, TERHelper.Orientation.NORTH,
						TERHelper.Orientation.FLIP_DOWN, TERHelper.Orientation.FLIP_NORTH, TERHelper.Orientation.DOWN,
						TERHelper.Orientation.FLIP_NORTH, TERHelper.Orientation.DOWN, TERHelper.Orientation.NORTH },
				new TERHelper.Orientation[] { TERHelper.Orientation.FLIP_UP, TERHelper.Orientation.NORTH,
						TERHelper.Orientation.FLIP_UP, TERHelper.Orientation.FLIP_NORTH, TERHelper.Orientation.UP,
						TERHelper.Orientation.FLIP_NORTH, TERHelper.Orientation.UP, TERHelper.Orientation.NORTH },
				new TERHelper.Orientation[] { TERHelper.Orientation.FLIP_UP, TERHelper.Orientation.SOUTH,
						TERHelper.Orientation.FLIP_UP, TERHelper.Orientation.FLIP_SOUTH, TERHelper.Orientation.UP,
						TERHelper.Orientation.FLIP_SOUTH, TERHelper.Orientation.UP, TERHelper.Orientation.SOUTH });

		private final Direction[] corners;
		private final boolean doNonCubicWeight;
		private final TERHelper.Orientation[] vert0Weights;
		private final TERHelper.Orientation[] vert1Weights;
		private final TERHelper.Orientation[] vert2Weights;
		private final TERHelper.Orientation[] vert3Weights;
		private static final TERHelper.NeighborInfo[] VALUES = Util.make(new TERHelper.NeighborInfo[6],
				(p_209260_0_) -> {
					p_209260_0_[Direction.DOWN.getIndex()] = DOWN;
					p_209260_0_[Direction.UP.getIndex()] = UP;
					p_209260_0_[Direction.NORTH.getIndex()] = NORTH;
					p_209260_0_[Direction.SOUTH.getIndex()] = SOUTH;
					p_209260_0_[Direction.WEST.getIndex()] = WEST;
					p_209260_0_[Direction.EAST.getIndex()] = EAST;
				});

		private NeighborInfo(Direction[] cornersIn, float brightness, boolean doNonCubicWeightIn,
				TERHelper.Orientation[] vert0WeightsIn, TERHelper.Orientation[] vert1WeightsIn,
				TERHelper.Orientation[] vert2WeightsIn, TERHelper.Orientation[] vert3WeightsIn) {
			this.corners = cornersIn;
			this.doNonCubicWeight = doNonCubicWeightIn;
			this.vert0Weights = vert0WeightsIn;
			this.vert1Weights = vert1WeightsIn;
			this.vert2Weights = vert2WeightsIn;
			this.vert3Weights = vert3WeightsIn;
		}

		public static TERHelper.NeighborInfo getNeighbourInfo(Direction facing) {
			return VALUES[facing.getIndex()];
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static enum Orientation {
		DOWN(Direction.DOWN, false), UP(Direction.UP, false), NORTH(Direction.NORTH, false),
		SOUTH(Direction.SOUTH, false), WEST(Direction.WEST, false), EAST(Direction.EAST, false),
		FLIP_DOWN(Direction.DOWN, true), FLIP_UP(Direction.UP, true), FLIP_NORTH(Direction.NORTH, true),
		FLIP_SOUTH(Direction.SOUTH, true), FLIP_WEST(Direction.WEST, true), FLIP_EAST(Direction.EAST, true);

		private final int shape;

		private Orientation(Direction facingIn, boolean flip) {
			this.shape = facingIn.getIndex() + (flip ? Direction.values().length : 0);
		}
	}

	@OnlyIn(Dist.CLIENT)
	static enum VertexTranslations {
		DOWN(0, 1, 2, 3), UP(2, 3, 0, 1), NORTH(3, 0, 1, 2), SOUTH(0, 1, 2, 3), WEST(3, 0, 1, 2), EAST(1, 2, 3, 0);

		private final int vert0;
		private final int vert1;
		private final int vert2;
		private final int vert3;
		private static final VertexTranslations[] VALUES = Util.make(new VertexTranslations[6], (p_209261_0_) -> {
			p_209261_0_[Direction.DOWN.getIndex()] = DOWN;
			p_209261_0_[Direction.UP.getIndex()] = UP;
			p_209261_0_[Direction.NORTH.getIndex()] = NORTH;
			p_209261_0_[Direction.SOUTH.getIndex()] = SOUTH;
			p_209261_0_[Direction.WEST.getIndex()] = WEST;
			p_209261_0_[Direction.EAST.getIndex()] = EAST;
		});

		private VertexTranslations(int vert0In, int vert1In, int vert2In, int vert3In) {
			this.vert0 = vert0In;
			this.vert1 = vert1In;
			this.vert2 = vert2In;
			this.vert3 = vert3In;
		}

		public static TERHelper.VertexTranslations getVertexTranslations(Direction facingIn) {
			return VALUES[facingIn.getIndex()];
		}
	}

}
