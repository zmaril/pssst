(ns pssst.core
  (:use [clojure.pprint :only (pprint)]))

(def type-record-filename (atom "type-record-filename.clj"))

(def type-record (atom {}))

(defn set-filename! [filename]
  (swap! type-record-filename (constantly filename)))

(defn find-type [param-name param-value]
  (condp = (type param-name)

    clojure.lang.PersistentArrayMap
    (update-in param-value [:keys] (partial map type))

    clojure.lang.PersistentVector
    (map find-type param-name param-value)

    clojure.lang.Symbol
    (type param-value)))

(defn update-record [fn-name param-names param-values]
  (let [typed-array (doall (map find-type param-names param-values))
        create-or-inc               (fn [v] (if v (inc v) 1))]
    (swap! type-record (fn [m]
                         (update-in m
                                    [fn-name param-names typed-array]
                                    create-or-inc)))))

(defn print-type-record []
  (spit @type-record-filename
        (with-out-str (pprint @type-record))))

(defn record [& args]
  (apply update-record args)
  (print-type-record))

(defmacro defn-recorded
  ([name params & body]
     `(defn ~name ~params
        (~record '~name '~params ~params)
        ~@body)))
