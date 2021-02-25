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
*DiyBlockTileEntity��DiyBlock�ķ���ʵ���࣬����洢ģ������
*��ģ�ͣ�ָdiyblock�����ģ�ͣ��ɶ�����������ģ�ͽ�϶���
*��ģ�ͷ��飺ָ��ģ�����������ķ���
*��ģ�ͣ�ָ��ģ�ͷ����ģ��
*��ģ�����꣺ָ��ģ�ͷ���ľ�������
*/
public class DiyBlockTileEntity extends TileEntity {

	private BlockPos renderpos1; // ��ģ��������x��y��z����С��
	private BlockPos renderpos2; // ��ģ��������x��y��z������
	private Matrix4fHelper matrix = new Matrix4fHelper(); // ��ģ�͵ı任����ƽ�ƣ���ת�����ţ�
	public Map<BlockPos, ModelData> modeldata = new HashMap<BlockPos, ModelData>(); // ���о�̬ģ�͵ķ�ģ��
	public Map<BlockPos, TranslucentData> translucentdata = new HashMap<BlockPos, TranslucentData>(); // ��͸����ģ��
	public Map<BlockPos, TERData> terdata = new HashMap<BlockPos, TERData>(); // ����TER�ķ�ģ��
	public Map<BlockPos, FluidData> fluiddata = new HashMap<BlockPos, FluidData>(); // �����ģ��
	private Map<BlockPos, List<BakedQuad>> renderquads1 = new HashMap<BlockPos, List<BakedQuad>>(); // ����Ⱦ��quads���ж�Ӧblockpos
	private List<BakedQuad> renderquads = new ArrayList<BakedQuad>(); // ����Ⱦ��quads���ṩ��IBakedModel�����Ժ��о�̬ģ�͵ķ�ģ�ͣ�
	public static final ModelProperty<List<BakedQuad>> RENDER_QUADS = new ModelProperty<>();
	public VoxelShape shape = VoxelShapes.empty();
	//public Map<BlockPos, List<AxisAlignedBB>> aabbs = new HashMap<BlockPos, List<AxisAlignedBB>>(); // ������ײ��

	// ���캯������ע���DIY_BLOCK_TILE_ENTITY��
	public DiyBlockTileEntity() {
		super(BlockLoader.DIY_BLOCK_TILE_ENTITY);
	}

	/*
	 * ������ģ�͵���ʼ����ͽ������꣨����������Ϊ�� server Pre������DiyBlockTileEntity
	 * Post��renderpos1��renderpos2�������£����ã�Ϊ�գ�
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
	 * ƽ����ģ�� server Pre������DiyBlockTileEntity Post����ģ�͵�matrix��ƽ��
	 */
	public void translate(float x, float y, float z) {
		matrix.mul(Matrix4fHelper.makeTranslate(x, y, z));
	}

	/*
	 * ������ģ�� server Pre������DiyBlockTileEntity Post����ģ�͵�matrix������
	 */
	public void scale(float x, float y, float z) {
		matrix.mul(Matrix4fHelper.makeScale(x, y, z));
	}

	/*
	 * ��ת��ģ�� server Pre������DiyBlockTileEntity Post����ģ�͵�matrix����ת
	 */
	public void rotate(float x, float y, float z) {
		Quaternion quaternion = new Quaternion(x, y, z, true);
		matrix.mul(quaternion);
	}

	//Server
	//��ģ������(��TileEntity)����ȫ���޸�
	//�������ͻ���
	public void update() {
		this.generateRenderData();
		this.generateBoundingBox();
		//markDirty(); //������ݱ仯�Ա�洢
		CompoundNBT nbt = new CompoundNBT();
		writeNBT(nbt);
		nbt.putBoolean("All", true);
		NetworkLoader.diyTileUpdate(nbt, this);
		//world.notifyBlockUpdate(this.pos, getBlockState(), getBlockState(), 3); //ͬ������ no ��������diyblock
		ModelWorldSavedData.get(world).updateModelNoBoard(world, this.pos, world.getBlockState(this.pos));
	}

