package eu.unifiedviews.plugins.convertors.rdf.files;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;
import java.util.Arrays;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dialog extends BaseConfigDialog<Configuration> {
	
    private static final Logger LOG = LoggerFactory.getLogger(Dialog.class);
    
	private VerticalLayout mainLayout;
	
    private NativeSelect selectRdfFormat;
    
    private CheckBox checkMergeGraphs;
    
    private Panel panelSingleGraph;
    
    private CheckBox checkGenGraphFile;
    
    private TextField txtOutGraphName;

    private TextField txtSingleFileSymbolicName;
    
    private Panel panelMultipleGraphs;
        
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
        mainLayout.setSpacing(true);
		
        selectRdfFormat = new NativeSelect("RDF format:");
        for (RDFFormat item : RDFFormat.values()) {
            selectRdfFormat.addItem(item);
            selectRdfFormat.setItemCaption(item, item.getName());
        }
        selectRdfFormat.setNullSelectionAllowed(false);
        mainLayout.addComponent(selectRdfFormat);
        
        checkMergeGraphs = new CheckBox("Merge graphs:");
        mainLayout.addComponent(checkMergeGraphs);
        
        buildPanelSingleGraph();
		mainLayout.addComponent(panelSingleGraph);
		
        
        checkMergeGraphs.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                
            }
        });
        
		setCompositionRoot(mainLayout);
	}

    private void buildPanelSingleGraph() {
        final VerticalLayout layout = new VerticalLayout();
		mainLayout.setWidth("100%");
		mainLayout.setHeight("-1px");        
        mainLayout.setSpacing(true);
       
        checkGenGraphFile = new CheckBox("Generate graph file:");
        mainLayout.addComponent(checkGenGraphFile);
                
        txtOutGraphName = new TextField("Output graph name:");
        txtOutGraphName.setWidth("100%");
        mainLayout.addComponent(txtOutGraphName);
        
        checkGenGraphFile.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                txtOutGraphName.setEnabled((Boolean)event.getProperty().getValue());
            }
        });
        
        txtSingleFileSymbolicName = new TextField(
                "File path/name without extension:");
        txtSingleFileSymbolicName.setWidth("100%");
        mainLayout.addComponent(txtSingleFileSymbolicName);
        
        panelSingleGraph = new Panel();
        panelSingleGraph.setContent(layout);
    }
    
	@Override
	protected void setConfiguration(Configuration c) throws DPUConfigException {
		selectRdfFormat.setValue(c.getRdfFileFormat());
        checkMergeGraphs.setValue(c.isMergeGraphs());
        if (c.isMergeGraphs()) {
            // single graph
            checkGenGraphFile.setValue(c.isGenGraphFile());
            if (c.isGenGraphFile()) {
                txtOutGraphName.setValue(c.getOutGraphName());
            }
            
            if (!c.getGraphToFileInfo().isEmpty()) {
                final Configuration.GraphToFileInfo info =
                        c.getGraphToFileInfo().get(0);
                txtSingleFileSymbolicName.setValue(info.getOutFileName());
                if (c.getGraphToFileInfo().size() > 1) {
                    LOG.warn("GraphToFileInfo.size() > 1, but were expected equal to 1.");
                }
            } else {
                LOG.warn("No GraphToFileInfo found in configuration.");
            }
        } else {
            // multiple files
        }
	}

	@Override
	protected Configuration getConfiguration() throws DPUConfigException {
		Configuration cnf = new Configuration();
		
        cnf.setRdfFileFormat((RDFFormat)selectRdfFormat.getValue());
        cnf.setMergeGraphs(checkMergeGraphs.getValue());
        
        if (cnf.isMergeGraphs()) {
            // single graph
            cnf.setGenGraphFile(checkGenGraphFile.getValue());
            if (cnf.isGenGraphFile()) {
                cnf.setOutGraphName(txtOutGraphName.getValue());
            }
            final Configuration.GraphToFileInfo info = cnf.new GraphToFileInfo();
            info.setInSymbolicName("");
            info.setOutFileName(txtSingleFileSymbolicName.getValue());
            cnf.setGraphToFileInfo(Arrays.asList(info));
        } else {
            // multiple files
            
        }
		return cnf;
	}

	@Override
	public String getDescription() {
		StringBuilder desc = new StringBuilder();
		
        if (checkMergeGraphs.getValue()) {
            // single file
            desc.append("input->");
            desc.append(txtSingleFileSymbolicName);
            desc.append(".");
            desc.append(((RDFFormat)selectRdfFormat.getValue()).getDefaultFileExtension());
            if (checkGenGraphFile.getValue()) {
                desc.append(" .graph is generated.");
            }
        } else {
            // multiple graphs
        }
        
		return desc.toString();
	}

}
