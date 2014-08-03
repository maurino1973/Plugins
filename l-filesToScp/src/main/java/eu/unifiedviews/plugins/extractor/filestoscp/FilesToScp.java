package eu.unifiedviews.plugins.extractor.filestoscp;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.marcoratto.scp.SCP;
import uk.co.marcoratto.scp.SCPPException;
import uk.co.marcoratto.scp.listeners.SCPListenerPrintStream;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

@DPU.AsLoader
public class FilesToScp extends ConfigurableBase<FilesToScpConfiguration>
        implements ConfigDialogProvider<FilesToScpConfiguration> {

    private static final Logger LOG = LoggerFactory.getLogger(FilesToScp.class);

    @DataUnit.AsInput(name = "input")
    public FilesDataUnit inFilesData;

    private DPUContext context;

    public FilesToScp() {
        super(FilesToScpConfiguration.class);
    }

    @Override
    public void execute(DPUContext context)
            throws DPUException {
        this.context = context;
        final FilesDataUnit.Iteration filesIteration;
        try {
            filesIteration = inFilesData.getIteration();
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "DPU Failed", "Can't get file iterator.", ex);
            return;
        }
        //
        // preapre scop
        //
        final SCP scp = new SCP(new SCPListenerPrintStream());
        scp.setPort(config.getPort());
        scp.setPassword(config.getPassword());
        scp.setTrust(true);
        if (context.isDebugging()) {
            scp.setVerbose(true);
        }
        // non recursion we copy ourselfs
        scp.setRecursive(true);
        // prepare destination
        String destinationBase = config.getUsername() + '@'
                + config.getHostname() + ':' + config.getDestination();
        if (!destinationBase.endsWith("/")) {
            destinationBase += "/";
        }

        LOG.debug("Global destination: {}", destinationBase);
        //
        // prepare to one directory
        //
        final File toUploadDir = new File(context.getWorkingDir(), "toUpload");
        toUploadDir.mkdirs();
        try {
            while (!context.canceled() && filesIteration.hasNext()) {
                final FilesDataUnit.Entry entry = filesIteration.next();

                final String relativePath = VirtualPathHelpers.getVirtualPath(inFilesData, 
                        entry.getSymbolicName());
                
                // TODO We can try to use symbolicName here
                if (relativePath == null) {
                    context.sendMessage(DPUContext.MessageType.WARNING,
                            "No virtual path set for: " + entry.getSymbolicName() 
                                    + ". File is ignored.");
                    continue;
                }

                FileUtils.copyFile(
                        new File(java.net.URI.create(entry.getFileURIString())),
                        new File(toUploadDir, relativePath));
            }
        } catch (IOException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "Can't prepare directory for upload.", "", ex);
            return;
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "Problem with dataunit.", "", ex);
            return;
        } finally {
            try {
                filesIteration.close();
            } catch (DataUnitException ex) {
                LOG.warn("Error in close.", ex);
            }
        }

        //
        // upload
        //
        try {
            scp.setFromUri(toUploadDir.toString());
            scp.setToUri(destinationBase);
            scp.execute();
        } catch (SCPPException ex) {
            if (config.isSoftFail()) {
                context.sendMessage(DPUContext.MessageType.WARNING,
                        "Failed to upload file/directory", "", ex);
                // ok continue
            } else {
                context.sendMessage(DPUContext.MessageType.ERROR,
                        "Failed to upload file/directory", "", ex);
            }
        }
        //
        // delete working directory
        //
    }

    @Override
    public AbstractConfigDialog<FilesToScpConfiguration> getConfigurationDialog() {
        return new FilesToScpVaadinDialog();
    }

}
