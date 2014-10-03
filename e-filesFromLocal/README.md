<!---

This template is intended to be used for describing UnifiedViews plugins as README.md. 

- Copy this README.md to root directory for the plugin to be described.  

- Replace <<descriptive information>> with proper values.

- If no values are available, replace <<descriptive information>> with 'N/A'.

- Use <BR> tag for creation of multi-line cells (in case the length of text exceeds the width of page, it is wrapped automatically to multi-line cell).

- Enclose each configuration parameter name with ** for highlighting the text as bold. 

- Add '(optional)' to Type of input or output if it is not mandatory (all inputs and outputs are mandatory by default). 

- Delete these template comments after the completion of the document.  

-->

# E-FilesFromLocal #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |E-FilesFromLocal                                               |
|**Description:**              |Extract local file or directory.                               |
|                              |                                                               |
|**DPU class name:**           |FilesFromLocal                                                 | 
|**Configuration class name:** |FilesFromLocalConfig_V1                                        |
|**Dialogue class name:**      |FilesFromLocalVaadinDialog                                     | 

***

###Configuration parameters###

|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**File or directory to extract:* |If directory is provided then all files and files in subdirectories are extracted. |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|output              |o          |FilesDataUnit                    |TODO: provide description          |


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

