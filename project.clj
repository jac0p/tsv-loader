(defproject tsv-loader "0.1.2-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [postgresql "9.3-1102.jdbc41"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [clojure-csv-pti "2.0.1"]
                 [org.clojure/tools.cli "0.3.3"]
                 [clj-time "0.11.0"]
                 ]
  :main ^:skip-aot tsv-loader.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
