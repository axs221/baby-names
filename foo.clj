(defn process-csv [file]
    (with-open [rdr (io/reader file)]
        (doall (csv/parse-csv rdr))
        (println rdr)))

(println (process-csv "yob2015.txt"))
