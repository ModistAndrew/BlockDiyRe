package modist.blockdiy.network;

import java.util.function.Supplier;

import modist.blockdiy.common.tileentity.DiyBlockTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.fml.network.NetworkEvent;

//DiyBlockTileEntity精细修改时发包
public class PacketDiyBlockTileEntity {

	//see DiyBlockTileEntity writeNBT(CompoundNBT compound, boolean[] flags, BlockPos updatepos)
	private CompoundNBT nbt;
	//DiyBlockTileEntity坐标
	private BlockPos pos;

	public PacketDiyBlockTileEntity(CompoundNBT nbt,BlockPos pos) {
		this.nbt = nbt;
		this.pos = pos;
	}
	
	public PacketDiyBlockTileEntity(PacketBuffer buf) {
		nbt = buf.readCompoundTag();
		pos = buf.readBlockPos();
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeCompoundTag(nbt);
		buf.writeBlockPos(pos);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			World world = Minecraft.getInstance().world;
			TileEntity tile = world.getTileEntity(pos);
			if(tile instanceof DiyBlockTileEntity) {
				DiyBlockTileEntity tile1 = (DiyBlockTileEntity)tile;
				if(nbt.contains("All")) {
				tile1.readNBT(nbt);
				} else {
				tile1.readNBTPacket(nbt);
				};
			}
		});
		ctx.get().setPacketHandled(true);
	}

	

}
