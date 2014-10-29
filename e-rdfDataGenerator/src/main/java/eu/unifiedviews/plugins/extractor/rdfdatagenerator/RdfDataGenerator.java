package eu.unifiedviews.plugins.extractor.rdfdatagenerator;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

@DPU.AsExtractor
public class RdfDataGenerator extends ConfigurableBase<RdfDataGeneratorConfig_V1> implements ConfigDialogProvider<RdfDataGeneratorConfig_V1> {
    private static final Logger LOG = LoggerFactory.getLogger(RdfDataGenerator.class);

    public RdfDataGenerator() {
        super(RdfDataGeneratorConfig_V1.class);
    }

    @DataUnit.AsOutput(name = "output")
    public WritableRDFDataUnit rdfOutput;

    @Override
    public void execute(DPUContext dpuContext)
            throws DPUException {
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage);
        LOG.info(shortMessage);

        RepositoryConnection connection = null;
        try {
            connection = rdfOutput.getConnection();
            URI outputGraph = rdfOutput.addNewDataGraph(config.getOutputGraphSymbolicName());
            ValueFactory f = new MemValueFactory();
            connection.begin();
            int j = 1;
            for (int i = 1; i <= config.getTripleCount(); i++) {
                connection.add(f.createStatement(
                        f.createURI("http://example.org/people/d" + String.valueOf(j++)),
                        f.createURI("http://example.org/ontology/e" + String.valueOf(j++)),
                        f.createLiteral("Alice" + String.valueOf(j++))
                        ), outputGraph);
                if (i % 25000 == 0) {
                    connection.commit();
                    dpuContext.sendMessage(DPUContext.MessageType.DEBUG, "Number of triples " + String.valueOf(i));
                    if (dpuContext.canceled()) {
                        break;
                    }
                    connection.begin();
                }
            }
            connection.commit();
            dpuContext.sendMessage(DPUContext.MessageType.DEBUG,
                    "Number of triples " + String.valueOf(connection.size(outputGraph)));
        } catch (RepositoryException | DataUnitException ex) {
            dpuContext.sendMessage(DPUContext.MessageType.ERROR, ex.getMessage(), ex
                    .fillInStackTrace().toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    dpuContext.sendMessage(DPUContext.MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                }
            }
        }
    }

    @Override
    public AbstractConfigDialog<RdfDataGeneratorConfig_V1> getConfigurationDialog() {
        return new RdfDataGeneratorVaadinDialog();
    }
}
