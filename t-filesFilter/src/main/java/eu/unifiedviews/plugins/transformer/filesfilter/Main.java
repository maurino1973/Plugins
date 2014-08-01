package eu.unifiedviews.plugins.transformer.filesfilter;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

@DPU.AsTransformer
public class Main extends ConfigurableBase<Configuration> implements ConfigDialogProvider<Configuration> {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    @DataUnit.AsInput(name = "input")
    public FilesDataUnit inFilesData;

    @DataUnit.AsOutput(name = "output")
    public WritableFilesDataUnit outFilesData;

    public Main() {
        super(Configuration.class);
    }

    @Override
    public AbstractConfigDialog<Configuration> getConfigurationDialog() {
        return new Dialog();
    }

    @Override
    public void execute(DPUContext context) throws DPUException {
        Pattern pattern = null;
        if (config.isUseRegExp()) {
            try {
                pattern = Pattern.compile(config.getObject());
            } catch (PatternSyntaxException ex) {
                context.sendMessage(DPUContext.MessageType.ERROR, "Configuration problem", "Error in object regexp.", ex);
                return;
            }
        }
        //
        // get file iterator
        //
        final FilesDataUnit.Iteration filesIteration;
        try {
            filesIteration = inFilesData.getIteration();
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed", "Can't get file iterator.", ex);
            return;
        }
        //
        // filter data here
        //
        boolean useSymbolicName = config.getPredicate().equals(Configuration.SYMBOLIC_NAME);

        try {
            while (filesIteration.hasNext()) {
                final FilesDataUnit.Entry entry = filesIteration.next();

                final String value;
                if (useSymbolicName) {
                    value = entry.getFileURIString();
                } else {
                    // virtual path
                    value = VirtualPathHelpers.getVirtualPath(inFilesData, entry.getSymbolicName());
                }

                if (value == null) {
                    // no value for predicate - continue
                    continue;
                }

                if (pattern == null) {
                    // match as string
                    if (value.compareTo(config.getObject()) != 0) {
                        continue;
                    }
                } else {
                    // use reg exp
                    if (!pattern.matcher(value).matches()) {
                        continue;
                    }
                }

                // if we are here, then file pass through our filters
                // CopyHelpers.copyMetadata(entry.getSymbolicName(), inFilesData, outFilesData);

                //
                // TODO here we should rather somehow copy metadata from input
                //  to output metadata graps, as otherwise we create new
                //  triples
                outFilesData.addExistingFile(entry.getSymbolicName(), entry.getFileURIString());
                // TODO Remove this
                // as a hack copy virtual path now
                final String virtualPath = VirtualPathHelpers.getVirtualPath(inFilesData, entry.getSymbolicName());
                VirtualPathHelpers.setVirtualPath(outFilesData, entry.getSymbolicName(), virtualPath);
            }
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, "Problem with DataUnit", "", ex);
        }
        //
        // close
        //
        try {
            filesIteration.close();
        } catch (DataUnitException ex) {
            LOG.warn("Error in close.", ex);
        }
    }

}
