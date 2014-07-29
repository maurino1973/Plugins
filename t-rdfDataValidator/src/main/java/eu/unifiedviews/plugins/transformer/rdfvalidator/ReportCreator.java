package eu.unifiedviews.plugins.transformer.rdfvalidator;

import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPUException;

/**
 * Class responsible for creting RDF report message from given found out
 * problems and save it to report TTL file.
 * 
 * @author Jiri Tomes
 */
public class ReportCreator {

    private static final Logger LOG = LoggerFactory.getLogger(
            ReportCreator.class);

    private static final String ODCS_VAL = "http://linked.opendata.cz/ontology/odcs/validation/";

    private static final String EXEC_ERROR = "http://linked.opendata.cz/resource/odcs/internal/pipeline/validation/error/";

    private List<TripleProblem> problems;

    private String reportPrefix;

    public ReportCreator(List<TripleProblem> problems, String graphName) {
        this.problems = problems;
        this.reportPrefix = getReportPrefix(graphName);

    }

    /**
     * Make RDF report and add this report to the given repository.
     * 
     * @param repository
     *            Repository where report will be added.
     * @throws RDFException
     *             if some problem during making report.
     */
    public void makeOutputReport(WritableRDFDataUnit repository) throws DPUException {

        setNamespaces(repository);
        addReports(repository);
    }

    private String getReportPrefix(String graphName) {

        String prefix;

        int index = graphName.indexOf("/du/");

        if (index > -1) {
            prefix = graphName.substring(0, index);
        } else {
            prefix = graphName;
        }

        return prefix + "/validation/error/";
    }

    private void addReports(WritableRDFDataUnit repository) throws DPUException {

        int count = 0;

        for (TripleProblem next : problems) {

            Statement st = next.getStatement();

            String sub = st.getSubject().toString();
            String pred = st.getPredicate().toString();
            String obj = st.getObject().toString();

            ParsingConfictType conflictType = next.getConflictType();
            String message = next.getMessage();
            int line = next.getLine();

            count++;

            RepositoryConnection connection = null;
            try {
                connection = repository.getConnection();
                connection.add(getSubject(count), new URIImpl("rdf:type"),
                        new URIImpl(ODCS_VAL + conflictType.toString()), repository.getBaseDataGraphURI());
                connection.add(getSubject(count), getPredicate("subject"),
                        getObject(sub), repository.getBaseDataGraphURI());
                connection.add(getSubject(count), getPredicate("predicate"),
                        getObject(pred), repository.getBaseDataGraphURI());
                connection.add(getSubject(count), getPredicate("object"),
                        getObject(obj), repository.getBaseDataGraphURI());
                connection.add(getSubject(count), getPredicate("reason"),
                        getObject(message), repository.getBaseDataGraphURI());
                connection.add(getSubject(count), getPredicate("sourceLine"),
                        getObject(line), repository.getBaseDataGraphURI());
            } catch (RepositoryException e) {
                LOG.error("Error", e);
            } catch (DataUnitException e) {
                throw new DPUException(e);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (RepositoryException ex) {
                        LOG.warn("Error when closing connection", ex);
                        // eat close exception, we cannot do anything clever here
                    }
                }
            }

        }
    }

    private void setNamespaces(WritableRDFDataUnit repository) throws DPUException {
        RepositoryConnection connection = null;
        try {

            connection = repository.getConnection();

            connection.setNamespace("rdf",
                    "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
            connection.setNamespace("xsd",
                    "http://www.w3.org/2001/XMLSchema#");
            connection.setNamespace("odcs-val", ODCS_VAL);
            connection.setNamespace("exec-error", EXEC_ERROR);

        } catch (RepositoryException | DataUnitException e) {
            final String message = "Not possible to set namespace"
                    + e.getMessage();
            LOG.debug(message);
            throw new DPUException(message);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error when closing connection", ex);
                    // eat close exception, we cannot do anything clever here
                }
            }
        }
    }

    private Resource getSubject(int count) {
        return new URIImpl(reportPrefix + String.valueOf(count));
    }

    private URI getPredicate(String text) {
        return new URIImpl(ODCS_VAL + text);
    }

    private Value getObject(String text) {
        return new LiteralImpl(text);
    }

    private Value getObject(int number) {
        URI datatype = new URIImpl("xsd:integer");
        return new LiteralImpl(String.valueOf(number), datatype);
    }
}
