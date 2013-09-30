(ns pssst.core-test
  (:require [pssst.example :as ex])
  (:use clojure.test
        pssst.core
        [clojure.pprint :only (pprint)]))

(def test-filename "test-file.clj")

(set-filename! test-filename)

(defn check-file-and-reset-atom []
  (swap! pssst.core/type-record (constantly {}))
  (slurp test-filename))

(defmacro pssst-testing [doc action m]
  (let [pm# (with-out-str (pprint m))]
    `(do ~action
         (testing
             (is ~pm#
                 (check-file-and-reset-atom))))))


(deftest basic-type-checking
  (defn-recorded f [a] a)
  (pssst-testing "Integers"
                 (f 1)
                 {'f {['a] {(java.lang.Long) 1}}}))

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

(deftest big-ol-test
  (pssst-testing "Lots of calls"
                 (run-my-tests)
                 '{f {[a] {(java.lang.Long) 1}},
                   plus-five
                   {[[[a] b] c]
                    {(((java.lang.Long) java.lang.Long) clojure.lang.Symbol) 1,
                     (((java.lang.Long) java.lang.Long) clojure.lang.Keyword) 1,
                     (((java.lang.Long) java.lang.Long) java.lang.Long) 2}},
                   plus-four
                   {[{:keys [a b c]} d]
                    {({:keys (java.lang.Long java.lang.Long clojure.lang.Keyword)}
                      java.lang.Long)
                     1,
                     ({:keys (java.lang.Long java.lang.Long java.lang.Long)}
                      java.lang.Long)
                     1}},
                   plus-three
                   {[{:keys [a b c]}]
                    {({:keys (java.lang.Long java.lang.Long clojure.lang.Keyword)}) 1,
                     ({:keys (java.lang.Long java.lang.Long java.lang.Long)}) 1,
                     ({:keys (java.lang.Long java.lang.Long nil)}) 1}},
                   plus-two
                   {[a b c]
                    {(java.lang.Long java.lang.Long java.util.Date) 1,
                     (java.lang.Long java.lang.Long clojure.lang.PersistentHashSet) 1,
                     (java.lang.Long java.lang.Long clojure.lang.PersistentVector) 1,
                     (java.lang.Long java.lang.Long clojure.lang.PersistentArrayMap) 1,
                     (java.lang.Long java.lang.Long clojure.lang.Symbol) 1,
                     (java.lang.Long java.lang.Long clojure.lang.Keyword) 1,
                     (java.lang.Long java.lang.Long java.lang.Long) 1,
                     (java.lang.Long java.lang.Long nil) 1}}}))

(defn run-example-tests []
  (ex/plus-two 1 1 nil)
  (ex/plus-two 1 2 1)
  (ex/plus-two 1 3 :a)
  (ex/plus-two 1 4 'symbol)
  (ex/plus-two 1 5 {})
  (ex/plus-two 1 6 [])
  (ex/plus-two 1 7 #{})
  (ex/plus-two 1 8 (java.util.Date.))

  (ex/plus-three {:a 1 :b 2})
  (ex/plus-three {:a 1 :b 2 :c 3})
  (ex/plus-three {:a 1 :b 2 :c :a})

  (ex/plus-four {:a 1 :b 2 :c 3} 10)
  (ex/plus-four {:a 1 :b 2 :c :a} 10)

  (ex/plus-five [[1] 2] 3)
  (ex/plus-five [[1] 10] 10)
  (ex/plus-five [[1] 2] :key)
  (ex/plus-five [[1] 2] 'symbol))

(deftest another-big-ol-test
  (pssst-testing "Lots of calls in another virigin namespace"
                 (do  
                   (record-ns 'pssst.example)
                   (run-example-tests))
                 '{pssst.example/plus-five
                   {[[[a] b] c]
                    {(((java.lang.Long) java.lang.Long) clojure.lang.Symbol) 1,
                     (((java.lang.Long) java.lang.Long) clojure.lang.Keyword) 1,
                     (((java.lang.Long) java.lang.Long) java.lang.Long) 2}},
                   pssst.example/plus-four
                   {[{:keys [a b c]} d]
                    {({:keys (), :a 1, :c :a, :b 2} java.lang.Long) 1,
                     ({:keys (), :a 1, :c 3, :b 2} java.lang.Long) 1}},
                   pssst.example/plus-three
                   {[{:keys [a b c]}]
                    {({:keys (), :a 1, :c :a, :b 2}) 1,
                     ({:keys (), :a 1, :c 3, :b 2}) 1,
                     ({:keys (), :a 1, :b 2}) 1}},
                   pssst.example/plus-two
                   {[a b c]
                    {(java.lang.Long java.lang.Long java.util.Date) 1,
                     (java.lang.Long java.lang.Long clojure.lang.PersistentHashSet) 1,
                     (java.lang.Long java.lang.Long clojure.lang.PersistentVector) 1,
                     (java.lang.Long java.lang.Long clojure.lang.PersistentArrayMap) 1,
                     (java.lang.Long java.lang.Long clojure.lang.Symbol) 1,
                     (java.lang.Long java.lang.Long clojure.lang.Keyword) 1,
                     (java.lang.Long java.lang.Long java.lang.Long) 1,
                     (java.lang.Long java.lang.Long nil) 1}}}))
