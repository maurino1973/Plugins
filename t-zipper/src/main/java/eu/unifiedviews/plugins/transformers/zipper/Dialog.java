package eu.unifiedviews.plugins.transformers.zipper;

import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

public class Dialog extends BaseConfigDialog<Configuration> {
	
	private VerticalLayout mainLayout;
	
    private TextField txtZipFile;
    
	public Dialog() {
		super(Configuration.class);
		buildMainLayout();
	}
	
	private void buildMainLayout() {
		setWidth("100%");
		setHeight("100%");

		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("-1px");
		
        txtZipFile = new TextField("Zip file path/name:");
        txtZipFile.setWidth("100%");
        txtZipFile.setRequired(true);
		mainLayout.addComponent(txtZipFile);

		setCompositionRoot(mainLayout);
	}

	@Override
	protected void setConfiguration(Configuration c) throws DPUConfigException {
		txtZipFile.setValue(c.getZipFile());
	}

	@Override
	protected Configuration getConfiguration() throws DPUConfigException {
		
        if (!txtZipFile.isValid()) {
			throw new DPUConfigException("Destination must be filled.");
		}
        
        Configuration cnf = new Configuration();
		
        cnf.setZipFile(txtZipFile.getValue());
        
		return cnf;
	}

	@Override
	public String getDescription() {
		final StringBuilder desc = new StringBuilder();

        desc.append("Load data into ");
		desc.append(txtZipFile.getValue());
        
		return desc.toString();
	}

}
