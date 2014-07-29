package ue.unifiedviews.plugins.loader.filestolocal;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.metadata.Manipulator;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelper;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DPU.AsLoader
public class Main extends ConfigurableBase<Configuration> implements
        ConfigDialogProvider<Configuration> {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
    @DataUnit.AsInput(name = "input")
    public FilesDataUnit inFilesData;
            
	public Main() {
		super(Configuration.class);
	}
	
	@Override
	public AbstractConfigDialog<Configuration> getConfigurationDialog() {
		return new Dialog();
	}
	
	@Override
	public void execute(DPUContext context) throws DPUException {
		
		FilesDataUnit.Iteration filesIteration;
        try {
            filesIteration = inFilesData.getIteration();
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, 
                    "DPU Failed", "Can't get file iterator.", ex);
            return;
        }
        
        // prepare output directory
        final File destinationDirFile = new File(config.getDestination());
        destinationDirFile.mkdirs();
		
        // prepare copy options
        final ArrayList<CopyOption> copyOptions = new ArrayList<>(1);
        if (config.isReplaceExisting()) {
            copyOptions.add(StandardCopyOption.REPLACE_EXISTING);
        }        
        final CopyOption[] copyOptionsArray =
                copyOptions.toArray(new CopyOption[0]);
        
        try {
            while(!context.canceled() && filesIteration.hasNext()) {
                final FilesDataUnit.Entry entry = filesIteration.next();
                LOG.debug("Found entry '{}' with path '{}'", 
                        entry.getSymbolicName(), entry.getFileURIString());
                //
                // We need source file and target file paths
                //
                final File inputFile = 
                        new File(URI.create(entry.getFileURIString()));
                final String relativePath = Manipulator.get(inFilesData, 
                        entry.getSymbolicName(), 
                        VirtualPathHelper.PREDICATE_VIRTUAL_PATH);
                
                // TODO We can try to use symbolicName here
                if (relativePath == null) {
                    context.sendMessage(DPUContext.MessageType.WARNING,
                            "No virtual path set for: " + entry.getSymbolicName() 
                                    + ". File is ignored.");
                    continue;
                }
                
                //
                // prepare output file and copy
                //
                final File outputFile = 
                        new File(destinationDirFile, relativePath);
                // create parent directory
                outputFile.getParentFile().mkdirs();                
                try {
                    // copy
                    java.nio.file.Files.copy(inputFile.toPath(), 
                            outputFile.toPath(), copyOptionsArray);
                } catch (IOException ex) {
                    context.sendMessage(DPUContext.MessageType.ERROR,
                            "Failed to copy file.", 
                            "Failed to copy, file ignored.", ex);
                }
            }
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, "DPU failed",
                    "Problem with DataUnit.", ex);
        } finally {
            try {
                filesIteration.close();
            } catch (DataUnitException ex) {
                LOG.warn("Error in close.", ex);
            }
        }
        
        
	}
	
}