	//Server
	//��ģ������(��TileEntity)���о�ϸ�޸�
	//�������ͻ���
	//state:new state
	public void update(BlockPos pos, BlockState state) {
		DataType[] types = this.generateRenderData(pos, state);
		//markDirty(); //������ݱ仯�Ա�洢
		CompoundNBT nbt = new CompoundNBT();
		writeNBT(nbt, types, pos);
		NetworkLoader.diyTileUpdate(nbt, this);
		//world.notifyBlockUpdate(this.pos, getBlockState(), getBlockState(), 3);
		ModelWorldSavedData.get(world).updateModelNoBoard(world, this.pos, world.getBlockState(this.pos));
	}

	//Server
	//��ģ������TileEntity���о�ϸ�޸�
	//�������ͻ���
	public void update(BlockPos pos, TileEntity tile, boolean add) {
		DataType[] types = this.generateRenderData(pos, tile, add);
		//markDirty(); //������ݱ仯�Ա�洢
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

	// ȷ������ģ�ͣ���renderpos1��renderpos2��Ϊnull���Ա�����Ƿ���Ⱦ��ͬʱ��ֹ����nullpointer
	public boolean checkRenderPos() {
		return renderpos1 != null && renderpos2 != null;
	}

	/*
	 * requestModelDataRefresh�����¾�̬ģ�ͣ�ʱ���ã���ȡmodeldata���ڴ�Ϊrenderquads�����ݸ�IBakedModel
	 * client Pre���ڿͻ��ˣ�renderquads�ѱ����ɣ�requestModelDataRefresh������
	 * Post��renderquads��IModelData
	 */
	@Override
	public IModelData getModelData() {
		return new ModelDataMap.Builder().withInitial(RENDER_QUADS, renderquads).build();
	}

	/*@Override
	public SUpdateTileEntityPacket getUpdatePacket() { // notifyBlockUpdateʱ�ӷ�����ռ�Ӧ�÷��͸��ͻ��˵�����
		CompoundNBT compound = new CompoundNBT();
		writeNBT(compound);
		return new SUpdateTileEntityPacket(getPos(), 1, compound);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) { // notifyBlockUpdateʱ�ͻ��˴ӷ���˵����ݰ��ж�ȡ����
		readNBT(pkt.getNbtCompound());
		getQuadsFromPos();
		ModelDataManager.requestModelDataRefresh(this);
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
	}*/

	/*@Override
	public CompoundNBT getUpdateTag()  { // �������ʱ�ӷ�����ռ�Ӧ�÷��͸��ͻ��˵����ݣ�д�����ݰ���
		ModelWorldSavedData.get(world).putPos(getPos(), renderpos1, renderpos2);
		this.generateRenderData();
		CompoundNBT compound = super.getUpdateTag();
		writeNBT(compound);
		return compound;
	}

	@Override
	public void handleUpdateTag(CompoundNBT tag) { // �������ʱ�ͻ��˴ӷ���˵����ݰ��ж�ȡ����
		super.handleUpdateTag(tag);
		readNBT(tag);
		this.getQuadsFromPos();
		ModelDataManager.requestModelDataRefresh(this);
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
	}*/

	@Override
	public void read(CompoundNBT compound) { // ���ݴӴ����ж���
		super.read(compound);
		if (compound.contains("RenderPos1") && compound.contains("RenderPos2")) {
			renderpos1 = NBTHelper.readBlockPos(compound.getCompound("RenderPos1"));
			renderpos2 = NBTHelper.readBlockPos(compound.getCompound("RenderPos2"));
		}
		matrix = Matrix4fHelper.read(compound.getCompound("Matrix"));
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) { // ���ݴ������
		if (checkRenderPos()) { 
			compound.put("RenderPos1", NBTHelper.writeBlockPos(renderpos1));
			compound.put("RenderPos2", NBTHelper.writeBlockPos(renderpos2));
		}
		compound.put("Matrix", Matrix4fHelper.write(matrix));
		super.write(compound);
		return compound;
	}

	public void readNBT(CompoundNBT compound) { // ȫ����ȡ����
		modeldata.clear();
		translucentdata.clear();
		terdata.clear();
		fluiddata.clear();//ע��ͻ����������
		if (compound.contains("RenderPos1") && compound.contains("RenderPos2")) {
			renderpos1 = NBTHelper.readBlockPos(compound.getCompound("RenderPos1"));
			renderpos2 = NBTHelper.readBlockPos(compound.getCompound("RenderPos2"));
				compound.getList("ModelData", 10).forEach(data -> 
				modeldata.put(NBTHelper.readBlockPos(((CompoundNBT)data).getCompound("BlockPos")), new ModelData((CompoundNBT) data)));
				compound.getList("TranslucentData", 10).forEach(data -> 
				translucentdata.put(NBTHelper.readBlockPos(((CompoundNBT)data).getCompound("BlockPos")), new TranslucentData((CompoundNBT) data)));
				compound.getList("TERData", 10).forEach(data -> { // �ж�te�Ƿ���ter
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

	public void writeNBT(CompoundNBT compound) { // ȫ��д�뷽��
		if (checkRenderPos()) { // ��������
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
		// ��������
		compound.put("Matrix", Matrix4fHelper.write(matrix));
	}
	
	//client
	//��ϸ�޸�read����
	//see PacketDiyBlockTileEntity
	//ע��Ҫ����modelrefresh��geetquadsfrompos��
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
		if(types[3].shouldAdd()) { // �ж�te�Ƿ���ter
			TERData ter = new TERData(compound.getCompound("TERData"));
			if (TileEntityRendererDispatcher.instance.getRenderer(ter.te) != null) {
				terdata.put(pos, ter);
			}		
		}
		if(types[0].shouldRemove()) { //�Ƿ���Ҫ�޸�quads
		getQuadsFromPos(pos);
		ModelDataManager.requestModelDataRefresh(this);
		world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 3);
		}
	}	

	//Server
	//��ϸ�޸�write����
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
	// ��renderpos1��renderpos2�����ɷ�ģ������
	private void generateRenderData() {
		modeldata.clear();
		translucentdata.clear();
		terdata.clear();
		fluiddata.clear();
		// ���֮ǰ�����ַ�ģ������
		if (checkRenderPos()) { // �������ģ��
			BlockPos.getAllInBox(renderpos1, renderpos2).forEach(pos -> { // ������ģ������
				BlockPos pos1 = pos.toImmutable();
				if (!world.isAirBlock(pos1)) { // �ǿ���
					BlockState state = world.getBlockState(pos1); // ��ȡ��ģ�ͷ����BlockState�����ж�����
					if (state.getRenderType().equals(BlockRenderType.MODEL)) { // ���о�̬ģ�͵ķ�ģ��
						if (RenderTypeLookup.canRenderInLayer(state, RenderType.getSolid())
								|| RenderTypeLookup.canRenderInLayer(state, RenderType.getCutout())
								|| RenderTypeLookup.canRenderInLayer(state, RenderType.getCutoutMipped())) { // �ų���͸������
							modeldata.put(pos1, new ModelData(state)); // ����modeldata��
						} else { // ��͸������
							translucentdata.put(pos1, new TranslucentData(state)); // ����translucentdata��
						}
					}
					if (state.hasTileEntity()) {// �Ƿ���TER��Serverֻ���ж��Ƿ���te��
						TileEntity tile = world.getTileEntity(pos1);
						if(tile!=null) {
						terdata.put(pos1, new TERData(tile)); // ����terdata��
						}
					}
					IFluidState fluid = world.getFluidState(pos1);
					if (!fluid.isEmpty()) { // �Ƿ�Ϊ����
						fluiddata.put(pos1, new FluidData
								(fluid, getFluidHeights(world, pos1, fluid.getFluid()), fluid.getFlow(world, pos1))); // ����fluiddata��
					}
				}
			});
		}
	}
	
	// Server
	// ��ϸ�޸�renderdata����tileentity
	// state newblockstate
	private DataType[] generateRenderData(BlockPos pos, BlockState state) {
		//flags[]:0 for model, 1 for translucent, 2 for fluid, 3 for ter
		//ter���ؼ��
		DataType[] flags = new DataType[] {DataType.REMOVE, DataType.REMOVE, DataType.REMOVE, DataType.PASS};
		//�����Եֻ����Һ��߶ȸ���
		if(new DiyBlockData(renderpos1, renderpos2).completelyContains(pos)) {
		modeldata.remove(pos);
		translucentdata.remove(pos);
		fluiddata.remove(pos);
				if (!state.isAir(world, pos)) { // �ǿ���
					if (state.getRenderType().equals(BlockRenderType.MODEL)) { // ���о�̬ģ�͵ķ�ģ��
						if (RenderTypeLookup.canRenderInLayer(state, RenderType.getSolid())
								|| RenderTypeLookup.canRenderInLayer(state, RenderType.getCutout())
								|| RenderTypeLookup.canRenderInLayer(state, RenderType.getCutoutMipped())) { // �ų���͸������
							modeldata.put(pos, new ModelData(state)); // ����modeldata��
							flags[0] = DataType.MODIFY;
						} else { // ��͸�����飨����������
							translucentdata.put(pos, new TranslucentData(state)); // ����translucentdata��
							flags[1] = DataType.MODIFY;
						}
					}
					IFluidState fluid = world.getFluidState(pos); //��ȡ��fluidstateΪ��
					if (!fluid.isEmpty()) { // �Ƿ�Ϊ����
						fluiddata.put(pos, new FluidData
								(fluid, getFluidHeights(world, pos, fluid.getFluid()), fluid.getFlow(world, pos))); // ����fluiddata��
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
	// ��ϸ�޸�ter����
	private DataType[] generateRenderData(BlockPos pos, TileEntity tile, boolean add) {
			if(add) {
				terdata.put(pos, new TERData(tile));
			} else {
				terdata.remove(pos);
			}
			return new DataType[] {DataType.PASS, DataType.PASS, DataType.PASS, DataType.fromBoolean(add)};
	}
	
	//see TERHelper, client�����޷���ȡ��Ⱦ���ݣ�����server��ȡ����
	private Vector4f getFluidHeights(IBlockReader reader, BlockPos pos, Fluid fluidIn) {
		return new Vector4f (
				getFluidHeight(reader, pos, fluidIn),
				getFluidHeight(reader, pos.south(), fluidIn),
				getFluidHeight(reader, pos.east().south(), fluidIn),
				getFluidHeight(reader, pos.east(), fluidIn)
		);
	}
	
	//see TERHelper, client�����޷���ȡ��Ⱦ���ݣ�����server��ȡ����
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
	// ��ģ������������ȫ��AABB
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
	 * ��modelposes�л�ȡquads�Թ���Ⱦ client
	 * Pre��matrix��modelposes�ڿͻ������ɣ���ȷ����Щ��ģ�������Ӧ�ķ�ģ�;��о�̬ģ��
	 * Post����ģ�͵ľ�̬ģ�͵�quads����ȷ���ţ�����matrix������Ϊrenderquads���Թ�IBakedModel��Ⱦ
	 */
	private void getQuadsFromPos() {
		renderquads.clear();
		renderquads1.clear(); 
		if (!modeldata.isEmpty()) { // ���modelposes��Ϊ��
			float sx = (float) 1 / (renderpos2.getX() - renderpos1.getX() + 1);
			float sy = (float) 1 / (renderpos2.getY() - renderpos1.getY() + 1);
			float sz = (float) 1 / (renderpos2.getZ() - renderpos1.getZ() + 1);
			/*
			 * �����ģ�͵�Ĭ�����ű�������matrix�޹أ��磺matrixΪ��λ���󣬶�ÿ���ַ���ģ���������ţ�ƽ�ƣ���ʹ��ģ��Ϊ1*1*1�ķ���ģ�ͣ�
			 * �ñ�����ÿ����ģ����ͬ�������ظ�����
			 */
			modeldata.forEach((renderpos, data) -> { // ������ģ������
				renderquads1.put(renderpos, new ArrayList<BakedQuad>());
				BlockState renderstate = data.state;
				IBakedModel bakedmodel = Minecraft.getInstance().getBlockRendererDispatcher()
						.getModelForState(renderstate); // ����BlockState��ȡ��Ӧ��IBakeModel
				Random random = new Random(); // ��������ڷ�ģ��IBakedModel��getQuads�д��루ʵ��ĳЩģ�͵����Ч����
				IModelData md = ModelDataManager.getModelData(world, renderpos);
				float tx = renderpos.getX() - renderpos1.getX();
				float ty = renderpos.getY() - renderpos1.getY();
				float tz = renderpos.getZ() - renderpos1.getZ();
				// �����ģ�͵�Ĭ��ƽ�Ʊ�����ÿ����ģ�Ͳ�ͬ������ݷ�ģ������ֱ����
				Matrix4fHelper matrix0 = new Matrix4fHelper(matrix); // ��¡һ��matrix�Ա��޸�
				matrix0.mul(Matrix4fHelper.makeScale(sx, sy, sz)); // ����
				matrix0.mul(Matrix4fHelper.makeTranslate(tx, ty, tz)); // ƽ�ƣ���������ƽ�ƣ�ƽ�ƾ������Ӧ���ţ�
				for (Direction side : Direction.values()) { // ÿ��������ʾ��quads
					renderquads1.get(renderpos).addAll
					(QuadHelper.generateQuads(bakedmodel.getQuads(renderstate, side, random, md),
							matrix0, renderstate, world, renderpos)); // ����quads
				}
				// ��Զ��ʾ��quads
				renderquads1.get(renderpos).addAll
				(QuadHelper.generateQuads(bakedmodel.getQuads(renderstate, null, random, md), matrix0,
						renderstate, world, renderpos)); // ����quads
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
				.getModelForState(renderstate); // ����BlockState��ȡ��Ӧ��IBakeModel
		Random random = new Random(); // ��������ڷ�ģ��IBakedModel��getQuads�д��루ʵ��ĳЩģ�͵����Ч����
		IModelData md = ModelDataManager.getModelData(world, updatepos);
		float tx = updatepos.getX() - renderpos1.getX();
		float ty = updatepos.getY() - renderpos1.getY();
		float tz = updatepos.getZ() - renderpos1.getZ();
		// �����ģ�͵�Ĭ��ƽ�Ʊ�����ÿ����ģ�Ͳ�ͬ������ݷ�ģ������ֱ����
		Matrix4fHelper matrix0 = new Matrix4fHelper(matrix); // ��¡һ��matrix�Ա��޸�
		matrix0.mul(Matrix4fHelper.makeScale(sx, sy, sz)); // ����
		matrix0.mul(Matrix4fHelper.makeTranslate(tx, ty, tz)); // ƽ�ƣ���������ƽ�ƣ�ƽ�ƾ������Ӧ���ţ�
		for (Direction side : Direction.values()) { // ÿ��������ʾ��quads
			renderquads1.get(updatepos).addAll
			(QuadHelper.generateQuads(bakedmodel.getQuads(renderstate, side, random, md),
					matrix0, renderstate, world, updatepos)); // ����quads
		}
		// ��Զ��ʾ��quads
		renderquads1.get(updatepos).addAll
		(QuadHelper.generateQuads(bakedmodel.getQuads(renderstate, null, random, md), matrix0,
				renderstate, world, updatepos)); // ����quads
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

	// ������Ⱦ���룬�������ű����ܴ�
	@Override
	@OnlyIn(Dist.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	// ������Ⱦ���룬�������ű����ܴ�
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
