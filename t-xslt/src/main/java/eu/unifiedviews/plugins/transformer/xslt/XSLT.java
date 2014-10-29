package eu.unifiedviews.plugins.transformer.xslt;

import java.io.File;
import java.io.StringReader;
import java.net.URI;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.fileshelper.FilesHelper;
import eu.unifiedviews.helpers.dataunit.maphelper.MapHelper;
import eu.unifiedviews.helpers.dataunit.maphelper.MapHelpers;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

@DPU.AsTransformer
public class XSLT extends ConfigurableBase<XSLTConfig_V1> implements ConfigDialogProvider<XSLTConfig_V1> {
    private static final Logger LOG = LoggerFactory.getLogger(XSLT.class);

    @DataUnit.AsInput(name = "filesInput")
    public FilesDataUnit filesInput;

    @DataUnit.AsOutput(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public XSLT() {
        super(XSLTConfig_V1.class);
    }

    @Override
    public AbstractConfigDialog<XSLTConfig_V1> getConfigurationDialog() {
        return new XSLTVaadinDialog();
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException {
        //check that XSLT is available
        if (config.getXslTemplate().isEmpty()) {
            throw new DPUException("No XSLT template available, execution interrupted");
        }

        String shortMessage = this.getClass().getSimpleName() + " starting.";
        String longMessage = String.valueOf(config);
        dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage, longMessage);

        //try to compile XSLT
        Processor proc = new Processor(false);
        XsltCompiler comp = proc.newXsltCompiler();
        XsltExecutable exp;
        try {
            exp = comp.compile(new StreamSource(new StringReader(config.getXslTemplate())));
        } catch (SaxonApiException ex) {
            throw new DPUException("Cannot compile XSLT", ex);
        }

        dpuContext.sendMessage(DPUContext.MessageType.INFO, "Stylesheet was compiled successully");

        final Iterator<FilesDataUnit.Entry> filesIteration;
        try {
            filesIteration = FilesHelper.getFiles(filesInput).iterator();
        } catch (DataUnitException ex) {
            dpuContext.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed", "Can't get file iterator.", ex);
            return;
        }
        long filesSuccessfulCount = 0L;
        long index = 0L;
        boolean shouldContinue = !dpuContext.canceled();

        MapHelper mapHelper = MapHelpers.create(filesInput);
        VirtualPathHelper inputVirtualPathHelper = VirtualPathHelpers.create(filesInput);
        VirtualPathHelper outputVirtualPathHelper = VirtualPathHelpers.create(filesOutput);
        String xsltParametersMapName = config.getXlstParametersMapName();
        try {
            while (shouldContinue && filesIteration.hasNext()) {
                FilesDataUnit.Entry entry;
                entry = filesIteration.next();

                String inSymbolicName = entry.getSymbolicName();

                String outputFilename = filesOutput.addNewFile(inSymbolicName);
                String inputVirtualPath = inputVirtualPathHelper.getVirtualPath(inSymbolicName);
                if (inputVirtualPath != null && config.getOutputFileExtension() != null && !config.getOutputFileExtension().isEmpty()) {
                    outputVirtualPathHelper.setVirtualPath(inSymbolicName, FilenameUtils.removeExtension(inputVirtualPath) + config.getOutputFileExtension());
                } else if (config.getOutputFileExtension() != null && !config.getOutputFileExtension().isEmpty()) {
                    outputVirtualPathHelper.setVirtualPath(inSymbolicName, inSymbolicName + config.getOutputFileExtension());
                }
                File outputFile = new File(URI.create(outputFilename));
                File inputFile = new File(URI.create(entry.getFileURIString()));
                try {
                    index++;

                    Date start = new Date();
                    if (dpuContext.isDebugging()) {
                        long inputSizeM = inputFile.length() / 1024 / 1024;
                        LOG.debug("Memory used: {}M", String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024));
                        LOG.debug("Processing {} file {} length {}M", appendNumber(index), entry, inputSizeM);

                    }
                    Serializer out = new Serializer(outputFile);

//                    DocumentBuilder builder = proc.newDocumentBuilder();
//                    builder.setTreeModel(TreeModel.TINY_TREE_CONDENSED);
//                    XdmNode source = builder.build(new StreamSource(entry.getFilesystemURI().toASCIIString()));
//                    trans.setInitialContextNode(source);
                    XsltTransformer trans = exp.load();
                    Map<String, String> xsltParameters = mapHelper.getMap(inSymbolicName, xsltParametersMapName);
                    if (xsltParameters != null) {
                        for (String key : xsltParameters.keySet()) {
                            trans.setParameter(new QName(key), new XdmAtomicValue(xsltParameters.get(key)));
                        }
                    }
                    trans.setSource(new StreamSource(inputFile));
                    trans.setDestination(out);
                    trans.transform();
                    trans.getUnderlyingController().clearDocumentPool();

                    filesSuccessfulCount++;

                    if (dpuContext.isDebugging()) {
                        LOG.debug("Processed {} file in {}s", appendNumber(index), (System.currentTimeMillis() - start.getTime()) / 1000);
                        LOG.debug("Memory used: " + String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024) + "M");
                    }
                } catch (SaxonApiException | DataUnitException ex) {
                    dpuContext.sendMessage(
                            config.isSkipOnError() ? DPUContext.MessageType.WARNING : DPUContext.MessageType.ERROR,
                                    "Error processing " + appendNumber(index) + " file",
                                    String.valueOf(entry),
                                    ex);
                }
                shouldContinue = !dpuContext.canceled();
            }
        } catch (DataUnitException ex) {
            throw new DPUException("Error iterating filesInput.", ex);
        } finally {
            try {
                mapHelper.close();
            } catch (DataUnitException ex) {
                LOG.warn("Error in close", ex);
            }
            try {
                inputVirtualPathHelper.close();
            } catch (DataUnitException ex) {
                LOG.warn("Error in close", ex);
            }
            try {
                outputVirtualPathHelper.close();
            } catch (DataUnitException ex) {
                LOG.warn("Error in close", ex);
            }
        }
        String message = String.format("Processed %d/%d", filesSuccessfulCount, index);
        dpuContext.sendMessage(filesSuccessfulCount < index ? DPUContext.MessageType.WARNING : DPUContext.MessageType.INFO, message);
    }

    public static String appendNumber(long number) {
        String value = String.valueOf(number);
        if (value.length() > 1) {
            // Check for special case: 11 - 13 are all "th".
            // So if the second to last digit is 1, it is "th".
            char secondToLastDigit = value.charAt(value.length() - 2);
            if (secondToLastDigit == '1') {
                return value + "th";
            }
        }
        char lastDigit = value.charAt(value.length() - 1);
        switch (lastDigit) {
            case '1':
                return value + "st";
            case '2':
                return value + "nd";
            case '3':
                return value + "rd";
            default:
                return value + "th";
        }
    }
}
