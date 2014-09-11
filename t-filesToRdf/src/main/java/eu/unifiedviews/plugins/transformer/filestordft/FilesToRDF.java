package eu.unifiedviews.plugins.transformer.filestordft;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.ParseErrorLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

@DPU.AsTransformer
public class FilesToRDF extends ConfigurableBase<FilesToRDFConfig_V1> implements ConfigDialogProvider<FilesToRDFConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(FilesToRDF.class);

    @DataUnit.AsInput(name = "filesInput")
    public FilesDataUnit filesInput;

    @DataUnit.AsOutput(name = "rdfOutput")
    public WritableRDFDataUnit rdfOutput;

    public FilesToRDF() {
        super(FilesToRDFConfig_V1.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, InterruptedException {
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        String longMessage = String.format("Configuration: commitSize: %d", config.getCommitSize());
        dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage, longMessage);
        LOG.info(shortMessage + " " + longMessage);

        VirtualPathHelper inputVirtualPathHelper = VirtualPathHelpers.create(filesInput);
        RepositoryConnection connection = null;
        try {
            FilesDataUnit.Iteration filesIteration = filesInput.getIteration();

            if (!filesIteration.hasNext()) {
                return;
            }

            while (filesIteration.hasNext()) {
                connection = rdfOutput.getConnection();
                FilesDataUnit.Entry entry = filesIteration.next();

                RDFInserter rdfInserter = new CancellableCommitSizeInserter(connection, config.getCommitSize(), dpuContext);
                rdfInserter.enforceContext(rdfOutput.addNewDataGraph(entry.getSymbolicName()));

                ParseErrorListenerEnabledRDFLoader loader = new ParseErrorListenerEnabledRDFLoader(connection.getParserConfig(), connection.getValueFactory());
                try {
                    if (dpuContext.isDebugging()) {
                        LOG.debug("Starting extraction of file " + entry.getSymbolicName() + " path URI " + entry.getFileURIString());
                    }
//                    ParseErrorCollector parseErrorCollector= new ParseErrorCollector();

                    RDFFormat format;
                    String inputVirtualPath = inputVirtualPathHelper.getVirtualPath(entry.getSymbolicName());
                    if (inputVirtualPath != null) {
                        format = Rio.getParserFormatForFileName(inputVirtualPath);
                    } else {
                        format = Rio.getParserFormatForFileName(entry.getSymbolicName());
                    }
                    loader.load(new File(URI.create(entry.getFileURIString())), null, format, rdfInserter, new ParseErrorLogger());

                    if (dpuContext.isDebugging()) {
                        LOG.debug("Finished extraction of file " + entry.getSymbolicName() + " path URI " + entry.getFileURIString());
                    }
                } catch (RDFHandlerException | RDFParseException | IOException ex) {
                    dpuContext.sendMessage(DPUContext.MessageType.ERROR, "Error when extracting.",
                            "Symbolic name " + entry.getSymbolicName() + " path URI " + entry.getFileURIString(), ex);
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (RepositoryException ex) {
                            dpuContext.sendMessage(DPUContext.MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                        }
                    }
                }
            }
        } catch (DataUnitException ex) {
            dpuContext.sendMessage(DPUContext.MessageType.ERROR, "Error when extracting.", "", ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    dpuContext.sendMessage(DPUContext.MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                }
            }
            try {
                inputVirtualPathHelper.close();
            } catch (DataUnitException ex) {
                LOG.warn("Error in close", ex);
            }
        }
    }

    @Override
    public AbstractConfigDialog<FilesToRDFConfig_V1> getConfigurationDialog() {
        return new FilesToRDFVaadinDialog();
    }
}
