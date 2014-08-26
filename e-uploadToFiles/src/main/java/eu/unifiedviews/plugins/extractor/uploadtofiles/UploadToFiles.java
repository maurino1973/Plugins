package eu.unifiedviews.plugins.extractor.uploadtofiles;

import java.util.Map;

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

@DPU.AsExtractor
public class UploadToFiles extends ConfigurableBase<UploadToFilesConfig_V1> implements ConfigDialogProvider<UploadToFilesConfig_V1> {
    private static final Logger LOG = LoggerFactory.getLogger(UploadToFiles.class);

    @DataUnit.AsOutput(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public UploadToFiles() {
        super(UploadToFilesConfig_V1.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, InterruptedException {
        Map<String, String> symbolicNameToURIMap = config.getSymbolicNameToURIMap();
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        String longMessage = String.format("Configuration: files providing: %d", symbolicNameToURIMap.size());
        dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage, longMessage);
        LOG.info(shortMessage + " " + longMessage);

        boolean shouldContinue = !dpuContext.canceled();
        for (String symbolicName : symbolicNameToURIMap.keySet()) {
            if (!shouldContinue) {
                break;
            }

            try {
                filesOutput.addExistingFile(symbolicName, symbolicNameToURIMap.get(symbolicName));
                // TODO mvi - virtual path from config
                VirtualPathHelpers.setVirtualPath(filesOutput, symbolicName, symbolicName);
                if (dpuContext.isDebugging()) {
                    LOG.debug("Providing " + symbolicName + " from " + symbolicNameToURIMap.get(symbolicName));
                }
            } catch (DataUnitException ex) {
                throw new DPUException("Error when providing: Symbolic name " + symbolicName + " from location " + symbolicNameToURIMap.get(symbolicName), ex);
            }
        }
    }

    @Override
    public AbstractConfigDialog<UploadToFilesConfig_V1> getConfigurationDialog() {
        return new UploadToFilesVaadinDialog();
    }
}
