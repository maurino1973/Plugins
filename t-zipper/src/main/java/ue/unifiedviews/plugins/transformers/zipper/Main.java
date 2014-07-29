package ue.unifiedviews.plugins.transformers.zipper;

import cz.cuni.mff.xrg.uv.utils.dataunit.metadata.Manipulator;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.copyhelper.CopyHelpers;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelper;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DPU.AsTransformer
public class Main extends ConfigurableBase<Configuration>
		implements ConfigDialogProvider<Configuration> {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
    @DataUnit.AsInput(name = "input")
    public FilesDataUnit inFilesData;
            
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
        // Prepare zip file 
        //
        final String zipSymbolicName;
        final String zipFileUri;
        try {
            zipSymbolicName = 
                    outFilesData.getBaseFileURIString() + config.getZipFile();
            zipFileUri = outFilesData.createFile(zipSymbolicName);
            outFilesData.addExistingFile(zipSymbolicName, zipFileUri);
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, 
                    "Problem with DataUnit", "Can't add new file.", ex);            
            return;
        }
        final File zipFile = new File(java.net.URI.create(zipFileUri));
        zipFile.mkdirs();
        try {
            // add metadata 
            Manipulator.set(outFilesData, zipSymbolicName, 
                    VirtualPathHelper.PREDICATE_VIRTUAL_PATH, 
                    config.getZipFile());
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed",
                    "Failed to add metadata.", ex);
            return;
        }
        //
        // Create zip file
        //        
        zipFiles(zipFile, zipSymbolicName, filesIteration);                
	}
    
    /**
     * Pack files in given iterator into zip file and add metadata.
     * 
     * @param zipFile
     * @param zipSymbolicName
     * @param filesIteration 
     */
    private void zipFiles(File zipFile, String zipSymbolicName,
            FilesDataUnit.Iteration filesIteration) {
        final byte[] buffer = new byte[8196];
        
		// used to publish the error mesage only for the first time
		boolean firstFailure = true;        
        
        try (FileOutputStream fos = new FileOutputStream(zipFile); 
				ZipOutputStream zos = new ZipOutputStream(fos)) {
            while(!context.canceled() && filesIteration.hasNext()) {
                final FilesDataUnit.Entry entry = filesIteration.next();                
                LOG.debug("Adding file: {}", entry.getSymbolicName());
                if(!addZipEntry(zos, buffer, entry)) {
                    if (firstFailure) {
                        context.sendMessage(DPUContext.MessageType.ERROR, 
                                "Faild to zip all files");
                    }             
                    firstFailure = false;
                } else {
                    // add metadata
                    CopyHelpers.copyMetadata(entry.getSymbolicName(), 
                            inFilesData, outFilesData);
                    Manipulator.set(outFilesData, zipSymbolicName, 
                            Ontology.PREDICATE_CONTAINS_FILE, 
                            entry.getSymbolicName());
                }
            }
        } catch (IOException | DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, 
                    "Failed to create zip file.", "", ex);
        }
        
    }

    /**
     * Add single file into stream as zip entry.
     * 
     * @param zos
     * @param buffer
     * @param entry
     * @return True if file has been added.
     * @throws DataUnitException 
     */
    private boolean addZipEntry(ZipOutputStream zos, byte[] buffer, 
            FilesDataUnit.Entry entry) throws DataUnitException {
        
        String virtualPath = Manipulator.get(inFilesData, 
                        entry.getSymbolicName(), 
                        VirtualPathHelper.PREDICATE_VIRTUAL_PATH);
        
        // TODO We can try to use symbolicName here
        if (virtualPath == null) {
            context.sendMessage(DPUContext.MessageType.WARNING,
                    "No virtual path set for: " + entry.getSymbolicName() 
                            + ". File is ignored.");
            return false;
        }        
                
        final File sourceFile = new File(
                java.net.URI.create(entry.getFileURIString()));
        //
        // Do the action .. 
        //
        try (FileInputStream in = new FileInputStream(sourceFile)) {
            final ZipEntry ze = new ZipEntry(virtualPath);
            zos.putNextEntry(ze);
            //
            // Copy data
            //
            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
        } catch (Exception ex) {
            LOG.error("Failed to add file: {}", entry.getSymbolicName(), ex);
            return false;
        }
        return true;
    }
    
}
