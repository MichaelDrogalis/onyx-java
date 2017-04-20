(ns onyx-java.utils.map-fns
  (:gen-class) 
  (:require [clojure.java.io :refer [resource]])
  (:import [clojure.lang IPersistentMap PersistentVector]
           org.onyxplatform.api.java.OnyxMap))

(defn to-onyx-map [^IPersistentMap m]
  (let [ent (OnyxMap.)
        ks (keys m) ]
    (reduce 
      ; Strip keywords, convert everything else to a string.
      (fn [ent k]
        (let [n (if (keyword? k) 
                  (name k) 
                  (str k))
              bv (get m k "MISSING")
              kw? (keyword? bv)
              v (if (keyword? bv)
                  (name bv) 
                  bv) ]
          ; Side-effectful
          (if kw?
            (.addKeywordParameter ent n v)
            (.addObjectParameter ent n v))
          ent))
      ent
      ks)
    ent))

(defn edn-from-resources [rsrc-path]
  (to-onyx-map (-> rsrc-path resource slurp read-string)))