package modist.blockdiy.client.block.model;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import modist.blockdiy.BlockDiy;
import modist.blockdiy.common.tileentity.DiyBlockTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

@OnlyIn(Dist.CLIENT)
public class DiyBlockBakedModel implements IDynamicBakedModel {

	public static final ResourceLocation DIY_BLOCK_TEXTURE = new ResourceLocation(BlockDiy.MODID+"block/diy_block");

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
		List<BakedQuad> quads = extraData.getData(DiyBlockTileEntity.RENDER_QUADS);
		if (quads != null) {
			if (side == null) {
				return quads;
			}
		}
		return Collections.emptyList();
	}

	//是否采用平滑光照，设置为否，否则因为模型的多变性，阴影渲染会变得相当怪异
	//TODO：对于简单DiyBlock，可设为是
	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return null;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return getTexture();
	}

	private TextureAtlasSprite getTexture() {
		return Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE)
				.apply((DIY_BLOCK_TEXTURE));
	}

	@Override
	public boolean func_230044_c_() {
		return false;
	}

}
