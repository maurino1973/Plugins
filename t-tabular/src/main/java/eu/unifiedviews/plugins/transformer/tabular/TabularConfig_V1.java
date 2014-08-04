package eu.unifiedviews.plugins.transformer.tabular;

import java.util.LinkedHashMap;
import java.util.Map;

public class TabularConfig_V1 {

    private Map<String, String> columnPropertyMap = new LinkedHashMap<>();

    private String baseURI = null;

    private String columnWithURISupplement = null;

    private String encoding = "UTF-8";

    private String quoteChar = "\"";

    private String delimiterChar = ";";

    private String eofSymbols = "\n";

    private Integer rowLimit = 10000;

    /**
     * Values from {@link TableType}.
     */
    private String tableType = TableType.CSV;

    public TabularConfig_V1() {
    }

    public Map<String, String> getColumnPropertyMap() {
        return columnPropertyMap;
    }

    public void setColumnPropertyMap(Map<String, String> columnPropertyMap) {
        this.columnPropertyMap = columnPropertyMap;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public String getColumnWithURISupplement() {
        return columnWithURISupplement;
    }

    public void setColumnWithURISupplement(String columnWithURISupplement) {
        this.columnWithURISupplement = columnWithURISupplement;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getQuoteChar() {
        return quoteChar;
    }

    public void setQuoteChar(String quoteChar) {
        this.quoteChar = quoteChar;
    }

    public String getDelimiterChar() {
        return delimiterChar;
    }

    public void setDelimiterChar(String delimiterChar) {
        this.delimiterChar = delimiterChar;
    }

    public String getEofSymbols() {
        return eofSymbols;
    }

    public void setEofSymbols(String eofSymbols) {
        this.eofSymbols = eofSymbols;
    }

    public Integer getRowLimit() {
        return rowLimit;
    }

    public void setRowLimit(Integer rowLimit) {
        this.rowLimit = rowLimit;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

}
