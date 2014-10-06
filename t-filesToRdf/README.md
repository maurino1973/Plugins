# T-FilesToRdf #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |T-FilesToRdf                                              |
|**Description:**              |Extracts RDF data from Files (any file format) and adds them to RDF. |
|                              |                                                               |
|**DPU class name:**           |FilesToRDF     | 
|**Configuration class name:** |FilesToRDFConfig_V1                           |
|**Dialogue class name:**      |FilesToRDFVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Commit size (0 = one file, one transaction, 1 = autocommit connection, n = commit every n triples)** |TODO: provide description  |
|**Symbolic name to baseURI and Format map. Line format: symbolicName;baseURI(optional);FileFormat(optional)** |TODO: provide description |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|filesInput |i |FilesDataUnit  |TODO: provide description  |
|rdfOutput|o |RDFDataUnit  |TODO: provide description | 

***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
|1.3.2              |N/A                                             |                                


***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   | 

