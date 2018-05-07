package gzl.sgd.multi;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import gzl.sgd.math.SparseMatrix;


public class DataSet {

	public static int userNumber=0;
	public static int itemNumber=0;
	
	
    public static SparseMatrix readDataSet(String file) throws FileNotFoundException {
    	SparseMatrix dataset;
    	Table<Integer, Integer, Double> dataTable = HashBasedTable.create();
    	Multimap<Integer, Integer> colMap = HashMultimap.create();
        Scanner scanner = new Scanner(new File(file));
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("#")) {
                continue;
            }
            String[] columns = line.split("\\::");

            int[] data = new int[3];
            data[0] = Integer.parseInt(columns[0]);
            data[1] = Integer.parseInt(columns[1]);
            double label = Double.parseDouble(columns[2]);
            if(data[0]>userNumber){
            	userNumber=data[0];
            }
            if(data[1]>itemNumber){
            	itemNumber=data[1];
            }
            dataTable.put(data[0], data[1], label);
            colMap.put(data[0], data[1]);
        }
        //System.out.println("user number: "+userNumber+" item number: "+itemNumber);
        userNumber++;
        itemNumber++;
        dataset=new SparseMatrix(userNumber,itemNumber,dataTable,colMap);
        dataTable = null;
        return dataset;
    }
    
    public static int getSize() {
       return userNumber*Weights.numFactors+Weights.numFactors*itemNumber;
    }
}
