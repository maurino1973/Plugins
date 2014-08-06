package eu.unifiedviews.plugins.loader.rdftosparql;

import eu.unifiedviews.plugins.loader.rdftosparql.RdfToSparqlEndpointConfig_V1;
import eu.unifiedviews.plugins.loader.rdftosparql.InsertType;
import eu.unifiedviews.plugins.loader.rdftosparql.LoaderEndpointParams;
import eu.unifiedviews.plugins.loader.rdftosparql.SPARQLoader;
import eu.unifiedviews.plugins.loader.rdftosparql.WriteGraphType;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;

/**
 * Test funcionality loading to SPARQL endpoint.
 * 
 * @author Jiri Tomes
 */
public class SPARQLLoaderTest1 {

    private final Logger logger = LoggerFactory.getLogger(
            SPARQLLoaderTest1.class);

    private LoaderEndpointParams virtuosoParams = new LoaderEndpointParams();

    private static WritableRDFDataUnit repository;

//        private static final String HOST_NAME = "v7.xrg.cz";
//
//	private static final String PORT = "1121";
//
//	private static final String USER = "dba";
//
//	private static final String PASSWORD = "dba";
//
//	private static final String DEFAULT_GRAPH = "http://test/loader/speed/1";
//
//	private static final String UPDATE_ENDPOINT = "http://v7.xrg.cz:8901/sparql-auth";
//        //private static final String UPDATE_ENDPOINT = "http://v7.xrg.cz:8901/sparql-graph-crud-auth";

    private static final String URL = "jdbc:virtuoso://odcs.xrg.cz:1120/charset=UTF-8/log_enable=2";

    private static final String USER = "dba";

    private static final String PASSWORD = "dba01OD";

    private static final String INPUT_GRAPH = "http://test/loader/speed/16/input";

    private static final String OUTPUT_GRAPH = "http://test/loader/speed/16/output";

    private static final String UPDATE_ENDPOINT = "http://odcs.xrg.cz:8900/sparql-auth";

    //private static final String UPDATE_ENDPOINT = "http://odcs.xrg.cz:8900/sparql-graph-crud-auth";

//        private static final String HOST_NAME = "localhost";
//
//	private static final String PORT = "1111";
//
//	private static final String USER = "dba";
//
//	private static final String PASSWORD = "dba";
//
//	private static final String DEFAULT_GRAPH = "http://test/loader/speed/3/1";
//
//	private static final String UPDATE_ENDPOINT = "http://localhost:8890/sparql-auth";
////        private static final String UPDATE_ENDPOINT = "http://localhost:8890/sparql-graph-crud-auth";

    @BeforeClass
    public static void setRDFDataUnit() throws DPUException {

//		repository = new VirtuosoRDFDataUnit(URL, 
//				USER, PASSWORD, "input", INPUT_GRAPH);

    }

    @AfterClass
    public static void deleteRDFDataUnit() {
        //((ManagableRdfDataUnit) repository).delete();
    }

    private void tryInsertToSPARQLEndpoint() {

        String goalGraphName = OUTPUT_GRAPH;

        boolean isLoaded = false;
        RdfToSparqlEndpointConfig_V1 c = new RdfToSparqlEndpointConfig_V1();
        c.setEndpointParams(virtuosoParams);
        c.setHost_name(USER);
        c.setPassword(PASSWORD);
        c.setSPARQL_endpoint(UPDATE_ENDPOINT);
        c.setGraphsUri(Arrays.asList(goalGraphName));
        c.setInsertOption(InsertType.SKIP_BAD_PARTS);
        c.setGraphOption(WriteGraphType.OVERRIDE);
        
        SPARQLoader loader = new SPARQLoader(repository, getTestContext(),
               c);
        try {

            loader.loadToSPARQLEndpoint();
            isLoaded = true;

        } catch (DPUException e) {
            logger.error("INSERT  failed");

        } finally {
//			try {
//				loader.clearEndpointGraph(endpoint, goalGraphName);
//			} catch (RDFException e) {
//				logger.error(
//						"TEMP graph <" + goalGraphName + "> was not delete");
//			}
        }

        assertTrue(isLoaded);
        try {
            Thread.sleep(100000);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(SPARQLLoaderTest1.class.getName()).log(Level.SEVERE, null, ex);
        }
        logger.info("Test finished!");
    }

    private DPUContext getTestContext() {
        TestEnvironment environment = new TestEnvironment();
        return environment.getContext();
    }

}
