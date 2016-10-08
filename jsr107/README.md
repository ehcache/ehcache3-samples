# Ehcache 3 JSR-107 (JCache) Samples

A set of samples that demonstrate how you can use (and extend) JSR-107 API (javax.cache) with ehcache3.
  
## Running the sample
 
### JSR-107 Simple API Usage - Cache created programmaticaly - No XML configuration
 
 mvn compile exec:java -P programmatic-simple -Dexec.args="5000 5 100"
 
### JSR-107 Simple API Usage - Caches created with XML configuration

 mvn compile exec:java -P xml -Dexec.args="5000 5 100" -Djsr107.config.classpath=ehcache-jsr107-simple.xml
 mvn compile exec:java -P xml -Dexec.args="5000 5 100" -Djsr107.config.classpath=ehcache-jsr107-simpleWithTemplates.xml

### JSR-107 Simple API Usage - Caches created programmaticaly but leverage XML to extend JSR107 via ehcache3 templates

 mvn compile exec:java -P programmatic-extended -Dexec.args="5000 5 100"
