package pku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FindEveryError {
	public static String outputpath;
	public static String sourcepath;
	public static String judgepath;
	public static String resultpath;
	public static String trainpath;
	public static String listPath;
	public static String poppath;
	public static int listSize;
	public static final int COLUMNCOUNT = 1682;
	public static final int PREFROWCOUNT = 943;
	public static String recentSize ;
	public static final int recomNum = 16;
	public static final String COM_PATH = "E:/doc/lab/dataset/recommending/movielens-100k/forcastPopByRecom/";
	
	public static void main(String[] args) {
		for (int id = 1; id <= 1; id++) {
			/*			switch (id) {
		case 1:recentSize = "all";break;
			case 2:recentSize = "last30";break;
			case 3:recentSize = "last60";break;
			case 4:recentSize = "last90";break;
			default:recentSize = "origin";break;
			}*/

			recentSize = "SimByTopN/" + id;
			outputpath = COM_PATH+recentSize+"/";
			judgepath = COM_PATH + "uPre_timeAcd2592000.txt";
			poppath = COM_PATH + "origin/Pop_of_"+id+"recListSize_popAcd.txt";
			listSize = 10;

			int j = 0;
			double[] precision = new double[recomNum];
			double[] recall = new double[recomNum];
			double[] fOne = new double[recomNum];
			double[] sibn = new double[recomNum];
			double[] esibn = new double[recomNum];

			for (int i = 50; i <= 800; i = i + 50) {
				long startTime = System.currentTimeMillis();
				trainpath = COM_PATH +"uUse_timeAcd2592000.txt";
/*				sourcepath = COM_PATH+recentSize+"/uUse_Item_"+i+"top.txt";*/
				sourcepath = trainpath;
				resultpath = outputpath + "Result_of_" + i + "top.txt";

     			/*Recommendation recommendation = new Recommendation(i,sourcepath, resultpath, COLUMNCOUNT,PREFROWCOUNT);*/
				Recommendation_calSimByTopN recommendation = new Recommendation_calSimByTopN(i,sourcepath, resultpath, poppath, COLUMNCOUNT,PREFROWCOUNT);
				int[][] trainFile = new int[PREFROWCOUNT][COLUMNCOUNT];
				double[][] resultFile = new double[PREFROWCOUNT][COLUMNCOUNT];
				resultFile = readResultFile(PREFROWCOUNT,COLUMNCOUNT,resultpath);
				trainFile = readPreFile(PREFROWCOUNT,COLUMNCOUNT,trainpath);
						
				Accuracy accuracy = new Accuracy(judgepath, recommendation.getResult(),trainFile, listSize, COLUMNCOUNT, PREFROWCOUNT);
				precision[j] = accuracy.getPrecision();
				recall[j] = accuracy.getRecall();
				fOne[j] = accuracy.getfOne();
				sibn[j] = accuracy.getSibn();
				esibn[j] = accuracy.getEsibn();

				j++;
				long endTime = System.currentTimeMillis();
				System.out.println("RecListSize " + id + "'s TOP " + i + " have used£º" + (endTime - startTime) + "ms");
			}

			File file = new File(outputpath + "EvaluationResult_fixed.txt");
			FileWriter fileWriter = null;
			try {
				fileWriter = new FileWriter(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int i = 0; i < recomNum; i++) {
				try {
					int top = (i + 1) * 50;
					fileWriter.write(top + "\t" + precision[i] + "\t"  + recall[i] + "\t" + fOne[i]
							+ "\t"+ sibn[i] + "\t"
							+ esibn[i] + "\n");
				} catch (IOException e) { // TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			try {
				fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("The " + id + "th data has been processed");

		}

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
	
	public static void coutArray(int[][] a,String string){
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
	 * This method is used to read file and store file data into arrays
	 * 
	 * @param rowCount
	 * @param colCount
	 * @param fileName
	 * @return int[][] preference
	 */
	private static int[][] readPreFile(int rowCount,int colCount,String fileName) {
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
	 * This method is used to read resultFile and store file data into arrays
	 * 
	 * @param rowCount
	 * @param colCount
	 * @param fileName
	 * @return int[][] preference
	 */
	private static double[][] readResultFile(int rowCount,int colCount,String fileName) {
		double[][] preference = new double[rowCount][colCount];

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
					preference[Integer.parseInt(data[0]) - 1][Integer.parseInt(data[1]) - 1] = Double.parseDouble(data[2]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return preference;
	}
}
