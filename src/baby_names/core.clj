(ns baby-names.core
  (:gen-class)
  (:import [java.io StringReader] [java.io BufferedReader FileReader])
  (:use clojure.java.io
    clojure-csv.core))

(defn take-csv
    "Takes file name and reads data."
    [fname]
    (with-open [file (reader fname)]
        (parse-csv (slurp file))))

(defn parse-int [s]
    (Integer. (re-find #"[0-9]*" s)))

(defn sex [r]
  (r 1))

(defn name-count [r]
  (parse-int (r 2)))

(defn sort-by-count [c]
  (reverse (sort-by name-count c)))

(defn male [c]
  (filter #(= (sex %) "M") c))

(defn female [c]
  (filter #(= (sex %) "F") c))

(defn less-common-names [c]
  (filter #(< (name-count %) 7000) c))

(defn less-obscure-names [c]
  (filter #(> (name-count %) 100) c))

(defn random-name [c]
  ((nth c (rand (count c))) 0))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [baby-names (doall (take-csv "./names/yob2015.txt"))
        sorted (sort-by-count baby-names)
        filtered ((comp female less-common-names less-obscure-names) sorted)
        random-names (repeatedly 25 #(random-name filtered))]
    (doseq [i (range (count random-names))]
      (println (nth random-names i) "Axsom"))))
