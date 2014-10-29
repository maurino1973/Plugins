package eu.unifiedviews.plugins.transformer.sparql.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.turtle.TurtleWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.helpers.dataunit.rdfhelper.RDFHelper;
import eu.unifiedviews.plugins.transformer.sparql.SPARQL;
import eu.unifiedviews.plugins.transformer.sparql.SPARQLConfig_V1;
import eu.unifiedviews.plugins.transformer.sparql.SPARQLQueryPair;

public class SPARQLLegacyTest {
    private static final Logger LOG = LoggerFactory.getLogger(SPARQLLegacyTest.class);

    @Test
    public void addAllTest() throws Exception {
        // prepare dpu
        SPARQL trans = new SPARQL();

        String SPARQL_Update_Query = "CONSTRUCT {?s ?p ?o} where {?s ?p ?o }";
        boolean isConstructType = true;

        SPARQLConfig_V1 config = new SPARQLConfig_V1(
                SPARQL_Update_Query, isConstructType);

        trans.configureDirectly(config);

        // prepare test environment
        TestEnvironment env = new TestEnvironment();

        // prepare data units
        WritableRDFDataUnit input = env.createRdfInput("input", false);
        WritableRDFDataUnit output = env.createRdfOutput("output", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");

        RepositoryConnection connection = null;
        RepositoryConnection connection2 = null;
        try {
            connection = input.getConnection();
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, "", RDFFormat.TURTLE, graph);
            ByteArrayOutputStream inputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(inputBos), graph);
            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // run
            env.run(trans);

            connection2 = output.getConnection();
            // verify result
            assertTrue(connection.size(graph) == connection2.size(RDFHelper.getGraphsURIArray(output)));
            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection2.export(new TurtleWriter(outputBos), RDFHelper.getGraphsURIArray(output));

            assertEquals(inputBos.toString("UTF-8"), outputBos.toString("UTF-8"));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }
            if (connection2 != null) {
                try {
                    connection2.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            } // release resources
            env.release();
        }
    }

    @Test
    public void addAllAndNew() throws Exception {
        // prepare dpu
        SPARQL trans = new SPARQL();

        SPARQLConfig_V1 config = new SPARQLConfig_V1();
        config.setQueryPairs(Arrays.asList(
                new SPARQLQueryPair("CONSTRUCT {?s ?p ?o} where {?s ?p ?o }", true),
                new SPARQLQueryPair("CONSTRUCT {?s <http://test> ?o . } where {?s ?p ?o}", true)));

        trans.configureDirectly(config);

        // prepare test environment
        TestEnvironment env = new TestEnvironment();

        // prepare data units
        WritableRDFDataUnit input = env.createRdfInput("input", false);
        WritableRDFDataUnit output = env.createRdfOutput("output", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");
        InputStream addAllOnceAgainInputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("addAllAndNew.ttl");

        RepositoryConnection connection = null;
        RepositoryConnection connection2 = null;
        try {
            connection = input.getConnection();
            String baseURI = "";
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, baseURI, RDFFormat.TURTLE, graph);

            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // run
            env.run(trans);
            connection2 = output.getConnection();
            // verify result
            assertTrue(connection.size(graph) * 2 == connection2.size(RDFHelper.getGraphsURIArray(output)));
            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection2.export(new TurtleWriter(outputBos), RDFHelper.getGraphsURIArray(output));

            assertEquals(IOUtils.toString(addAllOnceAgainInputStream, "UTF-8"), outputBos.toString("UTF-8"));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }
            if (connection2 != null) {
                try {
                    connection2.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            } // release resources
            env.release();
        }
    }

    @Test
    public void addAllAndNew2() throws Exception {
        // prepare dpu
        SPARQL trans = new SPARQL();

        SPARQLConfig_V1 config = new SPARQLConfig_V1();
        config.setQueryPairs(Arrays.asList(
                new SPARQLQueryPair("CONSTRUCT {?s ?p ?o} where {?s ?p ?o }", true),
                new SPARQLQueryPair("INSERT {?s <http://test> ?o . } where {?s ?p ?o}", false)));

        trans.configureDirectly(config);

        // prepare test environment
        TestEnvironment env = new TestEnvironment();

        // prepare data units
        WritableRDFDataUnit input = env.createRdfInput("input", false);
        WritableRDFDataUnit output = env.createRdfOutput("output", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");
        InputStream addAllOnceAgainInputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("addAllAndNew.ttl");

        RepositoryConnection connection = null;
        RepositoryConnection connection2 = null;
        try {
            connection = input.getConnection();
            String baseURI = "";
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, baseURI, RDFFormat.TURTLE, graph);

            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // run
            env.run(trans);
            connection2 = output.getConnection();
            // verify result
            assertTrue(connection.size(graph) * 2 == connection2.size(RDFHelper.getGraphsURIArray(output)));
            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection2.export(new TurtleWriter(outputBos), RDFHelper.getGraphsURIArray(output));

            assertEquals(IOUtils.toString(addAllOnceAgainInputStream, "UTF-8"), outputBos.toString("UTF-8"));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }
            if (connection2 != null) {
                try {
                    connection2.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            } // release resources
            env.release();
        }
    }

    @Test
    public void addAllAndNew3() throws Exception {
        // prepare dpu
        SPARQL trans = new SPARQL();

        SPARQLConfig_V1 config = new SPARQLConfig_V1();
        config.setQueryPairs(Arrays.asList(
                new SPARQLQueryPair("INSERT {?s <http://test> ?o . } where {?s ?p ?o}", false)));

        trans.configureDirectly(config);

        // prepare test environment
        TestEnvironment env = new TestEnvironment();

        // prepare data units
        WritableRDFDataUnit input = env.createRdfInput("input", false);
        WritableRDFDataUnit output = env.createRdfOutput("output", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");
        InputStream addAllOnceAgainInputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("addAllAndNew.ttl");

        RepositoryConnection connection = null;
        RepositoryConnection connection2 = null;
        try {
            connection = input.getConnection();
            String baseURI = "";
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, baseURI, RDFFormat.TURTLE, graph);

            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // run
            env.run(trans);
            connection2 = output.getConnection();
            // verify result
            assertTrue(connection.size(graph) * 2 == connection2.size(RDFHelper.getGraphsURIArray(output)));
            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection2.export(new TurtleWriter(outputBos), RDFHelper.getGraphsURIArray(output));

            assertEquals(IOUtils.toString(addAllOnceAgainInputStream, "UTF-8"), outputBos.toString("UTF-8"));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }
            if (connection2 != null) {
                try {
                    connection2.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            } // release resources
            env.release();
        }
    }

    @Test
    public void addOnlyNew() throws Exception {
        // prepare dpu
        SPARQL trans = new SPARQL();

        SPARQLConfig_V1 config = new SPARQLConfig_V1();
        config.setQueryPairs(Arrays.asList(
                new SPARQLQueryPair("CONSTRUCT {?s <http://test> ?o . } where {?s ?p ?o}", true)));

        trans.configureDirectly(config);

        // prepare test environment
        TestEnvironment env = new TestEnvironment();

        // prepare data units
        WritableRDFDataUnit input = env.createRdfInput("input", false);
        WritableRDFDataUnit output = env.createRdfOutput("output", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");
        InputStream addOnlyConstructInputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("addOnlyNew.ttl");

        RepositoryConnection connection = null;
        RepositoryConnection connection2 = null;
        try {
            connection = input.getConnection();
            String baseURI = "";
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, baseURI, RDFFormat.TURTLE, graph);

            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // run
            env.run(trans);
            connection2 = output.getConnection();
            // verify result
            assertTrue(connection.size(graph) == connection2.size(RDFHelper.getGraphsURIArray(output)));
            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection2.export(new TurtleWriter(outputBos), RDFHelper.getGraphsURIArray(output));
            assertEquals(IOUtils.toString(addOnlyConstructInputStream, "UTF-8"), outputBos.toString("UTF-8"));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }
            if (connection2 != null) {
                try {
                    connection2.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            } // release resources
            env.release();
        }
    }

    @Test
    public void addAllAndNewDeleteOnlyNew() throws Exception {
        // prepare dpu
        SPARQL trans = new SPARQL();

        SPARQLConfig_V1 config = new SPARQLConfig_V1();
        config.setQueryPairs(Arrays.asList(
                new SPARQLQueryPair("CONSTRUCT {?s ?p ?o} where {?s ?p ?o }", true),
                new SPARQLQueryPair("INSERT {?s <http://test> ?o . } where {?s ?p ?o}", false),
                new SPARQLQueryPair("DELETE where {?s <http://test> ?o}", false)));

        trans.configureDirectly(config);

        // prepare test environment
        TestEnvironment env = new TestEnvironment();

        // prepare data units
        WritableRDFDataUnit input = env.createRdfInput("input", false);
        WritableRDFDataUnit output = env.createRdfOutput("output", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");

        RepositoryConnection connection = null;
        RepositoryConnection connection2 = null;
        try {
            connection = input.getConnection();
            String baseURI = "";
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, baseURI, RDFFormat.TURTLE, graph);
            ByteArrayOutputStream inputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(inputBos), graph);

            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // run
            env.run(trans);
            connection2 = output.getConnection();
            // verify result
            assertTrue(connection.size(graph) == connection2.size(RDFHelper.getGraphsURIArray(output)));
            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection2.export(new TurtleWriter(outputBos), RDFHelper.getGraphsURIArray(output));
            assertEquals(inputBos.toString("UTF-8"), outputBos.toString("UTF-8"));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }
            if (connection2 != null) {
                try {
                    connection2.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            } // release resources
            env.release();
        }
    }

    @Test
    public void deleteOnlyNew() throws Exception {
        // prepare dpu
        SPARQL trans = new SPARQL();

        SPARQLConfig_V1 config = new SPARQLConfig_V1();
        config.setQueryPairs(Arrays.asList(
                new SPARQLQueryPair("INSERT {?s <http://test> ?o . } where {?s ?p ?o}", false),
                new SPARQLQueryPair("DELETE where {?s <http://test> ?o}", false)));

        trans.configureDirectly(config);

        // prepare test environment
        TestEnvironment env = new TestEnvironment();

        // prepare data units
        WritableRDFDataUnit input = env.createRdfInput("input", false);
        WritableRDFDataUnit output = env.createRdfOutput("output", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");

        RepositoryConnection connection = null;
        RepositoryConnection connection2 = null;
        try {
            connection = input.getConnection();
            String baseURI = "";
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, baseURI, RDFFormat.TURTLE, graph);
            ByteArrayOutputStream inputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(inputBos), graph);

            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // run
            env.run(trans);
            connection2 = output.getConnection();
            // verify result
            assertTrue(connection.size(graph) == connection2.size(RDFHelper.getGraphsURIArray(output)));
            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection2.export(new TurtleWriter(outputBos), RDFHelper.getGraphsURIArray(output));
            assertEquals(inputBos.toString("UTF-8"), outputBos.toString("UTF-8"));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }
            if (connection2 != null) {
                try {
                    connection2.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            } // release resources
            env.release();
        }
    }

    @Test
    public void addAllAndNewDeleteOnlyOld() throws Exception {
        // prepare dpu
        SPARQL trans = new SPARQL();

        SPARQLConfig_V1 config = new SPARQLConfig_V1();
        config.setQueryPairs(Arrays.asList(
                new SPARQLQueryPair("CONSTRUCT {?s ?p ?o} where {?s ?p ?o }", true),
                new SPARQLQueryPair("INSERT {?s <http://test> ?o . } where {?s ?p ?o}", false),
                new SPARQLQueryPair("DELETE { ?s ?p ?o } where {?s ?p ?o  FILTER ( ?p != <http://test> ) }", false)));

        trans.configureDirectly(config);

        // prepare test environment
        TestEnvironment env = new TestEnvironment();

        // prepare data units
        WritableRDFDataUnit input = env.createRdfInput("input", false);
        WritableRDFDataUnit output = env.createRdfOutput("output", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");
        InputStream addOnlyConstructInputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("addOnlyNew.ttl");

        RepositoryConnection connection = null;
        RepositoryConnection connection2 = null;
        try {
            connection = input.getConnection();
            String baseURI = "";
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, baseURI, RDFFormat.TURTLE, graph);
            ByteArrayOutputStream inputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(inputBos), graph);

            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // run
            env.run(trans);
            connection2 = output.getConnection();
            // verify result
            assertTrue(connection.size(graph) == connection2.size(RDFHelper.getGraphsURIArray(output)));

            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection2.export(new TurtleWriter(outputBos), RDFHelper.getGraphsURIArray(output));

            assertEquals(IOUtils.toString(addOnlyConstructInputStream, "UTF-8"), outputBos.toString("UTF-8"));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }
            if (connection2 != null) {
                try {
                    connection2.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            } // release resources
            env.release();
        }
    }

    @Test
    public void swapTripleTest() throws Exception {
        // prepare dpu
        SPARQL trans = new SPARQL();

        SPARQLConfig_V1 config = new SPARQLConfig_V1();
        config.setQueryPairs(Arrays.asList(
                new SPARQLQueryPair("DELETE { ?s ?p ?o. } INSERT { ?o ?p ?s. } WHERE { ?s  ?p ?o. }", false)
                ));
        trans.configureDirectly(config);

        // prepare test environment
        TestEnvironment env = new TestEnvironment();

        // prepare data units
        WritableRDFDataUnit input = env.createRdfInput("input", false);
        WritableRDFDataUnit output = env.createRdfOutput("output", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("onetriple.ttl");
        InputStream swappedInputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("onetripleswapped.ttl");

        RepositoryConnection connection = null;
        RepositoryConnection connection2 = null;
        try {
            connection = input.getConnection();
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, "", RDFFormat.TURTLE, graph);
            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // run
            env.run(trans);

            connection2 = output.getConnection();
            // verify result
            assertTrue(connection.size(graph) == connection2.size(RDFHelper.getGraphsURIArray(output)));

            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection2.export(new TurtleWriter(outputBos), RDFHelper.getGraphsURIArray(output));
            System.out.println(outputBos.toString("UTF-8"));
            assertEquals(IOUtils.toString(swappedInputStream, "UTF-8"), outputBos.toString("UTF-8"));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }
            if (connection2 != null) {
                try {
                    connection2.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            } // release resources
            env.release();
        }
    }

    @Test
    public void constrainedDelete() throws Exception {
        // prepare dpu
        SPARQL trans = new SPARQL();

        SPARQLConfig_V1 config = new SPARQLConfig_V1();
        config.setQueryPairs(Arrays.asList(
                new SPARQLQueryPair("CONSTRUCT {?s ?p ?o} where {?s ?p ?o }", true),
                new SPARQLQueryPair("DELETE { ?s ?p ?o. } WHERE { ?s  ?p ?o. }", false)
                ));
        trans.configureDirectly(config);

        // prepare test environment
        TestEnvironment env = new TestEnvironment();

        // prepare data units
        WritableRDFDataUnit input = env.createRdfInput("input", false);
        WritableRDFDataUnit output = env.createRdfOutput("output", false);

        RepositoryConnection connection = null;
        RepositoryConnection connection2 = null;
        try {
            connection = input.getConnection();
            URI graph = input.addNewDataGraph("test");
            ValueFactory vf = connection.getValueFactory();
            URI notGraph = vf.createURI("http://context");
            connection.add(vf.createURI("http://subject"), vf.createURI("http://predicate"), vf.createURI("http://object"), graph);
            connection.add(vf.createURI("http://subject"), vf.createURI("http://predicate"), vf.createURI("http://object"), notGraph);

            // some triples has been loaded
            assertTrue(connection.size(graph) == 1);
            assertTrue(connection.size(notGraph) == 1);
            // run
            env.run(trans);

            connection2 = output.getConnection();
            // verify result
            assertTrue(0 == connection2.size(RDFHelper.getGraphsURIArray(output)));
            assertTrue(connection2.size(notGraph) == 1);
            assertTrue(connection2.size(graph) == 1);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }
            if (connection2 != null) {
                try {
                    connection2.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            } // release resources
            env.release();
        }
    }

    @Test
    public void constrainedDelete2() throws Exception {
        // prepare dpu
        SPARQL trans = new SPARQL();

        SPARQLConfig_V1 config = new SPARQLConfig_V1();
        config.setQueryPairs(Arrays.asList(
                new SPARQLQueryPair("DELETE { ?s ?p ?o. } WHERE { ?s  ?p ?o. }", false)
                ));
        trans.configureDirectly(config);

        // prepare test environment
        TestEnvironment env = new TestEnvironment();

        // prepare data units
        WritableRDFDataUnit input = env.createRdfInput("input", false);
        WritableRDFDataUnit output = env.createRdfOutput("output", false);

        RepositoryConnection connection = null;
        RepositoryConnection connection2 = null;
        try {
            connection = input.getConnection();
            URI graph = input.addNewDataGraph("test");
            ValueFactory vf = connection.getValueFactory();
            URI notGraph = vf.createURI("http://context");
            connection.add(vf.createURI("http://subject"), vf.createURI("http://predicate"), vf.createURI("http://object"), graph);
            connection.add(vf.createURI("http://subject"), vf.createURI("http://predicate"), vf.createURI("http://object"), notGraph);

            // some triples has been loaded
            assertTrue(connection.size(graph) == 1);
            assertTrue(connection.size(notGraph) == 1);
            // run
            env.run(trans);

            connection2 = output.getConnection();
            // verify result
            assertTrue(0 == connection2.size(RDFHelper.getGraphsURIArray(output)));
            assertTrue(connection2.size(notGraph) == 1);
            assertTrue(connection2.size(graph) == 1);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }
            if (connection2 != null) {
                try {
                    connection2.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            } // release resources
            env.release();
        }
    }

    @Test
    public void constrainedDelete3() throws Exception {
        // prepare dpu
        SPARQL trans = new SPARQL();

        SPARQLConfig_V1 config = new SPARQLConfig_V1();
        config.setQueryPairs(Arrays.asList(
                new SPARQLQueryPair("DELETE WHERE { ?s  ?p ?o. }", false)
                ));
        trans.configureDirectly(config);

        // prepare test environment
        TestEnvironment env = new TestEnvironment();

        // prepare data units
        WritableRDFDataUnit input = env.createRdfInput("input", false);
        WritableRDFDataUnit output = env.createRdfOutput("output", false);

        RepositoryConnection connection = null;
        RepositoryConnection connection2 = null;
        try {
            connection = input.getConnection();
            URI graph = input.addNewDataGraph("test");
            ValueFactory vf = connection.getValueFactory();
            URI notGraph = vf.createURI("http://context");
            connection.add(vf.createURI("http://subject"), vf.createURI("http://predicate"), vf.createURI("http://object"), graph);
            connection.add(vf.createURI("http://subject"), vf.createURI("http://predicate"), vf.createURI("http://object"), notGraph);

            // some triples has been loaded
            assertTrue(connection.size(graph) == 1);
            assertTrue(connection.size(notGraph) == 1);
            // run
            env.run(trans);

            connection2 = output.getConnection();
            // verify result
            assertTrue(0 == connection2.size(RDFHelper.getGraphsURIArray(output)));
            assertTrue(connection2.size(notGraph) == 1);
            assertTrue(connection2.size(graph) == 1);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }
            if (connection2 != null) {
                try {
                    connection2.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            } // release resources
            env.release();
        }
    }
}
