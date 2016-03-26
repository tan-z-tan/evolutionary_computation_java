package arrayHandle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import random.RandomManager;

public class ArrayProcessor
{
	public static double Σ(double[] array)
	{
		double sum = 0;
		for (int i = 0; i < array.length; i++)
			sum += array[i];

		return sum;
	}

	public static double Σ(Number[] array)
	{
		double sum = 0;
		for (int i = 0; i < array.length; i++)
			sum += (Double) array[i];

		return sum;
	}

	public static double Σ(List<Double> list)
	{
		double sum = 0;
		for (int i = 0; i < list.size(); i++)
			sum += list.get(i);

		return sum;
	}

	/** returns average value. returns 0 if array is empty */
	public static double getAverage(double[] array)
	{
		double sum = Σ(array);

		if (sum == 0 || array.length == 0)
			return 0;
		return sum / array.length;
	}
	/** returns average value. returns 0 if array is empty */
	public static double getAverage(double[][] array)
	{
		int count = 0;
		List<Double> lineSumList = new ArrayList<Double>();
		for( int i = 0; i < array.length; i++ ) {
			double lineSum = Σ(array[i]);
			lineSumList.add(lineSum);
			count += array[i].length;
		}
		double sum = Σ(lineSumList);
		
		if (sum == 0 || array.length == 0)
			return 0;
		return sum / count;
	}

	/** returns average value. returns 0 if array is empty */
	public static double getAverage(Number[] array)
	{
		double sum = Σ(array);

		if (sum == 0 || array.length == 0)
			return 0;
		return sum / array.length;
	}

	/**
	 * scales each value in array by specified scale value. The absolutely max
	 * value in array should be scale or -scale
	 */
	public static void scaling(double[] array, double scale)
	{
		double absMax = getMax(array);
		double min = getMin(array);
		if (absMax < Math.abs(min))
			absMax = Math.abs(min);
		if (absMax == 0)
			return;
		double ratio = scale / absMax;

		for (int i = 0; i < array.length; i++)
			array[i] = array[i] * ratio;
	}

	/**
	 * scales each value in array by specified scale value. The absolutely max
	 * value in array should be bottom to top
	 */
	public static void scaling(double[] array, double bottom, double top)
	{
		double max = getMax(array);
		double min = getMin(array);
		if (bottom > top) // swap
		{
			double tmp = bottom;
			bottom = top;
			top = tmp;
		}

		for (int i = 0; i < array.length; i++)
			array[i] = bottom + ((array[i] - min) / (max - min)) * (top - bottom);
	}

	/** for all element of $array, adds $value */
	public static void plusScalar(double[] array, double value)
	{
		for (int i = 0; i < array.length; i++)
			array[i] = array[i] + value;
	}

	/** for all element of $array, multiples $value */
	public static void multipleScalar(double[] array, double value)
	{
		for (int i = 0; i < array.length; i++)
			array[i] = array[i] * value;
	}

