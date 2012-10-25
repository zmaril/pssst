(ns psssst.core)

(def ^:dynamic type-record-filename "type-record-filename.clj")

(def type-record (atom {}))

(defn find-type [param-name param-value]
  (type param-value))

(defn update-record [fn-name param-names param-values]
  (let [typed-array (doall (map find-type param-names param-values))
        create-or-inc               (fn [v] (if v (inc v) 1))]
    (swap! type-record (fn [m]
                         (update-in m
                                    [fn-name param-names typed-array]
                                    create-or-inc)))))

(defn print-type-record []
  (println type-record-filename)
  (spit type-record-filename
        (with-out-str (clojure.pprint/pprint @type-record))))

(defn record [& args]
  (apply update-record args)
  (print-type-record))

(defmacro defn-recorded
  ([name params & body]
     `(defn ~name ~params
        (~record '~name '~params ~params)
        ~@body)))

(defn-recorded plus-one [a b] (+ a b))
(defn-recorded plus-two [a b c] (+ a b))

(defn run-tests []
  (plus-one 1 2)
  (plus-one 1 3)
  (plus-one 1 4)
  (plus-one 1 5)
  (plus-two 1 1 nil)
  (plus-two 1 2 1)
  (plus-two 1 3 :a)
  (plus-two 1 4 'symbol)
  (plus-two 1 5 {})
  (plus-two 1 6 [])
  (plus-two 1 7 #{})
  (plus-two 1 8 (java.util.Date.)))

(run-tests)