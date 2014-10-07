# E-RdfFromSparqlEndpoint #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |E-RdfFromSparqlEndpoint                                             |
|**Description:**              |Extracts RDF data from SPARQL Endpoint.                                            |
|                              |                                                               |
|**DPU class name:**           |RdfFromSparqlEndpoint     | 
|**Configuration class name:** |RdfFromSparqlEndpointConfig_V1                           |
|**Dialogue class name:**      |RdfFromSparqlEndpointVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Core:**| | 
|**SPARQL endpoint** |SPARQL endpoint URL |
|**Name** |Username to connect to SPARQL enpoints (if authorization is required). |
|**Password** |Password for the username (if authorization is required). |
|**Default Graph** |Default graph parameter needed for HTTP request for extract from SPARQL endpoint. |
|**Named Graph** |Named graph parameter needed for HTTP request for extract from SPARQL endpoint. |
|**SPARQL Construct** |SPARQL CONSTRUCT query containing extraction criteria. |
|**Output graph symbolic name** |Symbolic name of the output graph. |
| | |
|**SPARQL protocol:**| |
|**HTTP REQUEST Variant**|Type of REQUEST variant for extracting RDF data from SPARQL endpoint.|
|**Query param**|Query parameter needed for HTTP request for extract from SPARQL endpoint. |
|**Default graph param**|Default graph URI. |
|**Named graph param**|Named graph URI. |
| | |
|**Details:**| |
|**Split the SPARQL construct query to subqueries (checkbox)**|Defines if construct query could be split in more queries or not. |
|**Every subquery contains at maximum (triples)**|Fill number - maximum of RDF triples at once. |
|**Extraction fails if there is no triple extracted. (checkbox)**|Specify whether the extraction fails if there is no triple. |
|**Use statistical and error handler (checkbox)**| If checked, two additional options are available: <BR>- Extract only triples with no errors. <BR>- Stop pipeline execution if extractor extracted some triples with an error. |
|**Count of attempts to reconnect if the connection to SPARQL fails**|Use 0 for no repeat, negative integer for infinity. |
|**Time in milliseconds how long to wait before trying to reconnect**|Time between reconnect attempts. |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|output |o |RDFDataUnit |RDF data as a result of SPARQL query. |


***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
|1.3.1              |N/A                                             |                                


***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   | 

