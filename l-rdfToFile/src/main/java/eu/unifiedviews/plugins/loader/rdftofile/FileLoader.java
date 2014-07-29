package eu.unifiedviews.plugins.loader.rdftofile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.UnsupportedRDFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUContext.MessageType;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

/**
 * Loads RDF data into file.
 *
 * @author Jiri Tomes
 * @author Petyr
 */
@DPU.AsLoader
public class FileLoader extends ConfigurableBase<FileLoaderConfig>
        implements ConfigDialogProvider<FileLoaderConfig> {

    private final Logger logger = LoggerFactory.getLogger(FileLoader.class);

    final String encode = "UTF-8";

    /**
     * The repository for file loader.
     */
    @DataUnit.AsInput(name = "input")
    public RDFDataUnit inputDataUnit;

    @DataUnit.AsOutput(name = "validationDataUnit", description = "Never connect any data to this unit please!")
    public WritableRDFDataUnit validationDataUnit;

    @DataUnit.AsOutput(name = "input_redirection", optional = true)
    public WritableRDFDataUnit inputShadow;

    public FileLoader() {
        super(FileLoaderConfig.class);
    }

    /**
     * Execute the file loader.
     *
     * @param context File loader context.
     * @throws DPUException if this DPU fails.
     */
    @Override
    public void execute(DPUContext context) throws DPUException {

        final String filePath = config.getFilePath();
        final RDFFormatType formatType = config.getRDFFileFormat();
        final boolean isNameUnique = config.isDiffName();
        final boolean canFileOverwritte = true;
        final boolean validateDataBefore = config.isValidDataBefore();

        if (validateDataBefore) {
            context.sendMessage(MessageType.ERROR, "Data validation is unsupported, use data validation DPU");
        }

        RepositoryConnection connection = null;
        long triplesCount = 0;
        try {
            connection = inputDataUnit.getConnection();
            triplesCount = connection.size(inputDataUnit.getDataGraphnames().toArray(new URI[0]));
            FileOutputStream out = new FileOutputStream(filePath);
            OutputStreamWriter os = new OutputStreamWriter(out, Charset.forName(encode));
            File file = new File(filePath);
            RDFFormat format;

            if (formatType == RDFFormatType.AUTO) {
                format = Rio.getWriterFormatForFileName(file.getName());
            } else {
                format = RDFFormatType.getRDFFormatByType(formatType);
            }

            RDFWriter rdfWriter = Rio.createWriter(format, os);
            connection.export(rdfWriter, inputDataUnit.getDataGraphnames().toArray(new URI[0]));

        } catch (RepositoryException e) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "connection to repository broke down");
        } catch (DataUnitException e) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "DataUnit exception" + e.getMessage(), e.fillInStackTrace().toString());
        } catch (FileNotFoundException | RDFHandlerException | UnsupportedRDFormatException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, ex.getMessage(), ex
                    .fillInStackTrace().toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    context.sendMessage(DPUContext.MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                }
            }
        }

        logger.info("Loading {} triples", triplesCount);

        if (config.isPenetrable()) {
            try {
                inputShadow.addAll(inputDataUnit);
            } catch (DataUnitException e) {
                context.sendMessage(DPUContext.MessageType.ERROR,
                        "DataUnit exception" + e.getMessage(), e.fillInStackTrace().toString());
            }
        }

    }

    /**
     * Returns the configuration dialogue for file loader.
     *
     * @return the configuration dialogue for file loader.
     */
    @Override
    public AbstractConfigDialog<FileLoaderConfig> getConfigurationDialog() {
        return new FileLoaderDialog();
    }
}
