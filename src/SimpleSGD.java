import gzl.sgd.common.LibrecException;
import gzl.sgd.math.DenseMatrix;
import gzl.sgd.math.DenseVector;
import gzl.sgd.math.SparseMatrix;
import gzl.sgd.math.VectorEntry;

public class SimpleSGD {

	public static int numIterations=10;
	public static int numFactors=10;
	public static double lamda=0.1;
	public static double h=0.1;
	
	public static int numUsers;
	public static int numItems;
	public static int numRates;
	
	public static SparseMatrix trainMatrix;
	public static SparseMatrix A;
	public static DenseMatrix U;
	public static DenseMatrix V;
	
	public static void main(String[] args) throws Exception{
		
		A=new SparseMatrix(trainMatrix);
		U=new DenseMatrix(numUsers, numFactors);
		V=new DenseMatrix(numFactors, numItems);
		double initMean=0.0f;
		double initStd=0.1f;
		U.init(initMean,initStd);
		V.init(initMean,initStd);
		double y,y_delta,derivative ;
		for (int iter = 1; iter <= numIterations; iter++) {
			
			for (int userIdx = 0; userIdx < numUsers; userIdx++) {
				for (int factorIdx = 0; factorIdx < numFactors; factorIdx++) {
					y=LossOfRow(userIdx,A,U,V);
					U.add(userIdx, factorIdx, h);
					y_delta=LossOfRow(userIdx,A,U,V);
					derivative =(y_delta-y)/h;
					U.set(userIdx, factorIdx, U.get(userIdx, factorIdx)-h-lamda*derivative);
				}
			}
			
			for (int factorIdx = 0; factorIdx < numFactors; factorIdx++) {
				for (int itemIdx = 0; itemIdx < numItems; itemIdx++) {
					y=LossOfCol(itemIdx,A,U,V);
					V.add(itemIdx, factorIdx, h);
					y_delta=LossOfCol(itemIdx,A,U,V);
					derivative =(y_delta-y)/h;
					V.set(factorIdx, itemIdx, V.get(factorIdx, itemIdx)-h-lamda*derivative);
				}
			}
			
		}
		//System.out.println("right");
	}
	
	public static double LossOfRow(int i,SparseMatrix A,DenseMatrix U,DenseMatrix V) throws Exception{
		DenseVector U_i=U.row(i);
		DenseVector A_i_approx=V.mult(U_i); //todo
		double loss=0;
		double square=0;
		double count=0;
		for(VectorEntry j : A.row(i)){
			loss=A_i_approx.get(j.index())-A.get(i, j.index());
			square+=loss*loss;
			count+=1;
		}
		return square/count;
	}
	
	public static double LossOfCol(int j,SparseMatrix A,DenseMatrix U,DenseMatrix V) throws Exception{
		DenseVector V_j=V.column(j);
		DenseVector A_j_approx=U.mult(V_j);//todo
		double loss=0;
		double square=0;
		double count=0;
		for(VectorEntry i : A.column(j)){
			loss=A_j_approx.get(i.index())-A.get(i.index(), j);
			square+=loss*loss;
			count+=1;
		}
		return square/count;
	}
}
