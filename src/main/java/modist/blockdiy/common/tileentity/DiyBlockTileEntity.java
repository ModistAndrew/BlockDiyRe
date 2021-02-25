package modist.blockdiy.common.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import modist.blockdiy.common.block.BlockLoader;
import modist.blockdiy.common.worldstorage.DiyBlockData;
import modist.blockdiy.common.worldstorage.ModelWorldSavedData;
import modist.blockdiy.network.NetworkLoader;
import modist.blockdiy.util.AABBHelper;
import modist.blockdiy.util.Matrix4fHelper;
import modist.blockdiy.util.NBTHelper;
import modist.blockdiy.util.QuadHelper;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

/*
*DiyBlockTileEntity：DiyBlock的方块实体类，负责存储模型数据
*总模型：指diyblock方块的模型，由多个其他方块的模型结合而成
*分模型方块：指总模型中所包含的方块
*分模型：指分模型方块的模型
*分模型坐标：指分模型方块的绝对坐标
*/
public class DiyBlockTileEntity extends TileEntity {

	private BlockPos renderpos1; // 分模型坐标中x，y，z均最小的
	private BlockPos renderpos2; // 分模型坐标中x，y，z均最大的
	private Matrix4fHelper matrix = new Matrix4fHelper(); // 总模型的变换矩阵（平移，旋转，缩放）
	public Map<BlockPos, ModelData> modeldata = new HashMap<BlockPos, ModelData>(); // 含有静态模型的分模型
	public Map<BlockPos, TranslucentData> translucentdata = new HashMap<BlockPos, TranslucentData>(); // 半透明分模型
	public Map<BlockPos, TERData> terdata = new HashMap<BlockPos, TERData>(); // 含有TER的分模型
	public Map<BlockPos, FluidData> fluiddata = new HashMap<BlockPos, FluidData>(); // 流体分模型
	private Map<BlockPos, List<BakedQuad>> renderquads1 = new HashMap<BlockPos, List<BakedQuad>>(); // 需渲染的quads，有对应blockpos
	private List<BakedQuad> renderquads = new ArrayList<BakedQuad>(); // 需渲染的quads，提供给IBakedModel（来自含有静态模型的分模型）
	public static final ModelProperty<List<BakedQuad>> RENDER_QUADS = new ModelProperty<>();
	public VoxelShape shape = VoxelShapes.empty();
	//public Map<BlockPos, List<AxisAlignedBB>> aabbs = new HashMap<BlockPos, List<AxisAlignedBB>>(); // 方块碰撞箱

	// 构造函数，与注册的DIY_BLOCK_TILE_ENTITY绑定
	public DiyBlockTileEntity() {
		super(BlockLoader.DIY_BLOCK_TILE_ENTITY);
	}

