# T-XSLT #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |T-XSLT                                              |
|**Description:**              |Does XSL Transformation over files and outputs Files |
|                              |                                                               |
|**DPU class name:**           |XSLT     | 
|**Configuration class name:** |XSLTConfig_V1                           |
|**Dialogue class name:**      |XSLTVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Skip file on error (checkbox)** |Do not stop pipeline execution if error occurs. |
|**Output file extension**|self-descriptive  | 
|**XSLT Template upload (button):** |Choose and upload XSLT template file.  |
|**Input:**|Displays the content of XSLT Template.  |


***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|filesInput  |i |FilesDataUnit  |File to be transformed.  |
|filesOutput |o |FilesDataUnit  |Transformed file of given type.  |

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

