(ns pssst.example)

(defn plus-two [a b c] (+ a b))
(defn plus-three [{:keys [a b c]}] (+ a b))
(defn plus-four [{:keys [a b c]} d] (+ a b d))
(defn plus-five [[[a] b] c] (+ a b))

