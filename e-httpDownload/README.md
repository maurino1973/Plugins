# E-HTTPDownload #
----------

###General###

|                              |                                                                             |
|------------------------------|-----------------------------------------------------------------------------|
|**Name:**                     |E-HTTPDownload                                                               |
|**Description:**              |Downloads a single file from given URL and saves it with given virtual path. |
|                              |                                                                             |
|**DPU class name:**           |HttpDownload                                                                 | 
|**Configuration class name:** |HttpDownloadConfig_V1 <BR> HttpDownloadConfig_V2                             |
|**Dialogue class name:**      |HttpDownloadVaadinDialog <BR> HttpDownloadVaadinDialog2                      |

***

###Configuration parameters###

|Parameter                                       |Description                                                              |                                                        
|------------------------------------------------|-------------------------------------------------------------------------|
|**URL:**                                        |Specifies the URL of the file to be downloaded.                          |
|**Target - file name and location in output:*** |Specifies the target file name and the location for the downloaded file. |
|**Max attempts at one download:***              |Number of download attempts before failure occurs (use -1 for infinite). |
|**Interval between downloads:***                |Delay between download attempts (in miliseconds).                        | 

***

### Version history ###

|Version          |Release notes               |
|-----------------|----------------------------|
|1.3.1            |N/A                         |                                


***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
|N/A              |N/A                             | 
