package pku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

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
	public static final int COLUMNCOUNT = 1682; // number of items
	public static final int PREFROWCOUNT = 943;
	public static final int TESTROWCOUNT = 20000;
	public double rmse;
	public double mae;
	public double precision;
	public double sibn ;
	public double esibn;

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
			int i = 0;
			while (br.ready()) {
				line = br.readLine();
				String[] data = line.split("\t");
				// System.out.println(Double.parseDouble(data[2]));
				readmatr[Integer.parseInt(data[0]) - 1][Integer
						.parseInt(data[1]) - 1] = Double.parseDouble(data[2]);

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readmatr;

	}

	public Accuracy(String judgepath, String resultpath, String trainpath,int listSize) {
		double[][] minematrix = new double[PREFROWCOUNT][COLUMNCOUNT];
		double[][] testmatrix = new double[PREFROWCOUNT][COLUMNCOUNT];
		double[][] trainmatrix = new double[PREFROWCOUNT][COLUMNCOUNT];
		minematrix = readMatrix(resultpath);
		testmatrix = readMatrix(judgepath);
		trainmatrix = readMatrix(trainpath);

		int count = 0;
		int calnum = 0;
		int trainUsers = 0;
		int itemRatedNum[] = new int[COLUMNCOUNT];
		
		double sum = 0;
		double sum_rmse = 0;
		double acc = 0;
		double acc_rmse = 0;

		double preNum = 0;
		double prePer = 0;
		
		double sibn_each = 0.0;//һ���û�ÿ����Ʒsibn�ĳ�ʼֵ
		double sibn_user = 0.0;
		int sibn_listsize = 0;
		
		double esibn_each = 0.0;
		double esibn_user = 0.0;

		trainUsers = findTheUserNum(trainmatrix);//��ѵ�����е��û���
		System.out.println("trainUsers:"+trainUsers);
		itemRatedNum = findTheRatedNum(trainmatrix);//��ѵ�����и�ÿ����Ʒ���ֵ�����
		GetNumber arr4sort[] = new GetNumber[COLUMNCOUNT];

		for (int i = 0; i < PREFROWCOUNT; i++) {
			for (Integer j = 0; j < COLUMNCOUNT; j++) {

				//����mae��rmse��sum
				if ((testmatrix[i][j] != 0.0) && (minematrix[i][j] != 0.0)) {
					sum += Math.abs(testmatrix[i][j] - minematrix[i][j]);
					sum_rmse += Math.pow(
							Math.abs(testmatrix[i][j] - minematrix[i][j]), 2);
					count = count + 1;

				}
				//����precision��������������и�ֵ
				arr4sort[j] = new GetNumber(j, minematrix[i]);

			}

			if (count == 0) {
			} else {
				calnum = calnum + 1;
				acc = sum / count + acc; // �ۼ�mae
				acc_rmse = Math.sqrt(sum_rmse / count) + acc_rmse;// �ۼ�rmse

				Arrays.sort(arr4sort, new ExchangeComparator());//�����������������
				
				sibn_listsize = listSize;//sibn��listsize��ʼ��Ϊlistsize

				for (int j = listSize; j > 0; j--) {
					int add[] = new int[listSize];// ���Լ�¼���ʮ�����ĵ�ַ
					add[j - 1] = arr4sort[COLUMNCOUNT - j].index;
					if (testmatrix[i][add[j - 1]] != 0.0) {
						preNum = preNum + 1;
					}
					
					//��¼list��ÿ����Ʒ��sibn,�����ۼ�
					if (itemRatedNum[add[j-1]] == 0) {
						sibn_listsize = sibn_listsize -1;
					}else {
						sibn_each = log2N(trainUsers/((double)itemRatedNum[add[j-1]])) +sibn_each;
						System.out.println("sibn_each:"+sibn_each);
					}
					
					//����esibn
					if (testmatrix[i][add[j -1]] !=0.0) {
						esibn_each = log2N(trainUsers/((double)itemRatedNum[add[j-1]])) +esibn_each;
						System.out.println("esibn_each:"+esibn_each);
					}
					
				}
				//ÿ���û��б��sibn�������ۼ�
				sibn_user = sibn_each/sibn_listsize + sibn_user;
				esibn_user = esibn_each/sibn_listsize + esibn_user;
				System.out.println("esibn_each/esibn_listsize:"+esibn_each+" "+sibn_listsize );
				sibn_each = 0.0;
				esibn_each = 0.0;
				System.out.println("sibn_user:"+sibn_user+" "+"esibn_user"+esibn_user);
			}
			prePer = preNum / listSize + prePer;//׼ȷ�ʵ�sum

			count = 0;
			sum = 0;
			sum_rmse = 0;
			preNum = 0;
		}
		mae = acc / calnum;
		rmse = acc_rmse / calnum;
		precision = prePer / calnum;
		sibn = sibn_user/calnum;
		esibn = esibn_user/calnum;
		System.out.println("calnum:"+calnum);
		System.out.println("MAE��" + mae);
		System.out.println("RMSE:" + rmse);
		System.out.println("precision:" + precision);
		System.out.println("SIBN:"+sibn);
		System.out.println("ESIBN:"+esibn);

	}

	private double log2N(double d) {
		//������2Ϊ�׵Ķ���
		return Math.log(d)/Math.log(2);
	}

	private int[] findTheRatedNum(double[][] trainmatrix) {
		int[] a = new int[COLUMNCOUNT];
		for (int i = 0; i < COLUMNCOUNT; i++) {
			for (int j = 0; j < PREFROWCOUNT; j++) {
				if (trainmatrix[j][i]!= 0.0) {
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
}
