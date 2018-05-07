package gzl.sgd.multi;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import gzl.sgd.math.DenseMatrix;
import gzl.sgd.math.DenseVector;
import gzl.sgd.math.SparseMatrix;

public class MultiSGD {
	public static SparseMatrix data;
	public static double lamda;
	public static double lamda2;
	public static double lrate;
	public static double global_mean;
	public static Weights weights;
	private static final int NUM_CORES = Runtime.getRuntime().availableProcessors();
	public MultiSGD(SparseMatrix dataSet,double lamda,double lamda2,double lrate,double global_mean){
		this.data=dataSet;
		this.lamda=lamda;
		this.lamda2=lamda2;
		this.lrate=lrate;
		this.global_mean=global_mean;
		this.weights=new Weights();
	}
	
	public Weights run(int cores) throws InterruptedException {
		if (data == null) {
            throw new IllegalArgumentException("MultiSGD: data set cannot be null");
        }
		final int coresToUse = (cores < 1 || cores > NUM_CORES) ? NUM_CORES : cores;
		resetFields();
		//setup thread pool
        ExecutorService pool = Executors.newFixedThreadPool(coresToUse);
        //initialize each thread
        for (int thread = 0; thread < coresToUse; thread++) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    Weights result = new Weights();
                    for (int i = 0; i < DataSet.getSize() * 4/ coresToUse; i++) {
                       sampleAndUpdate(result);
                    }
                    updateSharedWeights(result, coresToUse);                    
                }
            });
        }

        pool.shutdown();
        //block until all threads complete
        pool.awaitTermination(120, TimeUnit.HOURS);
        return weights;
		
	}
	 public void sampleAndUpdate(Weights threadWeights) {
		 	Random rand=new Random();
			Random rand1=new Random();
			Random rand2=new Random();
			int userIdx=rand1.nextInt(DataSet.userNumber-1);
			int itemIdx=rand2.nextInt(DataSet.itemNumber-1);	
			DenseVector U_i=threadWeights.U.row(userIdx);
			DenseVector V_j=threadWeights.V.column(itemIdx);
			double r_ij=data.get(userIdx, itemIdx)-U_i.inner(V_j)-threadWeights.ci.get(userIdx)-threadWeights.dj.get(itemIdx);
			for(int k=0;k<threadWeights.numFactors;k++){
				threadWeights.U.set(userIdx, k, threadWeights.U.get(userIdx, k)+lrate*(r_ij*threadWeights.V.get(k, itemIdx)-lamda*threadWeights.U.get(userIdx, k)));
				threadWeights.V.set(k, itemIdx, threadWeights.V.get(k, itemIdx)+lrate*(r_ij*threadWeights.U.get(userIdx, k)-lamda*threadWeights.V.get(k, itemIdx)));
				threadWeights.ci.set(userIdx, threadWeights.ci.get(userIdx)+lrate*(r_ij-lamda2*(threadWeights.ci.get(userIdx)+threadWeights.dj.get(itemIdx)-global_mean)));
				threadWeights.dj.set(itemIdx, threadWeights.dj.get(itemIdx)+lrate*(r_ij-lamda2*(threadWeights.ci.get(userIdx)+threadWeights.dj.get(itemIdx)-global_mean)));
			}
	    }
	 //此处使用了synchronized
	 public synchronized void updateSharedWeights(Weights result, int coresToUse){
		 for(int i=0;i<DataSet.userNumber;i++){
			 weights.ci.set(i, weights.ci.get(i)+result.ci.get(i)/coresToUse);
			 for(int j=0;j<weights.numFactors;j++){
				 weights.U.set(i, j, weights.U.get(i,j)+result.U.get(i, j)/coresToUse);
			 }
		 }
		 for(int i=0;i<DataSet.itemNumber;i++){
			 weights.dj.set(i, weights.dj.get(i)+result.dj.get(i)/coresToUse);
			 for(int j=0;j<weights.numFactors;j++){
				 weights.V.set(i, j, weights.V.get(i,j)+result.V.get(i, j)/coresToUse);
			 }
		 }
	 }
	public void resetFields(){
		 //clear weights and start from fresh
        weights = new Weights();
        
	}
	
}
