# L-FilesToSparql #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |L-FilesToSparql                                              |
|**Description:**              |Loads RDF data stored in Files to the specified remote SPARQL endpoint. |
|                              |                                                               |
|**DPU class name:**           |FilesToSparqlEndpoint     | 
|**Configuration class name:** |FilesToSparqlEndpointConfig_V1                           |
|**Dialogue class name:**      |FilesToSparqlEndpointVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Query endpoint URL:** |TODO: provide description  |
|**Update endpoint URL:** |TODO: provide description |
|**Commit size (0 = one file, one transaction, 1 = autocommit connection, n = commit every n triples):** |self-descriptive |
|**Skip file on error (checkbox)**|Do not stop the pipeline execution if error occurs. |
|**Target contexts:** |TODO: provide description  |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|filesInput |i |FilesDataUnit  |File containing RDF data.  |

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

