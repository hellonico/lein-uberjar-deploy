(defproject theladders/lein-uberjar-deploy "0.1.3-SNAPSHOT"
  :description "Create and Deploy uberjar."
  :license {:name "MIT"
            :url "http://www.opensource.org/licenses/mit-license.php"}
  :eval-in-leiningen true
  :dependencies[
                 [org.clojure/data.xml "0.0.7"]
                 [org.clojure/data.zip "0.1.1"]
               ]
      :deploy-repositories [
                                                      ["snapshots" {:id "nexus" :url "http://mercurial:8081/nexus/content/repositories/snapshots"}]
                                                      ["releases"  {:sign-releases false :id "nexus" :url "http://mercurial:8081/nexus/content/repositories/releases"}]
                                                     ] 

)

