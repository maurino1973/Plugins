package ue.unifiedviews.plugins.loaders.file.local;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

public class Dialog extends BaseConfigDialog<Configuration> {
	
	private VerticalLayout mainLayout;
	
    private TextField txtDestination;
    
    private CheckBox checkReplaceExisting;
    
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
		
        txtDestination = new TextField("Destination directory:");
        txtDestination.setWidth("100%");
        txtDestination.setRequired(true);
		mainLayout.addComponent(txtDestination);
        
        checkReplaceExisting = new CheckBox("Replace existing;");
        mainLayout.addComponent(checkReplaceExisting);
		
		setCompositionRoot(mainLayout);
	}

	@Override
	protected void setConfiguration(Configuration c) throws DPUConfigException {
		txtDestination.setValue(c.getDestination());
        checkReplaceExisting.setValue(c.isReplaceExisting());
	}

	@Override
	protected Configuration getConfiguration() throws DPUConfigException {
		
        if (!txtDestination.isValid()) {
			throw new DPUConfigException("Destination must be filled.");
		}
        
        Configuration cnf = new Configuration();
		
        cnf.setDestination(txtDestination.getValue());
        cnf.setReplaceExisting(checkReplaceExisting.getValue());
        
		return cnf;
	}

	@Override
	public String getDescription() {
		final StringBuilder desc = new StringBuilder();

        desc.append("Load data into ");
		desc.append(txtDestination.getValue());
        
		return desc.toString();
	}

}
