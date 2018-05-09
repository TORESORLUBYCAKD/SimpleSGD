import gzl.sgd.common.LibrecException;
import gzl.sgd.math.DenseMatrix;
import gzl.sgd.math.DenseVector;
import gzl.sgd.math.SparseMatrix;
import gzl.sgd.math.VectorEntry;
import gzl.sgd.math.GMatrix;

public class SimpleSGD {

	public static int numIterations=100;
	public static int numFactors=20;
	public static double lamda=0.02;
	public static double lamda2=0.05;
	public static double lrate=0.001;
	public static double global_mean=3.6033;
	
	public static int numUsers;
	public static int numItems;
	public static int numRates;
	
	public static GMatrix trainMatrix;
	public static GMatrix testMatrix;
	public static SparseMatrix A;
	public static SparseMatrix B;
	public static DenseMatrix U;
	public static DenseMatrix V;
	
	public static void main(String[] args) throws Exception{
		
		
		trainMatrix=new GMatrix(0,0);
		trainMatrix=trainMatrix.loadData("trainingset");
		testMatrix=new GMatrix(0,0);
		testMatrix=testMatrix.loadData("testingset");
		numUsers=trainMatrix.numRows;
		numItems=trainMatrix.numColumns;
		System.out.print("numUsers: "+numUsers+"numItems: "+numItems);
		U=new DenseMatrix(numUsers, numFactors);
		V=new DenseMatrix(numFactors, numItems);
		double initMean=0.0f;
		double initStd=0.1f;
		U.init(initMean,initStd);
		V.init(initMean,initStd);
		double preloss=100,curloss=10,cnt=0;
		double y,y_delta,derivative ;
		for (int iter = 1; iter <= numIterations; iter++) {
			for(int userIdx = 1; userIdx <numUsers; userIdx++){
				for(int itemIdx = 1; itemIdx <numItems; itemIdx++){
					DenseVector U_i=new DenseVector(numFactors);
							U_i=U.row(userIdx-1);
					DenseVector V_j=new DenseVector(numFactors);
							V_j=V.column(itemIdx-1);
					double r_ij=A.get(userIdx, itemIdx)-U_i.inner(V_j);
					for(int k=0;k<numFactors;k++){
						U.set(userIdx-1, k, U.get(userIdx-1, k)+lrate*(r_ij*V.get(k, itemIdx-1)-lamda*U.get(userIdx-1, k)));
						V.set(k, itemIdx-1, V.get(k, itemIdx-1)+lrate*(r_ij*U.get(userIdx-1, k)-lamda*V.get(k, itemIdx-1)));
					}	
				}				
			}  
			preloss=curloss;
			curloss=Loss(B,U,V);
			System.out.println("Iteration: "+(iter));
			System.out.println("Total Training Loss: "+Loss(A,U,V));
			System.out.println("Total Testing Loss: "+curloss);
		}
		
		System.out.println("Total Testing Loss: "+Loss(B,U,V));
		//System.out.println("right");
	}
	
	public static double Loss(SparseMatrix A,DenseMatrix U,DenseMatrix V) throws Exception{
		DenseMatrix A_approx;
		double loss=0;
		double square=0;
		double count=0;
		A_approx=U.mult(V);
		for(int i=1;i<A.numRows;i++){
			for(int j=1;j<A.numColumns;j++){
				if(A.get(i, j)!=0){
//					DenseVector U_i=new DenseVector(numFactors);
//					U_i=U.row(i-1);
//					DenseVector V_j=new DenseVector(numFactors);
//					V_j=V.column(j-1);
//					loss=A.get(i, j)-U_i.inner(V_j);
					loss=A_approx.get(i-1, j-1)-A.get(i,j);
					square+=loss*loss;
					count+=1;
				}				
			}
		}
		return Math.sqrt(square/count);
	}
	
}
