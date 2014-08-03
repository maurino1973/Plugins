package eu.unifiedviews.plugins.transformer.metadata;

import java.util.Date;
import java.util.LinkedList;

public class MetadataConfig {

    private String outputGraphName = "http://localhost/metadata";

    private String datasetURI;

    private String distroURI;

    private String title_cs = "Název datasetu";

    private String title_en = "Dataset title";

    private String desc_cs = "Popis datasetu";

    private String desc_en = "Dataset description";

    private String mime = "application/zip";

    private LinkedList<String> authors = new LinkedList<>();

    private LinkedList<String> possibleAuthors = new LinkedList<>();

    private LinkedList<String> publishers = new LinkedList<>();

    private LinkedList<String> possiblePublishers = new LinkedList<>();

    private LinkedList<String> licenses = new LinkedList<>();

    private LinkedList<String> possibleLicenses = new LinkedList<>();

    private LinkedList<String> sources = new LinkedList<>();

    private LinkedList<String> possibleSources = new LinkedList<>();

    private LinkedList<String> exampleResources = new LinkedList<>();

    private LinkedList<String> possibleExampleResources = new LinkedList<>();

    private LinkedList<String> languages = new LinkedList<>();

    private LinkedList<String> possibleLanguages = new LinkedList<>();

    private LinkedList<String> keywords = new LinkedList<>();

    private LinkedList<String> possibleKeywords = new LinkedList<>();

    private LinkedList<String> themes = new LinkedList<>();

    private LinkedList<String> possibleThemes = new LinkedList<>();

    private String contactPoint;

    private String sparqlEndpoint;

    private String dataDump;

    private String periodicity;

    private boolean useNow = true;

    private boolean isQb = false;

    private Date modified = new Date();

    public MetadataConfig() {
        datasetURI = "http://linked.opendata.cz/resource/dataset/";
        distroURI = "http://linked.opendata.cz/resource/dataset//distribution";
        licenses.add("http://opendatacommons.org/licenses/pddl/1-0/");
        sparqlEndpoint = "http://linked.opendata.cz/sparql";
        dataDump = "http://linked.opendata.cz/dump/";
        contactPoint = "http://opendata.cz/contacts";
        periodicity = "http://purl.org/linked-data/sdmx/2009/code#freq-M";
        modified = new Date();
        possibleSources.add("http://linked.opendata.cz");
        possibleLicenses.add("http://opendatacommons.org/licenses/pddl/1-0/");
        possibleLicenses.add("http://creativecommons.org/licenses/by/3.0/lu/");
        possiblePublishers.add("http://opendata.cz");
        publishers.add("http://opendata.cz");
        possibleAuthors.add("http://purl.org/klimek#me");
        possibleAuthors.add("http://opendata.cz/necasky#me");
        possibleAuthors.add("http://mynarz.net/#jindrich");
        possibleThemes.add("http://dbpedia.org/resource/EHealth");
        possibleLanguages.add("http://id.loc.gov/vocabulary/iso639-1/en");
        possibleLanguages.add("http://id.loc.gov/vocabulary/iso639-1/cs");
        licenses.add("http://opendatacommons.org/licenses/pddl/1-0/");
    }

    public String getOutputGraphName() {
        return outputGraphName;
    }

    public void setOutputGraphName(String outputGraphName) {
        this.outputGraphName = outputGraphName;
    }

    public String getDatasetURI() {
        return datasetURI;
    }

    public void setDatasetURI(String datasetURI) {
        this.datasetURI = datasetURI;
    }

    public String getDistroURI() {
        return distroURI;
    }

    public void setDistroURI(String distroURI) {
        this.distroURI = distroURI;
    }

    public String getTitle_cs() {
        return title_cs;
    }

    public void setTitle_cs(String title_cs) {
        this.title_cs = title_cs;
    }

    public String getTitle_en() {
        return title_en;
    }

    public void setTitle_en(String title_en) {
        this.title_en = title_en;
    }

    public String getDesc_cs() {
        return desc_cs;
    }

