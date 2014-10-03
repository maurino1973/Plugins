# T-Zipper #
----------

###General###

|                              |                                             |
|------------------------------|---------------------------------------------|
|**Name:**                     |T-Zipper                                     |
|**Description:**              |Zip input files into zip file of given name. |
|                              |                                             |
|**DPU class name:**           |Zipper                                       | 
|**Configuration class name:** |ZipperConfig_V1                              |
|**Dialogue class name:**      |ZipperVaadinDialog                           |

***

###Configuration parameters###

|Parameter                                       |Description                                                              |                                                        
|------------------------------------------------|-------------------------------------------------------------------------|
|**Zip file path/name (with extension):***       |Specifies the path/name for the output file to be created. Given path/name must be relative ie. /data.zip, /data/out.zip. Absolute path like c:/ must not be used. In case unix system /dir/data.zip is interpreted as a relative path. |

***

### Inputs and outputs ###

|Name    |Type           |DataUnit     |Description          |
|--------|---------------|-------------|---------------------|
|input   |i              |FilesDataUnit|list of files to zip |
|output  |o              |FilesDataUnit|name of zip file     |   

### Version history ###

|Version |Release notes |
|--------|--------------|
|1.3.2   |N/A           |                                

***

### Developer's notes ###

|Author |Notes |
|-------|------|
|N/A    |N/A   | 