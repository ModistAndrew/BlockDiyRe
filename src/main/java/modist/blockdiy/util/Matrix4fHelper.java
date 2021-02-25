package modist.blockdiy.util;

import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class Matrix4fHelper {
	private float m00;
	private float m01;
	private float m02;
	private float m03;
	private float m10;
	private float m11;
	private float m12;
	private float m13;
	private float m20;
	private float m21;
	private float m22;
	private float m23;
	private float m30;
	private float m31;
	private float m32;
	private float m33;

	public Matrix4fHelper() {
		this.m00 = 1.0F;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m03 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = 1.0F;
		this.m12 = 0.0F;
		this.m13 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = 1.0F;
		this.m23 = 0.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public Matrix4fHelper(Quaternion quaternionIn) {
		float f = quaternionIn.getX();
		float f1 = quaternionIn.getY();
		float f2 = quaternionIn.getZ();
		float f3 = quaternionIn.getW();
		float f4 = 2.0F * f * f;
		float f5 = 2.0F * f1 * f1;
		float f6 = 2.0F * f2 * f2;
		this.m00 = 1.0F - f5 - f6;
		this.m11 = 1.0F - f6 - f4;
		this.m22 = 1.0F - f4 - f5;
		this.m33 = 1.0F;
		float f7 = f * f1;
		float f8 = f1 * f2;
		float f9 = f2 * f;
		float f10 = f * f3;
		float f11 = f1 * f3;
		float f12 = f2 * f3;
		this.m10 = 2.0F * (f7 + f12);
		this.m01 = 2.0F * (f7 - f12);
		this.m20 = 2.0F * (f9 - f11);
		this.m02 = 2.0F * (f9 + f11);
		this.m21 = 2.0F * (f8 + f10);
		this.m12 = 2.0F * (f8 - f10);
	}
	
	public Matrix4fHelper(Matrix4fHelper matrixIn) {
	      this.m00 = matrixIn.m00;
	      this.m01 = matrixIn.m01;
	      this.m02 = matrixIn.m02;
	      this.m03 = matrixIn.m03;
	      this.m10 = matrixIn.m10;
	      this.m11 = matrixIn.m11;
	      this.m12 = matrixIn.m12;
	      this.m13 = matrixIn.m13;
	      this.m20 = matrixIn.m20;
	      this.m21 = matrixIn.m21;
	      this.m22 = matrixIn.m22;
	      this.m23 = matrixIn.m23;
	      this.m30 = matrixIn.m30;
	      this.m31 = matrixIn.m31;
	      this.m32 = matrixIn.m32;
	      this.m33 = matrixIn.m33;
	}

	public void mul(Matrix4fHelper matrix) {
		float f = this.m00 * matrix.m00 + this.m01 * matrix.m10 + this.m02 * matrix.m20 + this.m03 * matrix.m30;
		float f1 = this.m00 * matrix.m01 + this.m01 * matrix.m11 + this.m02 * matrix.m21 + this.m03 * matrix.m31;
		float f2 = this.m00 * matrix.m02 + this.m01 * matrix.m12 + this.m02 * matrix.m22 + this.m03 * matrix.m32;
		float f3 = this.m00 * matrix.m03 + this.m01 * matrix.m13 + this.m02 * matrix.m23 + this.m03 * matrix.m33;
		float f4 = this.m10 * matrix.m00 + this.m11 * matrix.m10 + this.m12 * matrix.m20 + this.m13 * matrix.m30;
		float f5 = this.m10 * matrix.m01 + this.m11 * matrix.m11 + this.m12 * matrix.m21 + this.m13 * matrix.m31;
		float f6 = this.m10 * matrix.m02 + this.m11 * matrix.m12 + this.m12 * matrix.m22 + this.m13 * matrix.m32;
		float f7 = this.m10 * matrix.m03 + this.m11 * matrix.m13 + this.m12 * matrix.m23 + this.m13 * matrix.m33;
		float f8 = this.m20 * matrix.m00 + this.m21 * matrix.m10 + this.m22 * matrix.m20 + this.m23 * matrix.m30;
		float f9 = this.m20 * matrix.m01 + this.m21 * matrix.m11 + this.m22 * matrix.m21 + this.m23 * matrix.m31;
		float f10 = this.m20 * matrix.m02 + this.m21 * matrix.m12 + this.m22 * matrix.m22 + this.m23 * matrix.m32;
		float f11 = this.m20 * matrix.m03 + this.m21 * matrix.m13 + this.m22 * matrix.m23 + this.m23 * matrix.m33;
		float f12 = this.m30 * matrix.m00 + this.m31 * matrix.m10 + this.m32 * matrix.m20 + this.m33 * matrix.m30;
		float f13 = this.m30 * matrix.m01 + this.m31 * matrix.m11 + this.m32 * matrix.m21 + this.m33 * matrix.m31;
		float f14 = this.m30 * matrix.m02 + this.m31 * matrix.m12 + this.m32 * matrix.m22 + this.m33 * matrix.m32;
		float f15 = this.m30 * matrix.m03 + this.m31 * matrix.m13 + this.m32 * matrix.m23 + this.m33 * matrix.m33;
		this.m00 = f;
		this.m01 = f1;
		this.m02 = f2;
		this.m03 = f3;
		this.m10 = f4;
		this.m11 = f5;
		this.m12 = f6;
		this.m13 = f7;
		this.m20 = f8;
		this.m21 = f9;
		this.m22 = f10;
		this.m23 = f11;
		this.m30 = f12;
		this.m31 = f13;
		this.m32 = f14;
		this.m33 = f15;
	}

	public void mul(float value) {
		this.m00 *= value;
		this.m01 *= value;
		this.m02 *= value;
		this.m03 *= value;
		this.m10 *= value;
		this.m11 *= value;
		this.m12 *= value;
		this.m13 *= value;
		this.m20 *= value;
		this.m21 *= value;
		this.m22 *= value;
		this.m23 *= value;
		this.m30 *= value;
		this.m31 *= value;
		this.m32 *= value;
		this.m33 *= value;
	}

	public void mul(Quaternion quaternion) {
		this.mul(new Matrix4fHelper(quaternion));
	}

	public static Matrix4fHelper makeScale(float x, float y, float z) {
		Matrix4fHelper matrix = new Matrix4fHelper();
		matrix.m00 = x;
		matrix.m11 = y;
		matrix.m22 = z;
		return matrix;
	}

	public static Matrix4fHelper makeTranslate(float x, float y, float z) {
		Matrix4fHelper matrix = new Matrix4fHelper();
		matrix.m03 = x;
		matrix.m13 = y;
		matrix.m23 = z;
		return matrix;
	}

	public static CompoundNBT write(Matrix4fHelper matrix) {
		CompoundNBT tag = new CompoundNBT();
		tag.putFloat("m00", matrix.m00);
		tag.putFloat("m01", matrix.m01);
		tag.putFloat("m02", matrix.m02);
		tag.putFloat("m03", matrix.m03);
		tag.putFloat("m10", matrix.m10);
		tag.putFloat("m11", matrix.m11);
		tag.putFloat("m12", matrix.m12);
		tag.putFloat("m13", matrix.m13);
		tag.putFloat("m20", matrix.m20);
		tag.putFloat("m21", matrix.m21);
		tag.putFloat("m22", matrix.m22);
		tag.putFloat("m23", matrix.m23);
		tag.putFloat("m30", matrix.m30);
		tag.putFloat("m31", matrix.m31);
		tag.putFloat("m32", matrix.m32);
		tag.putFloat("m33", matrix.m33);
		return tag;
	}

	public static Matrix4fHelper read(CompoundNBT tag) {
		Matrix4fHelper matrix = new Matrix4fHelper();
		matrix.m00 = tag.getFloat("m00");
		matrix.m01 = tag.getFloat("m01");
		matrix.m02 = tag.getFloat("m02");
		matrix.m03 = tag.getFloat("m03");
		matrix.m10 = tag.getFloat("m10");
		matrix.m11 = tag.getFloat("m11");
		matrix.m12 = tag.getFloat("m12");
		matrix.m13 = tag.getFloat("m13");
		matrix.m20 = tag.getFloat("m20");
		matrix.m21 = tag.getFloat("m21");
		matrix.m22 = tag.getFloat("m22");
		matrix.m23 = tag.getFloat("m23");
		matrix.m30 = tag.getFloat("m30");
		matrix.m31 = tag.getFloat("m31");
		matrix.m32 = tag.getFloat("m32");
		matrix.m33 = tag.getFloat("m33");
		return matrix;
	}

	public Vector4f transform(Vector4f vec) {
		float f = vec.getX();
		float f1 = vec.getY();
		float f2 = vec.getZ();
		float f3 = vec.getW();
		float x = this.m00 * f + this.m01 * f1 + this.m02 * f2 + this.m03 * f3;
		float y = this.m10 * f + this.m11 * f1 + this.m12 * f2 + this.m13 * f3;
		float z = this.m20 * f + this.m21 * f1 + this.m22 * f2 + this.m23 * f3;
		float w = this.m30 * f + this.m31 * f1 + this.m32 * f2 + this.m33 * f3;
		vec.set(x, y, z, w);
		return vec;
	}

	public Matrix4f toMatrix4f() {
		return new Matrix4f(new float[] {this.m00, this.m01, this.m02, this.m03,
										 this.m10, this.m11, this.m12, this.m13,
										 this.m20, this.m21, this.m22, this.m23,
										 this.m30, this.m31, this.m32, this.m33});
	}

}