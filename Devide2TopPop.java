package pku;

import javax.swing.text.StyledEditorKit.ForegroundAction;

/*
 * 将测试集，只留下预测之后流行度top50-800的商品的边
 */
public class Devide2TopPop {

	/*
	 * public static final String PopfileName =
	 * "E:/doc/lab/dataset/recommending/movielens-100k/data_2part/1/data1_0.5Item_pop_pre5_inorder.base"
	 * ; public static final String ChoosefileName =
	 * "E:/doc/lab/dataset/recommending/movielens-100k/data_2part/1/data1_0.5Item_Test_4use.base"
	 * ; public static final String path =
	 * "E:/doc/lab/dataset/recommending/movielens-100k/data_2part/1/";
	 */

	public static String PopfileName;
	public static String ChoosefileName;
	public static String path;
	public static String recentSize ;

	public static final String COM_PATH = "E:/doc/lab/dataset/recommending/movielens-100k/forcastPopByRecom/";

	public static void main(String[] args) {
		for (int id = 1; id <= 6; id = id + 1) {
/*			switch (id) {
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
			}*/

			PopfileName = COM_PATH + "origin/Pop_of_"+id+"recListSize_popAcd.txt";
			ChoosefileName = COM_PATH  + "uUse_ItemAcd2592000.txt";
			path = COM_PATH + id+"/";
			
			for (int i = 50; i <= 800; i = i + 50) {
				ChooseTopN ctn = new ChooseTopN(i, PopfileName, ChoosefileName,path);
				System.out.println(ctn.topNPath);
			}
		}
	}
}
