import java.util.ArrayList;
import java.util.List;

import gzl.sgd.multi.DataSet;
import gzl.sgd.math.DenseMatrix;
import gzl.sgd.math.SparseMatrix;
import gzl.sgd.multi.MultiSGD;
import gzl.sgd.multi.Weights;

public class MultiThreadSGD {

	public static int numIterations=10;
	public static double lamda=0.02;
	public static double lamda2=0.05;
	public static double lrate=0.001;
	public static double global_mean=3.6033;
	
	public static int numUsers;
	public static int numItems;
	public static int numRates;
	
	public static SparseMatrix TrainMatrix;
	public static SparseMatrix TestMatrix;
	
	public static void main(String[] args) throws Exception{
		
		numUsers=DataSet.userNumber;
		numItems=DataSet.itemNumber;
		//System.out.println("numUsers: "+numUsers+" numItems:"+numItems);
		TrainMatrix=new SparseMatrix(numUsers,numItems);
		TrainMatrix=DataSet.readDataSet("dataset1m.txt");
		TestMatrix=new SparseMatrix(numUsers,numItems);
		TestMatrix=DataSet.readDataSet("testSet1m.txt");
		//List<Weights> w=new ArrayList<Weights>();
		Weights w=new Weights();
		
		double preloss=100,curloss=10,cnt=0;
		MultiSGD sgd=new MultiSGD(TrainMatrix,lamda,lamda2,lrate,global_mean);
		for(int i=0;i<numIterations;i++){
			double start = System.currentTimeMillis();
			//w.add(sgd.run(4));
			w=sgd.run(4);
			double end = System.currentTimeMillis();
			curloss=Loss(TestMatrix,w);
			System.out.println("Iteration "+i+" Training Time: "+(end-start));
			System.out.println("Iteration "+i+" Training Loss: "+Loss(TrainMatrix,w));
			System.out.println("Iteration "+i+" Testing Loss: "+curloss);
		}		
	}
	
	public static double Loss(SparseMatrix A,Weights w) throws Exception{
		DenseMatrix A_approx;
		double loss=0;
		double square=0;
		double count=0;
		A_approx=w.U.mult(w.V);
		for(int i=0;i<A.numRows;i++){
			for(int j=0;j<A.numColumns;j++){
				if(A.get(i, j)!=0){
//					try{
						loss=A_approx.get(i, j)+w.ci.get(i)+w.dj.get(j)-A.get(i, j);
//					}catch(ArrayIndexOutOfBoundsException e){
//						System.out.println("i: "+i+" j:"+j);
//					}					
					square=loss*loss;
					count+=1;
				}				
			}
		}
		return Math.sqrt(square/count);
	}

}
