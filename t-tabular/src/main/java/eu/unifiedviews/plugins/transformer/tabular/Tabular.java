package eu.unifiedviews.plugins.transformer.tabular;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jamel.dbf.DbfReader;
import org.jamel.dbf.structure.DbfField;
import org.jamel.dbf.structure.DbfHeader;
import org.jamel.dbf.utils.DbfUtils;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.fileshelper.FilesHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

/**
 * @author Škoda Petr
 */
@DPU.AsTransformer
public class Tabular extends ConfigurableBase<TabularConfig_V1>
        implements ConfigDialogProvider<TabularConfig_V1> {

    private static final int COMMIT_SIZE = 50000;

    private static final Logger LOG = LoggerFactory.getLogger(Tabular.class);

    private final String baseODCSPropertyURI = "http://linked.opendata.cz/ontology/odcs/tabular/";

    @DataUnit.AsInput(name = "table")
    public FilesDataUnit inFilesTable;

    @DataUnit.AsOutput(name = "triplifiedTable")
    public WritableRDFDataUnit outRdfTriplifiedTable;

    private RepositoryConnection outConnection;

    private URI currentGraphURI;

    private final List<Statement> buffer = new ArrayList<>(COMMIT_SIZE);

    private ValueFactory valueFactory;

    private int rowNumber = 0;

    private boolean transactionOpen = false;

    private int statementCounter = 0;

    private long realStatementCounter = 0L;

    public Tabular() {
        super(TabularConfig_V1.class);
    }

    private void add(Resource rsrc, URI uri, Value value) throws RepositoryException {
        if (!transactionOpen) {
            outConnection.begin();
            transactionOpen = true;
        }
        outConnection.add(valueFactory.createStatement(rsrc, uri, value), currentGraphURI);
        statementCounter++;
        if (transactionOpen && statementCounter > COMMIT_SIZE) {
            outConnection.commit();
            if (LOG.isDebugEnabled()) {
                realStatementCounter += statementCounter;
                LOG.debug("Commit {}", realStatementCounter);
            }
            statementCounter = 0;
            transactionOpen = false;
        }
    }

    @Override
    public AbstractConfigDialog<TabularConfig_V1> getConfigurationDialog() {
        return new TabularVaadinDialog();
    }

    @Override
    public void execute(DPUContext context) throws DPUException {
        //
        // Get file iterator
        //
        final Iterator<FilesDataUnit.Entry> filesIteration;
        try {
            filesIteration = FilesHelper.getFiles(inFilesTable).iterator();
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed", "Can't get file iterator.", ex);
            return;
        }
        //
        // Get connection
        //
        try {
            outConnection = outRdfTriplifiedTable.getConnection();
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "DataUnit problem", "Can't get connection.", ex);
            return;
        }
        valueFactory = outConnection.getValueFactory();
        //
        // Iterate over files
        //
        try {
            while (!context.canceled() && filesIteration.hasNext()) {
                final FilesDataUnit.Entry entry = filesIteration.next();

                String virtualPath = VirtualPathHelpers.getVirtualPath(inFilesTable, entry.getSymbolicName());

                // TODO We can try to use symbolicName here
                if (virtualPath == null) {
                    context.sendMessage(DPUContext.MessageType.WARNING, "No virtual path set for: " + entry.getSymbolicName() + ". File is ignored.");
                    continue;
                }

                final File sourceFile = new File(java.net.URI.create(entry.getFileURIString()));

                currentGraphURI = outRdfTriplifiedTable.addNewDataGraph(entry.getSymbolicName());

                // TODO Add support for multiple graphs
                proceedFile(context, sourceFile);
                // store buffer

                //
                // Add metadata
                //
            }
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, "Problem with DataUnit", "", ex);
        } catch (RepositoryException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, "Problem with repository", "", ex);
        }

        try {
            if (outConnection != null) {
                outConnection.close();
            }
        } catch (RepositoryException ex) {
            LOG.warn("Error in close.", ex);
        }

    }

    // TODO Rework this method !!
    private void proceedFile(DPUContext context, File tableFile)
            throws RepositoryException {
        Boolean staticRowCounter = config.isStaticRowCounter();
        if (staticRowCounter == null || staticRowCounter == false) {
            // null = false
            rowNumber = 0;
        } else {
            // it's true, so we do not reset
        }

        String tableFileName = tableFile.getName();

        Map<String, String> columnPropertyMap = this.config.getColumnPropertyMap();
        if (columnPropertyMap == null) {
            LOG.warn("No mapping of table columns to RDF properties have been specified.");
            columnPropertyMap = new HashMap<>();
        }
        String baseURI = this.config.getBaseURI();
        if (baseURI == null || "".equals(baseURI)) {
            LOG.info("No base for URIs of resources extracted from rows of the table has been specified. Default base will be applied (http://linked.opendata.cz/resource/odcs/tabular/" + tableFileName + "/row/)");
            baseURI = "http://linked.opendata.cz/resource/odcs/tabular/" + tableFileName + "/row/";
        }
        String columnWithURISupplement = this.config.getColumnWithURISupplement();
        if (columnWithURISupplement == null || "".equals(columnWithURISupplement)) {
            LOG.info("No column with values supplementing the base for URIs of resources extracted from rows of the table has been specified. Row number (starting at 0) will be used instead.");
            columnWithURISupplement = null;
        }

        URI propertyRow = valueFactory.createURI(baseODCSPropertyURI + "row");
        if (TableType.CSV.equals(this.config.getTableType())) {

            String quoteChar = this.config.getQuoteChar();
            String delimiterChar = this.config.getDelimiterChar();
            String eofSymbols = this.config.getEofSymbols();

            if (quoteChar == null || "".equals(quoteChar)) {
                quoteChar = "\"";
                LOG.info("No quote char supplied. Default quote char '\"' will be used.");
            }

            if (delimiterChar == null || "".equals(delimiterChar)) {
                delimiterChar = "\"";
                LOG.info("No delimiter char supplied. Default delimiter char ',' will be used.");
            }

            if (eofSymbols == null || "".equals(eofSymbols)) {
                eofSymbols = "\n";
                LOG.info("No end of line symbols supplied. Default end of line symbols '\\n' will be used.");
            }

            final CsvPreference CSV_PREFERENCE = new CsvPreference.Builder(quoteChar.charAt(0), delimiterChar.charAt(0), eofSymbols).build();

            ICsvListReader listReader = null;
            try {

                listReader = new CsvListReader(new BufferedReader(new InputStreamReader(new FileInputStream(tableFile), config.getEncoding())), CSV_PREFERENCE);

                final String[] header = listReader.getHeader(true);
                int columnWithURISupplementNumber = -1;
                URI[] propertyMap = new URI[header.length];
                for (int i = 0; i < header.length; i++) {
                    String fieldName = header[i];
                    if (columnWithURISupplement != null && columnWithURISupplement.equals(fieldName)) {
                        columnWithURISupplementNumber = i;
                    }
                    if (columnPropertyMap.containsKey(fieldName)) {
                        propertyMap[i] = valueFactory.createURI(
                                columnPropertyMap.get(fieldName));
                    } else {
                        fieldName = this.convertStringToURIPart(fieldName);
                        propertyMap[i] = valueFactory.createURI(baseODCSPropertyURI + fieldName);
                    }
                }

                List<String> row = listReader.read();

                while (row != null) {

                    if (config.getRowLimit() > 0) {
                        if (rowNumber >= config.getRowLimit()) {
                            break;
                        }
                    }

                    String suffixURI;
                    if (columnWithURISupplementNumber >= 0) {
                        suffixURI = this.convertStringToURIPart(row.get(columnWithURISupplementNumber));
                    } else {
                        suffixURI = new Integer(rowNumber).toString();
                    }

                    Resource subj = valueFactory.createURI(baseURI + suffixURI);

                    int i = 0;
                    for (String strValue : row) {
                        if (strValue == null || "".equals(strValue)) {
                            if (config.isAddBlankCells()) {
                                URI obj = valueFactory.createURI("http://linked.opendata.cz/ontology/odcs/tabular/blank-cell");
                                add(subj, propertyMap[i], obj);
                            }
                        } else {
                            Value obj = valueFactory.createLiteral(strValue);
                            add(subj, propertyMap[i], obj);
                        }
                        i++;
                    }

                    Value rowvalue = valueFactory.createLiteral(String.valueOf(rowNumber));
                    add(subj, propertyRow, rowvalue);

                    if (rowNumber % 1000 == 0) {
                        LOG.debug("Row number {} processed.", rowNumber);
                    }

                    rowNumber++;
                    row = listReader.read();

                    if (context.canceled()) {
                        LOG.info("DPU cancelled");
                        listReader.close();
                        return;
                    }
                }
            } catch (IOException ex) {
                context.sendMessage(DPUContext.MessageType.ERROR, "DPU failed", "IO exception during processing the input CSV file.", ex);
            } finally {
                if (listReader != null) {
                    try {
                        listReader.close();
                    } catch (IOException ex) {
                        context.sendMessage(DPUContext.MessageType.ERROR, "DPU failed", "IO exception when closing the reader of the input CSV file.", ex);
                    }
                }

            }

        } else if (TableType.DBF.equals(this.config.getTableType())) {

            String encoding = this.config.getEncoding();
            if (encoding == null || "".equals(encoding)) {
                DbfReaderLanguageDriver languageDriverReader = new DbfReaderLanguageDriver(tableFile);
                DbfHeaderLanguageDriver languageDriverHeader = languageDriverReader.getHeader();
                languageDriverHeader.getLanguageDriver();
                languageDriverReader.close();

                //TODO Make proper mapping of DBF encoding codes to Java codes. Until this is repaired, we set UTF-8. We suppose that DPUs have set the encoding explicitly by the user.
                encoding = "UTF-8";
            }
            if (!Charset.isSupported(encoding)) {
                context.sendMessage(DPUContext.MessageType.ERROR, "Charset " + encoding + " is not supported.");
                return;
            }

            DbfReader reader = new DbfReader(tableFile);
            DbfHeader header = reader.getHeader();

            int columnWithURISupplementNumber = -1;
            URI[] propertyMap = new URI[header.getFieldsCount()];
            for (int i = 0; i < header.getFieldsCount(); i++) {
                DbfField field = header.getField(i);
                String fieldName = field.getName();

                LOG.debug("Filed: {} type: {} len: {}", field.getName(), field.getDataType(), field.getFieldLength());

                if (columnWithURISupplement != null && columnWithURISupplement.equals(fieldName)) {
                    columnWithURISupplementNumber = i;
                }
                if (columnPropertyMap.containsKey(fieldName)) {
                    propertyMap[i] = valueFactory.createURI(columnPropertyMap.get(fieldName));
                } else {
                    fieldName = this.convertStringToURIPart(fieldName);
                    propertyMap[i] = valueFactory.createURI(baseODCSPropertyURI + fieldName);
                }
            }

            Object[] row = null;

            while ((row = reader.nextRecord()) != null) {

                if (config.getRowLimit() > 0) {
                    if (rowNumber >= config.getRowLimit()) {
                        break;
                    }
                }

                String suffixURI;
                if (columnWithURISupplementNumber >= 0) {
                    suffixURI = this.convertStringToURIPart(this.getCellValue(row[columnWithURISupplementNumber], encoding));
                } else {
                    suffixURI = new Integer(rowNumber).toString();
                }

                Resource subj = valueFactory.createURI(baseURI + suffixURI);

                for (int i = 0; i < row.length; i++) {
                    String strValue = this.getCellValue(row[i], encoding);
                    if (strValue == null || "".equals(strValue)) {
                        if (config.isAddBlankCells()) {
                            URI obj = valueFactory.createURI("http://linked.opendata.cz/ontology/odcs/tabular/blank-cell");
                            add(subj, propertyMap[i], obj);
                        }
                    } else {
                        Value obj = valueFactory.createLiteral(this.getCellValue(row[i], encoding));
                        add(subj, propertyMap[i], obj);
                    }
                }

                Value rowvalue = valueFactory.createLiteral(this.getCellValue(rowNumber, encoding));
                add(subj, propertyRow, rowvalue);

                if (rowNumber % 1000 == 0) {
                    LOG.debug("Row number {} processed.", rowNumber);
                }
                rowNumber++;
                if (context.canceled()) {
                    LOG.info("DPU cancelled");
                    reader.close();
                    return;
                }
            }
            reader.close();
        }
        outConnection.commit();
    }

    private String getCellValue(Object cell, String encoding) {
        if (cell instanceof Date) {
            return ((Date) cell).toString();
        } else if (cell instanceof Float) {
            return ((Float) cell).toString();
        } else if (cell instanceof Boolean) {
            return ((Boolean) cell).toString();
        } else if (cell instanceof Number) {
            return ((Number) cell).toString();
        } else {
            try {
                return new String(DbfUtils.trimLeftSpaces((byte[]) cell),
                        encoding);
            } catch (UnsupportedEncodingException ex) {
                //	ignored, solved earlier when reading encoding of the file
                return "";
            }
        }
    }

    private String convertStringToURIPart(String part) {
        return part.replaceAll("\\s+", "-").replaceAll("[^a-zA-Z0-9-_]", "");
    }

}
