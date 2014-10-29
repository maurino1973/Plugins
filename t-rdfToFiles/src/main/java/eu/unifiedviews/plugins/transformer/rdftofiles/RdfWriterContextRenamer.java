package eu.unifiedviews.plugins.transformer.rdftofiles;

import java.util.Collection;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.*;

/**
 * @author Škoda Petr
 */
public class RdfWriterContextRenamer implements RDFWriter {

    /**
     * Wrap for single statement used to change context.
     */
    protected class StatementWrap implements Statement {

        protected Statement statement;

        @Override
        public Resource getSubject() {
            return statement.getSubject();
        }

        @Override
        public URI getPredicate() {
            return statement.getPredicate();
        }

        @Override
        public Value getObject() {
            return statement.getObject();
        }

        @Override
        public Resource getContext() {
            // TODO translate original context based on configuration
            return context;
        }

    }

    /**
     * Underlying rdf writer.
     */
    private final RDFWriter writer;

    /**
     * Context used for graphs.
     */
    private Resource context;

    /**
     * Wrap used to change context in statements.
     */
    private final StatementWrap statementWrap = new StatementWrap();

    public RdfWriterContextRenamer(RDFWriter writer) {
        this.writer = writer;
    }

    /**
     * Set context (graph URI) used in output for all statements.
     * 
     * @param context
     */
    public void setContext(Resource context) {
        this.context = context;
    }

    @Override
    public RDFFormat getRDFFormat() {
        return writer.getRDFFormat();
    }

    @Override
    public void setWriterConfig(WriterConfig wc) {
        writer.setWriterConfig(wc);
    }

    @Override
    public WriterConfig getWriterConfig() {
        return writer.getWriterConfig();
    }

    @Override
    public Collection<RioSetting<?>> getSupportedSettings() {
        return writer.getSupportedSettings();
    }

    @Override
    public void startRDF() throws RDFHandlerException {
        writer.startRDF();
    }

    @Override
    public void endRDF() throws RDFHandlerException {
        writer.endRDF();
    }

    @Override
    public void handleNamespace(String string, String string1) throws RDFHandlerException {
        writer.handleNamespace(string, string1);
    }

    @Override
    public void handleStatement(Statement stmnt) throws RDFHandlerException {
        // replace context = use our statement wrap
        statementWrap.statement = stmnt;
        // call original function
        writer.handleStatement(statementWrap);
    }

    @Override
    public void handleComment(String string) throws RDFHandlerException {
        writer.handleComment(string);
    }

}
