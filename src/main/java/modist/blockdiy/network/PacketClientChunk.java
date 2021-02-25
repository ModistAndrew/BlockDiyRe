package modist.blockdiy.network;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketClientChunk {

	private int chunkX;
	private int chunkZ;

	public PacketClientChunk(int x, int z) {
		this.chunkX = x;
		this.chunkZ = z;
	}
	
	public PacketClientChunk(PacketBuffer buf) {
		this.chunkX = buf.readInt();
		this.chunkZ = buf.readInt();
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeInt(this.chunkX);
		buf.writeInt(this.chunkZ);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerWorld world = ctx.get().getSender().getServerWorld();
			Method[] methods = ChunkManager.class.getDeclaredMethods();
			for(Method method: methods) {
				if(method.getName()=="setChunkLoadedAtClient") {
					method.setAccessible(true);
					try {
						IPacket<?>[] packet = new IPacket<?>[] {null, null};
						method.invoke(world.getChunkProvider().chunkManager,
								ctx.get().getSender(), new ChunkPos(chunkX,chunkZ), packet,
								world.getChunk(chunkX, chunkZ).isEmpty(), true);
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}

	

}
