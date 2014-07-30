package eu.unifiedviews.plugins.trandformer.sparqlselect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.URI;
import org.openrdf.query.*;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriterFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.metadata.MetadataHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

@DPU.AsTransformer
public class Main extends ConfigurableBase<Configuration>
        implements ConfigDialogProvider<Configuration> {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    @DataUnit.AsInput(name = "input")
    public RDFDataUnit inRdfData;

    @DataUnit.AsOutput(name = "output")
    public WritableFilesDataUnit outFilesData;

    private DPUContext context;

    public Main() {
        super(Configuration.class);
    }

    @Override
    public AbstractConfigDialog<Configuration> getConfigurationDialog() {
        return new Dialog();
    }

    @Override
    public void execute(DPUContext context) throws DPUException {
        //
        // Prepare output
        //
        final String outSymbolicName;
        final String outFileUri;
        try {
            outSymbolicName
                    = outFilesData.getBaseFileURIString() + config.
                    getTargetPath();
            outFileUri = outFilesData.addNewFile(outSymbolicName);
            outFilesData.addExistingFile(outSymbolicName, outFileUri);
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "Problem with DataUnit", "Can't add new file.", ex);
            return;
        }
        try {
            // add metadata
            VirtualPathHelpers.setVirtualPath(outFilesData, outSymbolicName,
                    config.getTargetPath());
            // iterata over imput files
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed",
                    "Failed to add metadata.", ex);
            return;
        }
        final File outFile = new File(java.net.URI.create(outFileUri));
        //
        // get input graphs, prepara dataset and metasata
        //
        final Map<String, URI> graphs;
        try {
            graphs = getGraphs();
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "Problem with DataUnit.", "", ex);
            return;
        }
        // metadata
        try {
            for (String symbolicName : graphs.keySet()) {
                MetadataHelper.add(outFilesData, outSymbolicName,
                        Ontology.PREDICATE_SOURCE_GRAPH, symbolicName);
            }
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "Problem with DataUnit.", "", ex);
            return;
        }

        // dataset
        final DatasetImpl dataset = new DatasetImpl();
        for (URI graph : graphs.values()) {
            dataset.addDefaultGraph(graph);
        }
        //
        // transform
        //
        RepositoryConnection connection = null;
        try (OutputStream outputStream = new FileOutputStream(outFile)) {
            connection = inRdfData.getConnection();
            // prepare resultwriter
            final SPARQLResultsCSVWriterFactory writerFactory
                    = new SPARQLResultsCSVWriterFactory();
            final TupleQueryResultWriter resultWriter
                    = writerFactory.getWriter(outputStream);
            // write result
            TupleQuery query = connection.prepareTupleQuery(
                    QueryLanguage.SPARQL, config.getQuery());
            // TODO add support for placeholders ?
            
            query.setDataset(dataset);

            query.evaluate(resultWriter);
        } catch (IOException | RepositoryException | QueryEvaluationException | TupleQueryResultHandlerException ex) {
            LOG.warn("IOException", ex);
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "DPU failed", "", ex);
        } catch (MalformedQueryException ex) {
            LOG.warn("MalformedQueryException", ex);
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "Invalid query.", "", ex);
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "DPU Failed.", "Problem with DataUnit.", ex);
        } finally {
            // in every case close conneciton
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (RepositoryException ex) {
                LOG.warn("Close on connection has failed.", ex);
            }
        }
    }

    /**
     * Return map of all input graphs.
     *
     * @return
     */
    private Map<String, URI> getGraphs() throws DataUnitException {
        final Map<String, URI> graphUris = new HashMap<>();
        // uncomment as the the RDFDataUnit.getIteration() will be implemented
        try (RDFDataUnit.Iteration iter = inRdfData.getIteration()) {
            while (iter.hasNext()) {
                final RDFDataUnit.Entry entry = iter.next();
                graphUris.put(entry.getSymbolicName(), entry.getDataGraphURI());
            }
        }
        return graphUris;
    }

}
