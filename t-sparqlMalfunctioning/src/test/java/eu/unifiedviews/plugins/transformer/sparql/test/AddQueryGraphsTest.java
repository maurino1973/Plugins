package eu.unifiedviews.plugins.transformer.sparql.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.rdfhelper.RDFHelper;
import eu.unifiedviews.plugins.transformer.sparql.SPARQL;

public class AddQueryGraphsTest {

    private static WritableRDFDataUnit writeRepository;

    private static TestEnvironment testEnvironment;

    private static SPARQL trans;

    private static String GRAPH_NAME;

    private static final Logger LOG = LoggerFactory.getLogger(
            AddQueryGraphsTest.class);

    @BeforeClass
    public static void initialize() throws DataUnitException {
        testEnvironment = new TestEnvironment();
        writeRepository = testEnvironment.createRdfInput("LocalRepository", false);
        GRAPH_NAME = writeRepository.addNewDataGraph("test").stringValue();
        trans = new SPARQL();
    }

    @AfterClass
    public static void clean() {
        testEnvironment.release();
    }

    @Test
    public void addGraphToInsertDataQuery() throws RepositoryException, DataUnitException {
        String originalQuery = "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
                + "PREFIX ns: <http://example.org/ns#>"
                + "INSERT DATA\n"
                + "{ <http://example/book1>  ns:price  42 } ";

        String expectedQuery = String.format(
                "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
                        + "PREFIX ns: <http://example.org/ns#>"
                        + "INSERT DATA\n"
                        + "{ GRAPH <%s> { <http://example/book1>  ns:price  42 } } ",
                GRAPH_NAME);

        String returnedQuery = trans.AddGraphToUpdateQuery(originalQuery,  new URIImpl(GRAPH_NAME));
        assertEquals(expectedQuery, returnedQuery);
        
        assertTrue("This update query can not be executed by transformer",
                tryExecuteUpdateQuery(originalQuery,  new URIImpl(GRAPH_NAME)));

    }

    @Test
    public void addGraphToDeleteDataQuery() throws RepositoryException, DataUnitException {
        String originalQuery = "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
                + "DELETE DATA\n"
                + "{ <http://example/book2> dc:title \"David Copperfield\" ;\n"
                + "dc:creator \"Edmund Wells\" . }";

        String expectedQuery = String.format(
                "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
                        + "DELETE DATA\n"
                        + "{ GRAPH <%s> { <http://example/book2> dc:title \"David Copperfield\" ;\n"
                        + "dc:creator \"Edmund Wells\" . } }",
                GRAPH_NAME);

        String returnedQuery = trans.AddGraphToUpdateQuery(originalQuery,  new URIImpl(GRAPH_NAME));
        assertEquals(expectedQuery, returnedQuery);
        
        assertTrue("This update query can not be executed by transformer",
                tryExecuteUpdateQuery(originalQuery,  new URIImpl(GRAPH_NAME)));

    }

    @Test
    public void addGraphToInsertDeleteQuery() throws RepositoryException, DataUnitException {
        String originalQuery = "PREFIX foaf:  <http://xmlns.com/foaf/0.1/>\n"
                + "DELETE { ?person foaf:givenName 'Bill' }\n"
                + "INSERT { ?person foaf:givenName 'William' }\n"
                + "WHERE\n"
                + "{ ?person foaf:givenName 'Bill' }";

        String expectedQuery = String.format(
                "PREFIX foaf:  <http://xmlns.com/foaf/0.1/>\n"
                        + " WITH <%s> "
                        + "DELETE { ?person foaf:givenName 'Bill' }\n"
                        + "INSERT { ?person foaf:givenName 'William' }\n"
                        + "WHERE\n"
                        + "{ ?person foaf:givenName 'Bill' }",
                GRAPH_NAME);
        
        String returnedQuery = trans.AddGraphToUpdateQuery(originalQuery, new URIImpl(GRAPH_NAME));
        assertEquals(expectedQuery, returnedQuery);
        
        assertTrue("This update query can not be executed by transformer",
                tryExecuteUpdateQuery(originalQuery,  new URIImpl(GRAPH_NAME)));
    }

    private boolean tryExecuteUpdateQuery(String updateQuery, URI graph) throws RepositoryException, DataUnitException {
        RepositoryConnection connection = null;
        try {
            connection = writeRepository.getConnection();

            trans.executeSPARQLUpdateQuery(connection, updateQuery, RDFHelper.getDatasetWithDefaultGraphs(writeRepository), graph);
            return true;
        } catch (DPUException e) {
            LOG.debug("Exception duering exectution query " + updateQuery + e
                    .getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }
        }
        return false;
    }
}
