package eu.unifiedviews.plugins.transformer.sparql2;

import java.util.List;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.query.Dataset;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.dataset.DatasetBuilder;
import eu.unifiedviews.helpers.dataunit.rdfhelper.RDFHelper;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

/**
 * SPARQL Transformer.
 *
 * @author Jiri Tomes
 * @author Petyr
 * @author tknap
 */
@DPU.AsTransformer
public class SPARQL extends ConfigurableBase<SPARQLConfig_V1> implements ConfigDialogProvider<SPARQLConfig_V1> {

    private final Logger LOG = LoggerFactory.getLogger(SPARQL.class);

    /**
     * The repository input for SPARQL transformer.
     */
    @DataUnit.AsInput(name = "input")
    public RDFDataUnit intputDataUnit;

    /**
     * The repository output for SPARQL transformer.
     */
    @DataUnit.AsOutput(name = "output")
    public WritableRDFDataUnit outputDataUnit;

    public SPARQL() {
        super(SPARQLConfig_V1.class);
    }

    /**
     * Returns the configuration dialogue for SPARQL transformer.
     *
     * @return the configuration dialogue for SPARQL transformer.
     */
    @Override
    public AbstractConfigDialog<SPARQLConfig_V1> getConfigurationDialog() {
        return new SPARQLVaadinDialog();
    }

    /**
     * Execute the SPARQL transformer.
     *
     * @param context
     *            SPARQL transformer context.
     * @throws DPUException
     *             if this DPU fails.
     */
    @Override
    public void execute(DPUContext context) throws DPUException {

        final List<SPARQLQueryPair> queryPairs = config.getQueryPairs();
        if (queryPairs == null) {
            throw new DPUException("All queries for SPARQL transformer are null values");
        } else {
            if (queryPairs.isEmpty()) {
                throw new DPUException("Queries for SPARQL transformer are empty, SPARQL transformer must constains at least one SPARQL query");
            }
        }
        int queryCount = 0;
        try {
            URI outputGraph = outputDataUnit.addNewDataGraph(config.getOutputGraphSymbolicName());
            for (SPARQLQueryPair nextPair : queryPairs) {
                queryCount++;
                String updateQuery = nextPair.getSPARQLQuery();
                boolean isConstructQuery = nextPair.isConstructType();

                if (updateQuery == null) {
                    throw new DPUException("Query number " + queryCount + " is not defined");
                } else if (updateQuery.trim().isEmpty()) {
                    throw new DPUException("Query number " + queryCount + " is not defined, SPARQL transformer must constain at least one SPARQL (Update) query");
                }
                Dataset dataset = null;
                if (isConstructQuery) {
                    Set<URI> inputGraphs = RDFHelper.getGraphsURISet(intputDataUnit);
                    dataset = new DatasetBuilder()
                    .withDefaultGraphs(inputGraphs)
                    .withNamedGraphs(inputGraphs)
                    .withInsertGraph(outputGraph)
                    .addDefaultRemoveGraph(outputGraph)
                    .build();
                } else {
                    Set<URI> outputGraphs = RDFHelper.getGraphsURISet(outputDataUnit);
                    dataset = new DatasetBuilder()
                    .withDefaultGraphs(outputGraphs)
                    .withNamedGraphs(outputGraphs)
                    .withInsertGraph(outputGraph)
                    .addDefaultRemoveGraph(outputGraph)
                    .build();
                }
                RepositoryConnection connection = null;
                try {
                    connection = intputDataUnit.getConnection();
                    Update update = connection.prepareUpdate(QueryLanguage.SPARQL, updateQuery);
                    update.setDataset(dataset);
                    update.execute();
                } catch (DataUnitException | RepositoryException | MalformedQueryException | UpdateExecutionException ex) {
                    throw new DPUException(ex);
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (RepositoryException ex) {
                            LOG.warn("Error in close.", ex);
                        }
                    }
                }
            }
        } catch (DataUnitException ex) {
            throw new DPUException(ex);
        }

        RepositoryConnection connection = null;
        try {
            connection = intputDataUnit.getConnection();
            final long beforeTriplesCount = connection.size(RDFHelper.getGraphsURIArray(intputDataUnit));
            final long afterTriplesCount = connection.size(RDFHelper.getGraphsURIArray(outputDataUnit));
            LOG.info("Transformed thanks {} SPARQL queries {} triples into {}",
                    queryCount, beforeTriplesCount, afterTriplesCount);
        } catch (DataUnitException | RepositoryException ex) {
            throw new DPUException("connection to repository broke down", ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error in close", ex);
                }
            }
        }
    }

}
