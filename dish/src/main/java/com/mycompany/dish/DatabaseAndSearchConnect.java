package dish;

import java.util.List;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.util.UUID;

public class DatabaseAndSearchConnect {

    Cluster cluster;
    Session session;
    String myKeyspace;
    boolean Init;

    public DatabaseAndSearchConnect(String keyspace, String contactPoint) {
        // TODO Multiple contactPoint feature 
        cluster = Cluster.builder().addContactPoint(contactPoint).build();
        Init = false;

        try {
            session = cluster.connect(keyspace);
        } catch (Exception InvalidQueryException) {
            Init = true;
            myKeyspace = keyspace;
            System.out.println("ATTEN : Initialize for keyspace: " + keyspace);
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
                    + "name text , "
                    + "price double , "
                    + "catergory text , "
                    + "description text , "
                    + "section text , "
                    + "PRIMARY KEY (did));";

            String createDishTableIndex = "CREATE INDEX rid ON dish (rid)";
            session.execute(createDishTable);
            session.execute(createDishTableIndex);

            for (Restaurant restaurant : list) {
                String insertRestaurant = "INSERT INTO restaurant (rid, fsqid, name, lat, lng, catergory, rating)"
                        + " VALUES( uuid(), '" + restaurant.id + " ', $$" + restaurant.name + "$$, " + restaurant.location.lat + ","
                        + restaurant.location.lng + ", $$" + restaurant.categories[0] + "$$," + restaurant.rating + ");";

                session.execute(insertRestaurant);
            }

            for (Restaurant restaurant : list) {
  
                String getRid = "SELECT rid  FROM restaurant  WHERE fsqid = '" + restaurant.id + " ';";
                ResultSet ridResult = session.execute(getRid);
                UUID rUuid = null;
                for (Row r : ridResult) {
                    rUuid = r.getUUID(0);
                }
                if (rUuid == null) {
                    continue;
                }

                for (Dish d : restaurant.dishes) {
                    String insertDish = "INSERT INTO dish (did, rid, name, price, catergory, description, section)"
                            + " VALUES( uuid(), " + rUuid.toString() + " , $$" + d.name + "$$, " + Double.parseDouble(d.price) + ", $$"
                            + d.category + "$$, $$" + d.description + "$$, $$" + d.section + "$$);";
                    session.execute(insertDish);
                }
            }
        }
        
     
        String getAllDish =  "SELECT * FROM dish ";
        ResultSet allDish = session.execute(getAllDish);
        System.out.println("Dishes inserted into DB:" + allDish.all().size());
        
    }

    public void close() {
        cluster.close();
    }

}
