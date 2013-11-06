# lein-uberjar-deploy
  
A Leiningen plugin to create and deploy an uberjar and associated pom.xml.
    
## Usage
  
Put `[lein-uberjar-deploy "0.1.0"]` into the `:plugins` vector of your project.clj, modified to the
current version.
  
Add `snapshots` and `releases` entries to the `:deploy-repositories` vector of your project.clj. For example,

    :deploy-repositories
    [
      ["snapshots" {:id "nexus" :url "http://host:8081/nexus/content/repositories/snapshots"}]
      ["releases"  {:id "nexus" :url "http://host:8081/nexus/content/repositories/releases"}]
    ]

Repository credentials are obtained from ~/.m2/settings.xml from the &lt;username&gt; and &lt;password&gt; elements for the &lt;server&gt; with an `id` matching the `id` specified in the `:deploy-repositories` entry.
    
Run:

    $ lein uberjar-deploy 

## License

Copyright © 2013 TheLadders, Inc

Distributed under the MIT public license.

