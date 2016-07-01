# Ehcache 3 Clustered Sample

This sample demonstrates the creation of a simple, clustered cache, both programmatically and via an external [XML resource file](src/main/resources/ehcache.xml). Both instances are identically configured to store up to 100 mappings in heap memory, up to 1 MB of mappings off heap (direct) memory, and up to 5 MB in the cluster storage tier.

A pair of different clients are demonstrated.

[ClusteredProgrammatic](src/main/java/org/ehcache/sample/ClusteredProgrammatic.java)
  - shows the creation of the clustered cache configured via the Ehcache API
  - populates the distributed cache with a single mapping

[ClusteredXML](src/main/java/org/ehcache/sample/ClusteredXML.java)
  - shows the creation of the clustered cache configured via an XML resource file
  - accesses the single mapping created by `ClusteredProgrammatic`

Finally, also demonstrated is the redirection of the *Terracotta Platform* client-side logging to a separate file, `terracotta-client.log`. See [log4j.xml](src/main/resources/log4j.xml).

## Running this sample

### Start the Terracotta Server

[Download](https://github.com/ehcache/ehcache3/releases) the full Ehcache clustering kit, if you have not.  This kit contains the Terracotta Server, which enables distributed caching with Ehcache.

Open a terminal and change into the directory where you have this sample.

Start the Terracotta Server, using the configuration supplied with this sample:

- `<path/to/ehcache-clustered-kit>/server/bin/start-tc-server.sh -f ./tc-config.xml`

(For Windows environments, use the .bat start script rather than the .sh one).

Wait a few seconds for the Terracotta Server to start up - there will be a clear message in the terminal stating the server is *ACTIVE* and *ready for work*.

### Run the sample clients

Open a second terminal and change into the directory where you have this sample.

Run the first client, which will create a distributed cache and put data into it:

  - `mvn exec:exec -P programmatic`

Run the second client, which will connect to the distributed cache and read the data written by the first client.

  - `mvn exec:exec -P xml`
