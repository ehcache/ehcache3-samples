# Ehcache 3 JSR-107 (JCache) Samples

A set of samples that demonstrate how you can use (and extend) JSR-107 API (javax.cache) with ehcache3.
  
## Running the samples

You are encouraged to "play" with the program arguments and the configuration file to test out things like cache time or capacity evictions etc...  
Program arguments are as follow:
- Arg 1: number of cache operations to run in a single iteration.
- Arg 2: number of iterations to perform.
- Arg 3: sleep time in millis between each iteration/
 
### JSR-107 Simple API Usage: Cache created programmaticaly - No XML configuration
 
 mvn -P programmatic-simple -Dexec.args="5000 5 100"
 
### JSR-107 Simple API Usage: Caches created with XML configuration

 mvn -P xml -Dexec.args="5000 5 100" -Djsr107.config.classpath=ehcache-jsr107-simple.xml
 mvn -P xml -Dexec.args="5000 5 100" -Djsr107.config.classpath=ehcache-jsr107-simpleWithTemplates.xml

### JSR-107 Simple API Usage: Caches created programmaticaly but leverage XML to extend JSR107 via ehcache3 templates

 mvn -P programmatic-extended -Dexec.args="5000 5 100"
