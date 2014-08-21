package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPUException;

public class AddQueryGraphsTest {

    private static WritableRDFDataUnit repository;

    private static TestEnvironment testEnvironment;

    private static SPARQLTransformer trans;

    private static String GRAPH_NAME;

    private static URI GRAPH_NAME_URI;

    private static final Logger LOG = LoggerFactory.getLogger(
            AddQueryGraphsTest.class);

    @BeforeClass
    public static void initialize() throws DataUnitException {
        testEnvironment = new TestEnvironment();
        repository = testEnvironment.createRdfInput("LocalRepository", false);
        GRAPH_NAME_URI = repository.addNewDataGraph("fdsafds");
        GRAPH_NAME = GRAPH_NAME_URI.stringValue();
        trans = new SPARQLTransformer();

    }

    @AfterClass
    public static void clean() {
        testEnvironment.release();
    }

    @Test
    public void addGraphToInsertDataQuery() throws RepositoryException {
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

        String returnedQuery = trans.AddGraphToUpdateQuery(originalQuery, GRAPH_NAME_URI);
        assertEquals(expectedQuery, returnedQuery);

        assertTrue("This update query can not be executed by transformer",
                tryExecuteUpdateQuery(originalQuery));

    }

    @Test
    public void addGraphToDeleteDataQuery() throws RepositoryException {
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

        String returnedQuery = trans.AddGraphToUpdateQuery(originalQuery, GRAPH_NAME_URI);
        assertEquals(expectedQuery, returnedQuery);

        assertTrue("This update query can not be executed by transformer",
                tryExecuteUpdateQuery(originalQuery));

    }

    @Test
    public void addGraphToInsertDeleteQuery() throws RepositoryException {
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
        String returnedQuery = trans.AddGraphToUpdateQuery(originalQuery, GRAPH_NAME_URI);
        assertEquals(expectedQuery, returnedQuery);

        assertTrue("This update query can not be executed by transformer",
                tryExecuteUpdateQuery(originalQuery));
    }

    private boolean tryExecuteUpdateQuery(String updateQuery) throws RepositoryException {
        RepositoryConnection connection = null;
        try {
            connection = repository.getConnection();

            DatasetImpl dataSet = new DatasetImpl();
            dataSet.addDefaultGraph(GRAPH_NAME_URI);
            dataSet.addNamedGraph(GRAPH_NAME_URI);
            trans.executeSPARQLUpdateQuery(connection, updateQuery, dataSet, GRAPH_NAME_URI);
            return true;
        } catch (DataUnitException | DPUException e) {
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
