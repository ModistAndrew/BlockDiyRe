package modist.blockdiy.asm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import modist.blockdiy.BlockDiy;
import modist.blockdiy.common.event.CommonEventHandler;
import modist.blockdiy.util.AsmHelper;

public class BlockDiyTransformer implements ITransformer<ClassNode> {

	public static final Logger LOGGER = LogManager.getLogger();

	public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
		LOGGER.info(input.name);
		if(input.name.equals("net/minecraft/world/server/ServerWorld")) {
			input.methods.forEach(method -> {
				if(method.name.equals("notifyBlockUpdate")) {
					InsnList instruc = method.instructions;
					AsmHelper.print(instruc);
					InsnList list1 = new InsnList();
					list1.add(new VarInsnNode(Opcodes.ALOAD, 0));
					list1.add(new VarInsnNode(Opcodes.ALOAD, 1));
					list1.add(new VarInsnNode(Opcodes.ALOAD, 2));
					list1.add(new VarInsnNode(Opcodes.ALOAD, 3));
					list1.add(new VarInsnNode(Opcodes.ILOAD, 4));
					list1.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "modist/blockdiy/common/event/CommonEventHandler", "updateModel", 
"(Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;I)V"));
					instruc.insert(instruc.get(1), list1);
				}
			});
		}
		if(input.name.equals("net/minecraft/world/World")) {
		input.methods.forEach(method -> {
			/*if(method.name.equals("setBlockState")) {
				InsnList instruc = method.instructions;
				if(instruc.size()>10) {
				AsmUtil.print(instruc);
				//26ºó
				InsnList list1 = new InsnList();
				list1.add(new VarInsnNode(Opcodes.ALOAD, 0));
				list1.add(new VarInsnNode(Opcodes.ALOAD, 1));
				list1.add(new VarInsnNode(Opcodes.ALOAD, 2));
				list1.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "modist/blockdiy/common/event/CommonEventHandler", "updateModel", 
						"(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"));
				instruc.insert(instruc.get(26), list1);
				}
			}
			if(method.name.equals("setTileEntity")) {
				InsnList instruc = method.instructions;
				AsmUtil.print(instruc);
				InsnList list1 = new InsnList();
				list1.add(new VarInsnNode(Opcodes.ALOAD, 0));
				list1.add(new VarInsnNode(Opcodes.ALOAD, 1));
				list1.add(new VarInsnNode(Opcodes.ALOAD, 2));
				list1.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "modist/blockdiy/common/event/CommonEventHandler", "updateTile", 
						"(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/tileentity/TileEntity;)V"));
				instruc.insert(instruc.get(11), list1);
			}*/
			if(method.name.equals("addTileEntity")) {
				InsnList instruc = method.instructions;
				AsmHelper.print(instruc);
				InsnList list1 = new InsnList();
				list1.add(new VarInsnNode(Opcodes.ALOAD, 0));
				list1.add(new VarInsnNode(Opcodes.ALOAD, 1));
				list1.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "modist/blockdiy/common/event/CommonEventHandler", "addTile", 
						"(Lnet/minecraft/world/World;Lnet/minecraft/tileentity/TileEntity;)V"));
				instruc.insert(instruc.get(1), list1);
			}
			if(method.name.equals("removeTileEntity")) {
				InsnList instruc = method.instructions;
				AsmHelper.print(instruc);
				InsnList list1 = new InsnList();
				list1.add(new VarInsnNode(Opcodes.ALOAD, 0));
				list1.add(new VarInsnNode(Opcodes.ALOAD, 2));
				list1.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "modist/blockdiy/common/event/CommonEventHandler", "removeTile", 
						"(Lnet/minecraft/world/World;Lnet/minecraft/tileentity/TileEntity;)V"));
				instruc.insert(instruc.get(7), list1);
			}
		});
		}
		if(input.name.equals("net/minecraft/client/multiplayer/ClientChunkProvider$ChunkArray")) {
			//input.fields.forEach((field -> LOGGER.info(field.name)));
			//FieldNode blockDiyFlag = new FieldNode(Opcodes.ACC_PUBLIC, "blockDiyFlag", "Z", null, false);
			//input.fields.add(blockDiyFlag);
			input.methods.forEach(method -> {
			if (method.name.equals("<init>")) {
				InsnList instruc = method.instructions;
				if(instruc.size()>30) {
				AsmHelper.print(instruc);
				InsnList list1 = new InsnList();
				list1.add(new InsnNode(Opcodes.ICONST_1));
				list1.add(new InsnNode(Opcodes.IADD));
				instruc.insert(instruc.get(30), list1);
				}
			}	
			if (method.name.equals("getIndex")) {
				InsnList instruc = method.instructions;
				AsmHelper.print(instruc);
				InsnList list1 = new InsnList();
				list1.add(new VarInsnNode(Opcodes.ILOAD, 1));
				LabelNode label1 = new LabelNode();
				list1.add(new JumpInsnNode(Opcodes.IFNE, label1));
				list1.add(new VarInsnNode(Opcodes.ILOAD, 2));
				list1.add(new JumpInsnNode(Opcodes.IFNE, label1));
				list1.add(new VarInsnNode(Opcodes.ALOAD, 0));
				list1.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/multiplayer/ClientChunkProvider$ChunkArray", "sideLength", "I"));
				list1.add(new VarInsnNode(Opcodes.ALOAD, 0));
				list1.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/multiplayer/ClientChunkProvider$ChunkArray", "sideLength", "I"));
				list1.add(new InsnNode(Opcodes.IMUL));
				list1.add(new InsnNode(Opcodes.IRETURN));
				list1.add(label1);
				list1.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
				instruc.insert(instruc.get(1), list1);
			}	
			if (method.name.equals("inView")) {
				InsnList instruc = method.instructions;
				AsmHelper.print(instruc);
				InsnList list1 = new InsnList();
				list1.add(new VarInsnNode(Opcodes.ISTORE, 3));
				list1.add(new VarInsnNode(Opcodes.ILOAD, 1));
				LabelNode label1 = new LabelNode();
				list1.add(new JumpInsnNode(Opcodes.IFNE, label1));
				list1.add(new VarInsnNode(Opcodes.ILOAD, 2));
				list1.add(new JumpInsnNode(Opcodes.IFNE, label1));
				list1.add(new InsnNode(Opcodes.ICONST_1));
				LabelNode label2 = new LabelNode();
				list1.add(new JumpInsnNode(Opcodes.GOTO, label2));
				list1.add(label1);
				list1.add(new FrameNode(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null));
				list1.add(new InsnNode(Opcodes.ICONST_0));
				list1.add(label2);
				list1.add(new FrameNode(Opcodes.F_SAME1, 0, null, 1, new Object[] {Opcodes.INTEGER}));
				list1.add(new VarInsnNode(Opcodes.ISTORE, 4));
				list1.add(new VarInsnNode(Opcodes.ILOAD, 3));
				LabelNode label3 = new LabelNode();
				list1.add(new JumpInsnNode(Opcodes.IFNE, label3));
				list1.add(new VarInsnNode(Opcodes.ILOAD, 4));
				LabelNode label4 = new LabelNode();
				list1.add(new JumpInsnNode(Opcodes.IFEQ, label4));
				list1.add(label3);
				list1.add(new FrameNode(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null));
				list1.add(new InsnNode(Opcodes.ICONST_1));
				LabelNode label5 = new LabelNode();
				list1.add(new JumpInsnNode(Opcodes.GOTO, label5));
				list1.add(label4);
				list1.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
				list1.add(new InsnNode(Opcodes.ICONST_0));
				list1.add(label5);
				list1.add(new FrameNode(Opcodes.F_SAME1, 0, null, 1, new Object[] {Opcodes.INTEGER}));
				instruc.insert(instruc.get(24), list1);
			}	
		});
	}
		return input;
	}

	public TransformerVoteResult castVote(ITransformerVotingContext context) {
		return TransformerVoteResult.YES;
	}

	public Set<Target> targets() {
		return new HashSet<Target>(
				Arrays.asList(Target.targetClass("net/minecraft/client/multiplayer/ClientChunkProvider$ChunkArray"),
						Target.targetClass("net/minecraft/world/World"), Target.targetClass("net/minecraft/world/server/ServerWorld")));
	}
}