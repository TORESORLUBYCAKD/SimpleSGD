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

	public static int userNumber=943;
	public static int itemNumber=1440;
	
	
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
            String[] columns = line.split("\\t");

            int[] data = new int[3];
            data[0] = Integer.parseInt(columns[0]);
            data[1] = Integer.parseInt(columns[1]);
            //data[2] =Integer.parseInt(columns[2]);
            double label = Double.parseDouble(columns[2]);
            //Instance instance = new Instance(label, data);
            //System.out.println(" user: "+data[0]+" item: "+data[1]+" rating: "+data[2]);
            //dataset.set(data[0], data[1], label);
            dataTable.put(data[0], data[1], label);
            colMap.put(data[0], data[1]);
        }
        dataset=new SparseMatrix(userNumber,itemNumber,dataTable,colMap);
        return dataset;
    }
}
