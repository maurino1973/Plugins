package eu.unifiedviews.plugins.extractor.httpdownload;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

/**
 * This DPU will be removed shortly
 * 
 * @author Škoda Petr
 */
@DPU.AsExtractor
public class HttpDownload extends ConfigurableBase<HttpDownloadConfig_V1>
        implements ConfigDialogProvider<HttpDownloadConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(HttpDownload.class);

    @DataUnit.AsOutput(name = "output")
    public WritableFilesDataUnit output;

    public HttpDownload() {
        super(HttpDownloadConfig_V1.class);
    }

    @Override
    public void execute(DPUContext context)
            throws DPUException {
        //
        // get url
        //
        final URL url = config.getURL();
        if (url == null) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "Source URL not specified.");
            return;
        }
        //
        // prepare output file and metadata
        // 
        final String outSymName;
        final String outUri;
        try {
            outSymName = output.getBaseFileURIString() + config.getTarget();
            outUri = output.addNewFile(outSymName);

            VirtualPathHelpers.setVirtualPath(output, outSymName, config.getTarget());
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "Problem with Dataunit.", "Can't create output file.", ex);
            return;
        }
        final File outFile = new File(java.net.URI.create(outUri));
        //
        // download
        //
        try {
            FileUtils.copyURLToFile(url, outFile);
            // ok we have downloaded the file .. 
            return;
        } catch (IOException ex) {
            LOG.error("Download failed.", ex);
        }

        // try again ?
        int retryCount = config.getRetryCount();
        while (retryCount != 0 && !context.canceled()) {
            // sleep for a while
            try {
                Thread.sleep(config.getRetryDelay());
            } catch (InterruptedException ex) {
            }
            // try to download
            try {
                FileUtils.copyURLToFile(url, outFile);
                return;
            } catch (IOException ex) {
                LOG.error("Download failed - {}/{}", ex, retryCount,
                        config.getRetryCount());
            }
            // update retry counter
            if (retryCount > 0) {
                retryCount--;
            }
        }
        context.sendMessage(DPUContext.MessageType.ERROR,
                "Failed to download file.");
    }

    @Override
    public AbstractConfigDialog<HttpDownloadConfig_V1> getConfigurationDialog() {
        return new HttpDownloadVaadinDialog();
    }

}
