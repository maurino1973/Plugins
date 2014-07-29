package ue.unifiedviews.plugins.transformer.tabular;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

import java.util.Map;

public class Dialog extends BaseConfigDialog<Configuration> {

    private VerticalLayout mainLayout;

    private FormLayout baseFormLayout;

    private GridLayout propertiesGridLayout;

    private OptionGroup ogInputFileType;

    private TextField tfBaseURI;

    private TextField tfColumnWithURISupplement;

    private TextField tfEncoding;

    private TextField tfQuoteChar;

    private TextField tfDelimiterChar;

    private TextField tfEOFSymbols;

    private TextField tfRowLimit;

    public Dialog() {
        super(Configuration.class);

        buildMainLayout();
        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(mainLayout);
        setCompositionRoot(panel);
    }

    private void buildMainLayout() {

        this.setWidth("100%");
        this.setHeight("100%");

        this.mainLayout = new VerticalLayout();
        this.mainLayout.setImmediate(false);
        this.mainLayout.setWidth("100%");
        this.mainLayout.setHeight("-1px");
        this.mainLayout.setMargin(false);

        this.baseFormLayout = new FormLayout();
        this.baseFormLayout.setSizeUndefined();

        this.ogInputFileType = new OptionGroup("Choose the input type:");
        for (String type : TableType.getAll()) {
            this.ogInputFileType.addItem(type);
        }
        this.ogInputFileType.setValue(TableType.CSV);
        this.baseFormLayout.addComponent(this.ogInputFileType);

        this.tfBaseURI = new TextField("Resource URI base");
        this.baseFormLayout.addComponent(this.tfBaseURI);
        tfBaseURI.setRequired(true);
        tfBaseURI.setRequiredError("Resource URI base must be supplied.");

        this.tfColumnWithURISupplement = new TextField("Key column");
        this.baseFormLayout.addComponent(this.tfColumnWithURISupplement);

        this.tfEncoding = new TextField("Encoding");
        this.tfEncoding.setInputPrompt("UTF-8, Cp1250, ...");
        this.baseFormLayout.addComponent(this.tfEncoding);

        this.tfQuoteChar = new TextField("Quote char (for CSV)");
        this.baseFormLayout.addComponent(this.tfQuoteChar);

        this.tfDelimiterChar = new TextField("Delimiter char (for CSV)");
        this.baseFormLayout.addComponent(this.tfDelimiterChar);

        this.tfEOFSymbols = new TextField("End of line symbols (for CSV)");
        this.baseFormLayout.addComponent(this.tfEOFSymbols);

        this.tfRowLimit = new TextField("Rows limit");
        this.baseFormLayout.addComponent(this.tfRowLimit);

        this.baseFormLayout.addComponent(new Label(
                "Column to property URI mappings"));

        this.mainLayout.addComponent(this.baseFormLayout);

        this.propertiesGridLayout = new GridLayout(2, 2);
        this.propertiesGridLayout.setWidth("100%");
        this.propertiesGridLayout.setColumnExpandRatio(0, 1);
        this.propertiesGridLayout.setColumnExpandRatio(1, 6);

        this.addColumnToPropertyMappingsHeading();

        TextField tfColumnName = new TextField();
        this.propertiesGridLayout.addComponent(tfColumnName);
        tfColumnName.setWidth("100%");

        TextField tfPropertyURI = new TextField();
        this.propertiesGridLayout.addComponent(tfPropertyURI);
        tfPropertyURI.setWidth("100%");

        this.mainLayout.addComponent(this.propertiesGridLayout);

        Button bAddColumnToPropertyMapping = new Button("Add mapping");
        bAddColumnToPropertyMapping.addClickListener(new ClickListener() {

            private static final long serialVersionUID = -8609995802749728232L;

            @Override
            public void buttonClick(ClickEvent event) {
                addColumnToPropertyMapping(null, null);
            }
        });
        this.mainLayout.addComponent(bAddColumnToPropertyMapping);
    }

    private void addColumnToPropertyMapping(String columnName,
            String propertyURI) {
        //int rowCount = this.propertiesGridLayout.getRows();

        TextField tfColumnName = new TextField();
        //this.propertiesGridLayout.addComponent(tfColumnName, 0, rowCount);
        this.propertiesGridLayout.addComponent(tfColumnName);
        tfColumnName.setWidth("100%");

        TextField tfPropertyURI = new TextField();
        //this.propertiesGridLayout.addComponent(tfPropertyURI, 1, rowCount);
        this.propertiesGridLayout.addComponent(tfPropertyURI);
        tfPropertyURI.setWidth("100%");

        if (columnName != null) {
            tfColumnName.setValue(columnName);
        }

        if (propertyURI != null) {
            tfPropertyURI.setValue(propertyURI);
        }
    }

    private void removeAllColumnToPropertyMappings() {
        this.propertiesGridLayout.removeAllComponents();
        this.addColumnToPropertyMappingsHeading();
    }

    private void addColumnToPropertyMappingsHeading() {
        this.propertiesGridLayout.addComponent(new Label("Column name"));
        this.propertiesGridLayout.addComponent(new Label("Property URI"));
    }

