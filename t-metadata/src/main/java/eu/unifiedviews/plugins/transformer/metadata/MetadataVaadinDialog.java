package eu.unifiedviews.plugins.transformer.metadata;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;
import java.util.Arrays;

public class MetadataVaadinDialog extends BaseConfigDialog<MetadataConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataVaadinDialog.class);

    private class URLandCaption {

        public URL url;

        public String caption;

        URLandCaption(URL u, String c) {
            url = u;
            caption = c;
        }
    }

    private static final long serialVersionUID = 7003725620084616056L;

    private VerticalLayout mainLayout;

    private TextField tfComsodeDatasetId;

    private TextField txtOutSymbolicName;

    private TextField tfLanguage;

    private TextField tfTitle;

    private TextField tfTitleEn;

    private TextField tfDesc;

    private TextField tfDescEn;

    private TextField tfDatasetUri;

    private TextField tfDistributionUri;

    private TextField tfDataDumpUrl;

    private TextField tfSparqlEndpointUrl;

    private TextField tfContactPoint;

    private CheckBox chkNow;

    private CheckBox chkQb;

    private DateField dfModified;

    private ComboBox cbPeriodicity;

    private ComboBox cbMime;

    private TwinColSelect tcsLicenses;

    private TwinColSelect tcsExamples;

    private TwinColSelect tcsSources;

    private TwinColSelect tcsAuthors;

    private TwinColSelect tcsPublishers;

    private TwinColSelect tcsKeywords;

    private TwinColSelect tcsThemes;

    private TwinColSelect tcsLanguages;

    private final List<URLandCaption> periodicities = new LinkedList<>();

    private final String[] mimes = { "application/zip", "text/csv", "application/rdf+xml", "text/plain", "application/x-turtle" };

    public MetadataVaadinDialog() {
        super(MetadataConfig_V1.class);
        try {
            periodicities.add(new URLandCaption(new URL("http://purl.org/linked-data/sdmx/2009/code#freq-A"), "Annual"));
            periodicities.add(new URLandCaption(new URL("http://purl.org/linked-data/sdmx/2009/code#freq-B"), "Daily - business week"));
            periodicities.add(new URLandCaption(new URL("http://purl.org/linked-data/sdmx/2009/code#freq-D"), "Daily"));
            periodicities.add(new URLandCaption(new URL("http://purl.org/linked-data/sdmx/2009/code#freq-M"), "Monthly"));
            periodicities.add(new URLandCaption(new URL("http://purl.org/linked-data/sdmx/2009/code#freq-N"), "Minutely"));
            periodicities.add(new URLandCaption(new URL("http://purl.org/linked-data/sdmx/2009/code#freq-Q"), "Quarterly"));
            periodicities.add(new URLandCaption(new URL("http://purl.org/linked-data/sdmx/2009/code#freq-S"), "Half Yearly, semester"));
            periodicities.add(new URLandCaption(new URL("http://purl.org/linked-data/sdmx/2009/code#freq-W"), "Weekly"));
        } catch (MalformedURLException ex) {
            LOG.warn("Error in ctor.", ex);
        }
        buildMainLayout();

        Panel p = new Panel();
        p.setSizeFull();
        p.setContent(mainLayout);

        setCompositionRoot(p);
    }

    private VerticalLayout buildMainLayout() {
        // common part: create layout
        mainLayout = new VerticalLayout();
        mainLayout.setImmediate(true);
        mainLayout.setWidth("100%");
        mainLayout.setHeight(null);
        mainLayout.setMargin(false);
        //mainLayout.setSpacing(true);

        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        txtOutSymbolicName = new TextField();
        txtOutSymbolicName.setCaption("Output graph name:");
        txtOutSymbolicName.setWidth("100%");
        mainLayout.addComponent(txtOutSymbolicName);

        tfComsodeDatasetId = new TextField();
        tfComsodeDatasetId.setCaption("COMSODE Dataset ID (will be used as part of the URI)");
        tfComsodeDatasetId.setWidth("100%");
        tfComsodeDatasetId.setInputPrompt("MICR_3");
        mainLayout.addComponent(tfComsodeDatasetId);

        tfDatasetUri = new TextField();
        tfDatasetUri.setCaption("Dataset URI:");
        tfDatasetUri.setWidth("100%");
        mainLayout.addComponent(tfDatasetUri);

        tfDistributionUri = new TextField();
        tfDistributionUri.setCaption("Distribution URI:");
        tfDistributionUri.setWidth("100%");
        mainLayout.addComponent(tfDistributionUri);

        tfDataDumpUrl = new TextField();
        tfDataDumpUrl.setCaption("Data dump URL:");
        tfDataDumpUrl.setWidth("100%");
        mainLayout.addComponent(tfDataDumpUrl);

        cbMime = new ComboBox();
        cbMime.setCaption("Media Type:");
        cbMime.setNewItemsAllowed(false);
        cbMime.setNullSelectionAllowed(false);
        cbMime.setWidth("100%");
        for (String u : mimes) {
            cbMime.addItem(u);
        }
        mainLayout.addComponent(cbMime);

        tfSparqlEndpointUrl = new TextField();
        tfSparqlEndpointUrl.setCaption("Sparql Endpoint URI:");
        tfSparqlEndpointUrl.setWidth("100%");
        mainLayout.addComponent(tfSparqlEndpointUrl);

        tfContactPoint = new TextField();
        tfContactPoint.setCaption("Contact Point URL:");
        tfContactPoint.setWidth("100%");
        mainLayout.addComponent(tfContactPoint);

        tfLanguage = new TextField();
        tfLanguage.setCaption("Original language - RDF language tag:");
        tfLanguage.setInputPrompt("cs|sk|it");
        tfLanguage.setWidth("100%");
        mainLayout.addComponent(tfLanguage);

        tfTitle = new TextField();
        tfTitle.setCaption("Title original language:");
        tfTitle.setWidth("100%");
        mainLayout.addComponent(tfTitle);

        tfTitleEn = new TextField();
        tfTitleEn.setCaption("Title in English:");
        tfTitleEn.setWidth("100%");
        mainLayout.addComponent(tfTitleEn);

        tfDesc = new TextField();
        tfDesc.setCaption("Description original language:");
        tfDesc.setWidth("100%");
        mainLayout.addComponent(tfDesc);

        tfDescEn = new TextField();
        tfDescEn.setCaption("Description in English:");
        tfDescEn.setWidth("100%");
        mainLayout.addComponent(tfDescEn);

        chkQb = new CheckBox();
        chkQb.setCaption("Dataset is RDF Data Cube");
        chkQb.setWidth("100%");
        mainLayout.addComponent(chkQb);

        dfModified = new DateField();
        dfModified.setCaption("Modified:");
        dfModified.setWidth("100%");
        dfModified.setResolution(Resolution.DAY);
        mainLayout.addComponent(dfModified);

        chkNow = new CheckBox();
        chkNow.setCaption("Always use current date instead");
        chkNow.setWidth("100%");
        mainLayout.addComponent(chkNow);

        cbPeriodicity = new ComboBox();
        cbPeriodicity.setCaption("Periodicity:");
        cbPeriodicity.setNewItemsAllowed(false);
        cbPeriodicity.setNullSelectionAllowed(false);
        cbPeriodicity.setItemCaptionMode(ItemCaptionMode.EXPLICIT);
        cbPeriodicity.setWidth("100%");
        for (URLandCaption u : periodicities) {
            cbPeriodicity.addItem(u.url.toString());
            cbPeriodicity.setItemCaption(u.url.toString(), u.caption);
        }
        mainLayout.addComponent(cbPeriodicity);

        tcsLicenses = new TwinColSelect();
        tcsLicenses.setWidth("97%");
        tcsLicenses.setNewItemsAllowed(true);
        tcsLicenses.setLeftColumnCaption("Available licenses");
        tcsLicenses.setRightColumnCaption("Selected licenses");
        mainLayout.addComponent(tcsLicenses);

        tcsExamples = new TwinColSelect();
        tcsExamples.setWidth("97%");
        tcsExamples.setNewItemsAllowed(true);
        tcsExamples.setLeftColumnCaption("Available example resources");
        tcsExamples.setRightColumnCaption("Selected example resources");
        mainLayout.addComponent(tcsExamples);

        tcsSources = new TwinColSelect();
        tcsSources.setWidth("97%");
        tcsSources.setNewItemsAllowed(true);
        tcsSources.setLeftColumnCaption("Available sources");
        tcsSources.setRightColumnCaption("Selected sources");
        mainLayout.addComponent(tcsSources);

        tcsKeywords = new TwinColSelect();
        tcsKeywords.setWidth("97%");
        tcsKeywords.setNewItemsAllowed(true);
        tcsKeywords.setLeftColumnCaption("Available keywords");
        tcsKeywords.setRightColumnCaption("Selected keywords");
        mainLayout.addComponent(tcsKeywords);

        tcsThemes = new TwinColSelect();
        tcsThemes.setWidth("97%");
        tcsThemes.setNewItemsAllowed(true);
        tcsThemes.setLeftColumnCaption("Available themes");
        tcsThemes.setRightColumnCaption("Selected themes");
        mainLayout.addComponent(tcsThemes);

        tcsLanguages = new TwinColSelect();
        tcsLanguages.setWidth("97%");
        tcsLanguages.setNewItemsAllowed(true);
        tcsLanguages.setLeftColumnCaption("Available languages");
        tcsLanguages.setRightColumnCaption("Selected languages");
        mainLayout.addComponent(tcsLanguages);

        tcsAuthors = new TwinColSelect();
        tcsAuthors.setWidth("97%");
        tcsAuthors.setNewItemsAllowed(true);
        tcsAuthors.setLeftColumnCaption("Available authors");
        tcsAuthors.setRightColumnCaption("Selected authors");
        mainLayout.addComponent(tcsAuthors);

        tcsPublishers = new TwinColSelect();
        tcsPublishers.setWidth("97%");
        tcsPublishers.setNewItemsAllowed(true);
        tcsPublishers.setLeftColumnCaption("Available publishers");
        tcsPublishers.setRightColumnCaption("Selected publishers");
        mainLayout.addComponent(tcsPublishers);

        return mainLayout;
    }

    @Override
    public void setConfiguration(MetadataConfig_V1 conf) throws DPUConfigException {
        //
        txtOutSymbolicName.setValue(conf.getOutputGraphName());
        tfComsodeDatasetId.setValue(conf.getComsodeDatasetId());
        tfDatasetUri.setValue(conf.getDatasetURI().toString());
        tfDistributionUri.setValue(conf.getDistroURI().toString());
        tfDataDumpUrl.setValue(conf.getDataDump().toString());
        tfSparqlEndpointUrl.setValue(conf.getSparqlEndpoint().toString());
        tfContactPoint.setValue(conf.getContactPoint().toString());
        tfLanguage.setValue(conf.getLanguage_cs());
        tfTitle.setValue(conf.getTitle_cs());
        tfTitleEn.setValue(conf.getTitle_en());
        tfDesc.setValue(conf.getDesc_cs());
        tfDescEn.setValue(conf.getDesc_en());
        chkNow.setValue(conf.isUseNow());
        chkQb.setValue(conf.isIsQb());
        dfModified.setValue(conf.getModified());
        cbMime.setValue(conf.getMime());
        cbPeriodicity.setValue(conf.getPeriodicity().toString());

        setTcsConfig(conf.getSources(), conf.getPossibleSources(), tcsSources);
        setTcsConfig(conf.getAuthors(), conf.getPossibleAuthors(), tcsAuthors);
        setTcsConfig(conf.getPublishers(), conf.getPossiblePublishers(), tcsPublishers);
        setTcsConfig(conf.getExampleResources(), conf.getPossibleExampleResources(), tcsExamples);
        setTcsConfig(conf.getLicenses(), conf.getPossibleLicenses(), tcsLicenses);
        setTcsConfig(conf.getThemes(), conf.getPossibleThemes(), tcsThemes);
        setTcsConfig(conf.getLanguages(), conf.getPossibleLanguages(), tcsLanguages);

        for (String c : conf.getPossibleKeywords()) {
            tcsKeywords.addItem(c);
        }
        tcsKeywords.setRows(conf.getPossibleKeywords().size());
        for (String l : conf.getKeywords()) {
            if (!tcsKeywords.containsId(l)) {
                tcsKeywords.addItem(l);
            }
        }
        tcsKeywords.setValue(conf.getKeywords());

        fillLeftDefault(conf);
    }

    private void setTcsConfig(LinkedList<String> list, LinkedList<String> possibleList,
            TwinColSelect tcs) {
        for (String c : possibleList) {
            tcs.addItem(c.toString());
        }
        tcs.setRows(possibleList.size());

        for (String l : list) {
            if (!tcs.containsId(l.toString())) {
                tcs.addItem(l.toString());
            }
        }

        Collection<String> srcs = new LinkedList<>();
        for (String l : list) {
            srcs.add(l.toString());
        }
        tcs.setValue(srcs);
    }

    private void getTcsConfig(LinkedList<String> list, LinkedList<String> possibleList,
            TwinColSelect tcs) throws MalformedURLException {
        list.clear();
        for (Object u : (Collection<Object>) tcs.getValue()) {
            if (u instanceof URL) {
                list.add(u.toString());
            } else if (u instanceof String) {
                list.add((new URL((String) u)).toString());
            }
        }

        possibleList.clear();
        for (Object u : (Collection<Object>) tcs.getItemIds()) {
            if (u instanceof URL) {
                possibleList.add(u.toString());
            } else if (u instanceof String) {
                possibleList.add((new URL((String) u)).toString());
            }
        }
    }

    @Override
    public MetadataConfig_V1 getConfiguration() throws DPUConfigException {
        MetadataConfig_V1 conf = new MetadataConfig_V1();

        conf.setTitle_cs(tfTitle.getValue());
        conf.setTitle_en(tfTitleEn.getValue());
        conf.setDesc_cs(tfDesc.getValue());
        conf.setDesc_en(tfDescEn.getValue());
        conf.setLicenses(new LinkedList<String>());
        conf.setUseNow((boolean) chkNow.getValue());
        conf.setIsQb((boolean) chkQb.getValue());
        conf.setModified(dfModified.getValue());
        conf.setMime((String) cbMime.getValue());

        try {
            conf.setOutputGraphName((new URL(txtOutSymbolicName.getValue())).toString());
            conf.setComsodeDatasetId(tfComsodeDatasetId.getValue());
            conf.setDatasetURI((new URL(tfDatasetUri.getValue())).toString());
            conf.setDistroURI((new URL(tfDistributionUri.getValue())).toString());
            conf.setDataDump((new URL(tfDataDumpUrl.getValue())).toString());
            conf.setSparqlEndpoint((new URL(tfSparqlEndpointUrl.getValue())).toString());
            conf.setLanguage_cs(tfLanguage.getValue());
            conf.setContactPoint((new URL(tfContactPoint.getValue())).toString());
            conf.setPeriodicity((new URL((String) cbPeriodicity.getValue())).toString());

            getTcsConfig(conf.getAuthors(), conf.getPossibleAuthors(), tcsAuthors);
            getTcsConfig(conf.getPublishers(), conf.getPossiblePublishers(), tcsPublishers);
            getTcsConfig(conf.getLicenses(), conf.getPossibleLicenses(), tcsLicenses);
            getTcsConfig(conf.getExampleResources(), conf.getPossibleExampleResources(), tcsExamples);
            getTcsConfig(conf.getSources(), conf.getPossibleSources(), tcsSources);
            getTcsConfig(conf.getThemes(), conf.getPossibleThemes(), tcsThemes);
            getTcsConfig(conf.getLanguages(), conf.getPossibleLanguages(), tcsLanguages);

            conf.getKeywords().clear();
            conf.getKeywords().addAll((Collection<String>) tcsKeywords.getValue());

            conf.getPossibleKeywords().clear();
            conf.getPossibleKeywords().addAll((Collection<String>) tcsKeywords.getItemIds());

        } catch (MalformedURLException ex) {
            throw new DPUConfigException("Wrong configuration.", ex);
        }

        return conf;
    }

    private void fillLeftDefault(MetadataConfig_V1 conf) {
        // alwayes presented
        fillLeftDefault(Arrays.asList("http://opendatacommons.org/licenses/pddl/1-0/", "http://creativecommons.org/licenses/by/3.0/lu/"),
                tcsLicenses);
        fillLeftDefault(Arrays.asList("http://opendata.cz"),
                tcsPublishers);
        // default
        if (conf.getPossibleSources().isEmpty()) {
            fillLeftDefault(Arrays.asList("http://linked.opendata.cz"),
                    tcsSources);
        }
        if (conf.getPossibleAuthors().isEmpty()) {
            fillLeftDefault(Arrays.asList("http://purl.org/klimek#me", "http://opendata.cz/necasky#me", "http://mynarz.net/#jindrich"),
                    tcsAuthors);
        }
        if (conf.getPossibleThemes().isEmpty()) {
            fillLeftDefault(Arrays.asList("http://dbpedia.org/resource/EHealth"),
                    tcsThemes);
        }
        if (conf.getPossibleLanguages().isEmpty()) {
            fillLeftDefault(Arrays.asList("http://id.loc.gov/vocabulary/iso639-1/cs", "http://id.loc.gov/vocabulary/iso639-1/en", "http://id.loc.gov/vocabulary/iso639-1/it", "http://id.loc.gov/vocabulary/iso639-1/sk"),
                    tcsLanguages);
        }
    }

    /**
     * @param list
     * @param tcs
     */
    private void fillLeftDefault(List<String> list, TwinColSelect tcs) {
        for (String item : list) {
            if (tcs.getItem(item) == null) {
                // not presented -> add
                tcs.addItem(item);
            }
        }
    }

}
