package pku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class ChooseTopN {
	/*
	 * 将前N个商品的数据库选择信息读入
	 */

/*	public static final String PopfileName = "E:/doc/lab/dataset/recommending/movielens-100k/0.5Item_pop_pre5_inorder.base";
	public static final String ChoosefileName = "E:/doc/lab/dataset/recommending/movielens-100k/0.5Item_Test_4pre.base";*/
	public String topNPath;

	/*
	 * 构造方法，得到在预测集中前N个流行商品的所有连边。
	 */
	public ChooseTopN(int n,String PopfileName,String ChoosefileName,String path) {
		topNPath = chooseTopNItem(n, PopfileName, ChoosefileName,path);
	}

	/*
	 * 输出数组的内容
	 */
	public void coutArray(int[] n) {
		for (int i = 0; i < n.length; i++) {
			System.out.print(n[i] + " ");
		}
		System.out.println();
	}

	/*
	 * 从choosefileName文件中选出popfileName里前n个项目的连边
	 */
	public String chooseTopNItem(int n, String popfileName, String choosefileName,String path) {

		//将前N个排好序
		int[] ItemPop = new int[n];
		ItemPop = readTopN(n, popfileName);
		Arrays.sort(ItemPop);
		coutArray(ItemPop);
		
		//将前N个连边输出
		String topPath = cinTopNEdge(n,ItemPop,choosefileName,path);
		
		return topPath;
	}

	/*
	 * 从流行性预测文件中选出前N个项目
	 */
	private int[] readTopN(int n, String popfileName) {
		int[] a = new int[n];
		try {
			File file = new File(popfileName);
			FileReader fReader = new FileReader(file);
			BufferedReader br = new BufferedReader(fReader);
			String line = "";

			for (int i = 0; i < a.length; i++) {
				line = br.readLine();
				String[] data = line.split("\t");
				a[i] = Integer.parseInt(data[0]);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return a;
	}

	private String cinTopNEdge(int n,int[] ItemPop, String choosefileName,String path) {

		String uuid = path+"uUse_Item_"+n+"top"+ ".txt";
		File outFile = new File(uuid);
		FileWriter outFileWriter = null;
		try {
			outFileWriter = new FileWriter(outFile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			File file = new File(choosefileName);
			FileReader fReader = new FileReader(file);
			BufferedReader bReader = new BufferedReader(fReader);
			String line = "";
			int i = 0;
			int temp = 0;

			while (bReader.ready()) {
				line = bReader.readLine();
				String[] data = line.split("\t");
				//System.out.println(line);
				
				if (Integer.parseInt(data[1]) == ItemPop[i]) {
					outFileWriter.write(data[0] + "\t"+data[1] + "\t"+data[2] + "\t"+data[3] + "\n");
				}else if ((i+1<ItemPop.length)&&(Integer.parseInt(data[1]) == ItemPop[i+1])) {
					outFileWriter.write(data[0] + "\t"+data[1] + "\t"+data[2] + "\t"+data[3] + "\n");
					i++;
				}else if ((i+1<ItemPop.length)&&(Integer.parseInt(data[1]) > ItemPop[i+1])) {
					do {
						i++;
					} while ((i+1<ItemPop.length)&&(Integer.parseInt(data[1]) > ItemPop[i+1]));
					outFileWriter.write(data[0] + "\t"+data[1] + "\t"+data[2] + "\t"+data[3] + "\n");
					i++;
					
				}
				
				// preference[Integer.parseInt(data[0]) -
				// 1][Integer.parseInt(data[1]) - 1] =
				// Integer.parseInt(data[2]);
			}
			System.out.println(i);
			
		} catch (Exception e) {
			// TODO: handle exception
			
		}
		
		try {
			outFileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return uuid;
	}

}
