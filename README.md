# FileUtil
utils for excel,csv,zip

[Wiki](https://github.com/Joycai/FileUtil/wiki)

## 需要的依赖
*GUAVA*和*JACKSON*
```groovy
compile group: 'com.google.guava', name: 'guava', version: '27.0-jre'  
compile group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.9.7'
```  
*POI*库  
```groovy
compile group: 'org.apache.poi', name: 'poi', version: '4.0.0'
compile group: 'org.apache.poi', name: 'poi-ooxml', version: '4.0.0'
```  
*CSV*库  
```groovy
compile group: 'net.sf.supercsv', name: 'super-csv-java8', version: '2.4.0'
```  

## 目前已经完成的模块
* CSV
* XLS
* XLSX
* Zip