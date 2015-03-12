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

	public static final String COM_PATH = "E:/doc/lab/dataset/recommending/movielens-100k/data_2part/";

	public static void main(String[] args) {
		// 给last进行处理
		for (int id = 1; id <= 10; id = id + 1) {
			PopfileName = COM_PATH + id + "/last/data" + id
					+ "_0.5Item_pop_pre5_inorder.base";
			ChoosefileName = COM_PATH + id + "/data" + id
					+ "_0.5Item_Test_4use.base";
			path = COM_PATH + id + "/last/";

			for (int i = 50; i <= 800; i = i + 50) {
				ChooseTopN ctn = new ChooseTopN(i, PopfileName, ChoosefileName,
						path);
				System.out.println(ctn.topNPath);
			}
		}
		// 给last4进行处理
		for (int id = 1; id <= 10; id = id + 1) {
			PopfileName = COM_PATH + id + "/last4/data" + id
					+ "_0.5Item_pop_pre5_inorder.base";
			ChoosefileName = COM_PATH + id + "/data" + id
					+ "_0.5Item_Test_4use.base";
			path = COM_PATH + id + "/last4/";

			for (int i = 50; i <= 800; i = i + 50) {
				ChooseTopN ctn = new ChooseTopN(i, PopfileName, ChoosefileName,
						path);
				System.out.println(ctn.topNPath);
			}
		}
		// 给mean进行处理
		for (int id = 1; id <= 10; id = id + 1) {
			PopfileName = COM_PATH + id + "/mean/data" + id
					+ "_0.5Item_pop_pre5_inorder.base";
			ChoosefileName = COM_PATH + id + "/data" + id
					+ "_0.5Item_Test_4use.base";
			path = COM_PATH + id + "/mean/";

			for (int i = 50; i <= 800; i = i + 50) {
				ChooseTopN ctn = new ChooseTopN(i, PopfileName, ChoosefileName,
						path);
				System.out.println(ctn.topNPath);
			}
		}
		// 给true进行处理
		for (int id = 1; id <= 10; id = id + 1) {
			PopfileName = COM_PATH + id + "/data" + id
					+ "_0.5Item_pop_tru5_inorder.base";
			ChoosefileName = COM_PATH + id + "/data" + id
					+ "_0.5Item_Test_4use.base";
			path = COM_PATH + id + "/true/";

			for (int i = 50; i <= 800; i = i + 50) {
				ChooseTopN ctn = new ChooseTopN(i, PopfileName, ChoosefileName,
						path);
				System.out.println(ctn.topNPath);
			}
		}
	}
}
