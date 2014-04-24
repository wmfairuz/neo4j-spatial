package maylab.my.neo4j;

import java.io.IOException;

import org.neo4j.gis.spatial.EditableLayer;
import org.neo4j.gis.spatial.EditableLayerImpl;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.gis.spatial.encoders.SimplePointEncoder;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class TestNeo4J {
	private EmbeddedGraphDatabase graphDb = null;
    private SpatialDatabaseService spatialDb = null;
    private EditableLayer layer = null;
    
    public TestNeo4J() {
    	// Create the graph db
        graphDb = new EmbeddedGraphDatabase("target/neo4j-spatial.db");
        System.out.println(graphDb.getStoreDir() + " Created");
        
        // Wrap it as a spatial db service
        
        spatialDb = new SpatialDatabaseService(graphDb);
        // Create the layer to store our spatial data
        layer = (EditableLayer) spatialDb.getOrCreateLayer("equipment", SimplePointEncoder.class, EditableLayerImpl.class, "lon:lat");
    }
    
    public static void main(String[] args) throws IOException {
    	TestNeo4J test = new TestNeo4J();
    }
}
