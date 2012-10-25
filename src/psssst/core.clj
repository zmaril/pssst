(ns psssst.core)

(def ^:dynamic type-record-filename "type-record-filename.clj")

(def type-record (atom {}))

(defn update-record [fn-name param-names param-values]
  (let [typed-array (map type param-values)
        create-or-inc               (fn [v] (if v (inc v) 1))]
    (swap! type-record (fn [m]
                         (update-in m
                                    [fn-name param-names typed-array]
                                    create-or-inc)))))

(defn print-type-record []
  (spit type-record-filename
        (with-out-str (clojure.pprint/pprint @type-record))))

(defn record [& args]
  (apply update-record args)
  (print-type-record))

(defmacro defn-recorded
  ([name params body]
     `(defn ~name ~params
        (~record '~name '~params ~params)
        ~body)))