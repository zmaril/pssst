(ns psssst.core-test
  (:use clojure.test
        psssst.core))

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
  (plus-five [[1] 2] 'symbol))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))