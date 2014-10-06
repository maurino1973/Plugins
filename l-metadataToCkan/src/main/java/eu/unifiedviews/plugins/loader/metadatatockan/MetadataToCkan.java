package eu.unifiedviews.plugins.loader.metadatatockan;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.comsode.libraries.jckan.CkanException;
import eu.comsode.libraries.jckan.CkanRepository;
import eu.comsode.libraries.jckan.model.Resource;
import eu.comsode.libraries.jckan.model.StreamUpload;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

@DPU.AsLoader
public class MetadataToCkan extends ConfigurableBase<MetadataToCkanConfig_V1> implements ConfigDialogProvider<MetadataToCkanConfig_V1> {
    private static final Logger LOG = LoggerFactory.getLogger(MetadataToCkan.class);

    public MetadataToCkan() {
        super(MetadataToCkanConfig_V1.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, InterruptedException {
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        String longMessage = String.format("Configuration: %s", config.toString());
        LOG.info(shortMessage + " " + longMessage);
        CkanRepository ckanRepository = new CkanRepository(config.getCkanApiUrl(), config.getCkanApiKey());

        try {
            LOG.info(ckanRepository.getDatasetDAO().read("somedataset").toString());;
            ckanRepository.getResourceDAO().create("somedataset", new Resource(), new StreamUpload("aaa.png", new URL("http://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/Durga%2C_Burdwan%2C_2011.JPG/500px-Durga%2C_Burdwan%2C_2011.JPG").openConnection().getInputStream()));
        } catch (CkanException | IOException ex) {
            throw new DPUException(ex);
        }
    }

    @Override
    public AbstractConfigDialog<MetadataToCkanConfig_V1> getConfigurationDialog() {
        return new MetadataToCkanVaadinDialog();
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
