(ns leiningen.uberjar-deploy
  (:use
    [leiningen.uberjar :only [uberjar]]
    [leiningen.deploy :only [deploy]]
    [leiningen.pom :only [pom]]
  )

  (:require 
    [clojure.data.xml :as xml]
    [clojure.zip :as zip]
    [clojure.data.zip.xml :as zx])
)

; E.g., com.theladders
(defn get-group [project] (format "%s" (project :group)))
; E.g., bigproject
(defn get-name [project] (format "%s" (project :name)))
; com.theladders/bigproject
(defn get-group-and-name [project] (format "%s/%s" (get-group project) (get-name project)))

; E.g., 0.1.0-SNAPSHOT
(defn get-version [project] (format "%s" (project :version)))

; E.g., target/bigproject-0.1.0-SNAPSHOT-standalone.jar
(defn get-uberjar-file-path [project] (format "target/%s-%s-standalone.jar" (get-name project) (get-version project)))
(def  get-pom-file-path "pom.xml")

(defn get-target [project]
  (if (.endsWith (get-version project) "-SNAPSHOT")
    "snapshots"
    "releases"
  )
)

(defn m2-settings-file [] (format "%s/.m2/settings.xml" (System/getProperty "user.home")))

(defn abort [message]
  (binding [*out* *err*] 
    (println message)
    (System/exit 1)
  )
)

(defn get-m2-settings-xml [] (xml/parse-str (slurp (m2-settings-file))))
(defn create-zip [xml] (zip/xml-zip xml))

; TODO: don't parse the file repeatedly
(defn get-username-from-m2-xml [id] (zx/xml-> (create-zip (get-m2-settings-xml)) :servers :server [:id id] :username zx/text))
(defn get-password-from-m2-xml [id] (zx/xml-> (create-zip (get-m2-settings-xml)) :servers :server [:id id] :password zx/text))

(defn get-repo-values [project name-to-find]
  (filter (fn[x](= name-to-find (first x))) (:repositories project))
)

(defn get-repo-value [project value]
  (let [entry (get-repo-values project (get-target project))]
    (nth (find (get (first entry) 1) value) 1)
  )
)

(defn check-m2-settings-file [project]
  (if-not (.exists (new java.io.File (m2-settings-file))) 
    (abort (format "File %s not found" (m2-settings-file)))
  )

  (if (empty? (get-username-from-m2-xml (get-repo-value project :id)))
    (abort (format "username not found for server with id \"%s\" in %s" (get-repo-value project :id) (m2-settings-file)))
  )

  (if (empty? (get-password-from-m2-xml (get-repo-value project :id)))
    (abort (format "password not found for server with id \"%s\" in %s" (get-repo-value project :id) (m2-settings-file)))
  )
)

(defn confirm-repo-defined-in-project [project]
  (if (nil? (get-repo-value project :url))
    (abort (format ":url not found for \"%s\" entry in project's :repositories" (get-target project)))
  )

  (if (nil? (get-repo-value project :id))
    (abort (format ":id not found for \"%s\" entry in project's :repositories" (get-target project)))
  )
)

(defn specify-credentials [project] 
  {:repositories 
   [
    ["snapshots" {:username (get-username-from-m2-xml (get-repo-value project :id)) :password (get-password-from-m2-xml (get-repo-value project :id))}]
    ["releases"  {:username (get-username-from-m2-xml (get-repo-value project :id)) :password (get-password-from-m2-xml (get-repo-value project :id))}]
   ]})

(defn merge-credentials-into-project [project]
    (leiningen.core.project/merge-profiles project [(specify-credentials project)])
)

(defn check-config [project]
  (confirm-repo-defined-in-project project)
  (check-m2-settings-file project)
)

(defn uberjar-deploy
  "Deploy project's uberjar and pom.xml. 

A :repositories entry must be present in the project.clj, containing the
snapshots and releases :url locations. For example,

  :repositories [
    [\"snapshots\" {:id \"nexus\" :url \"http://host:8081/nexus/content/repositories/snapshots\"}]
    [\"releases \" {:id \"nexus\" :url \"http://host:8081/nexus/content/repositories/releases\"}]
                       ]
  
Snapshots or releases is chosen based on the version in the project.clj.

The :id must match a server specified in ~/.m2/settings.xml, from which the username and password 
are obtained."

  [project & args]
  (check-config project)
  (let [project (merge-credentials-into-project project)]
    (uberjar project)
    (pom project)
    (deploy project (get-target project) (get-group-and-name project) (get-version project) (get-uberjar-file-path project) get-pom-file-path)
  )
)

