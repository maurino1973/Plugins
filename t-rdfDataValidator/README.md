# T-RdfDataValidator #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |T-RdfDataValidator                                             |
|**Description:**              |Validates RDF data and creates validation report. |
|                              |                                                               |
|**DPU class name:**           |RDFDataValidator     | 
|**Configuration class name:** |RDFDataValidatorConfig_V1                           |
|**Dialogue class name:**      |RDFDataValidatorVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**if invalid data find out, pipeline execution fails immediately (checkbox)** |Stop the pipeline execution if invalid data is found. |
|**Add triples to report output only if some data are invalid (checkbox)** |Create report only in case invalid data is found. |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|input |i |RDFDataUnit  |RDF data repository with data to be validated  |
|Validated_Data |o (optional) |RDFDataUnit  |RDF data repository with only validated triples get from input. | 
|Report|o |RDFDataUnit |RDF repository report about invalid data described as RDF triples. | 

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

