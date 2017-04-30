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
  (filter #(< (name-count %) 9000) c))

(defn less-obscure-names [c]
  (filter #(> (name-count %) 50) c))

(defn random-name [c]
  ((nth c (rand (count c))) 0))

(defn generate-name [previous-names]
  (let [baby-names (doall (take-csv "./names/yob2015.txt"))
        sorted (sort-by-count baby-names)
        filtered ((comp female less-common-names less-obscure-names) sorted)
        unseen (filter #(not (contains? previous-names %)) sorted)
        baby-name (random-name filtered)]
    baby-name))

(defn iterate-random-names []
  (loop [previous-names []]
    (let [baby-name (generate-name previous-names)]
      (println baby-name "Axsom  (enter 'y' to save)")
      (let [yn (read-line)]
        (if (= yn "y")
          (spit "./possible-names.txt" (str "\n" baby-name) :append true))
        (recur (conj previous-names name))))))

(defn ask-to-keep [baby-name]
  (println (baby-name 0) "Axsom  (press 'y' to keep)") 
  (= "y" (read-line)))

(defn iterate-possible-names []
  (let [baby-names (doall (take-csv "./possible-names.txt"))
        names-to-keep (filter ask-to-keep baby-names)]
    (spit "./possible-names.txt" (reduce (fn [prev baby-name] (str prev (baby-name 0) "\n")) "" names-to-keep))
    names-to-keep))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Enter 'r' to go through random names, or 'p' to go through possibilities")
  (let [yn (read-line)]
    (if (= yn "p")
      (iterate-possible-names)
      (iterate-random-names))))
