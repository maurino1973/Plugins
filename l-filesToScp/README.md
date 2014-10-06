# L-FilesToScp #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |L-FilesToScp                                             |
|**Description:**              |Uploads given files using scp. |
|                              |                                                               |
|**DPU class name:**           |FilesToScp     | 
|**Configuration class name:** |FilesToScpConfig_V1                           |
|**Dialogue class name:**      |FilesToScpVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Host:*** |Destination host name.  |
|**Port:*** |Communication port number.|
|**Username:*** |Username for destination host. |
|**Password:*** |Password for the username.  |
|**Destination: (must exist and not end with '/')***| Destination for files to be loaded, you need rights to access the target directory and all its parent directories. |
|**Soft failure (checkbox)**|If checked and upload failed, then pipeline continues, otherwise the pipeline is stopped. |


***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|input |i |FilesDataUnit |File uploaded to specified remote host using SCP.  |

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

