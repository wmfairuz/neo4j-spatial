package maylab.my.neo4j;

import java.io.IOException;
import java.util.List;

import org.neo4j.gis.spatial.EditableLayer;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.gis.spatial.pipes.GeoPipeFlow;
import org.neo4j.gis.spatial.pipes.GeoPipeline;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import com.vividsolutions.jts.geom.Coordinate;

public class TestNeo4J {
	private GraphDatabaseService graphDb = null;
    private SpatialDatabaseService spatialDb = null;
    private EditableLayer layer = null;
    
    public TestNeo4J() {
    	// Create the graph db
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("neo4j-spatial.db");
        
        // Wrap it as a spatial db service        
        spatialDb = new SpatialDatabaseService(graphDb);

        // Create/get the layer to store our spatial data
        layer = (EditableLayer) spatialDb.getLayer("geo-equipment");
        	
        if(layer != null){
        	System.out.println("Using existing layer " + layer.getName());
        } else {
        	layer = (EditableLayer) spatialDb.createSimplePointLayer("geo-equipment", "longitude", "latitude");
        	System.out.println("Creating new layer " + layer.getName());
        }
    }
    
    public void createTestNodes() {
    	Transaction tx = null; 
    	try {
    		tx = graphDb.beginTx();

            layer.add(createNode("node1", 11.59362, 43.14720));
            layer.add(createNode("node2", 11.59374, 43.14672));
            layer.add(createNode("node3", 11.59355, 43.14786));
            layer.add(createNode("node4", 11.59429, 43.14712));
            layer.add(createNode("node5", 11.59287, 43.14709));
            layer.add(createNode("node6", 11.59403, 43.14516));
            layer.add(createNode("node7", 11.59837, 43.14814));
            layer.add(createNode("node8", 11.57406, 43.13187));
            
            tx.success();
    	}catch(Exception ex){
    		if (tx != null){
                tx.failure();
            }
            throw new RuntimeException(ex.getMessage());
    	} finally {
            if (tx != null){
                tx.finish();
            }
        } 
    }
    
    private Node createNode(String name, double latitude, double longitude){
    	Node node = graphDb.createNode();
    	node.setProperty("name", name);
    	node.setProperty("latitude", latitude);
    	node.setProperty("longitude", longitude);
        
        System.out.println("Node ID " + node.getId());
        return node;
    }
    
    public void testFindClosest(){
    	Coordinate coordinate = new Coordinate(43.14720, 11.59362);
    	List<GeoPipeFlow> points = GeoPipeline
                .startNearestNeighborLatLonSearch(layer, coordinate, 1.0)
                .sort("OrthodromicDistance").toList();
    	System.out.println("Closest points " + points.size());
    	checkPointOrder(points);
    }
    
    private void checkPointOrder(List<GeoPipeFlow> results) {
		for (int i = 0; i < results.size(); i++) {
			GeoPipeFlow first = results.get(i);
			double d1 = (Double) first.getProperties().get("OrthodromicDistance");
			int distance = (int) (d1*1000);
			System.out.println("Point at position " + i + " (d=" + distance + " meters)");
		}
	}
    
    public static void main(String[] args) throws IOException {
    	TestNeo4J test = new TestNeo4J();
    	test.createTestNodes();
    	test.testFindClosest();
    }
}