	/** return maximum value in array */
	public static double getMax(double[] array)
	{
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < array.length; i++)
			if (max < array[i])
				max = array[i];
		return max;
	}
	
	/** return maximum value in array */
	public static double getMax(double[][] matrix)
	{
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if (max < matrix[i][j])
					max = matrix[i][j];
			}
		}
		return max;
	}

	/** return index of maximum value in array */
	public static int getMaxIndex(double[] array)
	{
		double max = Double.NEGATIVE_INFINITY;
		int maxIndex = 0;
		for (int i = 0; i < array.length; i++)
			if (max < array[i]) {
				max = array[i];
				maxIndex = i;
			}
		return maxIndex;
	}

	/** return minimum value in array */
	public static double getMin(double[] array)
	{
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < array.length; i++)
			if (min > array[i])
				min = array[i];
		return min;
	}
	
	/** return index of maximum value in array */
	public static int getMinIndex(double[] array)
	{
		double min = Double.POSITIVE_INFINITY;
		int minIndex = 0;
		for (int i = 0; i < array.length; i++)
			if (min > array[i]) {
				min = array[i];
				minIndex = i;
			}
		return minIndex;
	}
	/**
	 * returns MSE(Mean Squared Error) value, if size of sequence is different
	 * returns -1
	 */
	public static double MSE(double[] dataA, double[] dataB)
	{
		if (dataA.length != dataB.length)
			return -1;
		double result = 0;
		for (int i = 0; i < dataA.length; i++)
		{
			result += Math.pow(dataA[i] - dataB[i], 2) / dataA.length;
		}
		return result;
	}

	// TODO I don't know correctly mean normalization
	public static void normalization(double[] array)
	{
		double sum = Σ(array);

		if (sum == 0 || array.length == 0)
			return;

		for (int i = 0; i < array.length; i++)
			array[i] = array[i] / sum;
	}

	public static void normalization(Number[] array)
	{
		double sum = Σ(array);

		if (sum == 0 || array.length == 0)
			return;

		for (int i = 0; i < array.length; i++)
			array[i] = (Double) array[i] / sum;
	}

	public static void normalization(List<Double> list)
	{
		double sum = Σ(list);

		if (sum == 0 || list.size() == 0)
			return;
		
		for (int i = 0; i < list.size(); i++)
			list.set(i, (Double) list.get(i) / sum);
	}

	/**
	 * returns String that contains all data in array. it is likely toString()
	 * method
	 */
	public static String toString(double[] array)
	{
		String str = "[ ";
		for (int i = 0; i < array.length; i++)
			str += String.valueOf(array[i]) + " ";
		return str + "]";
	}

	/**
	 * returns String that contains all data in array. it is likely toString()
	 * method
	 */
	public static String toString(boolean[] array)
	{
		String str = "[ ";
		for (int i = 0; i < array.length; i++)
			if (array[i])
				str += "1" + "";
			else
				str += "0" + "";
		return str + "]";
	}

	/**
	 * returns String that contains all data in array. it is likely toString()
	 * method
	 */
	public static String toString(String format, int[] array)
	{
		String str = "[ ";
		for (int i = 0; i < array.length; i++)
			str += String.format(format, array[i]) + " ";
		return str + "]";
	}

	/**
	 * returns String that contains all data in array. it is likely toString()
	 * method
	 */
	public static String toString(String format, double[] array)
	{
		String str = "[ ";
		for (int i = 0; i < array.length; i++)
			str += String.format(format, array[i]) + " ";
		return str + "]";
	}
	
	public static double[] getDoubleArray(String str)
	{
		List<Double> flagList = new ArrayList<Double>(str.length());
		str = str.replace("[", "").replace("]", "");
		
		if( str.length() == 0 ) {
			return new double[0];
		}
		
		String[] flags = str.split(",\\s");
		double[] doubleArray = new double[flags.length];
		int i = 0;
		for (String flag : flags)
		{
			doubleArray[i++] = Double.valueOf(flag);
		}
		
		return doubleArray;
	}
	
	public static List<Double> getDoubleList(String str)
	{
		List<Double> flagList = new ArrayList<Double>(str.length());
		if (str.startsWith("["))
		{
			str = str.substring(1);
		}
		if (str.endsWith("]"))
		{
			str = str.substring(0, str.length() - 2);
		}
		
		String[] flags = str.split(",\\s");
		for (String flag : flags)
		{
			flagList.add(Double.valueOf(flag));
		}
		return flagList;
	}
	
	public static List<Integer> getIntegerList(String str)
	{
		List<Integer> flagList = new ArrayList<Integer>(str.length());
		if (str.startsWith("["))
		{
			str = str.substring(1);
		}
		if (str.endsWith("]"))
		{
			str = str.substring(0, str.length() - 2);
		}

		String[] flags = str.split(",\\s");
		for (String flag : flags)
		{
			flagList.add(Integer.valueOf(flag));
		}
		return flagList;
	}

	public static List<Integer> asList(int[] subDiffList)
	{
		List<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < subDiffList.length; i++)
		{
			list.add(subDiffList[i]);
		}
		return list;
	}
	
	public static List<Integer> asList(Integer[] subDiffList)
	{
		List<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < subDiffList.length; i++)
		{
			list.add(subDiffList[i]);
		}
		return list;
	}
	
	public static List<Double> asList(double[] subDiffList)
	{
		List<Double> list = new ArrayList<Double>();
		for(int i = 0; i < subDiffList.length; i++)
		{
			list.add(subDiffList[i]);
		}
		return list;
	}
	
	public static double[] arrayCast(Double[] array)
	{
		double[] newArray = new double[array.length];
		for (int i = 0; i < array.length; i++)
		{
			newArray[i] = array[i];
		}
		return newArray;
	}

	public static boolean[] arrayCast(Boolean[] array)
	{
		boolean[] newArray = new boolean[array.length];
		for (int i = 0; i < array.length; i++)
		{
			newArray[i] = array[i];
		}
		return newArray;
	}

	public static boolean[] arrayCastToBoolean(Object[] array)
	{
		boolean[] newArray = new boolean[array.length];
		for (int i = 0; i < array.length; i++)
		{
			newArray[i] = (Boolean) array[i];
		}
		return newArray;
	}

	public static double[] arrayCastToDouble(Object[] array)
	{
		double[] newArray = new double[array.length];
		for (int i = 0; i < array.length; i++)
		{
			newArray[i] = (Double) array[i];
		}
		return newArray;
	}

	public static String format_number(double[] array, int floatingNum)
	{
		return Arrays.toString(array).replaceAll("(?<=\\.\\d{1," + floatingNum + "}+)\\d+", "");
	}

	public static String format_number(List<Double> array, int floatingNum)
	{
		return array.toString().replaceAll("(?<=\\.\\d{1," + floatingNum + "}+)\\d+", "");
	}

	/** ルーレット選択する */
	public static int getRouletteSelection(List<Double> assignmentPrior)
	{
		double rand = RandomManager.getRandom();
		double sum = 0;
		for (int i = 0; i < assignmentPrior.size(); i++)
		{
			sum += assignmentPrior.get(i);
			if (sum >= rand)
			{
				return i;
			}
		}
		return 0;
	}

	// ----- main method -----
	public static void main(String args[])
	{
		double[] testData = { 50, 20, 40, -30, 0, -440 };
		System.out.println(toString(testData));
		scaling(testData, 0, 1);
		System.out.println(toString(testData));

		normalization(testData);
		System.out.println(Arrays.toString(testData));
	}
}
