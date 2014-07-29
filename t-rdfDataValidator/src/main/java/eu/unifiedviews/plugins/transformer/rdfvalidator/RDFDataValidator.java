package eu.unifiedviews.plugins.transformer.rdfvalidator;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

/**
 * DPU for RDF data validation and save validation report in RDF TURTLE (TTL)
 * syntax to given file.
 *
 * @author Jiri Tomes
 */
@DPU.AsTransformer
public class RDFDataValidator extends ConfigurableBase<RDFDataValidatorConfig>
        implements ConfigDialogProvider<RDFDataValidatorConfig> {

    private static final Logger LOG = LoggerFactory
            .getLogger(RDFDataValidator.class);

    /**
     * Input RDF data repository with data we want to validate.
     */
    @DataUnit.AsInput(name = "input")
    public RDFDataUnit dataInput;

    /**
     * Output RDF data repository with only validate triples get from input.
     */
    @DataUnit.AsOutput(name = "Validated_Data", optional = true, description = "Output RDF data repository with only validated triples get from input.")
    public WritableRDFDataUnit dataOutput;

    /**
     * Output RDF repository report about invalid data describe as RDF triples.
     */
    @DataUnit.AsOutput(name = "Report", description = "Output RDF repository report about invalid data described as RDF triples.")
    public WritableRDFDataUnit reportOutput;

    public RDFDataValidator() {
        super(RDFDataValidatorConfig.class);
    }

    /**
     * Returns the configuration dialogue for RDF Data validator.
     *
     * @return the configuration dialogue for RDF Data validator.
     */
    @Override
    public AbstractConfigDialog<RDFDataValidatorConfig> getConfigurationDialog() {
        return new RDFDataValidatorDialog();
    }

    private void makeValidationReport(DataValidator validator,
            String graphName, DPUContext context, boolean stopExecution)
            throws DPUException, DataUnitException {

        context.sendMessage(DPUContext.MessageType.INFO,
                "Start creating VALIDATION REPORT", String.format(
                        "Start generating validation report output for graph <%s> .",
                        graphName));

        ReportCreator reporter = new ReportCreator(validator
                .getFindedProblems(), graphName);
        reporter.makeOutputReport(reportOutput);

        context.sendMessage(DPUContext.MessageType.INFO,
                "VALIDATION REPORT created SUCCESSFULLY", String.format(
                        "Validation report output for graph <%s> created successfully",
                        graphName));

        if (stopExecution) {
            RepositoryConnection connection = null;
            try {
                connection = dataOutput.getConnection();
                connection.clear(dataOutput.getBaseDataGraphURI());
            } catch (RepositoryException ex) {
                LOG.warn("Error", ex);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (RepositoryException ex) {
                        LOG.warn("Error when closing connection", ex);
                        // eat close exception, we cannot do anything clever here
                    }
                }
            }
            throw new DPUException(
                    "RDFDataValidator found some invalid data - pipeline execution is stopped");
        }
    }

    /**
     * Execute the RDF Data validator.
     *
     * @param context RDF Data validator context.
     * @throws DPUException if this DPU fails.
     */
    @Override
    public void execute(DPUContext context)
            throws DPUException {

        final boolean stopExecution = config.canStopExecution();
        final boolean sometimesOutput = config.hasSometimesOutput();

        try {

            DataValidator validator = new RepositoryDataValidator(dataInput,
                    dataOutput);
            String graphName = dataInput.getDataGraphnames().toString();

            if (sometimesOutput) {
                if (!validator.areDataValid()) {
                    context.sendMessage(DPUContext.MessageType.WARNING,
                            "Validator found some INVALID DATA",
                            validator.getErrorMessage() + "\nIt will be created validation report.");
                    LOG.error(validator.getErrorMessage());

                    makeValidationReport(validator, graphName, context, stopExecution);

                } else {
                    context.sendMessage(DPUContext.MessageType.INFO,
                            "Validation Sucessful - NO errors",
                            "All RDF data are valid. Validation report will be not created.");
                }
            } else {
                if (!validator.areDataValid()) {
                    context.sendMessage(DPUContext.MessageType.WARNING,
                            "Validator found some INVALID DATA",
                            "Some RDF data are invalid:\n"
                            + validator.getErrorMessage()
                            + " It will be created validation report");
                } else {
                    context.sendMessage(DPUContext.MessageType.INFO,
                            "Validation Sucessful - NO errors",
                            "All RDF data are valid. Validation report output will be empty");
                }

                makeValidationReport(validator, graphName, context, stopExecution);

            }
        } catch ( DataUnitException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, e.getMessage(),
                    e.fillInStackTrace().toString());
        }

    }
}
