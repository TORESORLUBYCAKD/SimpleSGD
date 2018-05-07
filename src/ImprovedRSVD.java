import gzl.sgd.math.DenseMatrix;
import gzl.sgd.math.DenseVector;
import gzl.sgd.math.SparseMatrix;
import java.util.Random;

public class ImprovedRSVD {

	public static int numIterations=10;
	public static int numFactors=96;
	public static double lamda=0.02;
	public static double lamda2=0.05;
	public static double lrate=0.001;
	public static double global_mean=3.6033;
	
	public static int numUsers;
	public static int numItems;
	public static int numRates;
	
	public static SparseMatrix trainMatrix;
	public static SparseMatrix testMatrix;
	public static SparseMatrix TrainMatrix;
	public static SparseMatrix TestMatrix;
	public static DenseMatrix U;
	public static DenseMatrix V;
	public static DenseVector ci;
	public static DenseVector dj;
	
	public static void main(String[] args) throws Exception{
		
		
		numUsers=DataSet.userNumber;
		numItems=DataSet.itemNumber;
		TrainMatrix=new SparseMatrix(numUsers,numItems);
		TrainMatrix=DataSet.readDataSet("dataset.txt");
		TestMatrix=new SparseMatrix(numUsers,numItems);
		TestMatrix=DataSet.readDataSet("testSet.txt");
		U=new DenseMatrix(numUsers, numFactors);
		V=new DenseMatrix(numFactors, numItems);
		ci=new DenseVector(numUsers);
		dj=new DenseVector(numItems);
		double initMean=0.0f;
		double initStd=0.1f;
		U.init(initMean,initStd);
		V.init(initMean,initStd);
		ci.init(initMean, initStd);
		dj.init(initMean, initStd);
		double preloss=100,curloss=10,cnt=0;
		while(true) {
			Random rand=new Random();
			Random rand1=new Random();
			Random rand2=new Random();
			int userIdx=rand1.nextInt(numUsers-1);
			int itemIdx=rand2.nextInt(numItems-1);	
			DenseVector U_i=U.row(userIdx);
			DenseVector V_j=V.column(itemIdx);
			double r_ij=TrainMatrix.get(userIdx, itemIdx)-U_i.inner(V_j)-ci.get(userIdx)-dj.get(itemIdx);
			for(int k=0;k<numFactors;k++){
				U.set(userIdx, k, U.get(userIdx, k)+lrate*(r_ij*V.get(k, itemIdx)-lamda*U.get(userIdx, k)));
				V.set(k, itemIdx, V.get(k, itemIdx)+lrate*(r_ij*U.get(userIdx, k)-lamda*V.get(k, itemIdx)));
				ci.set(userIdx, ci.get(userIdx)+lrate*(r_ij-lamda2*(ci.get(userIdx)+dj.get(itemIdx)-global_mean)));
				dj.set(itemIdx, dj.get(itemIdx)+lrate*(r_ij-lamda2*(ci.get(userIdx)+dj.get(itemIdx)-global_mean)));
			}
			cnt++;
			if(cnt%50000==0){
				preloss=curloss;
				curloss=Loss(TestMatrix,U,V);
//				if(preloss<curloss){
//					System.out.println("preloss: "+preloss+"curloss: "+curloss);
//					break;
//				}
				System.out.println("Total Training Loss: "+Loss(TrainMatrix,U,V));
				System.out.println("Total Testing Loss: "+curloss);
			}			
			//System.out.println("Total Testing Loss: "+Loss(TestMatrix,U,V));
		}
		
	}
	
	public static double Loss(SparseMatrix A,DenseMatrix U,DenseMatrix V) throws Exception{
		DenseMatrix A_approx;
		double loss=0;
		double square=0;
		double count=0;
		A_approx=U.mult(V);
		for(int i=0;i<A.numRows;i++){
			for(int j=0;j<A.numColumns;j++){
				if(A.get(i, j)!=0){
					loss=A_approx.get(i, j)+ci.get(i)+dj.get(j)-A.get(i, j);
					square=loss*loss;
					count+=1;
				}				
			}
		}
		return Math.sqrt(square/count);
	}

}
