package eu.unifiedviews.plugins.transformer.unzipper;

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dataunit.fileshelper.FilesHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

/**
 * @author Škoda Petr
 */
@DPU.AsTransformer
public class UnZipper extends ConfigurableBase<UnZipperConfig_V1>
        implements ConfigDialogProvider<UnZipperConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(UnZipper.class);

    @DataUnit.AsInput(name = "input")
    public FilesDataUnit inFilesData;

    @DataUnit.AsOutput(name = "output")
    public WritableFilesDataUnit outFilesData;

    private DPUContext context;

    public UnZipper() {
        super(UnZipperConfig_V1.class);
    }

    @Override
    public void execute(DPUContext context) throws DPUException {
        this.context = context;

        final Iterator<FilesDataUnit.Entry> filesIteration;
        try {
            filesIteration = FilesHelper.getFiles(inFilesData).iterator();
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed", "Can't get file iterator.", ex);
            return;
        }

        final File baseTargetDirectory;
        try {
            baseTargetDirectory = new File(java.net.URI.create(outFilesData.getBaseFileURIString()));
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "DPU Failed", "Can't get base output directory.", ex);
            return;
        }

        boolean symbolicNameUsed = false;

        try {
            while (!context.canceled() && filesIteration.hasNext()) {
                FilesDataUnit.Entry entry = filesIteration.next();
                //
                // Prepare source/target file/directory
                //
                final File sourceFile = new File(java.net.URI.create(entry.getFileURIString()));

                String zipRelativePath = VirtualPathHelpers.getVirtualPath(inFilesData, entry.getSymbolicName());
                if (zipRelativePath == null) {
                    // use symbolicv name
                    zipRelativePath = entry.getSymbolicName();
                    if (!symbolicNameUsed) {
                        // first usage
                        LOG.warn("Not all input files use VirtualPath, symbolic name is used instead.");
                    }
                    symbolicNameUsed = true;
                }

                final File targetDirectory = new File(baseTargetDirectory, zipRelativePath);
                //
                // Unzip
                //
                if (!unzip(sourceFile, targetDirectory)) {
                    // failure
                    break;
                }
                //
                // Scan for new files and add them
                //
                scanDirectory(targetDirectory, entry.getSymbolicName());
                //
                // Copy metadata
                //

                //CopyHelpers.copyMetadata(entry.getSymbolicName(), inFilesData, outFilesData);
                // TODO Above command copy whole file (is visible for next DPU)
                // we should use something else and add triple to new file
                // about source
            }
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "Problem with data unit.", "", ex);
        } finally {
        }
    }

    private void scanDirectory(File directory, String sourceSymbolicName) throws DataUnitException {
        final Path directoryPath = directory.toPath();
        final Iterator<File> iter = FileUtils.iterateFiles(directory, null, true);
        while (iter.hasNext()) {
            final File newFile = iter.next();
            final String relativePath = directoryPath.relativize(newFile.toPath()).toString();
            final String newSymbolicName;
            if (config.isNotPrefixed()) {
                newSymbolicName = relativePath;
            } else {
                newSymbolicName = sourceSymbolicName + "/" + relativePath;
            }
            // add file
            outFilesData.addExistingFile(newSymbolicName, newFile.toURI().toString());
            //
            // add metadata
            //
            VirtualPathHelpers.setVirtualPath(outFilesData, newSymbolicName, newSymbolicName);
        }
    }

    /**
     * Unzip given file into given directory.
     *
     * @param zipFile
     * @param targetDirectory
     * @return
     */
    private boolean unzip(File zipFile, File targetDirectory) {
        try {
            final ZipFile zip = new ZipFile(zipFile);
            if (zip.isEncrypted()) {
                context.sendMessage(DPUContext.MessageType.ERROR, "Extraction failed.", "Zip file is encrypted.");
                return false;
            }
            zip.extractAll(targetDirectory.toString());
        } catch (ZipException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, "Extraction failed.", "", ex);
            return false;
        }
        return true;
    }

    @Override
    public AbstractConfigDialog<UnZipperConfig_V1> getConfigurationDialog() {
        return new UnZipperVaadinDialog();
    }

    @Override
    public void configureDirectly(UnZipperConfig_V1 newConfig) throws DPUConfigException {
        // workaround as configuration was initialy part of the Unzipper
        // so original version of this function throws an exception
        if (newConfig != null) {
            config = newConfig;
        } else {
            // ignore and use default
            config = new UnZipperConfig_V1();
        }
    }

}
