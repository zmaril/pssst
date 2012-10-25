(ns psssst.core-test
  (:use clojure.test
        psssst.core
        [clojure.pprint :only (pprint)]))

(def test-filename "example.clj")

(set-filename! test-filename)

(defn check-file-and-reset-atom []
  (swap! psssst.core/type-record (constantly {}))
  (slurp test-filename))

(defmacro psssst-testing [doc action m]
  (let [pm# (with-out-str (pprint m))]
    (println doc action m pm#)
    `(do ~action
         (testing
             (is ~pm#
                 (check-file-and-reset-atom))))))


(deftest basic-type-checking
  (defn-recorded f [a] a)
  (psssst-testing "Integers"
                 (f 1)
                 {'f {['a] {(java.lang.Long) 1}}}
                 ))

(defn-recorded plus-two [a b c] (+ a b))
(defn-recorded plus-three [{:keys [a b c]}] (+ a b))
(defn-recorded plus-four [{:keys [a b c]} d] (+ a b d))
(defn-recorded plus-five [[[a] b] c] (+ a b))

(defn run-my-tests []

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
  (plus-five [[1] 2] 'symbol))
