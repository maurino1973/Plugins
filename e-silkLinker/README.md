# E-SilkLinker #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |E-SilkLinker                                             |
|**Description:**              |Creates links between RDF resources based on the Silk Link Specification Language (LSL), https://www.assembla.com/spaces/silk/wiki/Link_Specification_Language. The script may be uploaded/adjusted in the DPU configuration. Output section of such script is always ignored, output is written to two output data units of the DPU - "links_confirmed", "links_to_be_verified". DPU configuration may also specify thresholds for the two outputs created. Uses 2.5.3 version of Silk. Not supporting cancelation of DPU. |
|                              |                                                               |
|**DPU class name:**           |SilkLinker     | 
|**Configuration class name:** |SilkLinkerConfig_V1                           |
|**Dialogue class name:**      |SilkLinkerVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Silk configuration file (button)** |Choose and upload the Silk configuration file. |
|**Text area** |displays the content of uploaded configuration file. |
|**Minimum score for links considered as 'confirmed links' (0.0 - 1.0):** |Minimum score for the links to be considered as "confirmed". |
|**Minimum score for links considered as 'to be verified links' (0.0 - 1.0):** |Minimum score for the links to be considered as "to be verified". |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|links_confirmed |o |RDFDataUnit |TODO: provide description |
|links_to_be_verified |o |RDFDataUnit |TODO: provide description |

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

