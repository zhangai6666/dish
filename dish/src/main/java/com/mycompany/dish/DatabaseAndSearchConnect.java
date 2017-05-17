package dish;

import java.util.List;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class DatabaseAndSearchConnect {

    Cluster cluster;
    Client client;
    Session session;
    String myKeyspace;
    String myIndex;
    boolean Init;
    
    // Constructor
    public DatabaseAndSearchConnect(String keyspace, String contactPoint, String index, boolean ReqInit) {
         
        //Determin what to do in InitOrUpdate
        Init = ReqInit;
        myKeyspace = keyspace;
        myIndex = index;
        
        // Connect to ElasticSearch
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(contactPoint), 9300));
        } catch (UnknownHostException ex) {
            System.out.println("ERROR: ElasticSearch Connection Failed" + ex.toString());
        }
        
        // Connect to Cassandra DB
        try {
            cluster = Cluster.builder().addContactPoint(contactPoint).build();
        } catch (Exception e) {
            System.out.println("ERROR: Cassandra Connection Failed" + e.toString());
        }
 
        
        // Setup Cassandra Session
        try {
            session = cluster.connect(myKeyspace);
        } catch (Exception InvalidQueryException) {  
            System.out.println("Keyspace: " + myKeyspace + "  Not yet exist");
            System.out.println("Creating Keyspace" + myKeyspace);
            
            Session sessionInit = cluster.connect();
            String createKeyspace = "CREATE KEYSPACE " + myKeyspace + " WITH replication "
                    + "= {'class':'SimpleStrategy', 'replication_factor':1}; ";
            sessionInit.execute(createKeyspace);
            
            session = cluster.connect(myKeyspace);
        }
    }

    // Initialize or Update DB and Elastic Search
    public void InitOrUpdate(List<Restaurant> list) {

        if (Init) {
            
            // Create Talble restaurant and dish
            String createRestaurantTable = "CREATE TABLE restaurant( "
                    + "rid uuid , "
                    + "fsqid text , "
                    + "name text , "
                    + "lat float , "
                    + "lng float , "
                    + "catergory text , "
                    + "rating float , "
                    + "PRIMARY KEY (rid));";

            String createRestaurantTableIndex = "CREATE INDEX fsqid ON restaurant (fsqid)";
            session.execute(createRestaurantTable);
            session.execute(createRestaurantTableIndex);

            String createDishTable = "CREATE TABLE dish( "
                    + "did uuid , "
                    + "rid uuid , "
                    + "rName text , "
                    + "name text , "
                    + "price double , "
                    + "catergory text , "
                    + "description text , "
                    + "lat float , "
                    + "lng float , "
                    + "type text , "
                    + "PRIMARY KEY (did));";

            String createDishTableIndex = "CREATE INDEX rid ON dish (rid)";
            session.execute(createDishTable);
            session.execute(createDishTableIndex);

            for (Restaurant restaurant : list) {
                
                // Insert restaurant into DB 
                String insertRestaurant = "INSERT INTO restaurant (rid, fsqid, name, lat, lng, catergory, rating)"
                        + " VALUES( uuid(), '" + restaurant.id + " ', $$" + restaurant.name + "$$, " + restaurant.location.lat + ","
                        + restaurant.location.lng + ", $$" + restaurant.categories[0] + "$$," + restaurant.rating + ");";
                session.execute(insertRestaurant);
                
                // Fetch rid for current restaurant
                String getRid = "SELECT rid  FROM restaurant  WHERE fsqid = '" + restaurant.id + " ';";
                ResultSet ridResult = session.execute(getRid);
                UUID rUUid = ridResult.one().getUUID(0);
                
                // Fetch restaurant as JSON from DB
                String restJson = "SELECT json *  FROM restaurant  WHERE fsqid = '" + restaurant.id + " ';";
                ResultSet restaurantResult = session.execute(restJson);
                String json = restaurantResult.one().getString(0);
               
                // Insert restaurant into Elasticsearch
                client.prepareIndex(myIndex, "restaurant", rUUid.toString()).setSource(json).get();
                
                // Insert dishes into DB
                for (Dish d : restaurant.dishes) {
                    String insertDish = "INSERT INTO dish (did, rid, rName, name, price, catergory, description, lat, lng, type)"
                            + " VALUES( uuid(), " + rUUid.toString() + " , $$" + restaurant.name + "$$, $$" + d.name + "$$, " + Double.parseDouble(d.price) + ", $$"
                            + d.category + "$$, $$" + d.description + "$$, " + restaurant.location.lat + ","
                        + restaurant.location.lng + ", $$" + d.type + "$$);";
                    session.execute(insertDish);
                }
                
                // Insert dishes into Elastisearch
                String getAllDish = "SELECT * FROM dish WHERE rid =" + rUUid;
                ResultSet allDish = session.execute(getAllDish);
                for (Row r : allDish) {
                    UUID dUUid = r.getUUID(0);
                    String getDishJson = "SELECT json * FROM dish WHERE did =" + dUUid;
                    String dishJson = session.execute(getDishJson).one().getString(0);
                    client.prepareIndex(myIndex, "dish", dUUid.toString()).setSource(dishJson).get();
                }
            }
            
            

        }
        
        
        // Print out INFO
        ResultSet allDish = session.execute("SELECT * FROM dish ");
        System.out.println("Dishes inserted into DB:" + allDish.all().size());
        

    }
    
    
    public void cleanup() {
        session.execute("DROP KEYSPACE " + myKeyspace);
        client.admin().indices().delete(new DeleteIndexRequest(myIndex));
        Init = true;
    }

    public void close() {
        cluster.close();
        client.close();
    }

}
