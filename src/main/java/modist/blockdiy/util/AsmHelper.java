package modist.blockdiy.util;

import java.lang.reflect.Field;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import modist.blockdiy.BlockDiy;

public class AsmHelper {

	public static void print(InsnList instruc) {
		AbstractInsnNode[] array = instruc.toArray();
		
		for (int i = 0; i < array.length; i++) {
			AbstractInsnNode ins = array[i];
			BlockDiy.LOGGER.info(ins.getClass().getName() + ";");
			BlockDiy.LOGGER.info(ins.getOpcode() + ";");
			if (ins instanceof LineNumberNode) {
				BlockDiy.LOGGER.info(((LineNumberNode) ins).line + ";");
			}
			if (ins instanceof TypeInsnNode) {
				BlockDiy.LOGGER.info(((TypeInsnNode) ins).desc + ";");
			}
			if (ins instanceof VarInsnNode) {
				BlockDiy.LOGGER.info(((VarInsnNode) ins).var + ";");
			}
			if (ins instanceof FieldInsnNode) {
				BlockDiy.LOGGER.info(((FieldInsnNode) ins).desc + ";");
				BlockDiy.LOGGER.info(((FieldInsnNode) ins).name + ";");
				BlockDiy.LOGGER.info(((FieldInsnNode) ins).owner + ";");
			}
			if (ins instanceof MethodInsnNode) {
				BlockDiy.LOGGER.info(((MethodInsnNode) ins).desc + ";");
				BlockDiy.LOGGER.info(((MethodInsnNode) ins).name + ";");
				BlockDiy.LOGGER.info(((MethodInsnNode) ins).owner + ";");
			}
			if (ins instanceof JumpInsnNode) {
				BlockDiy.LOGGER.info(getLine(((JumpInsnNode) ins).label) + ";");
			}
			if (ins instanceof LabelNode) {
				BlockDiy.LOGGER.info(getLine((LabelNode) ins) + ";");
			}
			BlockDiy.LOGGER.info(i);
			BlockDiy.LOGGER.info("\r\n");
		}
		;
	}

	public static String getOpcode(int i) {
		StringBuilder str = new StringBuilder();
		for (Field field : Opcodes.class.getFields()) {
			try {
				if (field.getInt(Opcodes.class) == i) {
					str.append(field.getName() + ";");
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return str.toString();
	}

	public static int getLine(LabelNode label) {
		if (label.getNext() instanceof LineNumberNode) {
			return ((LineNumberNode) label.getNext()).line;
		}
		return -1;
	}

}
