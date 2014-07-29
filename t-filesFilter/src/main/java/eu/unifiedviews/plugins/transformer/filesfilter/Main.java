package eu.unifiedviews.plugins.transformer.filesfilter;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.openrdf.model.ValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.copyhelper.CopyHelpers;
import eu.unifiedviews.helpers.dataunit.metadata.Manipulator;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelper;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

@DPU.AsTransformer
public class Main extends ConfigurableBase<Configuration>
        implements ConfigDialogProvider<Configuration> {

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
                context.sendMessage(DPUContext.MessageType.ERROR,
                    "Configuration problem", "Error in object regexp.", ex);
            return;
            }
        }
        //
        // get value factory
        //
        final ValueFactory valueFactory;
        try {
            valueFactory = inFilesData.getConnection().getValueFactory();
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "Problem with DataUnit", "Can't get ValueFactory.", ex);
            return;
        }
        //
        // get predicate
        //
        String predicate;
        if (config.isCustomPredicate()) {
            predicate = config.getPredicate();
        } else {
            if (config.getPredicate().compareTo(
                    FixedPredicates.SYMBOLIC_NAME) == 0) {
                predicate = MetadataDataUnit.PREDICATE_SYMBOLIC_NAME;
            } else if (config.getPredicate().compareTo(
                    FixedPredicates.VIRTUAL_PATH) == 0) {
                predicate = VirtualPathHelper.PREDICATE_VIRTUAL_PATH;
            } else {
                context.sendMessage(DPUContext.MessageType.ERROR,
                        "Configuration problem",
                        "Unknown non-cutom predicate: " + config.getPredicate());
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
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "DPU Failed", "Can't get file iterator.", ex);
            return;
        }
        //
        // filter data here
        //

        try {

// DEBUG
LOG.info("Input: ");
Manipulator.dump(inFilesData);

            while (filesIteration.hasNext()) {
                final FilesDataUnit.Entry entry = filesIteration.next();

                final String value = Manipulator.get(inFilesData,
                        entry.getSymbolicName(),
                        predicate);

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
                CopyHelpers.copyMetadata(entry.getSymbolicName(),
                        inFilesData, outFilesData);

                //
                // TODO here we should rather somehow copy metadata from input
                //  to output metadata graps, as otherwise we create new
                //  triples

                outFilesData.addExistingFile(entry.getSymbolicName(),
                        entry.getFileURIString());

                // TODO Remove this
                // as a hack copy virtual path now
                final String virtualPath =
                        Manipulator.get(inFilesData, entry.getSymbolicName(),
                                VirtualPathHelper.PREDICATE_VIRTUAL_PATH);
                Manipulator.set(outFilesData,  entry.getSymbolicName(),
                                VirtualPathHelper.PREDICATE_VIRTUAL_PATH,
                                virtualPath);

            }

LOG.info("Output: ");
Manipulator.dump(outFilesData);

        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "Problem with DataUnit", "", ex);
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
