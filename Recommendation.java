
package pku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Recommendation {

	public static final int KNEIGHBOUR = 20;
	public static final int COLUMNCOUNT = 1682; // number of items
	public static final int PREFROWCOUNT = 943;
	public static final int TESTROWCOUNT = 20000;

	//topN 从流行性中选择前N个流行的
	//sourcepath 资源的路径
	//recN 每个user推荐recN个商品
	public void generateRecommendations(int topN, String sourcepath,String resultpath) {
		String topNString = sourcepath + "0.5Item_Test_" + topN + "top.base";

		int[][] preference = readFile(PREFROWCOUNT, topNString);
		//wirteFile(preference, "d:/pre.txt");写入
		double[][] pre_minus_mean = minus_mean(preference);
		//wirteFile(pre_minus_mean, "d:/pre_mean.txt");写入

		// int[][] test = readFile(TESTROWCOUNT, "u1.test");

		// double[][] similarityMatrix = produceSimilarityMatrix(preference);

		int[] isZero = iszero(preference);  //某个商品是否被打分过
		double[][] similarityMatrix = produceSimilarityMatrix_double(isZero,preference, pre_minus_mean);
		//wirteFile(similarityMatrix, "d:/sMatrix.txt");写入
		// int[][] test = readFile(TESTROWCOUNT, "u1.test");
		// for (int i = 0; i < similarityMatrix.length; i++) {
		// for (int j = 0; j < similarityMatrix[0].length; j++) {
		// System.out.print(similarityMatrix[i][j]+" ");
		// }
		// System.out.println();
		// }

		File file = new File(resultpath); // 存放数组数据的文件
		FileWriter out = null;
		try {
			out = new FileWriter(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // 文件写入流

		List<Integer> neighborSerial = new ArrayList<Integer>();
		for (int i = 0; i < PREFROWCOUNT; i++) {
			neighborSerial.clear();
			double max = 0;
			int itemSerial = 0;
			double mean_row = user_means(preference[i]);
			for (int j = 0; j < COLUMNCOUNT; j++) {
				if (preference[i][j] == 0) { //给没有评分的预测评分
					double similaritySum = 0;
					double sum = 0;
					double score = 0;
					neighborSerial = findKNeighbors(preference[i], j,
							similarityMatrix );
					for (int m = 0; m < neighborSerial.size(); m++) {
						sum += similarityMatrix[j][neighborSerial.get(m)]* preference[i][neighborSerial.get(m)];
						similaritySum += similarityMatrix[j][neighborSerial.get(m)];
					}
					if (sum != 0) {
						score = sum / similaritySum;
					} else {
						score = mean_row;
					}
					if (score > max) {
						max = score;
						itemSerial = j;
					}

 				try {
						out.write((i + 1) + "\t" + (j + 1) + "\t" + score
								+ "\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					

				}/*else{//将训练集中有评分值的置零
					preference[i][j] = 0;
				}*/
				//只输出前N个预测结果
				
			}
			// System.out.println("The book recommended for user "+i+" is: "+bookName[itemSerial]+" score: "+max);
		}
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done");

	}

	private void wirteFile(double[][] preference, String string) {
		File file = new File(string); // 存放数组数据的文件

		FileWriter out = null;
		try {
			out = new FileWriter(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // 文件写入流

		// 将数组中的数据写入到文件中。每行各数据之间TAB间隔
		for (int i = 0; i < preference.length; i++) {
			for (int j = 0; j < preference[0].length; j++) {
				try {
					out.write(preference[i][j] + "\t");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				out.write("\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	private void wirteFile(int[][] preference, String string) {
		File file = new File(string); // 存放数组数据的文件

		FileWriter out = null;
		try {
			out = new FileWriter(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // 文件写入流

		// 将数组中的数据写入到文件中。每行各数据之间TAB间隔
		for (int i = 0; i < preference.length; i++) {
			for (int j = 0; j < preference[0].length; j++) {
				try {
					out.write(preference[i][j] + "\t");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				out.write("\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public int[] iszero(int[][] preference) {
		int pre_row = preference.length;
		int pre_col = preference[0].length;

		int[] isZero = new int[pre_col];

		for (int i = 0; i < pre_col; i++) {
			int temp = 0;

			for (int j = 0; j < pre_row; j++) {
				temp = temp + preference[j][i];
			}
			if (temp == 0) {
				isZero[i] = 1;
			} else {
				isZero[i] = 0;
			}

		}
		return isZero;
	}

	private double[][] produceSimilarityMatrix_double(int[] isZero,
			int[][] preference, double[][] pre_minus_mean) {
		double[] item1;
		double[] item2;
		item1 = new double[PREFROWCOUNT];
		item2 = new double[PREFROWCOUNT];

		double[][] similarityMatrix = new double[COLUMNCOUNT][COLUMNCOUNT];
		for (int i = 0; i < COLUMNCOUNT; i++) {
			for (int j = 0; j < COLUMNCOUNT; j++) {

				if (i == j) {
					similarityMatrix[i][j] = 0;
				} else if ((isZero[i] == 1) || (isZero[j] == 1)) {
					similarityMatrix[i][j] = 0;
				} else {
					// 将两个商品的列元素赋值到两个数组里面
					for (int k = 0; k < PREFROWCOUNT; k++) {
						item1[k] = pre_minus_mean[k][i];
						item2[k] = pre_minus_mean[k][j];
					}
					// System.out.println("item1"+Arrays.toString(item1)+"item2"+Arrays.toString(item2));
					similarityMatrix[i][j] = computeSimilarity(item1, item2);
					//similarityMatrix[i][j] = similarityMatrix[j][i] = computeSimilarity(item1, item2);

				}
				// System.out.println("similarityMatrix[" + i + "][" + j + "]  "
				// + similarityMatrix[i][j]);
			}
		}
		// for (int i = 0; i < similarityMatrix.length; i++) {
		// for (int j = 0; j < similarityMatrix[0].length; j++) {
		// System.out.print(similarityMatrix[i][j]);
		// }
		// System.out.println();
		// }
		// System.out.println("**********");
		return similarityMatrix;
	}

	/**
	 * This method is used to get the preference which have been minus the mean
	 * score
	 * 
	 * @param score
	 * @param i
	 * @param similarityMatrix
	 * @return
	 */
	private double[][] minus_mean(int[][] a) {
		double[][] b = new double[a.length][a[0].length];
		for (int i = 0; i < a.length; i++) {
			double mean_row = user_means(a[i]);
			// if (i<10) {
			// System.out.println("the meanrow is " + mean_row);
			// }
			for (int j = 0; j < a[0].length; j++) {
				if (a[i][j] == 0) {
					b[i][j] = 0;
				} else {
					b[i][j] = a[i][j] - mean_row;
				}
			}
		}
		return b;
	}

	/**
	 * This method is used to find the nearest K neighbors to the un_scored item
	 * 
	 * @param score
	 * @param i
	 * @param similarityMatrix
	 * @return
	 */
	private List<Integer> findKNeighbors(int[] score, int i,
			double[][] similarityMatrix) { // 该方法有三个参数，score表示某一用户对所有项目的评分；i表示某个项目的序号
		List<Integer> neighborSerial = new ArrayList<Integer>();
		double[] similarity = new double[similarityMatrix.length];
		for (int j = 0; j < similarityMatrix.length; j++) {
			if (score[j] != 0) {
				similarity[j] = similarityMatrix[j][i];
			} else {
				similarity[j] = 0;
			}
		}
		double[] temp = new double[similarity.length];
		for (int j = 0; j < temp.length; j++) {
			temp[j] = similarity[j];
		}
		Arrays.sort(temp);
		for (int j = 0; j < similarity.length; j++) {
			for (int m = temp.length - 1; m >= temp.length - KNEIGHBOUR; m--) {
				if (similarity[j] == temp[m] && similarity[j] != 0.0)
					neighborSerial.add(new Integer(j));
			}
		}
		return neighborSerial;
	}

	/**
	 * This method is used to produce similarity matrix among items
	 * 
	 * @param preference
	 * @return
	 */
	// private double[][] produceSimilarityMatrix(int[][] preference) {
	//
	// int[] item1;
	// int[] item2;
	// item1 = new int[PREFROWCOUNT];
	// item2 = new int[PREFROWCOUNT];
	//
	// double[][] similarityMatrix = new double[COLUMNCOUNT][COLUMNCOUNT];
	// for (int i = 0; i < COLUMNCOUNT; i++) {
	// for (int j = 0; j < COLUMNCOUNT; j++) {
	// if (i == j) {
	// similarityMatrix[i][j] = 0;
	// }
	// else {
	// //将两个商品的列元素赋值到两个数组里面
	// for(int k=0;k<PREFROWCOUNT;k++){
	// item1[k]=preference[k][i];
	// item2[k]=preference[k][j];
	// }
	// //
	// System.out.println("item1"+Arrays.toString(item1)+"item2"+Arrays.toString(item2));
	// similarityMatrix[i][j] = similarityMatrix[j][i]=computeSimilarity(item1,
	// item2);
	//
	// }
	// System.out.println("similarityMatrix["+i+"]["+j+"]  "+similarityMatrix[i][j]);
	// }
	// }
	// for (int i = 0; i < similarityMatrix.length; i++) {
	// for (int j = 0; j < similarityMatrix[0].length; j++) {
	// System.out.print(similarityMatrix[i][j]);
	// }
	// System.out.println();
	// }
	// System.out.println("**********");
	// return similarityMatrix;
	// }
	//
	/**
	 * This method is used to compute similarity between two items
	 * 
	 * @param item1
	 * @param item2
	 * @return
	 */
	private double computeSimilarity(double[] item1, double[] item2) {
		List<Double> list1 = new ArrayList<Double>();
		List<Double> list2 = new ArrayList<Double>();
		int j = 0;
		for (int i = 0; i < item1.length; i++) {
			if (item1[i] != 0 && item2[i] != 0) {
				list1.add(new Double(item1[i]));
				list2.add(new Double(item2[i]));
			}
			j++;
		}
		// System.out.println(list1+"  "+list2);
		return pearsonCorrelation(list1, list2);
	}

	/**
	 * This method is used to read file and store file data into arrays
	 * 
	 * @param rowCount
	 * @param fileName
	 * @return
	 */
	private int[][] readFile(int rowCount, String fileName) {
		int[][] preference = new int[rowCount][COLUMNCOUNT];

		for (int i = 0; i < preference.length; i++) {
			for (int j = 0; j < preference[0].length; j++) {
				preference[i][j] = 0;
			}
		}

		try {
			File file = new File(fileName);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			int i = 0;
			while (br.ready()) {
				line = br.readLine();
				String[] data = line.split("\t");
				// System.out.println(Arrays.toString(data));
				// if (data[0].equals("917")) {
				// System.out.println(Arrays.toString(data));
				// }
				preference[Integer.parseInt(data[0]) - 1][Integer
						.parseInt(data[1]) - 1] = Integer.parseInt(data[2]);
				// preference[0][0] = Integer.parseInt(data[2]);
				// System.out.println(Arrays.deepToString(preference));

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return preference;
	}

	/**
	 * This method is used to compute user mean score by Gavin Liu uestc
	 * 
	 * @param
	 * @return
	 */
	private double user_means(int[] a) {
		int count = 0;
		int sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += a[i];
			if (a[i] != 0) {
				count++;
			}
		}

		double means;
		if (count == 0) {
			means = 0;
		} else {
			means = (double) sum / (double) count;
		}

		// System.out.println("sum"+sum+"count"+count+"mean"+means);
		return means;
	}

	/**
	 * This method is used to compute Pearson Correlation between two items
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private double pearsonCorrelation(List<Double> a, List<Double> b) {

		/*
		 * 原代码计算相似度的方式 int num = a.size(); int sum_prefOne = 0; int sum_prefTwo
		 * = 0; int sum_squareOne = 0; int sum_squareTwo = 0; int sum_product =
		 * 0; for (int i = 0; i < num; i++) { sum_prefOne += a.get(i);
		 * sum_prefTwo += b.get(i); sum_squareOne += Math.pow(a.get(i), 2);
		 * sum_squareTwo += Math.pow(b.get(i), 2); sum_product += a.get(i) *
		 * b.get(i); } double sum = num * sum_product - sum_prefOne *
		 * sum_prefTwo; double den = Math.sqrt((num * sum_squareOne -
		 * Math.pow(sum_squareOne, 2)) * (num * sum_squareTwo -
		 * Math.pow(sum_squareTwo, 2))); double result = sum / den;
		 */

		// 现在用的余弦相似度

		int num = a.size();
		double sum_dot = 0;
		double sum_a_sqrt = 0;
		double sum_b_sqrt = 0;
		double sum_pro = 0;
		for (int i = 0; i < num; i++) {
			sum_dot += a.get(i) * b.get(i);
			sum_a_sqrt += a.get(i) * a.get(i);
			sum_b_sqrt += b.get(i) * b.get(i);
		}

		sum_pro = Math.sqrt(sum_a_sqrt) * Math.sqrt(sum_b_sqrt);
		// System.out.println(sum_a_sqrt + " " + sum_b_sqrt + " " + sum_dot +
		// " "
		// + sum_pro);
		double result;
		if (sum_dot != 0) {
			result = sum_dot / sum_pro;
		} else {
			result = 0;
		}
		return result;
	}

	public Recommendation(int n, String sourcepath,String resultpath) {
		generateRecommendations(n, sourcepath,resultpath);
	}
	/*
	 * public static void main(String[] args) { Recommendation application = new
	 * Recommendation(); application.generateRecommendations(); }
	 */
}
