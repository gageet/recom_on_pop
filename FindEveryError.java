package pku;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FindEveryError {
	public static String outputpath;
	public static String sourcepath;
	public static String judgepath;
	public static String resultpath;
	public static String trainpath;
	public static String listPath;
	public static int listSize;
	public static final int COLUMNCOUNT = 1682;
	public static final int PREFROWCOUNT = 943;
	public static String recentSize ;
	public static final String COM_PATH = "E:/doc/lab/dataset/recommending/movielens-100k/forcastPopByRecom/";
	
	public static void main(String[] args) {
		for (int id = 1; id <= 4; id++) {
			switch (id) {
			case 1:recentSize = "all";break;
			case 2:recentSize = "last30";break;
			case 3:recentSize = "last60";break;
			case 4:recentSize = "last90";break;
			default:recentSize = "origin";break;
			}

			outputpath = COM_PATH+recentSize+"/";
			judgepath = COM_PATH + "uPre_timeAcd2592000.txt";
			listSize = 10;

			int j = 0;
			double[] precision = new double[16];
			double[] recall = new double[16];
			double[] fOne = new double[16];
			double[] sibn = new double[16];
			double[] esibn = new double[16];

			for (int i = 50; i <= 800; i = i + 50) {
				long startTime = System.currentTimeMillis();
				trainpath = COM_PATH +"uUse_timeAcd2592000.txt";
				sourcepath = COM_PATH+recentSize+"/uUse_Item_"+i+"top.txt";
				resultpath = outputpath + "Result_of_" + i + "top.txt";

     			Recommendation recommendation = new Recommendation(i,sourcepath, resultpath, COLUMNCOUNT,PREFROWCOUNT);
/*     			coutArray(recommendation.getResult(), "E:/doc/lab/dataset/recommending/movielens-100k/forcastPopByRecom/temp/result.txt");
     			coutArray(recommendation.getPreference(), "E:/doc/lab/dataset/recommending/movielens-100k/forcastPopByRecom/temp/Preference.txt");*/
				Accuracy accuracy = new Accuracy(judgepath, recommendation.getResult(),recommendation.getPreference(), listSize, COLUMNCOUNT, PREFROWCOUNT);
				precision[j] = accuracy.getPrecision();
				recall[j] = accuracy.getRecall();
				fOne[j] = accuracy.getfOne();
				sibn[j] = accuracy.getSibn();
				esibn[j] = accuracy.getEsibn();

				j++;
				long endTime = System.currentTimeMillis();
				System.out.println("RecListSize " + id + "'s TOP " + i + " have used£º" + (endTime - startTime) + "ms");
			}

			File file = new File(outputpath + "EvaluationResult.txt");
			FileWriter fileWriter = null;
			try {
				fileWriter = new FileWriter(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int i = 0; i < 16; i++) {
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
}