	/*
	 * 设置总模型的起始坐标和结束坐标（包含）或设为空 server Pre：存在DiyBlockTileEntity
	 * Post：renderpos1和renderpos2被（重新）设置（为空）
	 */
	public void setRenderPos(BlockPos pos1, BlockPos pos2) {
		if (pos1 != null && pos2 != null) {
			BlockPos renderposmin = new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()),
					Math.min(pos1.getZ(), pos2.getZ()));
			BlockPos renderposmax = new BlockPos(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()),
					Math.max(pos1.getZ(), pos2.getZ()));
			this.renderpos1 = renderposmin;
			this.renderpos2 = renderposmax;
		} else {
			renderpos1 = null;
			renderpos2 = null;
		}
		ModelWorldSavedData.get(world).putPos(getPos(), renderpos1, renderpos2);
	}

	/*
	 * 平移总模型 server Pre：存在DiyBlockTileEntity Post：总模型的matrix被平移
	 */
	public void translate(float x, float y, float z) {
		matrix.mul(Matrix4fHelper.makeTranslate(x, y, z));
	}

	/*
	 * 缩放总模型 server Pre：存在DiyBlockTileEntity Post：总模型的matrix被缩放
	 */
	public void scale(float x, float y, float z) {
		matrix.mul(Matrix4fHelper.makeScale(x, y, z));
	}

	/*
	 * 旋转总模型 server Pre：存在DiyBlockTileEntity Post：总模型的matrix被旋转
	 */
	public void rotate(float x, float y, float z) {
		Quaternion quaternion = new Quaternion(x, y, z, true);
		matrix.mul(quaternion);
	}

	//Server
	//对模型数据(除TileEntity)进行全部修改
	//并发至客户端
	public void update() {
		this.generateRenderData();
		this.generateBoundingBox();
		//markDirty(); //标记数据变化以便存储
		CompoundNBT nbt = new CompoundNBT();
		writeNBT(nbt);
		nbt.putBoolean("All", true);
		NetworkLoader.diyTileUpdate(nbt, this);
		//world.notifyBlockUpdate(this.pos, getBlockState(), getBlockState(), 3); //同步数据 no 提醒其他diyblock
		ModelWorldSavedData.get(world).updateModelNoBoard(world, this.pos, world.getBlockState(this.pos));
	}

	//Server
	//对模型数据(除TileEntity)进行精细修改
	//并发至客户端
	//state:new state
	public void update(BlockPos pos, BlockState state) {
		DataType[] types = this.generateRenderData(pos, state);
		//markDirty(); //标记数据变化以便存储
		CompoundNBT nbt = new CompoundNBT();
		writeNBT(nbt, types, pos);
		NetworkLoader.diyTileUpdate(nbt, this);
		//world.notifyBlockUpdate(this.pos, getBlockState(), getBlockState(), 3);
		ModelWorldSavedData.get(world).updateModelNoBoard(world, this.pos, world.getBlockState(this.pos));
	}

	//Server
	//对模型数据TileEntity进行精细修改
	//并发至客户端
	public void update(BlockPos pos, TileEntity tile, boolean add) {
		DataType[] types = this.generateRenderData(pos, tile, add);
		//markDirty(); //标记数据变化以便存储
		CompoundNBT nbt = new CompoundNBT();
		writeNBT(nbt, types, pos);
		NetworkLoader.diyTileUpdate(nbt, this);
		//world.notifyBlockUpdate(this.pos, getBlockState(), getBlockState(), 3);
		ModelWorldSavedData.get(world).updateModelNoBoard(world, this.pos, world.getBlockState(this.pos));
	}
	
	public Matrix4fHelper getMatrix() {
		return matrix;
	}

	public BlockPos getRenderPos(boolean ismin) {
		return ismin ? renderpos1 : renderpos2;
	}

	// 确保有总模型，即renderpos1和renderpos2不为null，以便决定是否渲染，同时防止出现nullpointer
	public boolean checkRenderPos() {
		return renderpos1 != null && renderpos2 != null;
	}

	/*
	 * requestModelDataRefresh（更新静态模型）时调用，获取modeldata（在此为renderquads）传递给IBakedModel
	 * client Pre：在客户端，renderquads已被生成，requestModelDataRefresh被调用
	 * Post：renderquads以IModelData
	 */
	@Override
	public IModelData getModelData() {
		return new ModelDataMap.Builder().withInitial(RENDER_QUADS, renderquads).build();
	}

	/*@Override
	public SUpdateTileEntityPacket getUpdatePacket() { // notifyBlockUpdate时从服务端收集应该发送给客户端的数据
		CompoundNBT compound = new CompoundNBT();
		writeNBT(compound);
		return new SUpdateTileEntityPacket(getPos(), 1, compound);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) { // notifyBlockUpdate时客户端从服务端的数据包中读取数据
		readNBT(pkt.getNbtCompound());
		getQuadsFromPos();
		ModelDataManager.requestModelDataRefresh(this);
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
	}*/

	/*@Override
	public CompoundNBT getUpdateTag()  { // 区块加载时从服务端收集应该发送给客户端的数据，写入数据包内
		ModelWorldSavedData.get(world).putPos(getPos(), renderpos1, renderpos2);
		this.generateRenderData();
		CompoundNBT compound = super.getUpdateTag();
		writeNBT(compound);
		return compound;
	}

	@Override
	public void handleUpdateTag(CompoundNBT tag) { // 区块加载时客户端从服务端的数据包中读取数据
		super.handleUpdateTag(tag);
		readNBT(tag);
		this.getQuadsFromPos();
		ModelDataManager.requestModelDataRefresh(this);
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
	}*/

	@Override
	public void read(CompoundNBT compound) { // 数据从磁盘中读出
		super.read(compound);
		if (compound.contains("RenderPos1") && compound.contains("RenderPos2")) {
			renderpos1 = NBTHelper.readBlockPos(compound.getCompound("RenderPos1"));
			renderpos2 = NBTHelper.readBlockPos(compound.getCompound("RenderPos2"));
		}
		matrix = Matrix4fHelper.read(compound.getCompound("Matrix"));
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) { // 数据存入磁盘
		if (checkRenderPos()) { 
			compound.put("RenderPos1", NBTHelper.writeBlockPos(renderpos1));
			compound.put("RenderPos2", NBTHelper.writeBlockPos(renderpos2));
		}
		compound.put("Matrix", Matrix4fHelper.write(matrix));
		super.write(compound);
		return compound;
	}

	public void readNBT(CompoundNBT compound) { // 全部读取方法
		modeldata.clear();
		translucentdata.clear();
		terdata.clear();
		fluiddata.clear();//注意客户端先清除！
		if (compound.contains("RenderPos1") && compound.contains("RenderPos2")) {
			renderpos1 = NBTHelper.readBlockPos(compound.getCompound("RenderPos1"));
			renderpos2 = NBTHelper.readBlockPos(compound.getCompound("RenderPos2"));
				compound.getList("ModelData", 10).forEach(data -> 
				modeldata.put(NBTHelper.readBlockPos(((CompoundNBT)data).getCompound("BlockPos")), new ModelData((CompoundNBT) data)));
				compound.getList("TranslucentData", 10).forEach(data -> 
				translucentdata.put(NBTHelper.readBlockPos(((CompoundNBT)data).getCompound("BlockPos")), new TranslucentData((CompoundNBT) data)));
				compound.getList("TERData", 10).forEach(data -> { // 判断te是否有ter
					TERData ter = new TERData((CompoundNBT) data);
					if (TileEntityRendererDispatcher.instance.getRenderer(ter.te) != null) {
						terdata.put(NBTHelper.readBlockPos(((CompoundNBT)data).getCompound("BlockPos")), ter);
					}
				});
				compound.getList("FluidData", 10).forEach(data -> 
				fluiddata.put(NBTHelper.readBlockPos(((CompoundNBT)data).getCompound("BlockPos")), new FluidData((CompoundNBT) data)));
			shape = NBTHelper.readAABBs(compound.getList("AABBs", 10));
		}
		matrix = Matrix4fHelper.read(compound.getCompound("Matrix"));
		
		getQuadsFromPos();
		ModelDataManager.requestModelDataRefresh(this);
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
	}

	public void writeNBT(CompoundNBT compound) { // 全部写入方法
		if (checkRenderPos()) { // 基本数据
			compound.put("RenderPos1", NBTHelper.writeBlockPos(renderpos1));
			compound.put("RenderPos2", NBTHelper.writeBlockPos(renderpos2));
				ListNBT modellist = new ListNBT();
				modeldata.forEach((renderpos, data) -> modellist.add(data.write(renderpos)));
				compound.put("ModelData", modellist);
				ListNBT translucentlist = new ListNBT();
				translucentdata.forEach((renderpos, data) -> translucentlist.add(data.write(renderpos)));
				compound.put("TranslucentData", translucentlist);
				ListNBT terlist = new ListNBT();
				terdata.forEach((renderpos, data) -> terlist.add(data.write(renderpos)));
				compound.put("TERData", terlist);
				ListNBT fluidlist = new ListNBT();
				fluiddata.forEach((renderpos, data) -> fluidlist.add(data.write(renderpos)));
				compound.put("FluidData", fluidlist);
			compound.put("AABBs", NBTHelper.writeAABBs(shape));
		}
		// 额外数据
		compound.put("Matrix", Matrix4fHelper.write(matrix));
	}
	
	//client
	//精细修改read方法
	//see PacketDiyBlockTileEntity
	//注意要进行modelrefresh，geetquadsfrompos等
	public void readNBTPacket(CompoundNBT compound) {
		BlockPos pos = NBTHelper.readBlockPos(compound.getCompound("UpdatePos"));
		DataType[] types = NBTHelper.readTypes(compound.getCompound("Types"));
		if(types[0].shouldRemove()) {
			modeldata.remove(pos);
		}
		if(types[1].shouldRemove()) {
			translucentdata.remove(pos);
		}
		if(types[2].shouldRemove()) {
			fluiddata.remove(pos);
		}		
		if(types[3].shouldRemove()) {
			terdata.remove(pos);
		}
		if(types[0].shouldAdd()) {
			modeldata.put(pos, new ModelData(compound.getCompound("ModelData")));
		}
		if(types[1].shouldAdd()) {
			translucentdata.put(pos, new TranslucentData(compound.getCompound("TranslucentData")));
		}	
		if(types[2].shouldAdd()) {
			fluiddata.put(pos, new FluidData(compound.getCompound("FluidData")));
		}
		if(types[3].shouldAdd()) { // 判断te是否有ter
			TERData ter = new TERData(compound.getCompound("TERData"));
			if (TileEntityRendererDispatcher.instance.getRenderer(ter.te) != null) {
				terdata.put(pos, ter);
			}		
		}
		if(types[0].shouldRemove()) { //是否需要修改quads
		getQuadsFromPos(pos);
		ModelDataManager.requestModelDataRefresh(this);
		world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 3);
		}
	}	

	//Server
	//精细修改write方法
	//flags[]:0 for model, 1 for translucent, 2 for fluid, 3 for ter
	private void writeNBT(CompoundNBT compound, DataType[] types, BlockPos updatepos) {
		compound.put("UpdatePos", NBTHelper.writeBlockPos(updatepos));
		compound.put("Types", NBTHelper.writeTypes(types));
		if(types[0].shouldAdd()) {
			compound.put("ModelData", modeldata.get(updatepos).write(updatepos));
		}
		if(types[1].shouldAdd()) {
			compound.put("TranslucentData", translucentdata.get(updatepos).write(updatepos));
		}	
		if(types[2].shouldAdd()) {
			compound.put("FluidData", fluiddata.get(updatepos).write(updatepos));
		}
		if(types[3].shouldAdd()) {
			compound.put("TERData", terdata.get(updatepos).write(updatepos));
		}
	}


	// Server
	// 从renderpos1和renderpos2中生成分模型数据
	private void generateRenderData() {
		modeldata.clear();
		translucentdata.clear();
		terdata.clear();
		fluiddata.clear();
		// 清除之前的四种分模型数据
		if (checkRenderPos()) { // 如果有总模型
			BlockPos.getAllInBox(renderpos1, renderpos2).forEach(pos -> { // 遍历分模型坐标
				BlockPos pos1 = pos.toImmutable();
				if (!world.isAirBlock(pos1)) { // 非空气
					BlockState state = world.getBlockState(pos1); // 获取分模型方块的BlockState，以判断类型
					if (state.getRenderType().equals(BlockRenderType.MODEL)) { // 含有静态模型的分模型
						if (RenderTypeLookup.canRenderInLayer(state, RenderType.getSolid())
								|| RenderTypeLookup.canRenderInLayer(state, RenderType.getCutout())
								|| RenderTypeLookup.canRenderInLayer(state, RenderType.getCutoutMipped())) { // 排除半透明方块
							modeldata.put(pos1, new ModelData(state)); // 加入modeldata中
						} else { // 半透明方块
							translucentdata.put(pos1, new TranslucentData(state)); // 加入translucentdata中
						}
					}
					if (state.hasTileEntity()) {// 是否有TER（Server只可判断是否有te）
						TileEntity tile = world.getTileEntity(pos1);
						if(tile!=null) {
						terdata.put(pos1, new TERData(tile)); // 加入terdata中
						}
					}
					IFluidState fluid = world.getFluidState(pos1);
					if (!fluid.isEmpty()) { // 是否为流体
						fluiddata.put(pos1, new FluidData
								(fluid, getFluidHeights(world, pos1, fluid.getFluid()), fluid.getFlow(world, pos1))); // 加入fluiddata中
					}
				}
			});
		}
	}
	
	// Server
	// 精细修改renderdata，除tileentity
	// state newblockstate
	private DataType[] generateRenderData(BlockPos pos, BlockState state) {
		//flags[]:0 for model, 1 for translucent, 2 for fluid, 3 for ter
		//ter不必检查
		DataType[] flags = new DataType[] {DataType.REMOVE, DataType.REMOVE, DataType.REMOVE, DataType.PASS};
		//否则边缘只进行液体高度更新
		if(new DiyBlockData(renderpos1, renderpos2).completelyContains(pos)) {
		modeldata.remove(pos);
		translucentdata.remove(pos);
		fluiddata.remove(pos);
				if (!state.isAir(world, pos)) { // 非空气
					if (state.getRenderType().equals(BlockRenderType.MODEL)) { // 含有静态模型的分模型
						if (RenderTypeLookup.canRenderInLayer(state, RenderType.getSolid())
								|| RenderTypeLookup.canRenderInLayer(state, RenderType.getCutout())
								|| RenderTypeLookup.canRenderInLayer(state, RenderType.getCutoutMipped())) { // 排除半透明方块
							modeldata.put(pos, new ModelData(state)); // 加入modeldata中
							flags[0] = DataType.MODIFY;
						} else { // 半透明方块（或其他？）
							translucentdata.put(pos, new TranslucentData(state)); // 加入translucentdata中
							flags[1] = DataType.MODIFY;
						}
					}
					IFluidState fluid = world.getFluidState(pos); //获取的fluidstate为新
					if (!fluid.isEmpty()) { // 是否为流体
						fluiddata.put(pos, new FluidData
								(fluid, getFluidHeights(world, pos, fluid.getFluid()), fluid.getFlow(world, pos))); // 加入fluiddata中
							flags[2] = DataType.MODIFY;
					}
				}
		}
				//fluid notify neighbors fix
			BlockPos[] poses = new BlockPos[] {pos.east(), pos.west(), pos.south(), pos.north(),
					pos.east().south(), pos.east().north(), pos.west().south(), pos.west().north()};
			for(BlockPos fluidpos : poses) {	
				if(fluiddata.containsKey(fluidpos))	{	
				FluidData data = fluiddata.get(fluidpos);
				Vector4f vector4f = getFluidHeights(world, fluidpos, data.state.getFluid());
				Vec3d vec3d = data.state.getFlow(world, fluidpos);
				if(!vector4f.equals(data.height)||!vec3d.equals(data.vec)) {
					data.height = vector4f;
					data.vec = vec3d;
					//markDirty();
					CompoundNBT nbt = new CompoundNBT();
					writeNBT(nbt, new DataType[] 
							{DataType.PASS, DataType.PASS, DataType.MODIFY, DataType.PASS}, fluidpos);
					NetworkLoader.diyTileUpdate(nbt, this);
					}	
				}
			}
			return flags;
	}
	
	
	// Server
	// 精细修改ter数据
	private DataType[] generateRenderData(BlockPos pos, TileEntity tile, boolean add) {
			if(add) {
				terdata.put(pos, new TERData(tile));
			} else {
				terdata.remove(pos);
			}
			return new DataType[] {DataType.PASS, DataType.PASS, DataType.PASS, DataType.fromBoolean(add)};
	}
	
	//see TERHelper, client可能无法读取渲染数据，需在server获取后发送
	private Vector4f getFluidHeights(IBlockReader reader, BlockPos pos, Fluid fluidIn) {
		return new Vector4f (
				getFluidHeight(reader, pos, fluidIn),
				getFluidHeight(reader, pos.south(), fluidIn),
				getFluidHeight(reader, pos.east().south(), fluidIn),
				getFluidHeight(reader, pos.east(), fluidIn)
		);
	}
	
	//see TERHelper, client可能无法读取渲染数据，需在server获取后发送
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
	
	// Server
	// 从模型数据中生成全部AABB
	private void generateBoundingBox() {
		float sx = (float) 1 / (renderpos2.getX() - renderpos1.getX() + 1);
		float sy = (float) 1 / (renderpos2.getY() - renderpos1.getY() + 1);
		float sz = (float) 1 / (renderpos2.getZ() - renderpos1.getZ() + 1);
		List<AxisAlignedBB> aabbs = new ArrayList<AxisAlignedBB>();
		aabbs.addAll(AABBHelper.generateAABBs(renderpos1, modeldata.keySet(), world, sx, sy, sz));
		aabbs.addAll(AABBHelper.generateAABBs(renderpos1, translucentdata.keySet(), world, sx, sy, sz));
		aabbs.addAll(AABBHelper.generateAABBs(renderpos1, fluiddata.keySet(), world, sx, sy, sz));
		aabbs.addAll(AABBHelper.generateAABBs(renderpos1, terdata.keySet(), world, sx, sy, sz));
		shape = AABBHelper.fromAABBList(aabbs);
	}
	
	/*
	 * 从modelposes中获取quads以供渲染 client
	 * Pre：matrix，modelposes在客户端生成，已确保这些分模型坐标对应的分模型均有静态模型
	 * Post：分模型的静态模型的quads被正确缩放（考虑matrix）整合为renderquads，以供IBakedModel渲染
	 */
	private void getQuadsFromPos() {
		renderquads.clear();
		renderquads1.clear(); 
		if (!modeldata.isEmpty()) { // 如果modelposes不为空
			float sx = (float) 1 / (renderpos2.getX() - renderpos1.getX() + 1);
			float sy = (float) 1 / (renderpos2.getY() - renderpos1.getY() + 1);
			float sz = (float) 1 / (renderpos2.getZ() - renderpos1.getZ() + 1);
			/*
			 * 计算分模型的默认缩放比例，与matrix无关（如：matrix为单位矩阵，而每个分方块模型仍需缩放，平移，以使总模型为1*1*1的方块模型）
			 * 该比例对每个分模型相同，无需重复计算
			 */
			modeldata.forEach((renderpos, data) -> { // 遍历分模型坐标
				renderquads1.put(renderpos, new ArrayList<BakedQuad>());
				BlockState renderstate = data.state;
				IBakedModel bakedmodel = Minecraft.getInstance().getBlockRendererDispatcher()
						.getModelForState(renderstate); // 根据BlockState获取对应的IBakeModel
				Random random = new Random(); // 随机数，在分模型IBakedModel的getQuads中传入（实现某些模型的随机效果）
				IModelData md = ModelDataManager.getModelData(world, renderpos);
				float tx = renderpos.getX() - renderpos1.getX();
				float ty = renderpos.getY() - renderpos1.getY();
				float tz = renderpos.getZ() - renderpos1.getZ();
				// 计算分模型的默认平移比例，每个分模型不同，需根据分模型坐标分别计算
				Matrix4fHelper matrix0 = new Matrix4fHelper(matrix); // 克隆一个matrix以便修改
				matrix0.mul(Matrix4fHelper.makeScale(sx, sy, sz)); // 缩放
				matrix0.mul(Matrix4fHelper.makeTranslate(tx, ty, tz)); // 平移（先缩放再平移，平移距离会相应缩放）
				for (Direction side : Direction.values()) { // 每个方向显示的quads
					renderquads1.get(renderpos).addAll
					(QuadHelper.generateQuads(bakedmodel.getQuads(renderstate, side, random, md),
							matrix0, renderstate, world, renderpos)); // 生成quads
				}
				// 永远显示的quads
				renderquads1.get(renderpos).addAll
				(QuadHelper.generateQuads(bakedmodel.getQuads(renderstate, null, random, md), matrix0,
						renderstate, world, renderpos)); // 生成quads
			});
		}
		renderquads1.values().forEach(quads->renderquads.addAll(quads));
	}
	

	public void getQuadsFromPos(BlockPos updatepos) {
		renderquads.clear();
		renderquads1.remove(updatepos);
		if(modeldata.containsKey(updatepos)) {
		renderquads1.put(updatepos, new ArrayList<BakedQuad>());
		float sx = (float) 1 / (renderpos2.getX() - renderpos1.getX() + 1);
		float sy = (float) 1 / (renderpos2.getY() - renderpos1.getY() + 1);
		float sz = (float) 1 / (renderpos2.getZ() - renderpos1.getZ() + 1);
		BlockState renderstate = modeldata.get(updatepos).state;
		IBakedModel bakedmodel = Minecraft.getInstance().getBlockRendererDispatcher()
				.getModelForState(renderstate); // 根据BlockState获取对应的IBakeModel
		Random random = new Random(); // 随机数，在分模型IBakedModel的getQuads中传入（实现某些模型的随机效果）
		IModelData md = ModelDataManager.getModelData(world, updatepos);
		float tx = updatepos.getX() - renderpos1.getX();
		float ty = updatepos.getY() - renderpos1.getY();
		float tz = updatepos.getZ() - renderpos1.getZ();
		// 计算分模型的默认平移比例，每个分模型不同，需根据分模型坐标分别计算
		Matrix4fHelper matrix0 = new Matrix4fHelper(matrix); // 克隆一个matrix以便修改
		matrix0.mul(Matrix4fHelper.makeScale(sx, sy, sz)); // 缩放
		matrix0.mul(Matrix4fHelper.makeTranslate(tx, ty, tz)); // 平移（先缩放再平移，平移距离会相应缩放）
		for (Direction side : Direction.values()) { // 每个方向显示的quads
			renderquads1.get(updatepos).addAll
			(QuadHelper.generateQuads(bakedmodel.getQuads(renderstate, side, random, md),
					matrix0, renderstate, world, updatepos)); // 生成quads
		}
		// 永远显示的quads
		renderquads1.get(updatepos).addAll
		(QuadHelper.generateQuads(bakedmodel.getQuads(renderstate, null, random, md), matrix0,
				renderstate, world, updatepos)); // 生成quads
		}
		renderquads1.values().forEach(quads->renderquads.addAll(quads));
	}
	
	@Override
	public void remove() {
		if(!world.isRemote) {
			ModelWorldSavedData.get(world).removePos(getPos());
		}
	      super.remove();
	}

	// 无穷渲染距离，可能缩放比例很大
	@Override
	@OnlyIn(Dist.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	// 无穷渲染距离，可能缩放比例很大
	@Override
	@OnlyIn(Dist.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	public final class ModelData {
		public final BlockState state;

		public ModelData(BlockState state) {
			this.state = state;
		}

		public CompoundNBT write(BlockPos pos) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.put("BlockState", NBTHelper.writeBlockState(state));
			nbt.put("BlockPos", NBTHelper.writeBlockPos(pos));
			return nbt;
		}

		public ModelData(CompoundNBT nbt) {
			state = NBTHelper.readBlockState(nbt.getCompound("BlockState"));
		}

	}

	public final class TranslucentData {
		public final BlockState state;

		public TranslucentData(BlockState state) {
			this.state = state;
		}

		public CompoundNBT write(BlockPos pos) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.put("BlockState", NBTHelper.writeBlockState(state));
			nbt.put("BlockPos", NBTHelper.writeBlockPos(pos));
			return nbt;
		}

		public TranslucentData(CompoundNBT nbt) {
			state = NBTHelper.readBlockState(nbt.getCompound("BlockState"));
		}
		
	}

	public final class TERData {
		public TileEntity te; //test

		public TERData(TileEntity te) {
			this.te = te;
		}

		public CompoundNBT write(BlockPos pos) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.put("TileEntity", NBTHelper.writeTileEntity(te));
			nbt.put("BlockPos", NBTHelper.writeBlockPos(pos));
			return nbt;
		}

		public TERData(CompoundNBT nbt) {
			te = NBTHelper.readTileEntity(nbt.getCompound("TileEntity"), world);
			//te = te.getWorld().getTileEntity(te.getPos()); //test
		}
	}

	public final class FluidData {
		public final IFluidState state;
		public Vector4f height;
		public  Vec3d vec;

		public FluidData(IFluidState state, Vector4f height, Vec3d vec) {
			this.state = state;
			this.height = height;
			this.vec = vec;
		}

		public CompoundNBT write(BlockPos pos) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.put("FluidState", NBTHelper.writeFluid(state));
			nbt.put("Height", NBTHelper.writeVec4f(height));
			nbt.put("Vec", NBTHelper.writeVec3d(vec));
			nbt.put("BlockPos", NBTHelper.writeBlockPos(pos));
			return nbt;
		}

		public FluidData(CompoundNBT nbt) {
			state = NBTHelper.readFluid(nbt.getCompound("FluidState"));
			height = NBTHelper.readVec4f(nbt.getCompound("Height"));
			vec = NBTHelper.readVec3d(nbt.getCompound("Vec"));
		}
	}
	
	public enum DataType {
		REMOVE(0), MODIFY(1), PASS(2);
		
		public int type;
		
		private DataType(int type) {
			this.type = type;
		}
		
		public boolean shouldRemove() {
			return !(type == 2);
		}
		
		public boolean shouldAdd() {
			return type == 1;
		}
		
		public static DataType fromInt(int type) {
			switch(type) {
			case 0:
				return REMOVE;
			case 1:
				return MODIFY;
			default:
				return PASS;
			}
		}
		
		public static DataType fromBoolean(boolean add) {
			return add ? MODIFY:REMOVE;
		}
	}

}
