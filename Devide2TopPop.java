package pku;

import javax.swing.text.StyledEditorKit.ForegroundAction;

/*
 * �����Լ���ֻ����Ԥ��֮�����ж�top50-800����Ʒ�ı�
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
		// ��last���д���
		for (int id = 1; id <= 4; id = id + 1) {
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

/*			PopfileName = COM_PATH +  "Pop_of_"+id+"recListSize_popAcd.txt";
			ChoosefileName = COM_PATH  + "uUse_ItemAcd2592000.txt";
			path = COM_PATH +id+"/";*/

			PopfileName = COM_PATH + "Use_"+recentSize+"_trend.txt";
			ChoosefileName = COM_PATH  + "uUse_ItemAcd2592000.txt";
			path = COM_PATH + recentSize+"/";
			
			for (int i = 50; i <= 800; i = i + 50) {
				ChooseTopN ctn = new ChooseTopN(i, PopfileName, ChoosefileName,path);
				System.out.println(ctn.topNPath);
			}
		}
/*		// ��last4���д���
		for (int id = 2; id <= 2; id = id + 1) {
			PopfileName = COM_PATH + id + "/last4/0.5Item_pop_dec_2th.base";
			ChoosefileName = COM_PATH + id + "/0.5_1_10_Item_Test_4use.base";
			path = COM_PATH + id + "/last4/";

			for (int i = 50; i <= 800; i = i + 50) {
				ChooseTopN ctn = new ChooseTopN(i, PopfileName, ChoosefileName,path);
				System.out.println(ctn.topNPath);
			}
		}
		// ��mean���д���
		for (int id = 2; id <= 2; id = id + 1) {
			PopfileName = COM_PATH + id + "/mean/0.5Item_pop_dec_2th.base";
			ChoosefileName =COM_PATH + id + "/0.5_1_10_Item_Test_4use.base";
			path = COM_PATH + id + "/mean/";

			for (int i = 50; i <= 800; i = i + 50) {
				ChooseTopN ctn = new ChooseTopN(i, PopfileName, ChoosefileName,path);
				System.out.println(ctn.topNPath);
			}
		}
		// ��true���д���
		for (int id = 2; id <= 2; id = id + 1) {
			PopfileName = COM_PATH + id + "/true/0.5Item_pop_dec_2th.base";
			ChoosefileName = COM_PATH + id + "/0.5_1_10_Item_Test_4use.base";
			path = COM_PATH + id + "/true/";

			for (int i = 50; i <= 800; i = i + 50) {
				ChooseTopN ctn = new ChooseTopN(i, PopfileName, ChoosefileName,path);
				System.out.println(ctn.topNPath);
			}
		}
		// ��predict���д���
		for (int id = 2; id <= 2; id = id + 1) {
			PopfileName = COM_PATH + id + "/predict/0.5Item_pop_dec_2th.base";
			ChoosefileName = COM_PATH + id + "/0.5_1_10_Item_Test_4use.base";
			path = COM_PATH + id + "/predict/";

			for (int i = 50; i <= 800; i = i + 50) {
				ChooseTopN ctn = new ChooseTopN(i, PopfileName, ChoosefileName,path);
				System.out.println(ctn.topNPath);
			}
		}*/
	}
}