    public void setDesc_cs(String desc_cs) {
        this.desc_cs = desc_cs;
    }

    public String getDesc_en() {
        return desc_en;
    }

    public void setDesc_en(String desc_en) {
        this.desc_en = desc_en;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public LinkedList<String> getAuthors() {
        return authors;
    }

    public void setAuthors(LinkedList<String> authors) {
        this.authors = authors;
    }

    public LinkedList<String> getPossibleAuthors() {
        return possibleAuthors;
    }

    public void setPossibleAuthors(
            LinkedList<String> possibleAuthors) {
        this.possibleAuthors = possibleAuthors;
    }

    public LinkedList<String> getPublishers() {
        return publishers;
    }

    public void setPublishers(LinkedList<String> publishers) {
        this.publishers = publishers;
    }

    public LinkedList<String> getPossiblePublishers() {
        return possiblePublishers;
    }

    public void setPossiblePublishers(
            LinkedList<String> possiblePublishers) {
        this.possiblePublishers = possiblePublishers;
    }

    public LinkedList<String> getLicenses() {
        return licenses;
    }

    public void setLicenses(LinkedList<String> licenses) {
        this.licenses = licenses;
    }

    public LinkedList<String> getPossibleLicenses() {
        return possibleLicenses;
    }

    public void setPossibleLicenses(
            LinkedList<String> possibleLicenses) {
        this.possibleLicenses = possibleLicenses;
    }

    public LinkedList<String> getSources() {
        return sources;
    }

    public void setSources(LinkedList<String> sources) {
        this.sources = sources;
    }

    public LinkedList<String> getPossibleSources() {
        return possibleSources;
    }

    public void setPossibleSources(
            LinkedList<String> possibleSources) {
        this.possibleSources = possibleSources;
    }

    public LinkedList<String> getExampleResources() {
        return exampleResources;
    }

    public void setExampleResources(
            LinkedList<String> exampleResources) {
        this.exampleResources = exampleResources;
    }

    public LinkedList<String> getPossibleExampleResources() {
        return possibleExampleResources;
    }

    public void setPossibleExampleResources(
            LinkedList<String> possibleExampleResources) {
        this.possibleExampleResources = possibleExampleResources;
    }

    public LinkedList<String> getLanguages() {
        return languages;
    }

    public void setLanguages(LinkedList<String> languages) {
        this.languages = languages;
    }

    public LinkedList<String> getPossibleLanguages() {
        return possibleLanguages;
    }

    public void setPossibleLanguages(
            LinkedList<String> possibleLanguages) {
        this.possibleLanguages = possibleLanguages;
    }

    public LinkedList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(LinkedList<String> keywords) {
        this.keywords = keywords;
    }

    public LinkedList<String> getPossibleKeywords() {
        return possibleKeywords;
    }

    public void setPossibleKeywords(
            LinkedList<String> possibleKeywords) {
        this.possibleKeywords = possibleKeywords;
    }

    public LinkedList<String> getThemes() {
        return themes;
    }

    public void setThemes(LinkedList<String> themes) {
        this.themes = themes;
    }

    public LinkedList<String> getPossibleThemes() {
        return possibleThemes;
    }

    public void setPossibleThemes(
            LinkedList<String> possibleThemes) {
        this.possibleThemes = possibleThemes;
    }

    public String getSparqlEndpoint() {
        return sparqlEndpoint;
    }

    public void setSparqlEndpoint(String sparqlEndpoint) {
        this.sparqlEndpoint = sparqlEndpoint;
    }

    public String getDataDump() {
        return dataDump;
    }

    public void setDataDump(String dataDump) {
        this.dataDump = dataDump;
    }

    public String getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(String contactPoint) {
        this.contactPoint = contactPoint;
    }

    public String getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(String periodicity) {
        this.periodicity = periodicity;
    }

    public boolean isUseNow() {
        return useNow;
    }

    public void setUseNow(boolean useNow) {
        this.useNow = useNow;
    }

    public boolean isIsQb() {
        return isQb;
    }

    public void setIsQb(boolean isQb) {
        this.isQb = isQb;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

}
