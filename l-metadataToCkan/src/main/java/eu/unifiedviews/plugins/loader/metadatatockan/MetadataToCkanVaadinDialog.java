package eu.unifiedviews.plugins.loader.metadatatockan;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class MetadataToCkanVaadinDialog extends BaseConfigDialog<MetadataToCkanConfig_V1> {
    /**
     *
     */
    private static final long serialVersionUID = -5666075809428L;

    private static final String CKAN_API_URL_LABEL = "CKAN Action API URL";

    private static final String CKAN_API_KEY_LABEL = "CKAN API Key";

    private ObjectProperty<String> ckanApiUrl = new ObjectProperty<String>("");

    private ObjectProperty<String> ckanApiKey = new ObjectProperty<String>("");

    public MetadataToCkanVaadinDialog() {
        super(MetadataToCkanConfig_V1.class);
        initialize();
    }

    private void initialize() {
        FormLayout mainLayout = new FormLayout();

        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        mainLayout.addComponent(new TextField(CKAN_API_URL_LABEL, ckanApiUrl));
        mainLayout.addComponent(new TextField(CKAN_API_KEY_LABEL, ckanApiKey));

        setCompositionRoot(mainLayout);
    }

    @Override
    public void setConfiguration(MetadataToCkanConfig_V1 conf) throws DPUConfigException {
        ckanApiUrl.setValue(conf.getCkanApiUrl());
        ckanApiKey.setValue(conf.getCkanApiKey());
    }

    @Override
    public MetadataToCkanConfig_V1 getConfiguration() throws DPUConfigException {
        MetadataToCkanConfig_V1 conf = new MetadataToCkanConfig_V1();
        conf.setCkanApiUrl(ckanApiUrl.getValue());
        conf.setCkanApiKey(ckanApiKey.getValue());
        return conf;
    }
}
