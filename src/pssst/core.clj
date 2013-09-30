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
    (swap! type-record #(update-in %1 [fn-name param-names typed-array]
                                   create-or-inc))))

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

(defn record-var* 
  [v]
  (let [v (if (var? v) v (resolve v)) 
        ns (.ns v)
        s (.sym v)]
    (when (and (ifn? @v) (-> v meta :meta not))
      (let [f @v
            vname (symbol (str ns "/" s))]
        (-> v
            (alter-var-root #(fn recording-wrapper [& args]
                               (let [arglists (:arglists (meta v))
                                     correct-args 
                                     (filter (fn [maybe] (= (count maybe) (count args))) arglists)]
                                 (when (< 1 (count correct-args))
                                   (throw (str "Too many arglists to chose from:" correct-args )))
                                 (record vname (first correct-args) args)
                                 (apply % args)))))))))

(defn record-ns*
  [ns]
  (let [ns (the-ns ns)]
    (when-not ('#{clojure.core clojure.tools.trace} (.name ns))
      (let [ns-fns (->> ns ns-interns vals)]
        (doseq [f ns-fns]
          (record-var* f))))))

(defmacro record-ns
  "record-ns takes a namespace, finds all the fns in there and wraps
  them up with a recorder function. Copied almost directly from
  tools.trace."
  [ns]
  `(record-ns* ~ns))
