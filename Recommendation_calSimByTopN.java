package pku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

class GetNumber2 {
	double num;
	Integer index;

	public GetNumber2(Integer index, double a) {
		num = a;
		this.index = index;
	}
}

class ExchangeComparator2 implements Comparator<GetNumber2> {
	@Override
	public int compare(GetNumber2 num1, GetNumber2 num2) {
		if (num1.num > num2.num) {
			return 1;
		} else if (num1.num == num2.num) {
			return 0;
		} else {
			return -1;
		}
	}
}
public class Recommendation_calSimByTopN {

	public static int KNEIGHBOUR = 10; // the neighbour number of every item/user
	public static int COLUMNCOUNT; // the whole number of items
	public static int PREFROWCOUNT; // the whole number of users
	private double[][] result;
	private int[][] preference;
	
	public Recommendation_calSimByTopN(int topN, String sourcepath, String resultpath, String popPath,int COLUMNCOUNT, int PREFROWCOUNT) {
		Recommendation_calSimByTopN.COLUMNCOUNT = COLUMNCOUNT;
		Recommendation_calSimByTopN.PREFROWCOUNT = PREFROWCOUNT;

		int[] popItems = getPopItems(popPath,topN);
		
		preference = readFile(PREFROWCOUNT,COLUMNCOUNT, sourcepath);
		int[][] trans_pref = transpose_matrix(preference);
		int[] isZero = itemIsZero(preference); // ĳ����Ʒ�Ƿ񱻴�ֹ�
		double[][] similarityMatrix = produceSimilarityMatrix_double(isZero,trans_pref);
		computeScore_byTopN(resultpath,preference,similarityMatrix,popItems,COLUMNCOUNT,PREFROWCOUNT);
	}
/**
 * get the most popular items from the popPath
 * @param popPath
 * @param topN 
 * @return popItems[]
 */
private int[] getPopItems(String popPath, int topN) {
		int[] Items = new int[topN];
		File file = new File(popPath);
		FileReader fileReader;
		try {
			fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = "";
			int readNum = 0;
			while ((bufferedReader.ready())&&(readNum<topN)) {
				line = bufferedReader.readLine();
				String[] data = line.split("\t");
				Items[readNum] = Integer.parseInt(data[0]);
				readNum ++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Items;
	}

/**
 * compute the final score of every item for users after recommendation 
 * @param resultpath
 * @param preference
 * @param similarityMatrix
 * @param popItems 
 * @param colCount
 * @param rowCount
 */
	private void computeScore_byTopN(String resultpath,int[][] preference,double[][] similarityMatrix,int[] popItems, int colCount,int rowCount) {
		result = new double[rowCount][colCount];
		
		File file = new File(resultpath); // ����������ݵ��ļ�
		FileWriter out = null;
		try {
			out = new FileWriter(file);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		List<Integer> neighborSerial = new ArrayList<Integer>();
		for (int j = 0; j < colCount; j++) {
			neighborSerial.clear();
			neighborSerial = findKNeighbors_byTopN(j,similarityMatrix,popItems);
			for (int i = 0; i < rowCount; i++) {
				if (preference[i][j] == 0) { // ��û�����ֵ�Ԥ������
					double similaritySum = 0;
					double sum = 0;
					double score = 0;
					for (int m = 0; m < neighborSerial.size(); m++) {
						sum = sum + similarityMatrix[j][neighborSerial.get(m)] * preference[i][neighborSerial.get(m)];
						similaritySum = similaritySum + similarityMatrix[j][neighborSerial.get(m)];	
					}
					if (i < 5) {
						System.out.println(sum +" "+similaritySum);
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
		File file = new File(string); // ����������ݵ��ļ�

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
 * produce Similarity Matrix .
 * @param isZero
 * @param trans_pref
 * @param popItems 
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
	 * 20150506
	 * @param score
	 * @param i
	 * @param similarityMatrix
	 * @param popItems 
	 * @return
	 */
	private List<Integer> findKNeighbors_byTopN(int i,double[][] similarityMatrix, int[] popItems) { 
		List<Integer> neighborSerial = new ArrayList<Integer>();
		//�����Ʋ�Ʒ������������
		GetNumber2[] similarityGetNumbers = new GetNumber2[similarityMatrix.length];
		for (int j = 0; j < similarityGetNumbers.length; j++) {
			similarityGetNumbers[j] = new GetNumber2(j,similarityMatrix[i][j]);
		}
		Arrays.sort(similarityGetNumbers,new ExchangeComparator2());
/*		for (int j = 0; j < similarityGetNumbers.length; j++) {
			if (j > similarityGetNumbers.length - 10) {
				System.out.println(similarityGetNumbers[j].num);
			}
		}*/
		
		//��popitemsת����list
		List<Integer> popItemsList = new ArrayList<Integer>();
		for (int j = 0; j < popItems.length; j++) {
			popItemsList.add(popItems[j]);
		}

		int location = 0;
		int m = 0;
		/*for (int m = temp.length - 1; m >= temp.length - KNEIGHBOUR; m--) {*/
		for (location = similarityGetNumbers.length -1; m <= KNEIGHBOUR; m++) {
			while (!popItemsList.contains(similarityGetNumbers[location].index)) {
				location--;
				if (location < 0) {
					break;
				}
			}
			if (location >= 0) {
				neighborSerial.add(new Integer(similarityGetNumbers[location].index));
				m++;
/*				System.out.println(location+" "+similarityGetNumbers[location].index+" "+similarityGetNumbers[location].num);*/
			}
			if (location > 0) {
				location--;
			}else {
				break;
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