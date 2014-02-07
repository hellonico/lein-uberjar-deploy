# lein-uberjar-deploy
  
A Leiningen plugin to create and deploy an uberjar and associated pom.xml.

Uberjars are jar files containing your compiled code as well as all of its dependencies. They're used to enable standalone distributions, which can be run with `java -jar /path/to/uberjar`.

Leiningen's built-in deploy task (`lein deploy`) deploys a standard, non-uberjar, as can be built with `lein jar`. lein-uberjar-deploy allows simple deployment of an uberjar in a similar manner to standard deployment.

        
## Usage
  
Put `[lein-uberjar-deploy "0.1.4"]` into the `:plugins` vector of your project.clj, modified to the
current version. For example:

    :plugins [[theladders/lein-uberjar-deploy "0.1.4"]]

  
Add `snapshots` and `releases` entries to the `:repositories` vector of your project.clj. For example,

    :repositories [["snapshots" {:id "nexus" :url "http://host:8081/path/to/snapshots"}]
                   ["releases"  {:id "nexus" :url "http://host:8081/path/to/releases"}]]

**Note**: the uberjar created will not be signed, which Leiningen does at deployment-time for release jars that it creates itself (via `lein deploy`).

Repository credentials are obtained from `~/.m2/settings.xml` from the &lt;`username`&gt; and &lt;`password`&gt; elements for the &lt;`server`&gt; with an `id` matching the `:id` specified in the `:repositories` entry.
    
Run:

    $ lein uberjar-deploy

## License

Copyright © 2013 TheLadders, Inc

Distributed under the MIT public license.

