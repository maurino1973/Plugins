# T-FilesFilter #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |T-FilesFilter                                              |
|**Description:**              |Filters files. |
|                              |                                                               |
|**DPU class name:**           |FilesFilter     | 
|**Configuration class name:** |FilesFilterConfig_V1                           |
|**Dialogue class name:**      |FilesFilterVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Used filter:** |there are two filters to perform filtering on: <BR> symbolic name <BR> virtual path  |
|**Custom predicate:*** |filter pattern, for example '.csv'.|
|**Use regular expression: (checkbox)** |if checked, regular expressions are allowed in filter pattern |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|input |i |FilesDataUnit  |List of files to be filtered.  |
|output|o |FilesDataUnit |List of files passing the filter. | 

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

