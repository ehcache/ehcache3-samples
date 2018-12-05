# Ehcache 3 Basic Sample

This sample demonstrates the creation of a simple, stand-alone cache, both programmaticaly and via an [XML resource file](src/main/resources/ehcache.xml). 
Both instances are identically configured to store up to 100 mappings in heap memory, up to 1 MB of mappings off heap (direct) memory.

2 different clients are demonstrated:

[BasicProgrammatic](src/main/java/org/ehcache/sample/BasicProgrammatic.java)
  - shows the creation of the standalone cache configured *via the Ehcache API*
  - populates the cache with a single mapping
  - retrieve the mapping from cache

[BasicXML](src/main/java/org/ehcache/sample/BasicXML.java)
  - shows the creation of the standalone cache configured *via an XML resource file*
  - populates the cache with a single mapping
  - retrieve the mapping from cache
  
## Running this sample

  - BasicXML: mvn -P xml
  - BasicProgrammatic: mvn -P programmatic
