# T-UnZipper #
----------

###General###

|                              |                                                  |
|------------------------------|--------------------------------------------------|
|**Name:**                     |T-UnZipper                                        |
|**Description:**              |UnZips input file into files based on zip content. |
|                              |                      |
|**DPU class name:**           |UnZipper              | 
|**Configuration class name:** |UnZipperConfig_V1     |
|**Dialogue class name:**      |UnZipperVaadinDialog  |

***

###Configuration parameters###

|Parameter                                       |Description                                                              |                                                        
|------------------------------------------------|-------------------------------------------------------------------------|
|**Do not prefix symbolic name (checkbox):**     |If checked then output symbolic names of output files are not prefixed with symbolic name of unzipped file. Uncheck to prevent symbolic names collision if multiple zip files with same structure are unzipped. If you do not know, then uncheck this. | 

***

### Inputs and outputs ###

|Name    |Type           |DataUnit      |Description            |
|--------|---------------|--------------|-----------------------|
|input   |i              |FilesDataUnit |File to unzip.          |
|output  |o              |FilesDataUnit |List of unzipped files. |

***

### Version history ###

|Version          |Release notes               |
|-----------------|----------------------------|
|1.3.2            |N/A                         |                                

***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
|N/A              |N/A                             | 