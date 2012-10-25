# psssst

A Clojure library designed to give you insight into the types getting
passed into your functions.

## Usage

Via the repl
``` clojure
(use 'psssst.core)
(set-filename! "type-record.clj")

(defn-recorded f [a b] 1)
(f 1 2)
(f 3 2)
(f :a nil)
(f 'a identity)
(f 'a identity)
(f 'a identity)

(defn-recorded f-map [{:keys [a b]}] 1)
(f-map {:a 1 :b 2})
(f-map {:a 1 :b nil})
(f-map {:a 1 :b ['a]})
(f-map {:a 1 :b ['b]})
(f-map {:a 1 :b ['b]})
(f-map {:a 1 :b (java.util.Date.) :c "Not used"})
(f-map {:a 1 :b (java.util.Date.) :c "Me too not used as well"})

(defn-recorded f-seq [[[a] b] c] 1)
(f-seq [[1] 2] 3)
```

Now look at the contents of type-record.clj
``` clojure
{f-seq
 {[[[a] b] c] {(((java.lang.Long) java.lang.Long) java.lang.Long) 1}},
 f-map
 {[{:keys [a b]}]
  {({:keys (java.lang.Long java.util.Date)}) 2,
   ({:keys (java.lang.Long clojure.lang.PersistentVector)}) 3,
   ({:keys (java.lang.Long nil)}) 1,
   ({:keys (java.lang.Long java.lang.Long)}) 1}},
 f
 {[a b]
  {(clojure.lang.Symbol clojure.core$identity) 3,
   (clojure.lang.Keyword nil) 1,
   (java.lang.Long java.lang.Long) 2}}}
```

And viola, you have statistics about the types getting passed into
your functions! Checkout example.clj for a better example. 

## TODO
* Write better tests
* Could this *ever* be used in production? Like, *ever*?
* Expand the ways defn-recorded can be defined, i.e. support for doc
  strings, etc. 

## License

Copyright © 2012 Zack Maril

Distributed under the Eclipse Public License, the same as Clojure.
