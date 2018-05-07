package gzl.sgd.multi;

import gzl.sgd.multi.DataSet;
import gzl.sgd.math.DenseMatrix;
import gzl.sgd.math.DenseVector;

public class Weights {

	public DenseMatrix U;
	public DenseMatrix V;
	public DenseVector ci;
	public DenseVector dj;
	
	public static int numFactors=96;
	public static int numUsers;
	public static int numItems;
	
	public Weights(){
		numUsers=DataSet.userNumber;
		numItems=DataSet.itemNumber;
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
	}
}
