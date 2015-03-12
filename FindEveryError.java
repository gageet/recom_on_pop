package pku;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FindEveryError {
	/*
	 * public static final String outputpath =
	 * "E:/doc/lab/dataset/recommending/movielens-100k/method1_used_popularity/mean/"
	 * ; public static final String sourcepath =
	 * "E:/doc/lab/dataset/recommending/movielens-100k/method1_used_popularity/mean/"
	 * ; public static final String judgepath =
	 * "E:/doc/lab/dataset/recommending/movielens-100k/0.5Item_Test_4pre.base";
	 */

	public static String outputpath;
	public static String sourcepath;
	public static String judgepath;
	public static String resultpath;
	public static int listSize;

	public static final String COM_PATH = "E:/doc/lab/dataset/recommending/movielens-100k/data_2part/";

	public static void main(String[] args) {
		for (int id = 1; id <= 10; id++) {
			String wayname = null;
			for (int way = 4; way< 5; way++) {
				if (way == 0) {
					wayname = "last";
				}else if (way == 1) {
					wayname = "last4";
				}else if (way == 2) {
					wayname = "mean";
				}else if (way == 3) {
					wayname = "true";
				}else if (way == 4){
					wayname = "predict";
				}
			outputpath = COM_PATH + id + "/"+wayname+"/";
			sourcepath = COM_PATH + id + "/"+wayname+"/";
			judgepath = COM_PATH + id + "/data" + id+ "_0.5Item_Test_4pre.base";
			listSize = 10;

			int j = 0;
			double[] mae = new double[16];
			double[] rmse = new double[16];
			double[] precision = new double[16];
			
			for (int i = 50; i <= 800; i = i + 50) {
				long startTime = System.currentTimeMillis();
				resultpath = outputpath + "Result_of_" + i +"top.base";
				
				Recommendation recommendation = new Recommendation(i,sourcepath,resultpath);
				Accuracy accuracy = new Accuracy(judgepath,resultpath,listSize);
				mae[j] = accuracy.mae;
				rmse[j] = accuracy.rmse;
				precision[j] = accuracy.precision;
				j++;
				long endTime = System.currentTimeMillis();
				System.out.println("Data " +id+" :"+wayname+"'s TOP " + i + " have used£º"
						+ (endTime - startTime) + "ms");
			}

			File file = new File(outputpath + "EvaluationResult.base");
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
					fileWriter.write(top + "\t" + mae[i] + "\t" + rmse[i]
							+ "\t"+precision[i]+"\n");
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
			
			System.out.println("The "+id+"th data has been processed");

		}

		}
	}
}
