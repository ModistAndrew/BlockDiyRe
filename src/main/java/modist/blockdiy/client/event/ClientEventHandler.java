package modist.blockdiy.client.event;

import modist.blockdiy.BlockDiy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlockDiy.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEventHandler {

	/*@SubscribeEvent
	public static void diyBlockHitParticle(final ClickInputEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (event.isAttack() && event.getKeyBinding().equals(mc.gameSettings.keyBindAttack)
				&& event.getHand().equals(Hand.MAIN_HAND) && mc.objectMouseOver != null && mc.objectMouseOver.getType() == RayTraceResult.Type.BLOCK) {
			BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) mc.objectMouseOver;
			BlockPos pos = blockraytraceresult.getPos();
			World world = mc.world;
			if (world.getBlockState(pos).getBlock().equals(BlockLoader.DIY_BLOCK)) {
				Direction direction = blockraytraceresult.getFace();
				if (mc.playerController.onPlayerDamageBlock(pos, direction)) {
					ParticleHelper.addBlockHitEffects(pos, blockraytraceresult, world);
					mc.player.swingArm(Hand.MAIN_HAND);
				}
				event.setCanceled(true);
				event.setSwingHand(false);
			}
		}
	}*/

	/*@SubscribeEvent
	public static void test(final ClickInputEvent event) {
		Minecraft mc = Minecraft.getInstance();
		BlockDiy.LOGGER.info(mc.objectMouseOver.getType());
		if (event.isAttack() && event.getKeyBinding().equals(mc.gameSettings.keyBindAttack)
				&& event.getHand().equals(Hand.MAIN_HAND)) {
			getMouseOver(1.0F);
			BlockDiy.LOGGER.info(mc.objectMouseOver.getType());
			if(mc.objectMouseOver!=null&&mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY) {
			mc.playerController.attackEntity(mc.player, ((EntityRayTraceResult)mc.objectMouseOver).getEntity());
			NetworkLoader.INSTANCE.sendToServer(new PacketTest(((EntityRayTraceResult)mc.objectMouseOver).getEntity()));
			}
			mc.gameRenderer.getMouseOver(1.0F);
		}
	}
	
	public static void getMouseOver(float partialTicks) {
		  Minecraft mc = Minecraft.getInstance();
	      Entity entity = mc.getRenderViewEntity();
	      if (entity != null) {
	         if (mc.world != null) {
	            mc.getProfiler().startSection("pick");
	            mc.pointedEntity = null;
	            double d0 = 1024D;
	            mc.objectMouseOver = entity.pick(d0, partialTicks, false);
	            Vec3d vec3d = entity.getEyePosition(partialTicks);
	            boolean flag = false;
	            double d1 = d0;
	            if (mc.playerController.extendedReach()) {
	               d1 = 1024D;
	               d0 = d1;
	            } else {
	               if (d0 > 3.0D) {
	                  flag = true;
	               }
	            }

	            d1 = d1 * d1;
	            if (mc.objectMouseOver != null) {
	               d1 = mc.objectMouseOver.getHitVec().squareDistanceTo(vec3d);
	            }

	            Vec3d vec3d1 = entity.getLook(1.0F);
	            Vec3d vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
	            AxisAlignedBB axisalignedbb = entity.getBoundingBox().expand(vec3d1.scale(d0)).grow(1.0D, 1.0D, 1.0D);
	            EntityRayTraceResult entityraytraceresult = ProjectileHelper.rayTraceEntities(entity, vec3d, vec3d2, axisalignedbb, (p_215312_0_) -> {
	               return !p_215312_0_.isSpectator() && p_215312_0_.canBeCollidedWith();
	            }, d1);
	            if (entityraytraceresult != null) {
	               Entity entity1 = entityraytraceresult.getEntity();
	               Vec3d vec3d3 = entityraytraceresult.getHitVec();
	               double d2 = vec3d.squareDistanceTo(vec3d3);
	               if (flag && d2 > 9.0D) {
	                  mc.objectMouseOver = BlockRayTraceResult.createMiss(vec3d3, Direction.getFacingFromVector(vec3d1.x, vec3d1.y, vec3d1.z), new BlockPos(vec3d3));
	               } else if (d2 < d1 || mc.objectMouseOver == null) {
	                  mc.objectMouseOver = entityraytraceresult;
	                  if (entity1 instanceof LivingEntity || entity1 instanceof ItemFrameEntity) {
	                     mc.pointedEntity = entity1;
	                  }
	               }
	            }

	            mc.getProfiler().endSection();
	         }
	      }
	      BlockDiy.LOGGER.info("test"+mc.objectMouseOver.getType());
	   }*/

}
