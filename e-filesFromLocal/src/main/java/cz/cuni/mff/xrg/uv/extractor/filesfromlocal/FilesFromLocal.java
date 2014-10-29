package cz.cuni.mff.xrg.uv.extractor.filesfromlocal;

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;

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
 * @author Škoda Petr
 */
@DPU.AsExtractor
public class FilesFromLocal extends ConfigurableBase<FilesFromLocalConfig_V1> implements ConfigDialogProvider<FilesFromLocalConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(
            FilesFromLocal.class);

    @DataUnit.AsOutput(name = "output")
    public WritableFilesDataUnit outFilesData;

    public FilesFromLocal() {
        super(FilesFromLocalConfig_V1.class);
    }

    @Override
    public void execute(DPUContext context) throws DPUException, InterruptedException {
        File source = new File(config.getSource());

        if (source.isDirectory()) {
            //
            // extract from directory
            //
            try {
                scanDirectory(source);
            } catch (DataUnitException ex) {
                context.sendMessage(DPUContext.MessageType.ERROR,
                        "Problem with DataUnit", null, ex);
            }
        } else if (source.isFile()) {
            //
            // extract single file
            //
            try {
                outFilesData.addExistingFile(source.getName(), source.toURI().toASCIIString());
                VirtualPathHelpers.setVirtualPath(outFilesData, source.getName(), source.getName());
            } catch (DataUnitException ex) {
                context.sendMessage(DPUContext.MessageType.ERROR,
                        "Problem with DataUnit", null, ex);
            }
        } else {
            context.sendMessage(DPUContext.MessageType.ERROR, "Can't determine source type.");
        }
    }

    @Override
    public AbstractConfigDialog<FilesFromLocalConfig_V1> getConfigurationDialog() {
        return new FilesFromLocalVaadinDialog();
    }

    private void scanDirectory(File directory) throws eu.unifiedviews.dataunit.DataUnitException {
        final Path directoryPath = directory.toPath();
        final Iterator<File> iter = FileUtils.iterateFiles(directory, null, true);
        while (iter.hasNext()) {
            final File newFile = iter.next();
            final String relativePath = directoryPath.relativize(newFile.toPath()).toString();
            final String newSymbolicName = relativePath;
            // add file
            outFilesData.addExistingFile(newSymbolicName, newFile.toURI().toString());
            //
            // add metadata
            //
            VirtualPathHelpers.setVirtualPath(outFilesData, newSymbolicName, relativePath);
        }
    }

}
