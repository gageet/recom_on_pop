package pku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

class GetNumber {
	double num;
	Integer index;

	public GetNumber(Integer index, double a[]) {
		num = a[index];
		this.index = index;
	}
}

class ExchangeComparator implements Comparator<GetNumber> {
	@Override
	public int compare(GetNumber num1, GetNumber num2) {
		if (num1.num > num2.num) {
			return 1;
		} else if (num1.num == num2.num) {
			return 0;
		} else {
			return -1;
		}
	}
}

public class Accuracy {
	public static final int COLUMNCOUNT = 1682; // number of items
	public static final int PREFROWCOUNT = 943;
	public static final int TESTROWCOUNT = 20000;
	public double rmse;
	public double mae;
	public double precision;

	private static double[][] readMatrix(String string) {
		double[][] readmatr = new double[PREFROWCOUNT][COLUMNCOUNT];
		for (int i = 0; i < PREFROWCOUNT; i++) {
			for (int j = 0; j < COLUMNCOUNT; j++) {
				readmatr[i][j] = 0.0;
			}
		}

		try {
			File file = new File(string);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			int i = 0;
			while (br.ready()) {
				line = br.readLine();
				String[] data = line.split("\t");
				// System.out.println(Double.parseDouble(data[2]));
				readmatr[Integer.parseInt(data[0]) - 1][Integer
						.parseInt(data[1]) - 1] = Double.parseDouble(data[2]);

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readmatr;

	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * double[][] minematrix = new double[PREFROWCOUNT][COLUMNCOUNT]; double[][]
	 * testmatrix = new double[PREFROWCOUNT][COLUMNCOUNT]; minematrix =
	 * readMatrix("d:/result_all.txt"); testmatrix = readMatrix(
	 * "E:/doc/lab/dataset/recommending/movielens-100k/0.5Item_Test_4pre.base");
	 * 
	 * double count = 0; double sum = 0; double sum_rmse = 0; double acc = 0;
	 * double acc_rmse = 0; for (int i = 0; i < PREFROWCOUNT; i++) { for (int j
	 * = 0; j < COLUMNCOUNT; j++) {
	 * 
	 * //System.out.println(testmatrix[i][j]+" "+minematrix[i][j]); // if
	 * ((testmatrix[i][j]!=0.0)&&(minematrix[i][j]!=0.0)) { if
	 * ((testmatrix[i][j]!=0.0)&&(minematrix[i][j]!=0.0)) { sum +=
	 * Math.abs(testmatrix[i][j] - minematrix[i][j]); sum_rmse += Math.pow(
	 * Math.abs(testmatrix[i][j] - minematrix[i][j]), 2); count = count + 1;
	 * 
	 * } } } System.out.println(count); acc = sum / count; acc_rmse =
	 * Math.sqrt(sum_rmse / count); System.out.println("MAE：" + acc);
	 * System.out.println("RMSE:" + acc_rmse); mae = acc; rmse = acc_rmse;
	 * 
	 * }
	 */
	public Accuracy(String judgepath, String resultpath, int listSize) {
		double[][] minematrix = new double[PREFROWCOUNT][COLUMNCOUNT];
		double[][] testmatrix = new double[PREFROWCOUNT][COLUMNCOUNT];
		minematrix = readMatrix(resultpath);
		testmatrix = readMatrix(judgepath);

		int count = 0;
		double sum = 0;
		double sum_rmse = 0;
		double acc = 0;
		double acc_rmse = 0;

		double preNum = 0;
		double prePer = 0;

		int calnum = 0;

		GetNumber arr4sort[] = new GetNumber[COLUMNCOUNT];

		for (int i = 0; i < PREFROWCOUNT; i++) {
			for (Integer j = 0; j < COLUMNCOUNT; j++) {

				//计算mae和rmse的sum
				if ((testmatrix[i][j] != 0.0) && (minematrix[i][j] != 0.0)) {
					sum += Math.abs(testmatrix[i][j] - minematrix[i][j]);
					sum_rmse += Math.pow(
							Math.abs(testmatrix[i][j] - minematrix[i][j]), 2);
					count = count + 1;

				}
				//计算precision：对排序数组进行赋值
				arr4sort[j] = new GetNumber(j, minematrix[i]);

			}

			if (count == 0) {
			} else {
				calnum = calnum + 1;
				acc = sum / count + acc; // 累加mae
				acc_rmse = Math.sqrt(sum_rmse / count) + acc_rmse;// 累加rmse

				Arrays.sort(arr4sort, new ExchangeComparator());//对排序数组进行排序

				for (int j = listSize; j > 0; j--) {
					int add[] = new int[listSize];// 用以记录最大十个数的地址
					add[j - 1] = arr4sort[COLUMNCOUNT - j].index;
					if (testmatrix[i][add[j - 1]] != 0.0) {
						preNum = preNum + 1;
					}
	
				}

			}
			prePer = preNum / listSize + prePer;//准确率的sum
			

			count = 0;
			sum = 0;
			sum_rmse = 0;
			preNum = 0;
		}
		mae = acc / calnum;
		rmse = acc_rmse / calnum;
		precision = prePer / calnum;
		System.out.println("calnum:"+calnum);
		System.out.println("MAE：" + mae);
		System.out.println("RMSE:" + rmse);
		System.out.println("precision:" + precision);

	}
}
