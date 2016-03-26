package random;

//性質の良い擬似乱数を用いた乱数を得るクラス
//
//【使い方】
//
//import MT19937 ; 
//double uniformRandomNumber  = MT19937.uniform() ; // 0-1の一様乱数
//double GaussianRandomNumber = MT19937.Gaussian(0,1) ; // 中心値０、標準偏差１の正規分布
//                                                  // 第1パラメータ：中心値μ
//                                                  // 第2パラメータ：標準偏差σ
/*
 * MT19937.java
 * Copyright (C) 1998-1999 Yumae, S.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 * 
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 */

//package jp.gr.java_conf.yumae.randomx;
// import jp.gr.java_conf.yumae.math.YMath;
import java.util.Date;

/**
 * Java porting from a C-program for MT19937, originaly coded by Takuji
 * Nishimura, considering the suggestions by Topher Cooper and Marc Rieffel in
 * July-Aug. 1997.
 * 
 * You can find the original C-program at
 * http://www.math.keio.ac.jp/~matumoto/mt.html
 * 
 * <p>
 * 
 * @version Jun 23, 1999
 * @author Yumae, Shoji
 */

public class MT19937
// implements DoubleSequence
{

	// -------------------------------
	private static MT19937 drand48;

	// -------------------------------クラスロード時に下記が実行されて初期化
	static
	{
		drand48 = new MT19937();
	}

	/**
	 * initializing the array with a NONZERO seed
	 * 
	 * setting initial seeds to mt[N] using the generator Line 25 of Table 1 in
	 * [KNUTH 1981, The Art of Computer Programming Vol. 2 (2nd Ed.), pp102]
	 */
	public MT19937(long seed)
	{
		mt = new long[N]; /* the array for the state vector */
		mt[0] = seed & 0xffffffffL;
		for (mti = 1; mti < N; mti++)
		{
			mt[mti] = (69069 * mt[mti - 1]) & 0xffffffffL;
		}
	}

	/**
	 * a default initial seed is used
	 */
	public MT19937()
	{
		// this(4357);
		Date date = new Date();
		long seed = 0;
		do
		{
			seed = date.getTime();
		} while (seed == 0);
		mt = new long[N]; /* the array for the state vector */
		mt[0] = seed & 0xffffffffL;
		for (mti = 1; mti < N; mti++)
		{
			mt[mti] = (69069 * mt[mti - 1]) & 0xffffffffL;
		}
	}

	/**
	 * generating real
	 */
	public static double nextDouble()
	{
		long y;
		long mag01[] = { 0x0L, MATRIX_A };
		/* mag01[x] = x * MATRIX_A for x=0,1 */

		if (mti >= N)
		{
			/* generate N words at one time */
			int kk;
			for (kk = 0; kk < N - M; kk++)
			{
				y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
				mt[kk] = MASK_32BIT(mt[kk + M] ^ (y >>> 1) ^ mag01[(int) (y & 0x1L)]);
			}
			for (; kk < N - 1; kk++)
			{
				y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
				mt[kk] = MASK_32BIT(mt[kk + (M - N)] ^ (y >>> 1)
						^ mag01[(int) (y & 0x1L)]);
			}
			y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
			mt[N - 1] = MASK_32BIT(mt[M - 1] ^ (y >>> 1) ^ mag01[(int) y & 0x1]);
			mti = 0;
		}
		y = mt[mti++];
		y ^= TEMPERING_SHIFT_U(y);
		y = MASK_32BIT(y);
		y ^= TEMPERING_SHIFT_S(y) & TEMPERING_MASK_B;
		y = MASK_32BIT(y);
		y ^= TEMPERING_SHIFT_T(y) & TEMPERING_MASK_C;
		y = MASK_32BIT(y);
		y ^= TEMPERING_SHIFT_L(y);
		y = MASK_32BIT(y);

		return ((double) y / (double) 0xffffffffL);
	}

	/* Period parameters */
	private static final int N = 624;
	private static final int M = 397;
	/* constant vector a */
	private static final long MATRIX_A = 0x9908b0dfL;
	/* most significant w-r bits */
	private static final long UPPER_MASK = 0x80000000L;
	/* least significant r bits */
	private static final long LOWER_MASK = 0x7fffffffL;
	/* Tempering parameters */
	private static final long TEMPERING_MASK_B = 0x9d2c5680L;
	private static final long TEMPERING_MASK_C = 0xefc60000L;

	private static long TEMPERING_SHIFT_U(long y)
	{
		return (y >>> 11);
	}

	private static long TEMPERING_SHIFT_S(long y)
	{
		return MASK_32BIT(y << 7);
	}

	private static long TEMPERING_SHIFT_T(long y)
	{
		return MASK_32BIT(y << 15);
	}

	private static long TEMPERING_SHIFT_L(long y)
	{
		return (y >>> 18);
	}

	private static long MASK_32BIT(long y)
	{
		return (y & 0xffffffff);
	}

	static private long[] mt; /* the array for the state vector */
	static private int mti = N + 1; /* mti==N+1 means mt[N] is not initialized */

	// -------一様分布乱数のクラス
	public static double uniform()
	{
		return (nextDouble());
	}

	// -------正規分布乱数のクラス
	public static double Gaussian(double average, double standardDeviation)
	{
		double r1, r2, s; // 標準正規分布乱数発生のための変数
		// -------------------標準正規分布乱数発生
		double normal = nextDouble() + nextDouble() + nextDouble() + nextDouble()
				+ nextDouble() + nextDouble();
		normal += (nextDouble() + nextDouble() + nextDouble() + nextDouble()
				+ nextDouble() + nextDouble());
		normal -= 6;
		// ---------------------------
		normal *= standardDeviation; // 標準偏差 = standardDeviationの正規分布乱数
		normal += average; // 平均値 = average
		return (normal);
	}

	// --------------------------------------
	public static void main(String args[])
	{
		// MT19937 drand = new MT19937() ;
		System.out.println("Uniform distribution from 0 to 1");
		System.out.println(MT19937.nextDouble());
		System.out.println(MT19937.nextDouble());
		System.out.println(MT19937.nextDouble());
		System.out.println(MT19937.nextDouble());
		System.out.println(MT19937.nextDouble());
		System.out.println(MT19937.nextDouble());
		System.out.println(MT19937.nextDouble());
		System.out.println(MT19937.nextDouble());
		System.out.println(MT19937.nextDouble());
		System.out.println(MT19937.nextDouble());
		System.out.println("Gaussian (normal) distribution N(0,1)");
		System.out.println(MT19937.Gaussian(0, 1));
		System.out.println(MT19937.Gaussian(0, 1));
		System.out.println(MT19937.Gaussian(0, 1));
		System.out.println(MT19937.Gaussian(0, 1));
		System.out.println(MT19937.Gaussian(0, 1));
		System.out.println(MT19937.Gaussian(0, 1));
		System.out.println(MT19937.Gaussian(0, 1));
		System.out.println(MT19937.Gaussian(0, 1));
		System.out.println(MT19937.Gaussian(0, 1));
		System.out.println(MT19937.Gaussian(0, 1));
	}
}
