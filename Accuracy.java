package pku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.omg.CORBA.PRIVATE_MEMBER;

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
	public static  int COLUMNCOUNT ;
	public static  int PREFROWCOUNT ;
	
	private double precision;
	private double recall ;
	private double fOne ;
	private double sibn ;
	private double esibn;
	private double diversity;
	private int userNum;
	
	public Accuracy(String judgepath, double[][] result, int[][] train,int listSize,int columncount2,int prefrowcount2) {
		Accuracy.COLUMNCOUNT = columncount2;
		Accuracy.PREFROWCOUNT = prefrowcount2;
		
		int[] trueUser = new int[prefrowcount2];
		int[] itemRatedNum = new int[columncount2];
		double[][] trainMatrix = new double[prefrowcount2][columncount2];
		double[][] testMatrix = new double[prefrowcount2][columncount2];
		double[][] mineMatrix = new double[prefrowcount2][columncount2];
		
		testMatrix = readMatrix(judgepath);
		mineMatrix = getRecListMatrix(result,listSize);
		
		coutArray(mineMatrix, "E:/doc/lab/dataset/recommending/movielens-100k/forcastPopByRecom/temp/result_Rec.txt");
		coutArrayMatrix(testMatrix,  "E:/doc/lab/dataset/recommending/movielens-100k/forcastPopByRecom/temp/test_matrix.txt");
		trainMatrix = fromIntToDouble(train);
		trueUser = calculateUserNum(trainMatrix,testMatrix,prefrowcount2);
		itemRatedNum = findTheRatedNum(trainMatrix,trueUser);
		
		System.out.println("userNum:"+userNum);
		this.precision = calculatePrecision(trainMatrix,testMatrix,mineMatrix,userNum,trueUser,listSize);
		this.recall = calculateRecall(trainMatrix,testMatrix,mineMatrix,userNum,trueUser);
		this.fOne = calculateFOne(this.precision,this.recall);
		this.sibn = calculateSibn(trainMatrix,testMatrix,mineMatrix,itemRatedNum,listSize,userNum,trueUser);
		this.esibn = calculateEsibn(trainMatrix,testMatrix,mineMatrix,itemRatedNum,listSize,userNum,trueUser);
		this.diversity = calculateDiversity(trainMatrix,testMatrix,mineMatrix);

	}




	private int[] calculateUserNum(double[][] trainMatrix, double[][] testMatrix, int prefrowcount2) {
		int num = 0;
		int[] user = new int[prefrowcount2];
		
		for (int i = 0; i < testMatrix.length; i++) {
			double testCount = 0.0;
			double trainCount = 0.0;
			for (int j = 0; j < testMatrix[0].length; j++) {
				testCount = testCount +testMatrix[i][j];
				trainCount = trainCount + trainMatrix[i][j];
			}
			if ((testCount > 0.0)&&(trainCount > 0.0)) {
				num = num +1;
				user[i] = 1;
			}
		}
		setUserNum(num);
		return user;
	}




	private double[][] getRecListMatrix(double[][] result,int listSize) {
		double[][] recMatrix = new double[result.length][result[0].length];
		GetNumber[] num4sort = new GetNumber[result[0].length];
		
		for (int i = 0; i < recMatrix.length; i++) {
			for (int j = 0; j < recMatrix[0].length; j++) {
				num4sort[j] = new GetNumber(j, result[i]);
			}
			Arrays.sort(num4sort, new ExchangeComparator());
			for (int j = 0; j < listSize; j++) {
				recMatrix[i][num4sort[num4sort.length - j -1].index] = result[i][num4sort[num4sort.length - j -1].index];
			}
		}
		return recMatrix;
	}




	private double calculateDiversity(double[][] trainmatrix, double[][] testmatrix, double[][] minematrix) {
		// TODO Auto-generated method stub
		return 0;
	}




	private double calculateEsibn(double[][] trainmatrix, double[][] testmatrix,double[][] minematrix, int[] itemRatedNum, int listSize,int userNum, int[] trueUser) {
		double esibn_all = 0.0;
		double esibn_all_sum = 0.0;
		for (int i = 0; i < minematrix.length; i++) {
			if (trueUser[i] != 1) {
			} else {
				double esibn_each = 0.0;
				double esibn_each_sum = 0.0;
				for (int k = 0; k < minematrix[0].length; k++) {
					if ((minematrix[i][k] != 0.0)&&(testmatrix[i][k]!= 0.0)&&(itemRatedNum[k]!=0)) {
						esibn_each_sum = log2N(userNum/((double)itemRatedNum[k])) + esibn_each_sum;
					}
				}
				esibn_each = esibn_each_sum/listSize;
				esibn_all_sum = esibn_all_sum + esibn_each;
			}
		}
		esibn_all = esibn_all_sum/userNum;
		System.out.println("esibn:"+esibn_all);
		return esibn_all;
	}




	private double calculateSibn(double[][] trainmatrix, double[][] testmatrix,double[][] minematrix, int[] itemRatedNum, int listSize,int userNum, int[] trueUser) {
		double sibn_all = 0.0;
		double sibn_all_sum = 0.0;
		for (int i = 0; i < minematrix.length; i++) {
			if (trueUser[i] != 1) {
			} else {
				double sibn_each = 0.0;
				double sibn_each_sum = 0.0;
				for (int k = 0; k < minematrix[0].length; k++) {
					if ((minematrix[i][k] != 0.0)&&(itemRatedNum[k]!=0)) {
						sibn_each_sum = log2N(userNum/((double)itemRatedNum[k])) + sibn_each_sum;
					}
				}
				sibn_each = sibn_each_sum/listSize;
				sibn_all_sum = sibn_all_sum + sibn_each;
			}
		}
		sibn_all = sibn_all_sum/userNum;
		System.out.println("sibn:"+sibn_all);
		return sibn_all;
	}

	private double calculateFOne(double pre, double rec) {
		double fone = (2*rec*pre)/(rec + pre);
		System.out.println("fone:"+fone);
		return fone;
	}

	private double calculateRecall(double[][] trainmatrix, double[][] testmatrix, double[][] minematrix, int userNum, int[] trueUser) {
		double Rec_all = 0.0;
		double Rec_all_sum = 0.0;
		if (userNum <=0) {
			System.err.println("userNum is wrong! ");
		}
		
		for (int i = 0; i < minematrix.length; i++) {
			if (trueUser[i] != 1) {
			}else {
				double Rec_each = 0.0;
				double Rec_each_sum = 0.0;
				int listSize = 0;
				for (int j = 0; j < minematrix[0].length; j++) {
					if (testmatrix[i][j]!=0.0) {
						listSize = listSize +1;
						if (minematrix[i][j]!=0.0) {
							Rec_each_sum = Rec_each_sum +1;
						}
					}
				}
				Rec_each = Rec_each_sum/listSize;
				Rec_all_sum = Rec_all_sum +Rec_each;
				
			}
		}
		Rec_all = Rec_all_sum/userNum;
		System.out.println("recall:"+Rec_all);
		return Rec_all;
	}

	private double calculatePrecision(double[][] trainmatrix, double[][] testmatrix, double[][] minematrix, int userNum, int[] trueUser, int listSize) {
		double pre_all = 0.0;
		double pre_all_sum = 0.0;
		if (listSize <= 0) {
			System.err.println("listSize is wrong! ");
		}
		if (userNum <=0) {
			System.err.println("userNum is wrong! ");
		}
		
		for (int i = 0; i < minematrix.length; i++) {
			if (trueUser[i] != 1) {
			}else {
				double pre_each = 0.0;
				double pre_each_sum = 0.0;
				for (int j = 0; j < minematrix[0].length; j++) {
					if (((minematrix[i][j])!=0.0)&&(testmatrix[i][j]!=0.0)) {
						pre_each_sum = pre_each_sum +1;
					}
				}
				pre_each = pre_each_sum/listSize;
				pre_all_sum = pre_all_sum +pre_each;
			}
		}
		pre_all = pre_all_sum/userNum;
		System.out.println("pre_all:"+pre_all);
		return pre_all;
	}

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


	private double[][] fromIntToDouble(int[][] train) {
		double[][] trainmatrix = new double[train.length][train[0].length];
		for (int i = 0; i < trainmatrix.length; i++) {
			for (int j = 0; j < trainmatrix[0].length; j++) {
				trainmatrix[i][j] = train[i][j];
			}
		}
		return trainmatrix;
	}
