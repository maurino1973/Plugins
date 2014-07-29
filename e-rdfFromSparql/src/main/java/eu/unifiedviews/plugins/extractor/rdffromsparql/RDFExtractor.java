package eu.unifiedviews.plugins.extractor.rdffromsparql;

import java.net.MalformedURLException;
import java.net.URL;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

/**
 * Extracts RDF data from SPARQL endpoint.
 *
 * @author Jiri Tomes
 * @author Petyr
 */
@DPU.AsExtractor
public class RDFExtractor extends ConfigurableBase<RDFExtractorConfig>
        implements ConfigDialogProvider<RDFExtractorConfig> {

    private final Logger LOG = LoggerFactory.getLogger(RDFExtractor.class);

    /**
     * The repository for SPARQL extractor.
     */
    @DataUnit.AsOutput(name = "output")
    public WritableRDFDataUnit outputRdfDataUnit;

    public RDFExtractor() {
        super(RDFExtractorConfig.class);
    }

    /**
     * Execute the SPARQL extractor.
     *
     * @param context SPARQL extractor context.
     * @throws DPUException if this DPU fails.
     */
    @Override
    public void execute(DPUContext context)
            throws DPUException {
        RepositoryConnection connection = null;

        try {
            final URL endpointURL = new URL(config.getSPARQLEndpoint());
            final String hostName = config.getHostName();
            final String password = config.getPassword();
            String constructQuery = config.getSPARQLQuery();

            final boolean usedSplitConstruct = config.isUsedSplitConstruct();

            if (constructQuery.isEmpty()) {
                constructQuery = "construct {?x ?y ?z} where {?x ?y ?z}";
            }

            boolean useStatisticHandler = config.isUsedStatisticalHandler();
            boolean failWhenErrors = config.isFailWhenErrors();

            HandlerExtractType handlerExtractType = HandlerExtractType
                    .getHandlerType(useStatisticHandler, failWhenErrors);

            final boolean extractFail = config.isExtractFail();

            Integer retrySize = config.getRetrySize();
            if (retrySize == null) {
                retrySize = -1;
                LOG.info("retrySize is null, using -1 instead");
            }
            Long retryTime = config.getRetryTime();
            if (retryTime == null) {
                retryTime = 1000L;
                LOG.info("retryTime is null, using 1000 instead");
            }

            ExtractorEndpointParams endpointParams = config.getEndpointParams();

            if (endpointParams == null) {
                endpointParams = new ExtractorEndpointParams();
                LOG.info(
                        "Extractor endpoint params is null, used default values instead without setting ");
            }

            Integer splitConstructSize = config.getSplitConstructSize();
            if (splitConstructSize == null) {
                splitConstructSize = 50000;
                LOG.info("Split construct size is null, using 50000");
            }

            SPARQLExtractor extractor = new SPARQLExtractor(outputRdfDataUnit, context,
                    retrySize, retryTime, endpointParams);

            long lastrepoSize = 0;
            connection = outputRdfDataUnit.getConnection();
            lastrepoSize = connection.size(outputRdfDataUnit.getBaseDataGraphURI());

            if (usedSplitConstruct) {
                if (splitConstructSize <= 0) {
                    context.sendMessage(DPUContext.MessageType.ERROR,
                            "Split construct size must be positive number");
                }

                SplitConstructQueryHelper helper = new SplitConstructQueryHelper(
                        constructQuery, splitConstructSize);

                LOG.debug(
                        "The max size of one data part extracted from SPARQL extractor is set to {} TRIPLES",
                        splitConstructSize);
                while (true) {
                    String splitConstructQuery = helper.getSplitConstructQuery();

                    extractor.extractFromSPARQLEndpoint(endpointURL,
                            splitConstructQuery,
                            hostName, password, RDFFormat.NTRIPLES,
                            handlerExtractType, false);

                    long newrepoSize = connection.size(outputRdfDataUnit.getBaseDataGraphURI());

                    checkParsingProblems(useStatisticHandler, context);
                    if (lastrepoSize < newrepoSize) {
                        lastrepoSize = newrepoSize;
                        helper.goToNextQuery();
                    } else {
                        break;
                    }
                }

                if (extractFail && lastrepoSize == 0) {
                    throw new DPUException(
                            "No extracted triples from SPARQL endpoint");
                }

            } else {

                extractor.extractFromSPARQLEndpoint(endpointURL, constructQuery,
                        hostName, password, RDFFormat.NTRIPLES,
                        handlerExtractType, extractFail);

                checkParsingProblems(useStatisticHandler, context);
            }
            final long triplesCount = connection.size(outputRdfDataUnit.getBaseDataGraphURI());

            String tripleInfoMessage = String.format(
                    "Extracted %s triples from SPARQL endpoint %s",
                    triplesCount, endpointURL.toString());

            context.sendMessage(DPUContext.MessageType.INFO, tripleInfoMessage);

        } catch (InvalidQueryException ex) {
            LOG.debug("InvalidQueryException", ex);
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "InvalidQueryException: " + ex.getMessage());
        } catch (MalformedURLException ex) {
            LOG.debug("MalformedURLException", ex);
            context.sendMessage(DPUContext.MessageType.ERROR, "MalformedURLException: "
                    + ex.getMessage());
            throw new DPUException(ex);
        } catch (RepositoryException e) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "connection to repository broke down");
        } catch (DataUnitException ex) {
            LOG.debug("DataUnitException", ex);
            context.sendMessage(DPUContext.MessageType.ERROR, "DataUnitException: "
                    + ex.getMessage());
            throw new DPUException(ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    context.sendMessage(DPUContext.MessageType.ERROR, ex.getMessage(), ex.fillInStackTrace().toString());
                }
            }
        }
    }

    private void checkParsingProblems(boolean useStatisticHandler,
            DPUContext context) {

        if (useStatisticHandler && StatisticalHandler.hasParsingProblems()) {

            String problems = StatisticalHandler
                    .getFoundGlobalProblemsAsString();
            StatisticalHandler.clearParsingProblems();

            context.sendMessage(DPUContext.MessageType.WARNING,
                    "Statistical and error handler has found during parsing problems triples (these triples were not added)",
                    problems);
        }
    }

    /**
     * Returns the configuration dialogue for SPARQL extractor.
     *
     * @return the configuration dialogue for SPARQL extractor.
     */
    @Override
    public AbstractConfigDialog<RDFExtractorConfig> getConfigurationDialog() {
        return new RDFExtractorDialog();
    }
}
