
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

import sun.awt.DisplayChangedListener;

public class RecommendationDouble {

	public static final int KNEIGHBOUR = 10;
	public static  int COLUMNCOUNT ; // number of items
	public static  int PREFROWCOUNT;

	//topN 从流行性中选择前N个流行的
	//sourcepath 资源的路径
	//recN 每个user推荐recN个商品
	public void generateRecommendations(int topN, String sourcepath,String resultpath,String listPath,int COLUMNCOUNT,int PREFROWCOUNT) {
		RecommendationDouble.COLUMNCOUNT = COLUMNCOUNT;
		RecommendationDouble.PREFROWCOUNT = PREFROWCOUNT;
		//	String topNString = sourcepath + "0.5Item_Test_" + topN + "top.base";
		
		double[][] preference = readFile(PREFROWCOUNT, sourcepath);
		double[][] trans_pref = transpose_matrix(preference);

/*		double[][] pre_minus_mean = minus_mean(preference,trans_pref);
		double[][] mean_trans_pref = transpose_matrix(pre_minus_mean);*/
		
		int[] isZero = iszero(preference);  //某个商品是否被打分过
		double[][] similarityMatrix = produceSimilarityMatrix_double(isZero,trans_pref);
		wirteFile(preference, sourcepath+"preference.txt");
		wirteFile(trans_pref, sourcepath+"trans_pref.txt");
/*		wirteFile(pre_minus_mean, sourcepath+"pre_minus_mean.txt");
		wirteFile(mean_trans_pref, sourcepath+"mean_trans_pref.txt");*/
		wirteFile(similarityMatrix, sourcepath+"similarityMatrix.txt");

		File file = new File(resultpath); // 存放数组数据的文件
		FileWriter out = null;
		
/*		File file2 = new File(listPath);
		FileWriter out2 = null;*/
		try {
			out = new FileWriter(file);
			/*out2 = new FileWriter(file2);*/
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // 文件写入流

		List<Integer> neighborSerial = new ArrayList<Integer>();
		for (int j = 0; j < COLUMNCOUNT; j++) {
			neighborSerial.clear();
			double max = 0;
			int itemSerial = 0;
			double mean_col = item_means(trans_pref[j]); //每一列的item平均分
			
			for (int i = 0; i < PREFROWCOUNT; i++) {
				if (preference[i][j] == 0) { //给没有评分的预测评分
					
					double similaritySum = 0;
					double sum = 0;
					double score = 0;
					neighborSerial = findKNeighbors(preference[i], j,
							similarityMatrix);
					for (int m = 0; m < neighborSerial.size(); m++) {
						if (preference[i][neighborSerial.get(m)] != 0) {
							sum += similarityMatrix[j][neighborSerial.get(m)]* preference[i][neighborSerial.get(m)];
							similaritySum += similarityMatrix[j][neighborSerial.get(m)];
						}
						
					}
					if (sum != 0) {
						score = sum / similaritySum ;//+ mean_col ;
					} else {
						score = mean_col;
					}
					if (score > max) {
						max = score;
						itemSerial = j;
					}

 				try {
						out.write((i + 1) + "\t" + (j + 1) + "\t" + score
								+ "\n");
/*						out2.write((i + 1) + "\t" + (j + 1) + "\t");
						if (neighborSerial.size()>0) {
							for (int m = 0; m < neighborSerial.size()-1; m++) {
								out2.write(neighborSerial.get(m) + "\t");
							}
							out2.write(neighborSerial.get(neighborSerial.size()-1) + "\n");
						}else {
							out2.write( "\n");
						}*/
						
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
		/*	out2.close();*/
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
/**
 * find that if the item has been rated. 
 * 
 * @param preference
 * @return rated:0; unrated:1
 * 
 */
	public int[] iszero(double[][] preference) {
		int pre_row = preference.length;
		int pre_col = preference[0].length;

		int[] isZero = new int[pre_col];

		for (int i = 0; i < pre_col; i++) {
			double temp = 0;

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
			double[][] trans_pref) {
		double[] item1;
		double[] item2;
/*		item1 = new double[PREFROWCOUNT];
		item2 = new double[PREFROWCOUNT];*/

		double[][] similarityMatrix = new double[COLUMNCOUNT][COLUMNCOUNT];
		for (int i = 0; i < COLUMNCOUNT; i++) {
			for (int j = 0; j <= i; j++) {

				if (i == j) {
					similarityMatrix[i][j] = similarityMatrix[j][i] = 0;
				} else if ((isZero[i] == 1) || (isZero[j] == 1)) {
					similarityMatrix[i][j] = similarityMatrix[j][i] = 0;
				} else {
					// 将两个商品的列元素赋值到两个数组里面
/*					for (int k = 0; k < PREFROWCOUNT; k++) {
						item1[k] = preference[k][i];
						item2[k] = preference[k][j];
					}*/
					// System.out.println("item1"+Arrays.toString(item1)+"item2"+Arrays.toString(item2));
					similarityMatrix[i][j] = similarityMatrix[j][i] = computeSimilarity(trans_pref[i],trans_pref[j]);
					//similarityMatrix[i][j] = similarityMatrix[j][i] = computeSimilarity(item1, item2);
				}
				
/*				 System.out.println("similarityMatrix[" + i + "][" + j + "]  "
				 + similarityMatrix[i][j]);*/
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
	 * This method is used to get the preference which have minused the mean
	 * score
	 * 
	 * @param score
	 * @param i
	 * @param similarityMatrix
	 * @return
	 */
	private double[][] minus_mean(int[][] a,double[][] a_tran) {
		double[][] b = new double[a.length][a[0].length];
		for (int i = 0; i < b[0].length; i++) {
			double mean_col = item_means(a_tran[i]);
			// if (i<10) {
			// System.out.println("the meanrow is " + mean_row);
			// }
			for (int j = 0; j < b.length; j++) {
				if (a[j][i] ==0) {
					b[j][i] = 0;
				}else {
					b[j][i] = a[j][i] - mean_col;
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
	private List<Integer> findKNeighbors(double[] score, int i,
			double[][] similarityMatrix) { // 该方法有三个参数，score表示某一用户对所有项目的评分；i表示某个项目的序号
		List<Integer> neighborSerial = new ArrayList<Integer>();
		double[] similarity = new double[similarityMatrix.length];
		for (int j = 0; j < similarityMatrix.length; j++) {
			if (score[j] != 0.0) {
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
/*		for (int j = 0; j < similarity.length; j++) {
			for (int m = temp.length - 1; m >= temp.length - KNEIGHBOUR; m--) {
				if (similarity[j] == temp[m] && similarity[j] != 0.0)
					neighborSerial.add(new Integer(j));
			}
		}*/
		for (int m = temp.length - 1; m >= temp.length - KNEIGHBOUR; m--) {
			for (int j = 0; j < similarity.length; j++) {
				if (similarity[j] == temp[m] && similarity[j] != 0.0) {
					neighborSerial.add(new Integer(j));
					break;
				}
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
	 * @return Correlation
	 */
	private double computeSimilarity(double[] item1, double[] item2) {
		List<Double> list1 = new ArrayList<Double>();
		List<Double> list2 = new ArrayList<Double>();
		int j = 0;
		for (int i = 0; i < item1.length; i++) {
/*			if (item1[i] != 0 || item2[i] != 0) { //&&
*/				list1.add(new Double(item1[i]));
				list2.add(new Double(item2[i]));
/*			}*/
			j++;
		}
		// System.out.println(list1+"  "+list2);
		return pearsonCorrelation(list1, list2);
	}
	private double computeSimilarity(int[] item1, int[] item2) {
		List<Double> list1 = new ArrayList<Double>();
		List<Double> list2 = new ArrayList<Double>();
		int j = 0;
		for (int i = 0; i < item1.length; i++) {
			if (item1[i] != 0 && item2[i] != 0) { //&&
				list1.add(new Double(item1[i]));
				list2.add(new Double(item2[i]));
			}
			j++;
		}
		// System.out.println(list1+"  "+list2);
		return pearsonCorrelation(list1, list2);
	}
	
	/**
	 * get the transposition of a matrix
	 * @param preference a matrix
	 * @return transposition matrix
	 */
		public int[][] transpose_matrix (int[][] preference){
			int a[][] = new int[preference[0].length][preference.length];
			for(int i=0;i<preference.length;i++){
				for (int j = 0; j < preference[0].length; j++) {
					a[j][i] = preference[i][j];
				}
			}
			return a;
		}
		public double[][] transpose_matrix (double[][] preference){
			double a[][] = new double[preference[0].length][preference.length];
			for(int i=0;i<preference.length;i++){
				for (int j = 0; j < preference[0].length; j++) {
					a[j][i] = preference[i][j];
				}
			}
			return a;
		}

	/**
	 * This method is used to read file and store file data into arrays
	 * 
	 * @param rowCount
	 * @param fileName
	 * @return
	 */
	private double[][] readFile(int rowCount, String fileName) {
		double[][] preference = new double[rowCount][COLUMNCOUNT];
		/*System.out.println(COLUMNCOUNT);*/

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
				String[] data = line.split("::");
				// System.out.println(Arrays.toString(data));
				// if (data[0].equals("917")) {
				// System.out.println(Arrays.toString(data));
				// }
				//System.out.println(data[0]+" "+data[1]+COLUMNCOUNT);
				/*System.out.println((Integer.parseInt(data[0]) - 1) +" "+(Integer.parseInt(data[1]) - 1)+" "+Double.parseDouble(data[2]));*/
				preference[Integer.parseInt(data[0]) - 1][Integer.parseInt(data[1]) - 1] = Double.parseDouble(data[2]);
				
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

	private double[][] readFileDouble(int rowCount, String fileName) {
		double[][] preference = new double[rowCount][COLUMNCOUNT];

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
				String[] data = line.split("::");
				// System.out.println(Arrays.toString(data));
				// if (data[0].equals("917")) {
				// System.out.println(Arrays.toString(data));
				// }
				//System.out.println(data[0]+" "+data[1]+COLUMNCOUNT);
				preference[Integer.parseInt(data[0]) - 1][Integer.parseInt(data[1]) - 1] = Double.parseDouble(data[2]);
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
	 * This method is used to compute item mean score by Gavin Liu uestc
	 * 
	 * @param a
	 * @return mean score
	 */
	private double item_means(double[] a) {
		int count = 0;
		double sum = 0;
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
	 * @return Correlation
	 */
	private double pearsonCorrelation(List<Double> a, List<Double> b) {

		/*
		 * Preson相似度 int num = a.size(); int sum_prefOne = 0; int sum_prefTwo
		 * = 0; int sum_squareOne = 0; int sum_squareTwo = 0; int sum_product =
		 * 0; for (int i = 0; i < num; i++) { sum_prefOne += a.get(i);
		 * sum_prefTwo += b.get(i); sum_squareOne += Math.pow(a.get(i), 2);
		 * sum_squareTwo += Math.pow(b.get(i), 2); sum_product += a.get(i) *
		 * b.get(i); } double sum = num * sum_product - sum_prefOne *
		 * sum_prefTwo; double den = Math.sqrt((num * sum_squareOne -
		 * Math.pow(sum_squareOne, 2)) * (num * sum_squareTwo -
		 * Math.pow(sum_squareTwo, 2))); double result = sum / den;
		 */

		// 余弦相似度

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
		if (sum_pro != 0) {
			result = sum_dot / sum_pro;
		} else {
			result = 0;
		}
		return result;
	}

	public RecommendationDouble(int n, String sourcepath,String resultpath,String listPath,int COLUMNCOUNT,int PREFROWCOUNT) {
		generateRecommendations(n, sourcepath,resultpath,listPath,COLUMNCOUNT,PREFROWCOUNT);
	}
	/*
	 * public static void main(String[] args) { Recommendation application = new
	 * Recommendation(); application.generateRecommendations(); }
	 */
}
