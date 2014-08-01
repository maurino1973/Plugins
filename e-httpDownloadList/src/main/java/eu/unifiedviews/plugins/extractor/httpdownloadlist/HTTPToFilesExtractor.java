package eu.unifiedviews.plugins.extractor.httpdownloadlist;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

@DPU.AsExtractor
public class HTTPToFilesExtractor extends ConfigurableBase<HTTPToFilesExtractorConfig> implements ConfigDialogProvider<HTTPToFilesExtractorConfig> {

    private static final Logger LOG = LoggerFactory.getLogger(HTTPToFilesExtractor.class);

    @DataUnit.AsOutput(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public HTTPToFilesExtractor() {
        super(HTTPToFilesExtractorConfig.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, InterruptedException {
        Map<String, String> symbolicNameToURIMap = config.getSymbolicNameToURIMap();
        Map<String, String> symbolicNameToVirtualPathMap = config.getSymbolicNameToVirtualPathMap();
        
        int connectionTimeout = config.getConnectionTimeout();
        int readTimeout = config.getReadTimeout();
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        String longMessage = String.format("Configuration: files to download: %d, connectionTimeout: %d, readTimeout: %d", symbolicNameToURIMap.size(), connectionTimeout, readTimeout);
        dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage, longMessage);
        LOG.info(shortMessage + " " + longMessage);

        boolean shouldContinue = !dpuContext.canceled();
        VirtualPathHelper virtualPathHelper = VirtualPathHelpers.create(filesOutput);
        try {
            for (String symbolicName : symbolicNameToURIMap.keySet()) {
                if (!shouldContinue) {
                    break;
                }
    
                String downloadedFilename = null;
                File downloadedFile = null;
                String downloadFromLocation = null;
                try {
                    downloadedFilename = filesOutput.addNewFile(symbolicName);
                    downloadedFile = new File(URI.create(downloadedFilename));
                    downloadFromLocation = symbolicNameToURIMap.get(symbolicName);
                    URL downloadFromLocationURL = new URL(downloadFromLocation);
                    FileUtils.copyURLToFile(downloadFromLocationURL, downloadedFile, connectionTimeout, readTimeout);
                    if (dpuContext.isDebugging()) {
                        LOG.debug("Downloaded " + symbolicName + " from " + downloadFromLocation + " to " + downloadedFilename);
                    }
                    if (symbolicNameToVirtualPathMap.containsKey(symbolicName)){
                        virtualPathHelper.setVirtualPath(symbolicName, symbolicNameToVirtualPathMap.get(symbolicName));
                    } else {
                        virtualPathHelper.setVirtualPath(symbolicName,downloadFromLocationURL.getPath());
                    }
                } catch (DataUnitException ex) {
                    dpuContext.sendMessage(DPUContext.MessageType.ERROR, "Error when downloading.", "Symbolic name " + symbolicName + " from location ", ex);
                } catch (IOException ex) {
                    dpuContext.sendMessage(DPUContext.MessageType.ERROR, "Error when downloading.", "Symbolic name " + symbolicName + " from location " + downloadFromLocation + " could not be saved to " + downloadedFilename, ex);
                }
                shouldContinue = !dpuContext.canceled();
            }
        } finally {
            try {
                virtualPathHelper.close();
            } catch (DataUnitException ex) {
                LOG.warn("Error in close", ex);
            }
        }
    }

    @Override
    public AbstractConfigDialog<HTTPToFilesExtractorConfig> getConfigurationDialog() {
        return new HTTPToFilesExtractorConfigDialog();
    }

}
