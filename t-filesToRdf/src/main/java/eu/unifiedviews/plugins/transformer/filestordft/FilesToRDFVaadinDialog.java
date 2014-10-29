package eu.unifiedviews.plugins.transformer.filestordft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class FilesToRDFVaadinDialog extends BaseConfigDialog<FilesToRDFConfig_V1> {
    /**
     * 
     */
    private static final long serialVersionUID = -5668436075836909428L;

    private static final String MAP_TEXT = "Symbolic name to baseURI and Format map. Line format: symbolicName;baseURI(optional);FileFormat(optional)";

    private static final String COMMIT_SIZE_LABEL = "Commit size (0 = one file, one transaction, 1 = autocommit connection, n = commit every n triples)";

    private ObjectProperty<String> mapText = new ObjectProperty<String>("");

    private ObjectProperty<Integer> commitSize = new ObjectProperty<Integer>(0);

    public FilesToRDFVaadinDialog() {
        super(FilesToRDFConfig_V1.class);
        initialize();
    }

    private void initialize() {
    	// top-level component properties
    	setSizeFull();
    	
    	Panel panel = new Panel();
    	panel.setSizeFull();

    	VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(false);
        mainLayout.setSpacing(true);
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        
        mainLayout.addComponent(new TextField(COMMIT_SIZE_LABEL, commitSize));

        TextArea ta = new TextArea(MAP_TEXT, mapText);
        ta.setNullRepresentation("");
        ta.setImmediate(false);
        ta.setRows(50);
        ta.setColumns(50);
        mainLayout.addComponent(ta);
        
        panel.setContent(mainLayout);
        setCompositionRoot(panel);
    }

    @Override
    public void setConfiguration(FilesToRDFConfig_V1 conf) throws DPUConfigException {
        commitSize.setValue(conf.getCommitSize());
        
        StringBuilder sb = new StringBuilder();
        for (String key : conf.getSymbolicNameToBaseURIMap().keySet()) {
            sb.append(key);
            sb.append(";");
            sb.append(conf.getSymbolicNameToBaseURIMap().get(key));
            sb.append(";");
            if (conf.getSymbolicNameToFormatMap().get(key) != null) {
                sb.append(conf.getSymbolicNameToFormatMap().get(key));
            }
            sb.append("\n");
        }
        mapText.setValue(sb.toString());
    }

    @Override
    public FilesToRDFConfig_V1 getConfiguration() throws DPUConfigException {
        Map<String, String> symbolicNameToBaseURIMap = new LinkedHashMap<>();
        Map<String, String> symbolicNameToFormatMap = new LinkedHashMap<>();
        BufferedReader br = new BufferedReader(new StringReader(mapText.getValue()));

        String line;
        int i = 1;
        try {
            while ((line = br.readLine()) != null) {
                String[] val = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, ";");
                if (val.length < 2) {
                    throw new DPUConfigException(String.format("Line %d %s has invalid format.", i, line));
                }

                if (symbolicNameToBaseURIMap.containsKey(val[0])) {
                    throw new DPUConfigException(String.format("Duplicate symbolic name %s on line %d.", val[0], i));
                }

                if (val[1] != null && val[1].length() > 0) {
                    try {
                        new URIImpl(val[1]);
                    } catch (IllegalArgumentException ex) {
                        throw new DPUConfigException(String.format("Wrong URI on line %d symbolic name", i, val[0]), ex);
                    }
                    if (!val[1].startsWith("http://")) {
                        throw new DPUConfigException(String.format("Wrong base URI on line %d symbolic name", i, val[0]));
                    }
                    symbolicNameToBaseURIMap.put(val[0], val[1]);
                }

                if (val[2] != null && val[2].length() > 0) {
                    if (null == RDFFormat.valueOf(val[2])) {
                        throw new DPUConfigException(String.format("Unsupported format %s on line %d symbolic name", val[2], i, val[0]));
                    }
                    symbolicNameToFormatMap.put(val[0], val[2]);
                }
                i++;
            }
        } catch (IOException ex) {
            throw new DPUConfigException(ex);
        }

        FilesToRDFConfig_V1 conf = new FilesToRDFConfig_V1();
        conf.setSymbolicNameToBaseURIMap(symbolicNameToBaseURIMap);
        conf.setSymbolicNameToFormatMap(symbolicNameToFormatMap);
        conf.setCommitSize(commitSize.getValue());
        return conf;
    }

}
