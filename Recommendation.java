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

	public static int KNEIGHBOUR = 10; // the neighbour number of every item/user
	public static int COLUMNCOUNT; // the whole number of items
	public static int PREFROWCOUNT; // the whole number of users
	private double[][] result;
	private int[][] preference;
	
	public Recommendation(int topN, String sourcepath, String resultpath, int COLUMNCOUNT, int PREFROWCOUNT) {
		Recommendation.COLUMNCOUNT = COLUMNCOUNT;
		Recommendation.PREFROWCOUNT = PREFROWCOUNT;

		preference = readFile(PREFROWCOUNT,COLUMNCOUNT, sourcepath);
		int[][] trans_pref = transpose_matrix(preference);
		int[] isZero = itemIsZero(preference); // 某个商品是否被打分过
		double[][] similarityMatrix = produceSimilarityMatrix_double(isZero,trans_pref);
		computeScore(resultpath,preference,similarityMatrix,COLUMNCOUNT,PREFROWCOUNT);
		
	}

/**
 * compute the final score of every item for users after recommendation 
 * @param resultpath
 * @param preference
 * @param similarityMatrix
 * @param colCount
 * @param rowCount
 */
	private void computeScore(String resultpath,int[][] preference,double[][] similarityMatrix,int colCount,int rowCount) {
		result = new double[rowCount][colCount];
		
		File file = new File(resultpath); // 存放数组数据的文件
		FileWriter out = null;
		try {
			out = new FileWriter(file);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		List<Integer> neighborSerial = new ArrayList<Integer>();
		for (int j = 0; j < colCount; j++) {
			neighborSerial.clear();
			neighborSerial = findKNeighbors(j,similarityMatrix);
			for (int i = 0; i < rowCount; i++) {
				if (preference[i][j] == 0) { // 给没有评分的预测评分
					double similaritySum = 0;
					double sum = 0;
					double score = 0;
					for (int m = 0; m < neighborSerial.size(); m++) {
						sum = sum + similarityMatrix[j][neighborSerial.get(m)] * preference[i][neighborSerial.get(m)];
						similaritySum = similaritySum + similarityMatrix[j][neighborSerial.get(m)];
					}
					if (sum != 0) {
						score = sum / similaritySum;
					} else {
						score = 0;
					}
					
					result[i][j] = score;

					try {
						out.write((i + 1) + "\t" + (j + 1) + "\t" + score
								+ "\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
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
		File file = new File(string); 
		FileWriter out = null;
		try {
			out = new FileWriter(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
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
		}
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

	/**
	 * find that if the item has been rated.
	 * 
	 * @param preference
	 * @return rated:0; unrated:1
	 * 
	 */
	public int[] itemIsZero(int[][] preference) {
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

/**
 * produce Similarity Matrix(double)
 * @param isZero
 * @param trans_pref
 * @return 
 */
	private double[][] produceSimilarityMatrix_double(int[] isZero,int[][] trans_pref) {
		int matrixSize = trans_pref.length;
		double[][] similarityMatrix = new double[matrixSize][matrixSize];
		
		for (int i = 0; i < matrixSize; i++) {
			for (int j = 0; j <= i; j++) {
				if (i == j) {
					similarityMatrix[i][j] = similarityMatrix[j][i] = 0;
				} else if ((isZero[i] == 1) || (isZero[j] == 1)) {
					similarityMatrix[i][j] = similarityMatrix[j][i] = 0;
				} else {
					similarityMatrix[i][j] = similarityMatrix[j][i] = computeSimilarity(trans_pref[i], trans_pref[j]);
				}
			}
		}
		return similarityMatrix;
	}

	/**
	 * This method is used to find the nearest K neighbors to the un_scored item
	 * 
	 * @param score
	 * @param i
	 * @param similarityMatrix
	 * @return
	 */
	private List<Integer> findKNeighbors(int i,double[][] similarityMatrix) { // 该方法有三个参数，score表示某一用户对所有项目的评分；i表示某个项目的序号
		List<Integer> neighborSerial = new ArrayList<Integer>();
		double[] similarity = new double[similarityMatrix.length];
		for (int j = 0; j < similarityMatrix.length; j++) {
			similarity[j] = similarityMatrix[j][i];
		}
		double[] temp = new double[similarity.length];
		for (int j = 0; j < temp.length; j++) {
			temp[j] = similarity[j];
		}
		Arrays.sort(temp);

		for (int m = temp.length - 1; m >= temp.length - KNEIGHBOUR; m--) {
			for (int j = 0; j < similarity.length; j++) {
				if (similarity[j] == temp[m] && j != i) {
					neighborSerial.add(new Integer(j));
					break;
				}
			}
		}
		return neighborSerial;
	}

	/**
	 * This method is used to compute similarity between two items
	 * 
	 * @param item1
	 * @param item2
	 * @return Correlation
	 */
	private double computeSimilarity(int[] item1, int[] item2) {
		List<Double> list1 = new ArrayList<Double>();
		List<Double> list2 = new ArrayList<Double>();
		for (int i = 0; i < item1.length; i++) {
			if (item1[i] != 0 || item2[i] != 0) {
				list1.add(new Double(item1[i]));
				list2.add(new Double(item2[i]));
			}
		}
		return pearsonCorrelation(list1, list2);
	}

	/**
	 * get the transposition of a matrix
	 * 
	 * @param preference a matrix
	 * @return transposition matrix
	 */
	public int[][] transpose_matrix(int[][] preference) {
		int a[][] = new int[preference[0].length][preference.length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				a[i][j] = preference[j][i];
			}
		}
		return a;
	}

	public double[][] transpose_matrix(double[][] preference) {
		double a[][] = new double[preference[0].length][preference.length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				a[i][j] = preference[j][i];
			}
		}
		return a;
	}

	/**
	 * This method is used to read file and store file data into arrays
	 * 
	 * @param rowCount
	 * @param colCount
	 * @param fileName
	 * @return int[][] preference
	 */
	private int[][] readFile(int rowCount,int colCount,String fileName) {
		int[][] preference = new int[rowCount][colCount];

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
			while (br.ready()) {
				line = br.readLine();
				String[] data = line.split("\t");
				if (Double.parseDouble(data[2]) >= 3) {
					preference[Integer.parseInt(data[0]) - 1][Integer.parseInt(data[1]) - 1] = 1;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return preference;
	}

	/**
	 * This method is used to compute cosine Correlation between two items
	 * 
	 * @param a
	 * @param b
	 * @return Correlation
	 */
	private double pearsonCorrelation(List<Double> a, List<Double> b) {
		if (a.size() != b.size()) {
			System.err.println("pearsonCorrelation: The a.size is not equal with b.size! ");
		}
		
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
		double result;
		if (sum_pro != 0) {
			result = sum_dot / sum_pro;
		} else {
			result = 0;
		}
		return result;
	}
	/**
	 * return the result of recommendation
	 * @return double[][] result
	 */
	public double[][] getResult(){
		return result;
	}
	/**
	 * return the train data of recommendation
	 * @return int[][] preference
	 */
	public int[][] getPreference(){
		return preference;
	}


}