/**
 * Calculate the log2N
 * @param N
 * @return 
 */
	private double log2N(double d) {
		return Math.log(d)/Math.log(2);
	}

	private int[] findTheRatedNum(double[][] trainmatrix, int[] trueUser) {
		int[] a = new int[COLUMNCOUNT];
		for (int i = 0; i < COLUMNCOUNT; i++) {
			for (int j = 0; j < PREFROWCOUNT; j++) {
				if ((trainmatrix[j][i]!= 0.0)&&(trueUser[j] == 1)) {
					a[i] = a[i]+1;
				}
			}
		}
		return a;
	}
	

	private int findTheUserNum(double[][] matrix) {
		int userNum = 0;
		for (int i = 0; i < PREFROWCOUNT; i++) {
			for (Integer j = 0; j < COLUMNCOUNT; j++) {
				if(matrix[i][j]!= 0.0){
					userNum = userNum +1;
					break;
				}
			}
		}
		return userNum;
	}
	
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
			while (br.ready()) {
				line = br.readLine();
				String[] data = line.split("\t");
				if (Double.parseDouble(data[2]) >= 3) {
					readmatr[Integer.parseInt(data[0]) - 1][Integer.parseInt(data[1]) - 1] = 1;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readmatr;

	}
	
	public static void coutArray(double[][] a,String string){
		File file = new File(string);
		FileWriter writer = null ;
		try {
			writer = new FileWriter(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				if (a[i][j] != 0.0) {
					try {
						writer.write((i+1)+"\t"+(j+1)+"\t"+a[i][j]+"\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
		}
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void coutArrayMatrix(double[][] a,String string){
		File file = new File(string);
		FileWriter writer = null ;
		try {
			writer = new FileWriter(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				try {
					writer.write(a[i][j]+"\t");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				writer.write("\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @return the userNum
	 */
	public int getUserNum() {
		return userNum;
	}
	/**
	 * @param userNum the userNum to set
	 */
	public void setUserNum(int userNum) {
		this.userNum = userNum;
	}




	/**
	 * @return the precision
	 */
	public double getPrecision() {
		return precision;
	}




	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(double precision) {
		this.precision = precision;
	}




	/**
	 * @return the recall
	 */
	public double getRecall() {
		return recall;
	}




	/**
	 * @param recall the recall to set
	 */
	public void setRecall(double recall) {
		this.recall = recall;
	}




	/**
	 * @return the fOne
	 */
	public double getfOne() {
		return fOne;
	}




	/**
	 * @param fOne the fOne to set
	 */
	public void setfOne(double fOne) {
		this.fOne = fOne;
	}




	/**
	 * @return the sibn
	 */
	public double getSibn() {
		return sibn;
	}




	/**
	 * @param sibn the sibn to set
	 */
	public void setSibn(double sibn) {
		this.sibn = sibn;
	}




	/**
	 * @return the esibn
	 */
	public double getEsibn() {
		return esibn;
	}




	/**
	 * @param esibn the esibn to set
	 */
	public void setEsibn(double esibn) {
		this.esibn = esibn;
	}




	/**
	 * @return the diversity
	 */
	public double getDiversity() {
		return diversity;
	}




	/**
	 * @param diversity the diversity to set
	 */
	public void setDiversity(double diversity) {
		this.diversity = diversity;
	}
}
