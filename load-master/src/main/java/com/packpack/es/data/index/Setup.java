package com.packpack.es.data.index;

import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Properties;

/**
 * 
 * @author Saurav
 *
 */
public class Setup {

	public static void main(String[] args) throws Exception {
		Properties p = new Properties();
		p.load(new FileReader(new File(/*"D:/Saurav/packpack/load-master/src/conf/elasticsearch.properties")));*/"../conf/elasticsearch.properties")));
		Iterator<Object> itr = p.keySet().iterator();
		while(itr.hasNext()) {
			String key = (String) itr.next();
			String value = (String) p.get(key);
			System.setProperty(key, value);
		}
		new LocalityMasterSetup().setup();
	}
}