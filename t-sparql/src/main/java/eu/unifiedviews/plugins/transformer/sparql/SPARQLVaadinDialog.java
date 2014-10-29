package eu.unifiedviews.plugins.transformer.sparql;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
public class SPARQLVaadinDialog extends BaseConfigDialog<SPARQLConfig_V1> {
    private static final String OUTPUT_GRAPH_SYMBOLIC_NAME = "Output graph symbolic name";

    private ObjectProperty<String> outputGraphSymbolicName = new ObjectProperty<String>("");

    private static final String REWRITE_CONSTRUCT_TO_INSERT_LABEL = "Rewrite construct query type to insert (always done, cannot change)";

    private enum QueryType {
        INVALID,
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

    public SPARQLVaadinDialog() {
        super(SPARQLConfig_V1.class);
        init();
    }

    private void init() {
        this.setSizeFull();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);

        HorizontalLayout topLineLayout = new HorizontalLayout();
        topLineLayout.setSizeUndefined();
        topLineLayout.setSpacing(true);

        Button btnAddQuery = new Button();
        btnAddQuery.setCaption("Add query tab");
        btnAddQuery.setSizeUndefined();
        btnAddQuery.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                addGraph("CONSTRUCT { ?s ?p ?o } WHERE {?s ?p ?o }");
            }
        });
        topLineLayout.addComponent(btnAddQuery);

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

        topLineLayout.addComponent(btnDelete);

        mainLayout.addComponent(topLineLayout);
        mainLayout.setExpandRatio(topLineLayout, 0);

        accordion = new Accordion();
        accordion.setSizeFull();
        mainLayout.addComponent(accordion);
        mainLayout.setExpandRatio(accordion, 1);

        mainLayout.addComponent(new TextField(OUTPUT_GRAPH_SYMBOLIC_NAME, outputGraphSymbolicName));
        CheckBox rewriteConstructToInsertCheckbox = new CheckBox(REWRITE_CONSTRUCT_TO_INSERT_LABEL, true);
        rewriteConstructToInsertCheckbox.setEnabled(false);
        mainLayout.addComponent(rewriteConstructToInsertCheckbox);

        setCompositionRoot(mainLayout);
    }

    private void addGraph(String query) {
        VerticalLayout subLayout = new VerticalLayout();
        subLayout.setSizeFull();
        subLayout.setMargin(true);

        final TextArea txtQuery = new TextArea();
        txtQuery.setSizeFull();
        txtQuery.setValue(query);
        txtQuery.setSizeFull();

        subLayout.addComponent(txtQuery);

        // add to main component list
        this.queries.add(txtQuery);
        this.queryTypes.put(txtQuery, QueryType.INVALID);

        final Tab tab = this.accordion.addTab(subLayout, "Query");

        txtQuery.addValidator(new Validator() {

            @Override
            public void validate(Object value) throws InvalidValueException {
                final String query = value.toString();

                if (query.isEmpty()) {
                    throw new InvalidValueException(
                            "SPARQL query is empty it must be filled");
                }

                QueryValidator updateValidator =
                        new SPARQLUpdateValidator(query);
                SPARQLQueryValidator constructValidator =
                        new SPARQLQueryValidator(query, SPARQLQueryType.CONSTRUCT);

                // also store type in case of sucessful validation
                if (constructValidator.isQueryValid()) {
                    queryTypes.put(txtQuery, QueryType.CONSTRUCT);
                    return;
                }

                if (updateValidator.isQueryValid()) {
                    queryTypes.put(txtQuery, QueryType.UPDATE);
                    return;
                }

                queryTypes.put(txtQuery, QueryType.INVALID);

                // return message based on query type
                if (constructValidator.hasSameType()) {
                    throw new InvalidValueException(
                            constructValidator.getErrorMessage());
                } else {
                    throw new InvalidValueException(
                            updateValidator.getErrorMessage());
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
