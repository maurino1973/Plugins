package eu.unifiedviews.plugins.extractor.httpdownload;

import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class HttpDownloadVaadinDialog2 extends BaseConfigDialog<HttpDownloadConfig_V2> {
    /**
     * 
     */
    private static final long serialVersionUID = -5668436075836909428L;

    private static final String CONNECTION_TIMEOUT_LABEL = "Connection timeout (HTTP)";

    private static final String READ_TIMEOUT_LABEL = "Read timeout (HTTP)";

	private static final int SYMBOLIC_NAME_COL = 0;
	private static final int URL_COL = 1;
	private static final int VIRTUAL_PATH_COL = 2;

    private static final String INFO_TEXT = "Files to download (1st field = <symbolicName>, 2nd field = <URL>, 3rd field = <virtualPath>)";

    private ObjectProperty<Integer> connectionTimeout = new ObjectProperty<Integer>(0);

    private ObjectProperty<Integer> readTimeout = new ObjectProperty<Integer>(0);

	private ManipulableListManager dataManager;

    public HttpDownloadVaadinDialog2() {
        super(HttpDownloadConfig_V2.class);
        initialize();
    }

    private void initialize() {
    	setSizeFull();
    	
        FormLayout formLayout = new FormLayout();

        formLayout.addComponent(new TextField(CONNECTION_TIMEOUT_LABEL, connectionTimeout));
        formLayout.addComponent(new TextField(READ_TIMEOUT_LABEL, readTimeout));

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        mainLayout.addComponent(formLayout);
        
        Panel panel = new Panel();
        panel.setSizeFull();
        
        dataManager = new ManipulableListManager(new ManipulableListComponentProvider() {
			
			@Override
			public Component createNewComponent() {
				return createNewComponent(new String[] {"", "", ""});
			}

			@Override
			public Component createNewComponent(String[] values) {
				GridLayout layout = new GridLayout(3, 1);
				layout.setSpacing(true);

				TextField text = new TextField();
				text.setRequired(true);
				text.setValue(values[0] == null ? "" : values[0].trim());
				text.setInputPrompt("shakespeare");
				layout.addComponent(text, SYMBOLIC_NAME_COL, 0);
				
				text = new TextField();
				text.setRequired(true);
				text.setValue(values[1] == null ? "" : values[1].trim());
				text.setInputPrompt("https://commondatastorage.googleapis.com/ckannet-storage/2012-04-24T183403/will_play_text.csv");
				layout.addComponent(text, URL_COL, 0);
				
				text = new TextField();
				text.setRequired(true);
				text.setValue(values[2] == null ? "" : values[2].trim());
				text.setInputPrompt("inputs/shakespeare.csv");
				layout.addComponent(text, VIRTUAL_PATH_COL, 0);
				return layout;
			}
		});
        panel.setContent(dataManager.initList(null));
        mainLayout.addComponent(new Label(INFO_TEXT));
        mainLayout.addComponent(panel);
        mainLayout.setExpandRatio(panel, 1);

        setCompositionRoot(mainLayout);
    }

    @Override
    public void setConfiguration(HttpDownloadConfig_V2 conf) throws DPUConfigException {
        connectionTimeout.setValue(conf.getConnectionTimeout());
        readTimeout.setValue(conf.getReadTimeout());
        
        dataManager.clearComponents();
        for (String key : conf.getSymbolicNameToURIMap().keySet()) {
            
            dataManager.addComponent(new String[]{key,
            		conf.getSymbolicNameToURIMap().get(key),
            		conf.getSymbolicNameToVirtualPathMap().get(key)});
        }
        dataManager.refreshData();
    }

    @Override
    public HttpDownloadConfig_V2 getConfiguration() throws DPUConfigException {
        Map<String, String> symbolicNameToURIMap = new LinkedHashMap<>();
        Map<String, String> symbolicNameToVirtualPathMap = new LinkedHashMap<>();

        List<Component> dataList = dataManager.getComponentList();
        String symbolicName = null;
        String url = null;
        String virtualPath = null;
        for (Component layout : dataList) {
        	symbolicName = getValue(layout, SYMBOLIC_NAME_COL);
        	url = getValue(layout, URL_COL);
        	virtualPath = getValue(layout, VIRTUAL_PATH_COL);
        	
        	if (symbolicName.isEmpty()) {
        		throw new DPUConfigException("Symbolic name can't be empty.");
        	}
        	
        	if (symbolicNameToURIMap.containsKey(symbolicName)) {
        		throw new DPUConfigException(String.format("Duplicate symbolic name %s.", symbolicName));
        	}
        	
        	try {
        		new java.net.URL(url);
        	} catch (MalformedURLException ex) {
        		throw new DPUConfigException(String.format("Wrong URL for symbolic name %s", symbolicName), ex);
        	}
        	
        	if (!getTextField(layout, VIRTUAL_PATH_COL).isValid()) {
        		throw new DPUConfigException(String.format("Missing virtual path for symbolic name %s.", symbolicName));
			}
        	
        	symbolicNameToURIMap.put(symbolicName, url);
        	symbolicNameToVirtualPathMap.put(symbolicName, virtualPath);
        }

        HttpDownloadConfig_V2 conf = new HttpDownloadConfig_V2();
        conf.setSymbolicNameToURIMap(symbolicNameToURIMap);
        conf.setSymbolicNameToVirtualPathMap(symbolicNameToVirtualPathMap);
        conf.setConnectionTimeout(connectionTimeout.getValue());
        conf.setReadTimeout(readTimeout.getValue());
        return conf;
    }
    
    private TextField getTextField(Component layout, int column) {
    	GridLayout gridLayout = (GridLayout) layout;
    	return (TextField) gridLayout.getComponent(column, 0);    	
    }
    
    private String getValue(Component layout, int column) {
    	return getTextField(layout, column).getValue().trim();
    }
}
