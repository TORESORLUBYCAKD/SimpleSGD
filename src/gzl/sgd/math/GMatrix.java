package gzl.sgd.math;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import org.apache.commons.io.IOUtils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

public class GMatrix extends SparseMatrix{

	/** number of nonzero global feature */
    public int    num_global;
    /** number of nonzero user feature */
    public int    num_ufactor;
    /** number of nonzero item feature*/
    public int    num_ifactor;
    /**  number of rows in the sparse matrix */
    protected int num_row;
    /**  number of nonzero entries in sparse matrix */
    protected int num_val;
    
    Table<Integer, Integer, Double> dataTable = HashBasedTable.create();
	Multimap<Integer, Integer> colMap = HashMultimap.create();
	
	private static final long serialVersionUID = 1L;

	public GMatrix(int rows, int cols) {
		super(rows, cols);
		// TODO Auto-generated constructor stub
	}

	public GMatrix(int rows, int cols,Table<Integer, Integer, ? extends Number> dataTable,
            Multimap<Integer, Integer> colMap) {
		super(cols, cols, dataTable, colMap);
		// TODO Auto-generated constructor stub
	}
	
	public void loadNext(String line) {
        if (line == null || (line.length()) == 0) {
            System.out.println("line cannot be blank!");
        }

        Scanner scanner = new Scanner(line);
        scanner.skip("^(\\d+\\s+){1}");
        this.num_global += scanner.nextInt();
        this.num_ufactor += scanner.nextInt();
        this.num_ifactor += scanner.nextInt();

        scanner.useDelimiter(":+|\\s+");

        int uId = scanner.nextInt();        
        scanner.nextFloat();

        while (scanner.hasNextInt()) {
            int rowval = uId;
            int colval = scanner.nextInt();
            if(this.numRows<rowval) this.numRows=rowval;
            if(this.numColumns<colval) this.numColumns=colval;
            double vals = scanner.nextDouble();
            dataTable.put(rowval, colval, vals);
            colMap.put(rowval, colval);
        }
        IOUtils.closeQuietly(scanner);
    }
	
	public GMatrix loadData(String path){
		 BufferedReader reader = null;
		 GMatrix dataset = null;
	        try {
	            File file = new File(path);
	            reader = new BufferedReader(new FileReader(file));
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	            	this.loadNext(line);
	            }
//	            this.numRows++;
//	            this.numColumns++;
	            System.out.println("number of rows: "+this.numRows+" number of columns: "+this.numColumns);
	            dataset=new GMatrix(this.numRows,this.numColumns,dataTable,colMap);
	            dataTable = null;
	            
	        } catch (FileNotFoundException e) {
	        	System.out.println(e+" File Path: "+ path);
	        } catch (IOException e) {
	        	System.out.println(e+" Error In Reading");
	        } finally {
	            IOUtils.closeQuietly(reader);
	        }
	        return dataset;
	}
}
