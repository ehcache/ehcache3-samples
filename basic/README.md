# Ehcache3 Basic Sample

This [sample](src/main/java/org/ehcache/sample/Basic.java) demonstrates the creation of a simple, stand-alone cache, both programmatically and via an [XML resource file](src/main/resources/ehcache.xml). Both instances are identically configured to store up to 100 mappings in heap memory and up to 1 MB of mappings off heap (direct) memory.

## Running this sample

  - mvn exec:exec
