package modist.blockdiy.util;

import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class Matrix3fHelper {
	private float m00;
	private float m01;
	private float m02;
	private float m10;
	private float m11;
	private float m12;
	private float m20;
	private float m21;
	private float m22;

	public Matrix3fHelper() {
		this.m00 = 1.0F;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = 1.0F;
		this.m12 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = 1.0F;
	}

	public Matrix3fHelper(Quaternion quaternionIn) {
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

	public void mul(Matrix3fHelper matrix) {
		float f = this.m00 * matrix.m00 + this.m01 * matrix.m10 + this.m02 * matrix.m20;
		float f1 = this.m00 * matrix.m01 + this.m01 * matrix.m11 + this.m02 * matrix.m21;
		float f2 = this.m00 * matrix.m02 + this.m01 * matrix.m12 + this.m02 * matrix.m22;
		float f3 = this.m10 * matrix.m00 + this.m11 * matrix.m10 + this.m12 * matrix.m20;
		float f4 = this.m10 * matrix.m01 + this.m11 * matrix.m11 + this.m12 * matrix.m21;
		float f5 = this.m10 * matrix.m02 + this.m11 * matrix.m12 + this.m12 * matrix.m22;
		float f6 = this.m20 * matrix.m00 + this.m21 * matrix.m10 + this.m22 * matrix.m20;
		float f7 = this.m20 * matrix.m01 + this.m21 * matrix.m11 + this.m22 * matrix.m21;
		float f8 = this.m20 * matrix.m02 + this.m21 * matrix.m12 + this.m22 * matrix.m22;
		this.m00 = f;
		this.m01 = f1;
		this.m02 = f2;
		this.m10 = f3;
		this.m11 = f4;
		this.m12 = f5;
		this.m20 = f6;
		this.m21 = f7;
		this.m22 = f8;
	}

	public void mul(float value) {
		this.m00 *= value;
		this.m01 *= value;
		this.m02 *= value;
		this.m10 *= value;
		this.m11 *= value;
		this.m12 *= value;
		this.m20 *= value;
		this.m21 *= value;
		this.m22 *= value;
	}

	public static Matrix3fHelper makeScaleMatrix(float x, float y, float z) {
		Matrix3fHelper matrix3f = new Matrix3fHelper();
		matrix3f.m00 = x;
		matrix3f.m11 = y;
		matrix3f.m22 = z;
		return matrix3f;
	}

	public void mul(Quaternion quaternion) {
		this.mul(new Matrix3fHelper(quaternion));
	}

	public static CompoundNBT write(Matrix3fHelper matrix) {
		CompoundNBT tag = new CompoundNBT();
		tag.putFloat("m00", matrix.m00);
		tag.putFloat("m01", matrix.m01);
		tag.putFloat("m02", matrix.m02);
		tag.putFloat("m10", matrix.m10);
		tag.putFloat("m11", matrix.m11);
		tag.putFloat("m12", matrix.m12);
		tag.putFloat("m20", matrix.m20);
		tag.putFloat("m21", matrix.m21);
		tag.putFloat("m22", matrix.m22);
		return tag;
	}

	public static Matrix3fHelper read(CompoundNBT tag) {
		Matrix3fHelper matrix = new Matrix3fHelper();
		matrix.m00 = tag.getFloat("m00");
		matrix.m01 = tag.getFloat("m01");
		matrix.m02 = tag.getFloat("m02");
		matrix.m10 = tag.getFloat("m10");
		matrix.m11 = tag.getFloat("m11");
		matrix.m12 = tag.getFloat("m12");
		matrix.m20 = tag.getFloat("m20");
		matrix.m21 = tag.getFloat("m21");
		matrix.m22 = tag.getFloat("m22");
		return matrix;
	}

	public Vector3f transform(Vector3f vec) {
		float f = vec.getX();
		float f1 = vec.getY();
		float f2 = vec.getZ();
		float x = this.m00 * f + this.m01 * f1 + this.m02 * f2;
		float y = this.m10 * f + this.m11 * f1 + this.m12 * f2;
		float z = this.m20 * f + this.m21 * f1 + this.m22 * f2;
		vec.set(x, y, z);
		vec.normalize();
		return vec;
	}

	public Matrix3f toMatrix3f() {
		return new Matrix3f(new Matrix4f(new float[] {this.m00, this.m01, this.m02, 0F,
				 						 this.m10, this.m11, this.m12, 0F,
				 						 this.m20, this.m21, this.m22, 0F,
				 						 0F, 0F,0F,0F}));
	}

}
