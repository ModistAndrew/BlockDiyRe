package modist.blockdiy.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import modist.blockdiy.common.tileentity.DiyBlockTileEntity.DataType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class NBTHelper {

	public static CompoundNBT writeBlockState(BlockState state) {
		return NBTUtil.writeBlockState(state);
	}

	public static BlockState readBlockState(CompoundNBT nbt) {
		return NBTUtil.readBlockState(nbt);
	}

	public static CompoundNBT writeBlockPos(BlockPos pos) {
		return NBTUtil.writeBlockPos(pos);
	}

	public static BlockPos readBlockPos(CompoundNBT nbt) {
		return NBTUtil.readBlockPos(nbt);
	}

	public static CompoundNBT writeTileEntity(TileEntity te) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.put("pos", writeBlockPos(te.getPos()));
		/*nbt.putString("Name", te.getBlockState().getBlock().getRegistryName().toString());
		CompoundNBT nbt1 = new CompoundNBT();
		nbt.put("TileEntity", te.write(nbt1));
		nbt.put("BlockState", NBTUtil.writeBlockState(te.getBlockState()));*/
		return nbt;
	}

	public static TileEntity readTileEntity(CompoundNBT nbt, World world) {
		/*Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("Name")));
		TileEntity te = block.createTileEntity(block.getDefaultState(), world);
		//TODO special tile
		te.read(nbt.getCompound("TileEntity"));
		te.setWorldAndPos(world, te.getPos());
		try { // TODO AccessTransformer
			Field field = TileEntity.class.getDeclaredField("cachedBlockState");
			field.setAccessible(true);
			field.set(te, NBTUtil.readBlockState(nbt.getCompound("BlockState")));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return te;*/
		return world.getTileEntity(readBlockPos(nbt.getCompound("pos")));
	}

	public static CompoundNBT writeFluid(IFluidState state) {
		Fluid fluid = state.getFluid();
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("Name", fluid.getRegistryName().toString());
		CompoundNBT nbt1 = new CompoundNBT();
		fluid.getStateContainer().getProperties().forEach(property->
		nbt1.putString(property.getName(), getNameForValue(property, state.getValues().get(property))));
		nbt.put("Properties", nbt1);
		return nbt;
	}

	public static IFluidState readFluid(CompoundNBT nbt) {
		Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(nbt.getString("Name")));
		IFluidState state = fluid.getDefaultState();
		StateContainer<Fluid, IFluidState> statecontainer = fluid.getStateContainer();
		CompoundNBT properties = nbt.getCompound("Properties");
		properties.keySet().forEach(key -> {
			IProperty<?> iproperty = statecontainer.getProperty(key);
			IStateHolder.withString(state, iproperty, key, "input", properties.getString(key));
		});
		return state;
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Comparable<T>> String getNameForValue(IProperty<T> property, Comparable<?> value) {
		      return property.getName((T)value);
	}
	
	public static CompoundNBT writeVec3d(Vec3d vec) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putDouble("X", vec.x);
		nbt.putDouble("Y", vec.y);
		nbt.putDouble("Z", vec.z);
		return nbt;
	}

	public static Vec3d readVec3d(CompoundNBT nbt) {
		return new Vec3d(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
	}
	
	public static CompoundNBT writeVec4f(Vector4f vec) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putFloat("X", vec.getX());
		nbt.putFloat("Y", vec.getY());
		nbt.putFloat("Z", vec.getZ());
		nbt.putFloat("W", vec.getW());
		return nbt;
	}

	public static Vector4f readVec4f(CompoundNBT nbt) {
		return new Vector4f(nbt.getFloat("X"), nbt.getFloat("Y"), nbt.getFloat("Z"), nbt.getFloat("W"));
	}
	
	public static CompoundNBT writeAABB(AxisAlignedBB aabb) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putDouble("NX", aabb.minX);
		nbt.putDouble("NY", aabb.minY);
		nbt.putDouble("NZ", aabb.minZ);
		nbt.putDouble("XX", aabb.maxX);
		nbt.putDouble("XY", aabb.maxY);
		nbt.putDouble("XZ", aabb.maxZ);
		return nbt;
	}
	
	public static AxisAlignedBB readAABB(CompoundNBT nbt) {
		return new AxisAlignedBB(nbt.getDouble("NX"), nbt.getDouble("NY"), nbt.getDouble("NZ"), 
				nbt.getDouble("XX"), nbt.getDouble("XY"), nbt.getDouble("XZ"));
	}
	
	public static ListNBT writeAABBs(VoxelShape aabbs) {
		ListNBT aabblist = new ListNBT();
		aabbs.toBoundingBoxList().forEach(aabb -> aabblist.add(writeAABB(aabb)));
		return aabblist;
	}

	public static VoxelShape readAABBs(ListNBT nbt) {
		List<AxisAlignedBB> aabbs = new ArrayList<AxisAlignedBB>();
		nbt.forEach(compound -> aabbs.add(readAABB((CompoundNBT)compound)));
		return AABBHelper.fromAABBList(aabbs);
	}
	
	public static CompoundNBT writeTypes(DataType[] types) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("ModelFlag", types[0].type);
		nbt.putInt("TranslucentFlag", types[1].type);
		nbt.putInt("FluidFlag", types[2].type);
		nbt.putInt("TERFlag", types[3].type);
		return nbt;
	}

	public static DataType[] readTypes(CompoundNBT nbt) {
		return new DataType[] {DataType.fromInt(nbt.getInt("ModelFlag")), DataType.fromInt(nbt.getInt("TranslucentFlag")), 
				DataType.fromInt(nbt.getInt("FluidFlag")), DataType.fromInt(nbt.getInt("TERFlag"))};
	}

}
