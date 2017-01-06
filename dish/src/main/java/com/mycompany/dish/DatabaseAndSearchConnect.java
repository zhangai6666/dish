package dish;

import java.util.List;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class DatabaseAndSearchConnect {
	Cluster cluster;
	Session session;
	boolean Init;
	
	
	public DatabaseAndSearchConnect(String keyspace, String contactPoint) {
		// TODO Multiple contactPoint feature 
		cluster = Cluster.builder().addContactPoint(contactPoint).build();
		Init = false;
	
		try {
			session = cluster.connect(keyspace);
		} catch (Exception InvalidQueryException) {
			Init = true;
			System.out.println("Initialize for keyspace: " + keyspace);
			Session sessionInit = cluster.connect();
			String createKeyspace = "CREATE KEYSPACE " + keyspace + " WITH replication "
					   + "= {'class':'SimpleStrategy', 'replication_factor':1}; ";
			sessionInit.execute(createKeyspace);
			session = cluster.connect(keyspace);
		}
	}
	
	// Initialize or Update DB and Elastic Search
	public void InitOrUpdate(List<Restaurant> list) {
		
		if (Init) {
		      String createRestaurantTable = "CREATE TABLE restaurant( "
		    	         + "rid uuid , "
		    	         + "fsqid text , "
		    	         + "name text , "
		    	         + "lat double , "
		    	         + "lng double , "  	         
		    	         + "catergory text , "
		    	         + "rating double , "
		    	         + "PRIMARY KEY (rid));";
		      
		      String createRestaurantTableIndex = "CREATE INDEX fsqid ON restaurant (fsqid)";
		      
		      session.execute(createRestaurantTable);
		      session.execute(createRestaurantTableIndex);
		      
		      String createDishTable = "CREATE TABLE dish( "
		    	         + "did uuid , "
		    	         + "rid uuid , "
		    	         + "name text , "
		    	         + "price double , "  	         
		    	         + "catergory text , "
		    	         + "description text , "
		    	         + "section text , "
		    	         + "PRIMARY KEY (did));";
		    
		      String createDishTableIndex = "CREATE INDEX rid ON dish (rid)";
		      
		      session.execute(createDishTable);
		      session.execute(createDishTableIndex);
		     
//		      for(Restaurant restaurant : list) {
//		    	  
//		    	  String insertRestaurant = "INSERT INTO restaurant (rid, fsqid, name, lat, lng, catergory, rating)"
//		    			    + " VALUES('ram', 'Hyderabad', 9848022338, 50000);" ; 
//		    	  session.execute(insertRestaurant);
//		    	  	    	  
//		      }	
		} 
		
	}

public void close() {
    cluster.close();
}
        
	
	
}


