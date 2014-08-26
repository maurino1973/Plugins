package eu.unifiedviews.plugins.transformer.sparql2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

/**
 * Configuration dialog for DPU SPARQL Transformer.
 *
 * @authod Petr Škoda
 */
public class SPARQLVaadinDialog2 extends BaseConfigDialog<SPARQLConfig_V1> {
    private static final String OUTPUT_GRAPH_SYMBOLIC_NAME = "Output graph symbolic name";

    private ObjectProperty<String> outputGraphSymbolicName = new ObjectProperty<String>("");

    private static final String DATASET_INPUT_LABEL = "Query should be run as construct (with input data unit graphs as default/named graphs)";

    private enum QueryType {
        CONSTRUCT,
        UPDATE
    };

    private Accordion accordion;

    private Button btnDelete;

    private final LinkedList<TextArea> queries = new LinkedList<>();

    /**
     * Is valid only after isValid is called on all components in queries.
     */
    private final HashMap<TextArea, QueryType> queryTypes = new HashMap<>();

    public SPARQLVaadinDialog2() {
        super(SPARQLConfig_V1.class);
        init();
    }

    private void init() {
        this.setSizeFull();

        Button btnAddQuery = new Button();
        btnAddQuery.setCaption("Add query tab");
        btnAddQuery.setSizeUndefined();
        btnAddQuery.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                addGraph("INSERT { ?s ?p ?o } WHERE {?s ?p ?o }");
            }
        });

        btnDelete = new Button("Delete current");
        btnDelete.setEnabled(false);
        btnDelete.setSizeUndefined();
        btnDelete.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (accordion.getSelectedTab() == null) {
                    return;
                }

                final Tab tab = accordion.getTab(accordion.getSelectedTab());
                final int index = accordion.getTabPosition(tab);

                TextArea txtQuery = queries.get(index);
                queries.remove(txtQuery);
                queryTypes.remove(txtQuery);
                accordion.removeTab(tab);

                btnDelete.setEnabled(!queries.isEmpty());
            }
        });

        HorizontalLayout topLineLayout = new HorizontalLayout();
        topLineLayout.setSizeUndefined();
        topLineLayout.setSpacing(true);
        topLineLayout.addComponent(btnAddQuery);
        topLineLayout.addComponent(btnDelete);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);

        mainLayout.addComponent(topLineLayout);
        mainLayout.setExpandRatio(topLineLayout, 0);

        accordion = new Accordion();
        accordion.setSizeFull();
        mainLayout.addComponent(accordion);
        mainLayout.setExpandRatio(accordion, 1);

        mainLayout.addComponent(new TextField(OUTPUT_GRAPH_SYMBOLIC_NAME, outputGraphSymbolicName));
        setCompositionRoot(mainLayout);
    }

    private void addGraph(String query) {
        final TextArea txtQuery = new TextArea();
        txtQuery.setSizeFull();
        txtQuery.setValue(query);
        txtQuery.setSizeFull();

        final CheckBox checkBox = new CheckBox(DATASET_INPUT_LABEL, false);
        checkBox.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (checkBox.getValue()) {
                    queryTypes.put(txtQuery, QueryType.CONSTRUCT);
                } else {
                    queryTypes.put(txtQuery, QueryType.UPDATE);
                }
            }
        });

        VerticalLayout subLayout = new VerticalLayout();
        subLayout.setSizeFull();
        subLayout.setMargin(true);

        subLayout.addComponent(txtQuery);
        subLayout.addComponent(checkBox);

        // add to main component list
        this.queries.add(txtQuery);
        this.queryTypes.put(txtQuery, QueryType.UPDATE);

        final Tab tab = this.accordion.addTab(subLayout, "Query");

        txtQuery.addValidator(new Validator() {

            @Override
            public void validate(Object value) throws InvalidValueException {
                final String query = value.toString();

                if (query.isEmpty()) {
                    throw new InvalidValueException("SPARQL query is empty it must be filled");
                }

                SPARQLUpdateValidator updateValidator = new SPARQLUpdateValidator(query);
                if (updateValidator.isQueryValid()) {
                    queryTypes.put(txtQuery, QueryType.UPDATE);
                    return;
                } else {
                    throw new InvalidValueException("Not a valid SPARQL update query " + updateValidator.getErrorMessage());
                }
            }
        });

        accordion.setSelectedTab(tab);
    }

    /**
     * Load values from configuration object implementing {@link DPUConfig} interface and configuring DPU into the dialog
     * where the configuration object may be edited.
     *
     * @throws DPUConfigException
     *             Exception not used in current implementation of
     *             this method.
     * @param conf
     *            Object holding configuration which is used to initialize
     *            fields in the configuration dialog.
     */
    @Override
    public void setConfiguration(SPARQLConfig_V1 conf) throws DPUConfigException {
        queries.clear();
        queryTypes.clear();
        accordion.removeAllComponents();

        for (SPARQLQueryPair pair : conf.getQueryPairs()) {
            addGraph(pair.getSPARQLQuery());
        }

        btnDelete.setEnabled(!conf.getQueryPairs().isEmpty());
        outputGraphSymbolicName.setValue(conf.getOutputGraphSymbolicName());
    }

    /**
     * Set values from from dialog where the configuration object may be edited
     * to configuration object implementing {@link DPUConfigObject} interface
     * and configuring DPU
     *
     * @throws DPUConfigException
     *             Exception which might be thrown when any of
     *             SPARQL queries are invalid.
     * @return conf Object holding configuration which is used in {@link #setConfiguration} to initialize fields in the
     *         configuration dialog.
     */
    @Override
    public SPARQLConfig_V1 getConfiguration() throws DPUConfigException {

        SPARQLConfig_V1 conf = new SPARQLConfig_V1();
        List<SPARQLQueryPair> queryPairs = conf.getQueryPairs();

        for (int i = 0; i < queries.size(); i++) {
            TextArea txtQuery = queries.get(i);
            if (!txtQuery.isValid()) {
                throw new DPUConfigException("All queries must be valid!");
            }
            // add to conf
            final boolean isConstruct = queryTypes.get(txtQuery) == QueryType.CONSTRUCT;
            queryPairs.add(new SPARQLQueryPair(txtQuery.getValue(), isConstruct));
        }

        conf.setOutputGraphSymbolicName(outputGraphSymbolicName.getValue());
        return conf;
    }

}
