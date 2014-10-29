# E-RdfDataGenerator #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |E-RdfDataGenerator                                             |
|**Description:**              |Generates specified number of unique triples to RDF data. Usualy used for testing purposes.                                            |
|                              |                                                               |
|**DPU class name:**           |RdfDataGenerator     | 
|**Configuration class name:** |RdfDataGeneratorConfig_V1                           |
|**Dialogue class name:**      |RdfDataGeneratorVaadinDialog | 

***

###Configuration parameters###

|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Generate this count of triples** |Number of triples to generate. |
|**Commit transaction every this triples** |Number of triples after which the transaction needs to be commited. |
|**Output graph symbolic name** |Symbolic name of the output graph. |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|output |o |RDFDataUnit |RDF data containing unique triples. |


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

