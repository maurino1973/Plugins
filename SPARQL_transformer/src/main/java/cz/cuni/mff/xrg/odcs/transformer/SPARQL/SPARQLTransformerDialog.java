package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.*;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.configuration.DPUConfigObject;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;
import cz.cuni.mff.xrg.odcs.rdf.enums.SPARQLQueryType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.SPARQLValidationException;
import cz.cuni.mff.xrg.odcs.rdf.validators.SPARQLQueryValidator;
import cz.cuni.mff.xrg.odcs.rdf.validators.SPARQLUpdateValidator;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.QueryValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Configuration dialog for DPU SPARQL Transformer.
 *
 * @author Jiri Tomes
 * @author Maria Kukhar
 */
public class SPARQLTransformerDialog extends BaseConfigDialog<SPARQLTransformerConfig> {

	private GridLayout mainLayout;

	private Label labelUpQuer;

	private GridLayout gridLayoutQuery;

	private Button buttonQueryRem;

	private Button buttonQueryAdd;

	/**
	 * Mapping pairs(query,isSpecialContructQuery)
	 */
	private Map<String, Boolean> map = new HashMap<>();

	private String validationErrorMessage = "No errors";

	/**
	 * Constructor.
	 */
	public SPARQLTransformerDialog() {
		super(SPARQLTransformerConfig.class);
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	private List<String> getQueriesFromPairs(List<SPARQLQueryPair> pairs) {
		List<String> result = new LinkedList<>();

		if (pairs != null) {
			for (SPARQLQueryPair nextPair : pairs) {
				result.add(nextPair.getSPARQLQuery());
			}
		}

		return result;
	}

	/**
	 * Load values from configuration object implementing
	 * {@link DPUConfigObject} interface and configuring DPU into the dialog
	 * where the configuration object may be edited.
	 *
	 * @throws ConfigException Exception not used in current implementation of
	 *                         this method.
	 * @param conf Object holding configuration which is used to initialize
	 *             fields in the configuration dialog.
	 */
	@Override
	public void setConfiguration(SPARQLTransformerConfig conf) throws ConfigException {

		List<SPARQLQueryPair> queryPairs = conf.getQueryPairs();
		SPARQLQueries = getQueriesFromPairs(queryPairs);

		refreshSparqlQueryData();

	}

	/**
	 * Set values from from dialog where the configuration object may be edited
	 * to configuration object implementing {@link DPUConfigObject} interface
	 * and configuring DPU
	 *
	 * @throws ConfigException Exception which might be thrown when any of
	 *                         SPARQL queries are invalid.
	 * @return conf Object holding configuration which is used in
	 *         {@link #setConfiguration} to initialize fields in the
	 *         configuration dialog.
	 */
	@Override
	public SPARQLTransformerConfig getConfiguration() throws ConfigException {

		//Right SPARQL VALIDATOR - default false
		if (!areSparqlQueriesValid()) {
			throw new SPARQLValidationException(validationErrorMessage);
		} else {
			saveEditedTexts();

			List<SPARQLQueryPair> queryPairs = new LinkedList<>();

			for (String query : getSPARQLQueries()) {
				boolean isConstructQuery = map.get(query);

				if (isConstructQuery && !hasValidMoreGraphsForContruct(query)) {
					throw new SPARQLValidationException(validationErrorMessage);
				}
				queryPairs.add(new SPARQLQueryPair(query, isConstructQuery));
			}

			SPARQLTransformerConfig conf = new SPARQLTransformerConfig(
					queryPairs);

			return conf;
		}
	}

	private boolean isPossibleDPUName(String extractedName) {

		boolean result = false;

		for (String dpuName : SPARQLTransformer.DPUNames) {
			if (dpuName.equals(extractedName)) {
				result = true;
				break;
			}
		}
		return result;

	}

	private boolean hasValidMoreGraphsForContruct(String contructQuery) {

		PlaceholdersHelper helper = new PlaceholdersHelper();
		List<String> extractedNames = helper.getExtractedDPUNames(contructQuery);

		if (extractedNames.isEmpty()) {
			return true;
		} else {

			for (String nextExtractedName : extractedNames) {
				if (!isPossibleDPUName(nextExtractedName)) {
					validationErrorMessage = String.format(
							"This INPUT name \"%s\" used in query doesnt exists. "
							+ "Use name defined in mapping for this DPU !!!",
							nextExtractedName);
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Builds main layout with all dialog components.
	 *
	 * @return mainLayout GridLayout with all components of configuration
	 *         dialog.
	 */
	private GridLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new GridLayout(2, 1);
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);
		//mainLayout.setSpacing(true);

		// top-level component properties
		setWidth("100%");
		setHeight("100%");

		// labelUpQuer
		labelUpQuer = new Label();
		labelUpQuer.setImmediate(false);
		labelUpQuer.setWidth("68px");
		labelUpQuer.setHeight("-1px");
		labelUpQuer.setValue("SPARQL  Update Query");
		mainLayout.addComponent(labelUpQuer, 0, 0);

		initializeSparqlQueryList();

		mainLayout.addComponent(gridLayoutQuery, 1, 0);

		mainLayout.setColumnExpandRatio(0, 0.00001f);
		mainLayout.setColumnExpandRatio(1, 0.99999f);

		return mainLayout;
	}

	/**
	 * List<String> that contains SPARQL Update Query.
	 */
	private List<String> SPARQLQueries = initializeGridData();

	/**
	 * Initializes data of the SPARQL Update Query component
	 */
	private static List<String> initializeGridData() {
		List<String> result = new LinkedList<>();
		result.add("");

		return result;

	}

	private List<String> getSPARQLQueries() {
		List<String> result = new LinkedList<>();

		for (String nextQuery : SPARQLQueries) {
			String query = nextQuery.trim();
			if (!query.isEmpty()) {
				result.add(query);
			}
		}
		return result;
	}

	/**
	 * Add new data to SPARQL Update Query component
	 *
	 * @param newData. Query that will be added
	 */
	private void addDataToGridData(String newData) {
		SPARQLQueries.add(newData.trim());
	}

	/**
	 * Remove data from SPARQL Update Query component. Only if component contain
	 * more then 1 row.
	 *
	 * @param row Data that will be removed.
	 */
	private void removeDataFromGridData(Integer row) {
		int index = row;
		if (SPARQLQueries.size() > 1) {
			SPARQLQueries.remove(index);
		}
	}

	private List<TextArea> listedEditText = null;

	/**
	 * Save edited texts in the Named Graph component
	 */
	private void saveEditedTexts() {
		SPARQLQueries.clear();
		for (TextArea editText : listedEditText) {
			SPARQLQueries.add(editText.getValue().trim());
		}
	}

	/**
	 *
	 * @return if all SPARQL Update Queries are valid or not.
	 */
	private boolean areSparqlQueriesValid() {
		for (TextArea next : listedEditText) {
			if (!next.isValid()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Builds SPARQL Update Query component which consists of textareas for
	 * SPARQL query and buttons for add and remove this textareas. Used in
	 * {@link #initializeSparqlQueryList()} and also in adding and removing
	 * textareas for component refresh
	 */
	private void refreshSparqlQueryData() {
		gridLayoutQuery.removeAllComponents();
		int row = 0;
		listedEditText = new ArrayList<>();
		if (SPARQLQueries.size() < 1) {
			SPARQLQueries.add("");
		}
		gridLayoutQuery.setRows(SPARQLQueries.size() + 1);
		for (String item : SPARQLQueries) {
			final TextArea textFieldQuery = new TextArea();
			listedEditText.add(textFieldQuery);
			//text area for the query
			textFieldQuery.setNullRepresentation("");
			textFieldQuery.setImmediate(true);
			textFieldQuery.setWidth("100%");
			textFieldQuery.setHeight("100px");
			textFieldQuery.setData(row);
			textFieldQuery.setValue(item.trim());
			textFieldQuery.setInputPrompt(
					"PREFIX br:<http://purl.org/business-register#>\nMODIFY\nDELETE { ?s pc:contact ?o}\nINSERT { ?s br:contact ?o}\nWHERE {\n\t     ?s a gr:BusinessEntity .\n\t      ?s pc:contact ?o\n}");
			textFieldQuery.addValidator(new Validator() {
				@Override
				public void validate(Object value) throws InvalidValueException {
					final String query = textFieldQuery.getValue().trim();

					if (query.isEmpty()) {

						validationErrorMessage = "SPARQL query is empty a must be filled";
						throw new EmptyValueException(
								"SPARQL query must be filled");

					}
					QueryValidator updateValidator = new SPARQLUpdateValidator(
							query);
					SPARQLQueryValidator constructValidator = new SPARQLQueryValidator(
							query, SPARQLQueryType.CONSTRUCT);

					boolean isConstructValid = constructValidator.isQueryValid();
					boolean isUpdateValid = updateValidator.isQueryValid();

					if (isConstructValid) {
						//query is valid
						map.put(query, true);
						return;
					} else {
						//if is construct query, but no valid
						if (constructValidator.hasSameType()) {
							validationErrorMessage = constructValidator
									.getErrorMessage();
							throw new InvalidValueException(
									"SPARQL query is not valid");
						}
					}

					if (isUpdateValid) {
						map.put(query, false);
					} else {
						validationErrorMessage = updateValidator
								.getErrorMessage();
						throw new InvalidValueException(
								"SPARQL query is not valid");
					}
				}
			});

			//remove button
			buttonQueryRem = new Button();
			buttonQueryRem.setWidth("55px");
			buttonQueryRem.setCaption("-");
			buttonQueryRem.setData(row);
			buttonQueryRem.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(Button.ClickEvent event) {
					saveEditedTexts();
					Button senderButton = event.getButton();
					Integer row = (Integer) senderButton.getData();
					removeDataFromGridData(row);
					refreshSparqlQueryData();
				}
			});
			gridLayoutQuery.addComponent(textFieldQuery, 0, row);
			gridLayoutQuery.addComponent(buttonQueryRem, 1, row);
			gridLayoutQuery.setComponentAlignment(buttonQueryRem,
					Alignment.TOP_RIGHT);
			row++;
		}

		//add button
		buttonQueryAdd = new Button();
		buttonQueryAdd.setCaption("+");
		buttonQueryAdd.setImmediate(true);
		buttonQueryAdd.setWidth("55px");
		buttonQueryAdd.setHeight("-1px");
		buttonQueryAdd.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				saveEditedTexts();
				addDataToGridData("");
				refreshSparqlQueryData();
			}
		});
		gridLayoutQuery.addComponent(buttonQueryAdd, 0, row);
	}

	/**
	 * Initializes Named Graph component. Calls from
	 * {@link #buildVerticalLayoutCore()}
	 */
	private void initializeSparqlQueryList() {

		gridLayoutQuery = new GridLayout();
		gridLayoutQuery.setImmediate(true);
		gridLayoutQuery.setWidth("100%");
		gridLayoutQuery.setHeight("100%");
		gridLayoutQuery.setMargin(false);
		gridLayoutQuery.setColumns(2);
		gridLayoutQuery.setColumnExpandRatio(0, 0.95f);
		gridLayoutQuery.setColumnExpandRatio(1, 0.05f);

		refreshSparqlQueryData();
	}
}
