* SpringJCacheRegionFactory is a hack. We should look for a better solution
  * Look at EhcacheCachingProvider to see if the DefaultConfigurationResolver can return the right provider
  * Be able to provide the same URI as Spring, maybe using the LocalRegionFactoryProxy
  * Separate the CachingProvider URI from the EhCache configuration
* Create the CacheManager in a programmative way (no ehcache.xml)... This pushes for separating URI in SPI
* Do a front page with performance graphs of different style of caching
* Generate a CSV of the biographies to feed to Gatling
* Gatling get stars and weather injector
* Have a list of stars with a filter
* Have a star details
* Setting screen to tweak the cache at runtime
* Have a NoCache persistence resource in Ehcache
* Codehale Metrics
* Liquibase hibernate
  * Fix liquibase:diff
  * Update the NamingStrategy in the pom.xml to hibernate 5.2
