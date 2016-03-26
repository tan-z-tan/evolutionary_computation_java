package math;

/**
 * Supplemental mathematical functions. 
 * 
 * @author Yoshihiko Hasegawa
 * 
 */

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ExMath {
	/**
	 * Gamma function
	 * 
	 * @param d
	 *            An input value.
	 * @return \Gamma(d).
	 */
	public static double gamma(double d) {
		return Math.exp(ExMath.lnGamma(d));
	}

	/**
	 * Log gamma function.
	 * 
	 * @param d
	 *            An input value.
	 * @return log(\Gamma(d)).
	 */
	/*
	 * 
	 * SSJライブラリからコピーしたもの
	 */
	public static double lnGamma(double d) {
		double ad[] = { 0.52854303698223459D, 0.54987644612141406D,
				0.020739800616136651D, -0.00056916770421543844D,
				2.3245872104001691E-05D, -1.13060758570393E-06D,
				6.0656530989480001E-08D, -3.4628435777000001E-09D,
				2.0624998805999999E-10D, -1.266351116E-11D,
				7.9531006999999997E-13D, -5.0820769999999998E-14D,
				3.29187E-15D, -2.1556E-16D, 1.4240000000000001E-17D,
				-9.4999999999999995E-19D };
		if (d < 0.0D) {
			double d5 = 1.0D - d - Math.floor(1.0D - d);
			return 1.1447298858494002D - lnGamma(1.0D - d)
					- Math.log(Math.sin(3.1415926535897931D * d5));
		}
		if (d > 18D) {
			double d1;
			if (d > 4503599627370496D) {
				d1 = 0.0D;
			} else {
				d1 = 1.0D / (d * d);
			}
			double d3 = ((-(0.00059523809523799999D * d1) + 0.00079365007936510001D)
					* d1 - 0.0027777777777778D)
					* d1 + 0.083333333333332996D;
			d3 = ((d - 0.5D) * Math.log(d) - d) + 0.91893853320467278D + d3 / d;
			return d3;
		}
		double d2;
		double d4;
		if (d > 4D) {
			int k = (int) d;
			d4 = d - k;
			d2 = 1.0D;
			for (int i = 3; i < k; i++) {
				d2 *= d4 + i;
			}

			d2 = Math.log(d2);
		} else {
			if (d <= 0.0D) {
				return 1.7976931348623157E+308D;
			}
			if (d < 3D) {
				int l = (int) d;
				d4 = d - l;
				d2 = 1.0D;
				for (int j = 2; j >= l; j--) {
					d2 *= d4 + j;
				}

				d2 = -Math.log(d2);
			} else {
				d4 = d - 3D;
				d2 = 0.0D;
			}
		}
		d4 = evalCheby(ad, 15, 2D * d4 - 1.0D);
		return d4 + 0.95741869905106269D + d2;
	}

	/**
	 * Chebychev polynomial.
	 * 
	 * @param ad
	 * @param i
	 * @param d
	 * @return
	 */
	/*
	 * SSJライブラリからのコピー
	 */
	public static double evalCheby(double ad[], int i, double d) {
		if (Math.abs(d) > 1.0D) {
			throw new IllegalArgumentException(
					"Chebychev polynomial evaluated at x outside [-1, 1]");
		}
		double d1 = 2D * d;
		double d2 = 0.0D;
		double d3 = 0.0D;
		double d4 = 0.0D;
		for (int j = i; j >= 0; j--) {
			d4 = d3;
			d3 = d2;
			d2 = (d1 * d3 - d4) + ad[j];
		}

		return (d2 - d4) / 2D;
	}

	/**
	 * Calculate digamma function.
	 * 
	 * @param x
	 *            A value to calculate digamma function.
	 * @return A value of digamma function.
	 */
	/*
	 * SSJのライブラリからコピーしたもの
	 */
	public static double digamma(double d) {
		
//		System.out.println(d);
//		if(Double.isNaN(d)) {
//			throw new IllegalArgumentException("Illegal value NaN");
//		}
		
		double ad[][] = {
				{ 13524.999667726346D, 45285.601699547289D,
						45135.168469736665D, 18529.01181858261D,
						3329.1525149406934D, 240.68032474357202D,
						5.1577892000139087D, 0.0062283506918984748D },
				{ 6.9389111753763447E-07D, 19768.574263046736D,
						41255.160835353832D, 29390.287119932684D,
						9081.9666074855177D, 1244.7477785670856D,
						67.429129516378595D, 1.0D } };
		double ad1[][] = {
				{ -2.7281757513152966E-15D, -0.6481571237661965D,
						-4.4861654391801933D, -7.0167722776675863D,
						-2.1294044513101054D },
				{ 7.777885485229616D, 54.611773810321509D, 89.292070048186133D,
						32.270349379114336D, 1.0D } };
		double d1 = 0.0D;
		double d2 = 0.0D;
		double d3 = 0.0D;
		if (d >= 3D) {
			double d4 = 1.0D / (d * d);
			for (int i = 4; i >= 0; i--) {
				d1 = d1 * d4 + ad1[0][i];
				d2 = d2 * d4 + ad1[1][i];
			}

			d3 = (Math.log(d) - 0.5D / d) + d1 / d2;
		} else if (d >= 0.5D) {
			for (int j = 7; j >= 0; j--) {
				d1 = d * d1 + ad[0][j];
				d2 = d * d2 + ad[1][j];
			}

			d3 = (d - 1.4616321449683622D) * (d1 / d2);
		} else {
			try {
				double d5 = 1.0D - d - Math.floor(1.0D - d);
				
				d3 = digamma(1.0D - d) + 3.1415926535897931D
						/ Math.tan(3.1415926535897931D * d5);
			}
			catch(Exception e) {
				System.err.println("d = " + d);
			}
		}
		return d3;
	}

	/*
	 * Logの中身の和を，Logxのまま近似する Log(1 + x)のorder次の項まで計算 途中のDoubleによるOverflowがない
	 * Log(x+y) = logPlus(Log(x),Log(y))
	 * 
	 * logx又はlogyが0の場合の処理をしていなかった ＞logPlus(x,0) = log(e^x + 1) ＞logPlus(0,y) =
	 * log(1 + e^y) 修正した
	 * 
	 */
	public static double logPlus(double logx, double logy) {
		if (Double.NEGATIVE_INFINITY == logx) {
			return logy;
		}
		if (Double.NEGATIVE_INFINITY == logy) {
			return logx;
		}

		if (logx == 0) {
			return logPlusZero(logy);
		}
		if (logy == 0) {
			return logPlusZero(logx);
		}
		if (logx == Double.POSITIVE_INFINITY
				|| logy == Double.POSITIVE_INFINITY) {
			throw new IllegalArgumentException(
					"Argument is positive infinity. ");
		}

		double diff = Math.abs(logx - logy);

		// 二つの値の差によって，アルゴリズム，Orderを自動選択
		if (diff == 0) {
			return Math.log(2) + logx;
		} else if (diff < Math.log(1.1)) {
			return logPlusClose(logx, logy, 2);
		} else if (diff < Math.log(1.6)) {
			return logPlusClose(logx, logy, 6);
		} else if (diff < Math.log(2.0)) {
			return logPlusClose(logx, logy, 11);
		} else if (diff < Math.log(2.5)) {
			return logPlusDistant(logx, logy, 12);
		} else if (diff < Math.log(4.0)) {
			return logPlusDistant(logx, logy, 9);
		} else if (diff < Math.log(10.0)) {
			return logPlusDistant(logx, logy, 6);
		} else if (diff < Math.log(20.0)) {
			return logPlusDistant(logx, logy, 4);
		} else if (diff < Math.log(50.0)) {
			return logPlusDistant(logx, logy, 3);
		} else {
			return logPlusDistant(logx, logy, 2);
		}
	}

	/*
	 * 片方が0のとき あまり使わないから4次まででOKかな チェックOK
	 */
	private static double logPlusZero(double logv) {
		if (logv == 0) {
			return Math.log(2);
		} else if (logv > 0) {
			return logv + Math.exp(-logv) - 1. / 2. * Math.exp(-logv * 2) + 1.
					/ 3. * Math.exp(-logv * 3) - 1. / 4. * Math.exp(-logv * 4);
		} else {
			return Math.exp(logv) - 1. / 2. * Math.exp(logv * 2) + 1. / 3.
					* Math.exp(logv * 3) - 1. / 4. * Math.exp(logv * 4);
		}
	}

	/*
	 * 二つの値が近くないとき
	 */
	private static double logPlusDistant(double logx, double logy, int order) {

		if (logx > logy) {
			double sum = logx;

			for (int i = 1; i <= order; i++) {
				if (i % 2 == 1) {
					sum += 1.0 / i * Math.exp((logy - logx) * i);
				} else {
					sum -= 1.0 / i * Math.exp((logy - logx) * i);
				}
			}

			return sum;
		} else {
			return ExMath.logPlusDistant(logy, logx, order);
		}
	}

	/*
	 * 二つの値が近い場合
	 */
	private static double logPlusClose(double logx, double logy, int order) {
		if (logx > logy) {
			double sum = Math.log(2) + logy;

			for (int i = 1; i <= order; i++) {
				if (i % 2 == 1) {
					sum += 1.0
							/ i
							* Math.pow(
									Math.exp(logx - logy - Math.log(2)) - 0.5,
									i);
				} else {
					sum -= 1.0
							/ i
							* Math.pow(
									Math.exp(logx - logy - Math.log(2)) - 0.5,
									i);
				}
			}

			return sum;
		} else {
			return logPlusClose(logy, logx, order);
		}
	}

	/**
	 * Generate random number between {@code from} to {@code to}.
	 * 
	 * @param from
	 *            Inclusuve range from.
	 * @param to
	 *            Exclusive range to.
	 * @return A random integer.
	 */
	public static int randomInteger(int from, int to) {
		if (to <= from) {
			throw new IllegalArgumentException("Invalid range arguments. ");
		}

		return from + randomInteger(to - from);
	}

	public static int randomInteger(int range) {
		return (int) (range * Math.random());
	}

	public static int randomInteger() {
		return randomInteger(Integer.MAX_VALUE);
	}

	/*
	 * 多変数Hypot チェック完了
	 */
	public static double hypot(double... ds) {

		double hy = 0;

		for (int i = 0; i < ds.length; i++) {
			hy = Math.hypot(hy, ds[i]);
		}

		return hy;
	}

	public static double hypot(List<? extends Number> ds) {

		double hy = 0;

		for (int i = 0; i < ds.size(); i++) {
			hy = Math.hypot(hy, ds.get(i).doubleValue());
		}

		return hy;
	}

	public static int sign(double val) {
		if (val > 0) {
			return 1;
		} else if (val < 0) {
			return -1;
		} else {
			return 0;
		}
	}

	public static int[] decimalToNumeral(long value, int n) {
		if (value != 0) {
			int array_size = (int) logb(value, n) + 1;
			int[] x = new int[array_size];

			long base = 1;
			for (int i = 0; i < array_size - 1; i++) {
				base *= n;
			}

			long tmp = value;
			for (int i = array_size - 1; i >= 0; i--) {
				x[i] = (int) (tmp / base);
				if (i == 0) {
					break;
				}
				tmp = tmp % base;
				base /= n;
			}

			return x;
		} else {
			int[] x = new int[1];
			x[0] = 0;
			return x;
		}
	}

	public static int[] decimalToBinary(long value) {

		int[] temp = decimalToNumeral(value, 2);

		int[] ret = new int[temp.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = temp[i];
		}

		return ret;
	}

	public static long toDecimal(int[] val, int n) {
		long sum = 0;

		for (int i = 0; i < val.length; i++) {
			sum += val[i] * (long) Math.pow(n, i);
		}

		return sum;
	}

	public static long binaryToDecimal(int[] val) {
		return toDecimal(val, 2);
	}

	public static long binaryToDecimal(List<Integer> list) {
		return binaryToDecimal(toArray(list));
	}

	public static double logb(double value, double base) {
		return Math.log(value) / Math.log(base);
	}

	public static int[] grayToBinary(int[] g) {
		int[] b = new int[g.length];

		for (int k = 0; k < b.length; k++) {
			int sum = 0;
			for (int i = k; i < b.length; i++) {
				sum += g[i];
			}
			b[k] = (sum % 2);
		}

		return b;
	}

	public static int[] binaryToGray(int[] b) {
		int[] g = new int[b.length];

		for (int k = 0; k < b.length; k++) {
			if (k == b.length - 1) {
				g[k] = b[k];
			} else {
				g[k] = (b[k + 1] ^ b[k]);
			}
		}
		return g;
	}

	public static int[] decimalToGray(long value) {
		int[] b = decimalToBinary(value);

		return binaryToGray(b);
	}

	/*
	 * グレイコードから10進数への変換
	 */
	public static long grayToDecimal(int[] g) {
		return binaryToDecimal(grayToBinary(g));
	}

	public static long grayToDecimal(List<Integer> list) {
		return grayToDecimal(toArray(list));
	}

	private static int[] toArray(List<Integer> list) {
		int[] temp = new int[list.size()];

		for (int i = 0; i < list.size(); i++) {
			temp[i] = list.get(i);
		}

		return temp;
	}

	public static double max(double... values) {
		double currentMax = -Double.MAX_VALUE;

		for (int i = 0; i < values.length; i++) {
			if (currentMax < values[i]) {
				currentMax = values[i];
			}
		}

		return currentMax;
	}

	public static double min(double... values) {
		double currentMin = Double.MAX_VALUE;

		for (int i = 0; i < values.length; i++) {
			if (currentMin > values[i]) {
				currentMin = values[i];
			}
		}

		return currentMin;
	}

	/*
	 * リストの和
	 */
	public static double getListSum(List<? extends Number> list) {
		double sum = 0;

		for (Number n : list) {
			sum += n.doubleValue();
		}

		return sum;
	}

	public static double getListMean(List<? extends Number> list) {
		return getListSum(list) / list.size();
	}

	/*
	 * 整数かどうか
	 */
	public static boolean isProbablyInteger(double value) {
		int intValue = (int) Math.round(value);

		if (Math.abs(value - intValue) < Math.pow(10, -8)) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * 大体同じ
	 */
	public static boolean approxEquals(double val1, double val2, double error) {

		double sm = Math.min(val1, val2);

		if (Math.abs((val1 - val2) / sm) < error) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * mCn個の選択インスタンスを計算するメソッド 再帰を用いて計算する
	 * 
	 * 相当無駄の多いコード
	 * 
	 * 完了＞チェックOK
	 */
	public static <TYPE> List<List<TYPE>> possibleCombination(
			List<TYPE> targetList, int selectionSize) {
		if (selectionSize < 0) {
			throw new IllegalArgumentException(
					"Selection size must not be negative value");
		} else if (selectionSize > targetList.size()) {
			throw new IllegalArgumentException(
					"Selection size must be smaller than the size of target list");

		}

		if (selectionSize != 0) {
			List<Integer> indexList = new ArrayList<Integer>();

			for (int i = 0; i < targetList.size(); i++) {
				indexList.add(i);
			}

			List<List<Integer>> combinationList = new ArrayList<List<Integer>>();

			iterateCombination(indexList, combinationList, selectionSize);
			List<List<TYPE>> combinationResult = new ArrayList<List<TYPE>>();

			for (List<Integer> list : combinationList) {
				List<TYPE> tmpList = new ArrayList<TYPE>();
				for (Integer i : list) {
					tmpList.add(targetList.get(i));
				}
				combinationResult.add(tmpList);
			}

			return combinationResult;
		} else {
			List<TYPE> emptyList = new ArrayList<TYPE>();
			List<List<TYPE>> emptyListList = new ArrayList<List<TYPE>>();

			emptyListList.add(emptyList);

			return emptyListList;
		}

	}

	/*
	 * ArrayList.clear()の安全性をチェックする必要がある
	 */
	private static void iterateCombination(List<Integer> indexList,
			List<List<Integer>> currentCombination, int selectionSize) {
		// selectionSize == 1の場合は単純に要素を全て加えればよい
		if (selectionSize == 1) {
			for (Integer e : indexList) {
				List<Integer> tmpList = new ArrayList<Integer>();
				tmpList.add(e);
				currentCombination.add(tmpList);
			}
		} else {
			iterateCombination(indexList, currentCombination, selectionSize - 1);

			// System.out.println("Inside method = " + currentCombination);

			List<List<Integer>> tmpNewCurrentCombination = new ArrayList<List<Integer>>();

			for (List<Integer> e : currentCombination) {
				// 追加するインデックスの候補
				List<Integer> tmpList = new ArrayList<Integer>();

				for (Integer targetElement : indexList) {
					if (!e.contains(targetElement)) {
						tmpList.add(targetElement);
					}
				}

				for (Integer newElement : tmpList) {
					List<Integer> tmpNewCombination = new ArrayList<Integer>();

					if (e.get(e.size() - 1) < newElement) {
						// tmpNewCombination.add(newElement);
						tmpNewCombination.addAll(e);
						tmpNewCombination.add(newElement);
						tmpNewCurrentCombination.add(tmpNewCombination);
					}

				}
			}

			currentCombination.clear();

			for (List<Integer> c : tmpNewCurrentCombination) {
				currentCombination.add(c);
			}

		}
	}

	/*
	 * factorial : ２０までの階乗を行うlong bigFactorial：任意の値の階乗 BigIntegerで返す 階乗を計算する
	 * check OK
	 */
	public static BigInteger bigFactorial(int value) {
		if (value < 0) {
			throw new IllegalArgumentException("Negative Argument Exception");
		} else if (value <= 20) {
			return new BigInteger(Long.toString(factorial(value)));
		} else {
			BigInteger prod = new BigInteger("1");

			for (int i = 2; i <= value; i++) {
				prod = prod.multiply(new BigInteger(Integer.toString(i)));
			}

			return prod;
		}
	}

	public static long factorial(int value) {
		if (value < 0) {
			throw new IllegalArgumentException("Negative Argument Exception");
		} else if (value > 20) {
			throw new IllegalArgumentException("Argument is too large");
		} else {
			long prod = 1;

			for (int i = 2; i <= value; i++) {
				prod *= i;
			}

			return prod;
		}
	}

	/*
	 * mCnを計算する max(m-n,n)>20の場合は，計算できない
	 */
	public static long combination(int m, int n) {
		if (m < 0 || n < 0) {
			throw new IllegalArgumentException("Negative Argument Exception");
		} else if (n > m) {
			throw new IllegalArgumentException(
					"The first argument must not be smaller than the second one");
		}

		int sub = m - n;

		if (sub > n) {
			long prod = 1;

			for (int i = 0; i < (m - sub); i++) {
				prod *= (sub + 1 + i);
			}
			return prod / factorial(n);
		} else {
			long prod = 1;

			for (int i = 0; i < (m - n); i++) {
				prod *= (n + 1 + i);
			}
			return prod / factorial(sub);
		}
	}

	/*
	 * 対数の和を計算する \sum_{v = from}^{to}{\log{v}} Check OK
	 */
	public static double logSum(double base, // 底
			int from, // 含む
			int to // 含む
	) {
		if (base <= 0 || from <= 0 || to <= 0) {
			throw new IllegalArgumentException("Negative Argument Exception");
		}

		double sum = 0;

		for (int v = from; v < to + 1; v++) {
			sum += ExMath.logb(v, base);
		}

		return sum;
	}

	/*
	 * n!/m!を計算する チェック完了！ 引数が０の場合も正しい
	 */
	public static double factorialFraction(int bunshi, // 分子
			int bunbo // 分母
	) {
		if (bunshi < 0 || bunbo < 0) {
			throw new IllegalArgumentException("Negative Argument Exception");
		}

		if (bunshi > bunbo) {
			double prod = 1;
			for (int i = 0; i < (bunshi - bunbo); i++) {
				prod *= (bunbo + 1 + i);
			}
			return prod;
		} else if (bunshi < bunbo) {
			double prod = 1;
			for (int i = 0; i < (bunbo - bunshi); i++) {
				prod *= (bunshi + 1 + i);
			}
			return 1 / prod;
		} else {
			return 1;
		}
	}

	/*
	 * すべての場合の数え上げ
	 */
	public static <TYPE> List<List<TYPE>> tuples(
			List<List<TYPE>> cardinalityTable) {
		return ExMath.<TYPE> countLoop(cardinalityTable, null, 0);
	}

	private static <TYPE> List<List<TYPE>> countLoop(
			List<List<TYPE>> allCardinality,
			List<List<TYPE>> currentCardinalInstance, int currentIndex) {
		// System.out.println("currentIndex = " + currentIndex);
		if (currentIndex < allCardinality.size()) {
			List<List<TYPE>> currentAllInstance = new ArrayList<List<TYPE>>();

			if (currentCardinalInstance == null) {

				// この変数の数え上げ
				for (TYPE in : allCardinality.get(currentIndex)) {
					List<TYPE> tmpList = new ArrayList<TYPE>();
					tmpList.add(in);
					currentAllInstance.add(tmpList);
				}
			} else {
				for (List<TYPE> current : currentCardinalInstance) {

					// この変数の数え上げ
					for (TYPE in : allCardinality.get(currentIndex)) {
						List<TYPE> tmpList = new ArrayList<TYPE>();
						tmpList.addAll(current);
						tmpList.add(in);
						currentAllInstance.add(tmpList);
					}
				}
			}

			return countLoop(allCardinality, currentAllInstance, ++currentIndex);
		} else {
			return currentCardinalInstance;
		}
	}

	// メソッドテスト用
	public static void main(String args[]) {
		/*
		 * List<Integer> t = new ArrayList<Integer>();
		 * 
		 * for(int i = 0;i<200;i++) { t.add(i); }
		 */
		// System.out.println(ExMath.<Integer>possibleCombination(Arrays.<Integer>asList(1,2,3,4,5),3));
		// System.out.println(ExMath.<Integer>possibleCombination(t, 3).size());
		// System.out.println(ExMath.combination(200, 3));
		// System.out.println(ExMath.hypot(18,2,3,4,5));
		/*
		 * System.out.println(Math.log(+15 + 10));
		 * System.out.println(ExMath.logPlusClose(Math.log(15), Math.log(10),
		 * 4)); System.out.println(ExMath.logPlusFar(Math.log(15), Math.log(10),
		 * 4));
		 * 
		 * System.out.println(); System.out.println(Math.log(+20 + 10));
		 * System.out.println(ExMath.logPlusClose(Math.log(20), Math.log(10),
		 * 10)); System.out.println(ExMath.logPlusFar(Math.log(20),
		 * Math.log(10), 10));
		 * 
		 * System.out.println(); System.out.println(Math.log(+30 + 10));
		 * System.out.println(ExMath.logPlusClose(Math.log(30), Math.log(10),
		 * 10)); System.out.println(ExMath.logPlusFar(Math.log(30),
		 * Math.log(10), 10));
		 */

		// System.out.println(Math.log(1.0E-100 + 50.01E-100));
		// System.out.println(ExMath.logPlus(Math.log(1.0E-100),
		// Math.log(50.01E-100)));
		// System.out.println(ExMath.digamma(1));
		/*
		 * double logx = -0; double logy = -0;
		 * 
		 * System.out.println(ExMath.logPlus(logx, logy));
		 * System.out.println(Math.log(Math.exp(logx) + Math.exp(logy)));
		 */
		System.out.println(ExMath.gamma(100.0));
	}

}
