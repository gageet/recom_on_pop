package pku;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

class GetNumber1{
	double num;
	Integer index;
	public GetNumber1(Integer index,double a[]){
		num = a[index];
		this.index = index;
	}
}
class ExchangeComparator1 implements Comparator<GetNumber1>{
    @Override
    public int compare(GetNumber1 num1, GetNumber1 num2) {
    	if (num1.num > num2.num) {
    		return 1;
    	} else if (num1.num == num2.num) {
    		return 0;
    	} else {
    		return -1;
    	}
    }
}
public class TestFindIndex {
	GetNumber1 arr4sort[] = new GetNumber1[10];
	double[] minematrix = new double[10];
	
	public TestFindIndex(){
		for(int j=0;j<10;j++){
			minematrix[j] = new Random().nextDouble();
			arr4sort[j] = new GetNumber1(j,minematrix);
			System.out.println(arr4sort[j].index+" "+arr4sort[j].num);
		}
		System.out.println("\n");
		Arrays.sort(arr4sort, new ExchangeComparator1());
		for (int i = 0; i < 10; i++) {
			System.out.println(arr4sort[i].index+" "+arr4sort[i].num);
		}
	}
	
}
