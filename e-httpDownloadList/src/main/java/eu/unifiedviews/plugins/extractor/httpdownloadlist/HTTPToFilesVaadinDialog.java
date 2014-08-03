package eu.unifiedviews.plugins.extractor.httpdownloadlist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class HTTPToFilesVaadinDialog extends BaseConfigDialog<HTTPToFilesConfig> {
    /**
     * 
     */
    private static final long serialVersionUID = -5668436075836909428L;

    private static final String CONNECTION_TIMEOUT_LABEL = "Connection timeout (HTTP)";

    private static final String READ_TIMEOUT_LABEL = "Read timeout (HTTP)";

    private static final String MAP_TEXT = "Files to download (one file = one line in format <symbolicName>;<URL>;[virtualPath])";

    private ObjectProperty<Integer> connectionTimeout = new ObjectProperty<Integer>(0);

    private ObjectProperty<Integer> readTimeout = new ObjectProperty<Integer>(0);

    private ObjectProperty<String> mapText = new ObjectProperty<String>("");

    public HTTPToFilesVaadinDialog() {
        super(HTTPToFilesConfig.class);
        initialize();
    }

    private void initialize() {
        FormLayout formLayout = new FormLayout();

        formLayout.addComponent(new TextField(CONNECTION_TIMEOUT_LABEL, connectionTimeout));
        formLayout.addComponent(new TextField(READ_TIMEOUT_LABEL, readTimeout));

        VerticalLayout mainLayout = new VerticalLayout();

        TextArea textArea = new TextArea(MAP_TEXT, mapText);
        textArea.setWidth("100%");
        textArea.setInputPrompt("shakespeare;https://commondatastorage.googleapis.com/ckannet-storage/2012-04-24T183403/will_play_text.csv;inputs/shakespeare.csv");

        mainLayout.addComponent(formLayout);
        mainLayout.addComponent(textArea);

        setCompositionRoot(mainLayout);
    }

    @Override
    public void setConfiguration(HTTPToFilesConfig conf) throws DPUConfigException {
        connectionTimeout.setValue(conf.getConnectionTimeout());
        readTimeout.setValue(conf.getReadTimeout());
        StringBuilder sb = new StringBuilder();
        for (String key : conf.getSymbolicNameToURIMap().keySet()) {
            sb.append(key);
            sb.append(";");
            sb.append(conf.getSymbolicNameToURIMap().get(key));
            if (conf.getSymbolicNameToVirtualPathMap().containsKey(key)) {
                sb.append(";");
                sb.append(conf.getSymbolicNameToVirtualPathMap().get(key));
            }
            sb.append("\n");
        }
        mapText.setValue(sb.toString());
    }

    @Override
    public HTTPToFilesConfig getConfiguration() throws DPUConfigException {
        Map<String, String> symbolicNameToURIMap = new LinkedHashMap<>();
        Map<String, String> symbolicNameToVirtualPathMap = new LinkedHashMap<>();
        BufferedReader br = new BufferedReader(new StringReader(mapText.getValue()));

        String line;
        int i = 1;
        try {
            while ((line = br.readLine()) != null) {
                String[] val = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, ";");
                if (val.length < 2) {
                    throw new DPUConfigException(String.format("Line %d %s has invalid format.", i, line));
                }

                if (symbolicNameToURIMap.containsKey(val[0])) {
                    throw new DPUConfigException(String.format("Duplicate symbolic name %s on line %d.", val[0], i));
                }

                try {
                    new java.net.URL(val[1]);
                } catch (MalformedURLException ex) {
                    throw new DPUConfigException(String.format("Wrong URL on line %d symbolic name", i, val[0]), ex);
                }
                symbolicNameToURIMap.put(val[0], val[1]);
                if (val.length >= 3) {
                    symbolicNameToVirtualPathMap.put(val[0], val[2]);
                }
                i++;
            }
        } catch (IOException ex) {
            throw new DPUConfigException(ex);
        }

        HTTPToFilesConfig conf = new HTTPToFilesConfig();
        conf.setSymbolicNameToURIMap(symbolicNameToURIMap);
        conf.setSymbolicNameToVirtualPathMap(symbolicNameToVirtualPathMap);
        conf.setConnectionTimeout(connectionTimeout.getValue());
        conf.setReadTimeout(readTimeout.getValue());
        return conf;
    }

}
