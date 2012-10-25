(ns psssst.core)

(def ^:dynamic type-record-filename "type-record-filename.clj")

(def type-record (atom {}))

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
(defn-recorded plus-three [{:keys [a b c]}] (+ a b))
(defn-recorded plus-four [{:keys [a b c]} d] (+ a b d))
(defn-recorded plus-five [[[a] b] c] (+ a b))

(defn run-tests []
  (plus-one 1 2)
  (plus-one 1 3)

  (plus-two 1 1 nil)
  (plus-two 1 2 1)
  (plus-two 1 3 :a)
  (plus-two 1 4 'symbol)
  (plus-two 1 5 {})
  (plus-two 1 6 [])
  (plus-two 1 7 #{})
  (plus-two 1 8 (java.util.Date.))

  (plus-three {:a 1 :b 2})
  (plus-three {:a 1 :b 2 :c 3})
  (plus-three {:a 1 :b 2 :c :a})

  (plus-four {:a 1 :b 2 :c 3} 10)
  (plus-four {:a 1 :b 2 :c :a} 10)

  (plus-five [[1] 2] 3)
  (plus-five [[1] 10] 10)
  (plus-five [[1] 2] :key)
  (plus-five [[1] 2] 'symbol)

  )
