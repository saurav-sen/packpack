package weka.test.sample.code;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * 
 * @author Saurav
 *
 */
public class Main {

	public static void main1(String[] args) throws Exception {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		System.out.println(calendar.get(Calendar.DAY_OF_MONTH) + "_" + calendar.get(Calendar.MONTH) + "_" + calendar.get(Calendar.YEAR));
		/*Thread.sleep(5000);
		System.out.println(new Date().toString());*/
	}
	
	public static void main(String[] args) throws Exception {		
		File csvFile = new File("D:/Saurav/Weka-3-8/data/iris.csv");
		CSVLoader csvLoader = new CSVLoader();
		csvLoader.setSource(csvFile);
		Instances data = csvLoader.getDataSet();
		
		File arffFile = new File("D:/Saurav/Weka-3-8/data/iris_new.arff");
		ArffSaver arffSaver = new ArffSaver();
		arffSaver.setInstances(data);
		arffSaver.setFile(arffFile);
		//arffSaver.setDestination(arffFile);
		arffSaver.writeBatch();
		
		J48 tree = new J48();
		DataSource source = new DataSource("D:/Saurav/Weka-3-8/data/iris_new.arff");
		data = source.getDataSet();
		if(data.classIndex() == -1) {
			data.setClassIndex(data.numAttributes() - 1);
		}
		tree.buildClassifier(data);
		System.out.println(tree.toString());
		
		Attribute sepallength = new Attribute("sepallength");
		Attribute sepalwidth = new Attribute("sepalwidth");
		Attribute petallength = new Attribute("petallength");
		Attribute petalwidth = new Attribute("petalwidth");
		
		List<String> l = new ArrayList<String>();
		l.add("Iris-setosa");
		l.add("Iris-versicolor");
		l.add("Iris-virginica");
		
		Attribute classAttr = new Attribute("class", l);
		
		ArrayList<Attribute> attrList = new ArrayList<Attribute>();
		attrList.add(sepallength);
		attrList.add(sepalwidth);
		attrList.add(petallength);
		attrList.add(petalwidth);
		attrList.add(classAttr);
		
		Instances newData = new Instances("to_clasify", attrList, 1);
		
		double[] attValues = new double[newData.numAttributes()];
	    attValues[0] = 5.0;
	    attValues[1] = 4.2;
	    attValues[2] = 1.2;
	    attValues[3] = 0.74;

	    Instance newDataInstance = new SparseInstance(1.0, attValues);
	    newDataInstance.setDataset(newData);
	    newData.add(newDataInstance);
	    newData.setClassIndex(newData.numAttributes()-1);
	    
	    double classifyInstance = tree.classifyInstance(newDataInstance);
	    if(classifyInstance == 0) {
	    	System.out.println("Iris-setosa");
	    } else if(classifyInstance == 1) {
	    	System.out.println("Iris-versicolor");
	    } else if(classifyInstance == 2) {
	    	System.out.println("Iris-virginica");
	    }
	}
}