# T-Tabular #
----------

###General###

|                              |                                                                             |
|------------------------------|-----------------------------------------------------------------------------|
|**Name:**                     |T-Tabular                                                               |
|**Description:**              |Converts tabular data into rdf data. |
|                              |                                                                             |
|**DPU class name:**           |Tabular                                                                 | 
|**Configuration class name:** |TabularConfig_V1                             |
|**Dialogue class name:**      |TabularVaadinDialog                      |

***

###Configuration parameters###

|Parameter                                       |Description                                                              |                                                        
|------------------------------------------------|-------------------------------------------------------------------------|
|**Choose the input type:**                      |Choose the file type of the input type:<BR>- CVS<BR>- DBF                |
|**Resource URI base***                          |Specifies base URI of the resource.                                      |
|**Key column**                                  |TODO: provide description |
|**Encoding***                                   |Specify character encoding of the input file. | 
|**Quote char (for CSV)**         		 |Define a character for quoting (if no character is provided '\"' will be used by default). |
|**Delimiter char (for CSV)**			 |Define value delimiter character (if no character is provided ',' will be used by default). |
|**End of line symbols (for CSV)**	         |Define an end of line symbol (if no symbol is provided '\\n' will be used by default). |
|**Rows limit**              			 |Number of rows to include in transformation. |
|**Use static row counter (checkbox)**           |Row counter is not set to 0 between processing of multiple files. |
|**Add blank cells <http://linked.opendata.cz/ontology/odcs/tabular/blank-cell> (checkbox)** |Creates blank cell URIs in mapping if no mapping is provided. |
|**Column name/Property URI**|Define column to property URI mappings. Create new mapping using 'Add mapping' button below. |

***

### Inputs and outputs ###

|Name         |Type           |DataUnit     |Description             |
|-------------|---------------|-------------|------------------------|
|table        |i              |FilesDataUnit|Input file containing tabular data. |  
|triplifiedTable  |o          |RDFDataUnit  |RDF data. |

***

### Version history ###

|Version          |Release notes               |
|-----------------|----------------------------|
|1.3.2            |N/A                         |                                


***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
|N/A              |N/A                             | 
