# Ehcache3 Clustered Sample

This sample demonstrates the creation of a simple, clustered cache, both programmatically and via an external [XML resource file](src/main/resources/ehcache.xml). Both instances are identically configured to store up to 100 mappings in heap memory, up to 1 MB of mappings off heap (direct) memory, and up to 5 MB in the cluster storage tier.

A pair of different clients are demonstrated.

[`ClusteredProgrammatic`](src/main/java/org/ehcache/sample/ClusteredProgrammatic.java)
  - shows the creation of the clustered cache configured via the Ehcache API
  - populates the distributed cache with a single mapping

[`ClusteredXML`](src/main/java/org/ehcache/sample/ClusteredXML.java)
  - shows the creation of the clustered cache configured via an XML resource file
  - accesses the single mapping created by `ClusteredProgrammatic`

Finally, also demonstrated is the redirection of the *Terracotta Platform* client-side logging to a separate file, `terracotta-client.log`. See [log4j.xml](src/main/resources/log4j.xml).

## Running this sample

  - start-tc-server
  - mvn exec:exec -P programmatic
  - mvn exec:exec -P xml
