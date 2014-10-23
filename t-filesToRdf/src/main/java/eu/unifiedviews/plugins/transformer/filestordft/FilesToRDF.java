package eu.unifiedviews.plugins.transformer.filestordft;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.ParseErrorLogger;
import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.fileshelper.FilesHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;
import java.util.Date;

@DPU.AsTransformer
public class FilesToRDF extends ConfigurableBase<FilesToRDFConfig_V1> implements ConfigDialogProvider<FilesToRDFConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(FilesToRDF.class);

    @DataUnit.AsInput(name = "filesInput")
    public FilesDataUnit filesInput;

    @DataUnit.AsOutput(name = "rdfOutput")
    public WritableRDFDataUnit rdfOutput;

    /**
     * True if at least one file has been skipped during conversion.
     */
    private boolean fileSkipped = false;

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
        final Iterator<FilesDataUnit.Entry> filesIteration;
        try {
            filesIteration = FilesHelper.getFiles(filesInput).iterator();
        } catch (DataUnitException ex) {
            dpuContext.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed", "Can't get file iterator.", ex);
            return;
        }

        final URI outputGraphUri;
        if (config.getOutputNaming().equals(FilesToRDFConfig_V1.USE_FIXED_SYMBOLIC_NAME)) {
            // Use given value from config as output graph name.
            try {
                String value = config.getOutputSymbolicName();
                if (value == null || value.isEmpty()) {
                    Date currentTime = new Date();
                    value = "FilesToRDF/generated_" + Long.toString(currentTime.getTime());
                }
                LOG.info("Output symbolic name: {}", value);
                outputGraphUri = rdfOutput.addNewDataGraph(value);
            } catch (DataUnitException ex) {
                dpuContext.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed", "Can't create output graph.", ex);
                return;
            }
        } else {
            outputGraphUri = null;
        }

        try {

            if (!filesIteration.hasNext()) {
                return;
            }
            // If true then next file is processed.
            boolean shouldContinue = true;

            while (filesIteration.hasNext() && !dpuContext.canceled() && shouldContinue) {
                connection = rdfOutput.getConnection();
                FilesDataUnit.Entry entry = filesIteration.next();

                RDFInserter rdfInserter = new CancellableCommitSizeInserter(connection, config.getCommitSize(), dpuContext);
                // Set output graph name.
                if (outputGraphUri == null) {
                    rdfInserter.enforceContext(rdfOutput.addNewDataGraph(entry.getSymbolicName()));
                } else {
                    rdfInserter.enforceContext(outputGraphUri);
                }

                ParseErrorListenerEnabledRDFLoader loader = new ParseErrorListenerEnabledRDFLoader(connection.getParserConfig(), connection.getValueFactory());
                try {
                    LOG.debug("Starting extraction of file on '{}' with uri: '{}'", entry.getSymbolicName(), entry.getFileURIString());

                    RDFFormat format;
                    String inputVirtualPath = inputVirtualPathHelper.getVirtualPath(entry.getSymbolicName());
                    if (inputVirtualPath != null) {
                        format = Rio.getParserFormatForFileName(inputVirtualPath);
                    } else {
                        format = Rio.getParserFormatForFileName(entry.getSymbolicName());
                    }
                    loader.load(new File(java.net.URI.create(entry.getFileURIString())), null, format, rdfInserter, new ParseErrorLogger());

                    LOG.debug("Finished extraction of file " + entry.getSymbolicName() + " path URI " + entry.getFileURIString());
                } catch (RDFHandlerException | RDFParseException | IOException ex) {
                    // Problem with a single file, decide what next based on configuration.
                    switch (config.getFatalErrorHandling()) {
                        case FilesToRDFConfig_V1.SKIP_CONTINUE_NEXT_FILE_ERROR_HANDLING:
                            LOG.error("Symbolic name '{}' with path '{}'", entry.getSymbolicName(), entry.getFileURIString());
                            fileSkipped = true;
                            break;
                        case FilesToRDFConfig_V1.STOP_EXTRACTION_ERROR_HANDLING:
                        default:
                            dpuContext.sendMessage(DPUContext.MessageType.ERROR, "Error when extracting.", "Symbolic name " + entry.getSymbolicName() + " path URI " + entry.getFileURIString(), ex);
                            // And we need to stop the execution, as we got finally blocks we can just jump out.
                            shouldContinue = false;
                            break;
                    }
                } finally {
                    // If we get here, null pointer exception would already been thrown, so connection != null.
                    try {
                        connection.close();
                    } catch (RepositoryException ex) {
                        dpuContext.sendMessage(DPUContext.MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                    }
                }
            }
        } catch (DataUnitException ex) {
            dpuContext.sendMessage(DPUContext.MessageType.ERROR, "Error when extracting.", "", ex);
        } finally {
            // This close connection if throws methods before inner try-catch block.
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
        // Publish messsage.
        if (fileSkipped) {
            dpuContext.sendMessage(DPUContext.MessageType.WARNING, "Some files has been skipped during conversion.", "See logs for more details.");
        }
    }

    @Override
    public AbstractConfigDialog<FilesToRDFConfig_V1> getConfigurationDialog() {
        return new FilesToRDFVaadinDialog();
    }
}
