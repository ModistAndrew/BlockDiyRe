package modist.blockdiy.network;

import modist.blockdiy.BlockDiy;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkLoader {
	
	public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(BlockDiy.MODID, "network"), () -> "1.0", s -> true, s -> true);
       INSTANCE.registerMessage(nextID(),
        		PacketClientChunk.class,
                PacketClientChunk::toBytes,
                PacketClientChunk::new,
                PacketClientChunk::handle);
       INSTANCE.registerMessage(nextID(),
       		PacketDiyBlockTileEntity.class,
               PacketDiyBlockTileEntity::toBytes,
               PacketDiyBlockTileEntity::new,
               PacketDiyBlockTileEntity::handle);
    }
    
    //Server
    //DiyTile精细更改时发包至客户端
    public static void diyTileUpdate(CompoundNBT nbt, TileEntity tile) {
    INSTANCE.send
	(PacketDistributor.TRACKING_CHUNK.with(()->tile.getWorld().getChunkAt(tile.getPos())), 
			new PacketDiyBlockTileEntity(nbt, tile.getPos()));
    }

}