    @Override
    protected void setConfiguration(Configuration c) throws DPUConfigException {

        if (c.getBaseURI() == null) {
            this.tfBaseURI.setValue("");
        } else {
            this.tfBaseURI.setValue(c.getBaseURI());
        }

        if (c.getColumnWithURISupplement() == null) {
            this.tfColumnWithURISupplement.setValue("");
        } else {
            this.tfColumnWithURISupplement.setValue(c
                    .getColumnWithURISupplement());
        }

        if (c.getEncoding() == null) {
            this.tfEncoding.setValue("");
        } else {
            this.tfEncoding.setValue(c.getEncoding());
        }

        if (c.getQuoteChar() == null) {
            this.tfQuoteChar.setValue("");
        } else {
            this.tfQuoteChar.setValue(c.getQuoteChar());
        }

        if (c.getDelimiterChar() == null) {
            this.tfDelimiterChar.setValue("");
        } else {
            this.tfDelimiterChar.setValue(c.getDelimiterChar());
        }

        if (c.getEofSymbols() == null) {
            this.tfEOFSymbols.setValue("");
        } else {
            this.tfEOFSymbols.setValue(c.getEofSymbols());
        }

        if (c.getRowLimit() == 0) {
            this.tfRowLimit.setValue("");
        } else {
            this.tfRowLimit.setValue(String.valueOf(c.getRowLimit()));
        }

        final String tableType = c.getTableType();
        if (tableType.equals(TableType.DBF)) {
            this.ogInputFileType.setValue("DBF");
        } else if (tableType.equals(TableType.CSV)) {
            this.ogInputFileType.setValue("CSV");
        }

        if (c.getColumnPropertyMap() != null) {

            Map<String, String> columnPropertyMap = c.getColumnPropertyMap();

            this.removeAllColumnToPropertyMappings();

            // add mappings
            for (String key : columnPropertyMap.keySet()) {
                this.addColumnToPropertyMapping(key, columnPropertyMap.get(key));
            }

            // add one empty mapping
            this.addColumnToPropertyMapping(null, null);

        }
    }

    @Override
    protected Configuration getConfiguration() throws DPUConfigException {
        Configuration cnf = new Configuration();

        Map<String, String> columnPropertiesMap = cnf.getColumnPropertyMap();

        // the first row is heading !
        for (int row = 1; row < this.propertiesGridLayout.getRows(); row++) {

            String columnName = ((TextField) this.propertiesGridLayout
                    .getComponent(0, row)).getValue();
            String propertyURI = ((TextField) this.propertiesGridLayout
                    .getComponent(1, row)).getValue();

            if (columnName != null && columnName.length() > 0 && propertyURI != null && propertyURI
                    .length() > 0) {
                columnPropertiesMap.put(columnName, propertyURI);
            }

        }

        String baseURI = this.tfBaseURI.getValue();
        if (baseURI == null || baseURI.length() == 0) {
            cnf.setBaseURI(null);
        } else {
            cnf.setBaseURI(baseURI);
        }

        String columnWithURISupplement = this.tfColumnWithURISupplement
                .getValue();
        if (columnWithURISupplement == null || columnWithURISupplement.length() == 0) {
            cnf.setColumnWithURISupplement(null);
        } else {
            cnf.setColumnWithURISupplement(columnWithURISupplement);
        }

        String encoding = this.tfEncoding.getValue();
        if (encoding == null || encoding.length() == 0) {
            cnf.setEncoding(null);
        } else {
            cnf.setEncoding(encoding);
        }

        String inputFileType = (String) this.ogInputFileType.getValue();
        if (TableType.DBF.equals(inputFileType)) {
            cnf.setTableType(TableType.DBF);
        } else {
            cnf.setTableType(TableType.CSV);
        }

        String quoteChar = this.tfQuoteChar.getValue();
        if (quoteChar == null || quoteChar.length() == 0) {
            cnf.setQuoteChar(null);
        } else {
            cnf.setQuoteChar(quoteChar.substring(0, 1));
        }

        String delimiterChar = this.tfDelimiterChar.getValue();
        if (delimiterChar == null || delimiterChar.length() == 0) {
            cnf.setDelimiterChar(null);
        } else {
            cnf.setDelimiterChar(delimiterChar.substring(0, 1));
        }

        String eofSymbols = this.tfEOFSymbols.getValue();
        if (eofSymbols == null || eofSymbols.length() == 0) {
            cnf.setEofSymbols(null);
        } else {
            cnf.setEofSymbols(eofSymbols);
        }

        int rowLimit;
        try {
            Integer rowLimitObj = new Integer(this.tfRowLimit.getValue());
            rowLimit = rowLimitObj.intValue();
            if (rowLimit < 0) {
                cnf.setRowLimit(0);
            } else {
                cnf.setRowLimit(rowLimit);
            }
        } catch (NumberFormatException ex) {
            cnf.setRowLimit(0);
        }

        return cnf;
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder();

        return desc.toString();
    }

}
