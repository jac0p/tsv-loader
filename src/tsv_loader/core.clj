(ns tsv-loader.core
  (:require [clojure.core.async :as async]
            [clojure.java.jdbc.deprecated :as jdbc]
            [clojure-csv.core :as csv]
            [clj-time.core :as ct]
            [clj-time.format :as cf]
            [clj-time.coerce :as ce]
            [clj-time.jdbc]
            )
  (:gen-class))

;; SAMPLE DB
;(def dev-dbspec {:subprotocol "postgresql"
;                 :classname   "org.postgresql.Driver"
;                 :subname     "//10.10.11.94:5432/PerfResults"
;                 :user        "tabjolt"
;                 :password    "12345"})


(defn convert-epoch-to-sql [time]
  (clj-time.coerce/to-sql-time (* 1000 time)))


;; TODO: Move headers to config
(defn parse-row [row]
  (let [v (first (csv/parse-csv row :delimiter \tab))]
    (zipmap [:time_stamp :hostname :jmx_groups :jmx_counters
             :tableau_instances :thread_id :jmx_counter_value] v)))


(defn format-log-row [r]
  [(convert-epoch-to-sql (read-string (:time_stamp r)))
   (:hostname r) (:jmx_groups r) (:jmx_counters r)
   (:tableau_instances r) (Long/parseLong (:thread_id r)) (Float/parseFloat (:jmx_counter_value r))])


(defn insert-rows [dbspec table log-rows input-file]
  (let [rows (->> (map format-log-row log-rows)
                  (vec))]
    (apply
               clojure.java.jdbc/insert! dbspec table
               [:time_stamp :hostname :jmx_groups :jmx_counters
                :tableau_instances :thread_id :jmx_counter_value]
               rows))


  (println "Finished inserting data from " input-file))


(defn -main [dbspec table tsv-file & args]
  (println "USAGE: java -jar <JAR FILE> <DB-SPEC> <TABLE> <TSV-FILE>")
  ;"jdbc:postgresql://10.10.11.94/PerfResults?user=tabjolt&password=12345"
  (println "DB-SPEC FORMAT: jdbc:postgresql://HOSTNAME/DATABASE?user=USERNAME&password=PASSWORD")
  ;(println "PLACE TSV FILES UNDER resources/os_out.tsv AND resources/jmx_out.tsv")

  (let [log-entries (with-open [file (clojure.java.io/reader tsv-file)]
                      (doall
                        (map parse-row (line-seq file))))
        log-rows (vec log-entries)]
    (insert-rows dbspec table log-rows tsv-file))
  )

