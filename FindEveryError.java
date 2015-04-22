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
			/*
			 * String wayname = null; for (int way = 5; way< 6; way++) { if (way
			 * == 0) { wayname = "last"; }else if (way == 1) { wayname =
			 * "last4"; }else if (way == 2) { wayname = "mean"; }else if (way ==
			 * 3) { wayname = "true"; }else if (way == 4){ wayname = "predict";
			 * }else if (way == 5){ wayname = "origin"; }
			 */
			switch (id) {
			case 1:
				recentSize = "all";
				break;
			case 2:
				recentSize = "last30";
				break;
			case 3:
				recentSize = "last60";
				break;
			case 4:
				recentSize = "last90";
				break;
			default:
				break;
			}

			outputpath = COM_PATH+recentSize+"/";
			//outputpath = COM_PATH;
			judgepath = COM_PATH + "uPre_timeAcd2592000.txt";
			listSize = 10;

			int j = 0;
			double[] mae = new double[16];
			double[] rmse = new double[16];
			double[] precision = new double[16];
			double[] recall = new double[16];
			double[] fOne = new double[16];
			double[] sibn = new double[16];
			double[] esibn = new double[16];

			for (int i = 50; i <= 800; i = i + 50) {
				long startTime = System.currentTimeMillis();
				trainpath = COM_PATH +"/uUse_timeAcd2592000.txt";
				//trainpath = COM_PATH +"uUse_timeAcd2592000.txt";
				sourcepath = COM_PATH + recentSize+"/uUse_Item_"+i+"top.txt";
				resultpath = outputpath + "Result_of_" + i + "top.txt";
				//resultpath = COM_PATH + "Result_of_uUse_timeAcd2592000.txt";
				listPath = outputpath + "KNeighbor_of_" + i + "top.txt";

     			Recommendation recommendation = new Recommendation(i,
						sourcepath, resultpath, listPath, COLUMNCOUNT,
						PREFROWCOUNT);
				
				Accuracy accuracy = new Accuracy(judgepath, resultpath,
						trainpath, listSize, COLUMNCOUNT, PREFROWCOUNT);
				mae[j] = accuracy.mae;
				rmse[j] = accuracy.rmse;
				precision[j] = accuracy.precision;
				recall[j] = accuracy.recall;
				fOne[j] = accuracy.fOne;
				sibn[j] = accuracy.sibn;
				esibn[j] = accuracy.esibn;

				j++;
				long endTime = System.currentTimeMillis();
				System.out.println("RecListSize " + id + "'s TOP " + i
						+ " have used£º" + (endTime - startTime) + "ms");
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
}
