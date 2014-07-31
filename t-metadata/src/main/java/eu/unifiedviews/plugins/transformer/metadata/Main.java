package eu.unifiedviews.plugins.transformer.metadata;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.rdfhelper.RDFHelper;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.query.Dataset;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@DPU.AsTransformer
public class Main extends ConfigurableBase<Configuration>
        implements ConfigDialogProvider<Configuration> {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    @DataUnit.AsInput(name = "data")
    public RDFDataUnit inRdfData;

    @DataUnit.AsOutput(name = "metadata")
    public WritableRDFDataUnit outRdfData;

    private DPUContext context;

    private RepositoryConnection inConnection;

    private RepositoryConnection outConnection;

    public Main() {
        super(Configuration.class);
    }

    @Override
    public AbstractConfigDialog<Configuration> getConfigurationDialog() {
        return new Dialog();
    }

    @Override
    public void execute(DPUContext context) throws DPUException {
        this.context = context;

        Date date = new Date();
        long start = date.getTime();

        try {
            // create wraps
            inConnection = inRdfData.getConnection();
            outConnection = outRdfData.getConnection();
            // generate metadata
            generateMetadata();
        } catch (DataUnitException | RepositoryException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "DPU Failed", "", ex);
        } finally {
            if (inConnection != null) {
                try {
                    inConnection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error in close.", ex);
                }
            }
            if (outConnection != null) {
                try {
                    outConnection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error in close.", ex);
                }
            }
        }

        Date date2 = new Date();
        long end = date2.getTime();
        context.sendMessage(DPUContext.MessageType.INFO,
                "Done in " + (end - start) + "ms");
    }

    private void generateMetadata() throws DataUnitException, RepositoryException {

        //Void dataset and DCAT dataset
        String ns_dcat = "http://www.w3.org/ns/dcat#";
        String ns_foaf = "http://xmlns.com/foaf/0.1/";
        String ns_void = "http://rdfs.org/ns/void#";
        String ns_qb = "http://purl.org/linked-data/cube#";

        final ValueFactory valueFactory;
        valueFactory = outConnection.getValueFactory();

        URI foaf_agent = valueFactory.createURI(ns_foaf + "Agent");
        URI qb_DataSet = valueFactory.createURI(ns_qb + "DataSet");
        URI dcat_keyword = valueFactory.createURI(ns_dcat + "keyword");
        URI dcat_distribution = valueFactory.createURI(ns_dcat + "distribution");
        URI dcat_downloadURL = valueFactory.createURI(ns_dcat + "downloadURL");
        URI dcat_mediaType = valueFactory.createURI(ns_dcat + "mediaType");
        URI dcat_theme = valueFactory.createURI(ns_dcat + "theme");
        URI xsd_date = valueFactory.createURI(
                "http://www.w3.org/2001/XMLSchema#date");
        URI xsd_integer = valueFactory.createURI(
                "http://www.w3.org/2001/XMLSchema#integer");
        URI dcat_distroClass = valueFactory.createURI(ns_dcat + "Distribution");
        URI dcat_datasetClass = valueFactory.createURI(ns_dcat + "Dataset");
        URI void_datasetClass = valueFactory.createURI(ns_void + "Dataset");
        URI void_triples = valueFactory.createURI(ns_void + "triples");
        URI void_entities = valueFactory.createURI(ns_void + "entities");
        URI void_classes = valueFactory.createURI(ns_void + "classes");
        URI void_properties = valueFactory.createURI(ns_void + "properties");
        URI void_dSubjects = valueFactory
                .createURI(ns_void + "distinctSubjects");
        URI void_dObjects = valueFactory.createURI(ns_void + "distinctObjects");

        URI datasetURI = valueFactory.createURI(config.getDatasetURI()
                .toString());
        URI distroURI = valueFactory.createURI(config.getDistroURI().toString());
        URI exResURI = valueFactory.createURI(ns_void + "exampleResource");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        outConnection.begin();

        outConnection.add(datasetURI, RDF.TYPE, void_datasetClass);
        outConnection.add(datasetURI, RDF.TYPE, dcat_datasetClass);
        if (config.isIsQb()) {
            outConnection.add(datasetURI, RDF.TYPE, qb_DataSet);
        }
        if (config.getDesc_cs() != null) {
            outConnection.add(datasetURI, DCTERMS.DESCRIPTION, valueFactory
                    .createLiteral(config.getDesc_cs(), "cs"));
        }
        if (config.getDesc_en() != null) {
            outConnection.add(datasetURI, DCTERMS.DESCRIPTION, valueFactory
                    .createLiteral(config.getDesc_en(), "en"));
        }
        if (config.getTitle_cs() != null) {
            outConnection.add(datasetURI, DCTERMS.TITLE, valueFactory
                    .createLiteral(
                            config.getTitle_cs(), "cs"));
        }
        if (config.getTitle_en() != null) {
            outConnection.add(datasetURI, DCTERMS.TITLE, valueFactory
                    .createLiteral(
                            config.getTitle_en(), "en"));
        }
        if (config.getDataDump() != null) {
            outConnection
                    .add(datasetURI, valueFactory
                            .createURI(ns_void + "dataDump"), valueFactory
                            .createURI(config.getDataDump().toString()));
        }
        if (config.getSparqlEndpoint() != null) {
            outConnection.add(datasetURI, valueFactory.createURI(
                    ns_void + "sparqlEndpoint"), valueFactory.createURI(config
                    .getSparqlEndpoint().toString()));
        }

        for (URL u : config.getAuthors()) {
            outConnection.add(datasetURI, DCTERMS.CREATOR, valueFactory
                    .createURI(u
                            .toString()));
        }
        for (URL u : config.getPublishers()) {
            URI publisherURI = valueFactory.createURI(u.toString());
            outConnection.add(datasetURI, DCTERMS.PUBLISHER, publisherURI);
            outConnection.add(publisherURI, RDF.TYPE, foaf_agent);
            //TODO: more publisher data?
        }
        for (URL u : config.getLicenses()) {
            outConnection.add(datasetURI, DCTERMS.LICENSE, valueFactory
                    .createURI(u
                            .toString()));
        }
        for (URL u : config.getExampleResources()) {
            outConnection.add(datasetURI, exResURI, valueFactory.createURI(u
                    .toString()));
        }
        for (URL u : config.getSources()) {
            outConnection.add(datasetURI, DCTERMS.SOURCE, valueFactory
                    .createURI(u
                            .toString()));
        }
        for (String u : config.getKeywords()) {
            outConnection.add(datasetURI, dcat_keyword, valueFactory
                    .createLiteral(u
                            .toString()));
        }
        for (URL u : config.getLanguages()) {
            outConnection.add(datasetURI, DCTERMS.LANGUAGE, valueFactory
                    .createURI(u
                            .toString()));
        }
        for (URL u : config.getThemes()) {
            URI themeURI = valueFactory.createURI(u.toString());
            outConnection.add(datasetURI, dcat_theme, themeURI);
            outConnection.add(themeURI, RDF.TYPE, SKOS.CONCEPT);
            outConnection.add(themeURI, SKOS.IN_SCHEME, valueFactory.createURI(
                    "http://linked.opendata.cz/resource/catalog/Themes"));
        }

        if (config.isUseNow()) {
            outConnection.add(datasetURI, DCTERMS.MODIFIED, valueFactory
                    .createLiteral(df.format(new Date()), xsd_date));
        } else {
            outConnection.add(datasetURI, DCTERMS.MODIFIED, valueFactory
                    .createLiteral(df.format(config.getModified()), xsd_date));
        }

        outConnection.add(datasetURI, dcat_distribution, distroURI);
        outConnection.commit();

        // DCAT Distribution
        outConnection.begin();
        outConnection.add(distroURI, RDF.TYPE, dcat_distroClass);
        if (config.getDesc_cs() != null) {
            outConnection.add(distroURI, DCTERMS.DESCRIPTION, valueFactory
                    .createLiteral(config.getDesc_cs(), "cs"));
        }
        if (config.getDesc_en() != null) {
            outConnection.add(distroURI, DCTERMS.DESCRIPTION, valueFactory
                    .createLiteral(config.getDesc_en(), "en"));
        }
        if (config.getTitle_cs() != null) {
            outConnection.add(distroURI, DCTERMS.TITLE, valueFactory
                    .createLiteral(
                            config.getTitle_cs(), "cs"));
        }
        if (config.getTitle_en() != null) {
            outConnection.add(distroURI, DCTERMS.TITLE, valueFactory
                    .createLiteral(
                            config.getTitle_en(), "en"));
        }
        if (config.getDataDump() != null) {
            outConnection.add(distroURI, dcat_downloadURL, valueFactory
                    .createURI(
                    config.getDataDump().toString()));
        }
        if (config.getDataDump() != null) {
            outConnection.add(distroURI, dcat_mediaType, valueFactory
                    .createLiteral(
                    config.getMime()));
        }
        for (URL u : config.getLicenses()) {
            outConnection.add(distroURI, DCTERMS.LICENSE, valueFactory
                    .createURI(u
                            .toString()));
        }

        if (config.isUseNow()) {
            outConnection.add(distroURI, DCTERMS.MODIFIED, valueFactory
                    .createLiteral(
                            df.format(new Date()), xsd_date));
        } else {
            outConnection.add(distroURI, DCTERMS.MODIFIED, valueFactory
                    .createLiteral(
                            df.format(config.getModified()), xsd_date));
        }
        outConnection.commit();

        // Now compute statistics on input data
        context.sendMessage(DPUContext.MessageType.INFO,
                "Starting statistics computation");

        final DatasetImpl dataset = new DatasetImpl();
        for (URI uri : RDFHelper.getGraphsURIArray(inRdfData)) {
            dataset.addDefaultGraph(uri);
        }

        executeCountQuery("SELECT (COUNT (*) as ?count) WHERE {?s ?p ?o}",
                void_triples, datasetURI, dataset);
        executeCountQuery(
                "SELECT (COUNT (distinct ?s) as ?count) WHERE {?s a ?t}",
                void_entities, datasetURI, dataset);
        executeCountQuery(
                "SELECT (COUNT (distinct ?t) as ?count) WHERE {?s a ?t}",
                void_classes, datasetURI, dataset);
        executeCountQuery(
                "SELECT (COUNT (distinct ?p) as ?count) WHERE {?s ?p ?o}",
                void_properties, datasetURI, dataset);
        executeCountQuery(
                "SELECT (COUNT (distinct ?s) as ?count) WHERE {?s ?p ?o}",
                void_dSubjects, datasetURI, dataset);
        executeCountQuery(
                "SELECT (COUNT (distinct ?o) as ?count) WHERE {?s ?p ?o}",
                void_dObjects, datasetURI, dataset);
        context.sendMessage(DPUContext.MessageType.INFO,
                "Statistics computation done");
        // Done computing statistics
    }

    void executeCountQuery(String countQuery, URI property, URI datasetURI,
            Dataset dataset) {
        final ValueFactory valueFactory = inConnection.getValueFactory();
        URI xsd_integer = valueFactory.createURI(
                "http://www.w3.org/2001/XMLSchema#integer");
        try {
            TupleQuery query = inConnection.prepareTupleQuery(
                    QueryLanguage.SPARQL, countQuery);
            query.setDataset(dataset);
            TupleQueryResult res = query.evaluate();

            int number = Integer.parseInt(res.next().getValue("count")
                    .stringValue());
            outConnection.add(datasetURI, property, valueFactory.createLiteral(
                    Integer
                            .toString(number), xsd_integer));
        } catch (MalformedQueryException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "Wrong query format", "", ex);
        } catch (NumberFormatException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "Query result is not a number", "", ex);
        } catch (QueryEvaluationException | RepositoryException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "Query failed", "", ex);
        }
    }

}
