(ns onyx-java.utility.interop
  (:gen-class :name onyx-java.utility.interop
              :methods [^:static [write_batch [clojure.lang.IPersistentMap] clojure.lang.IPersistentMap]
                        ^:static [read_batch [clojure.lang.IPersistentMap] clojure.lang.IPersistentMap]])
  (:require [onyx.information-model :refer [model]]))

(defn -write_batch
  [event]
  ((resolve 'onyx.peer.function/write-batch) event))

(defn -read_batch
  [event]
  ((resolve 'onyx.peer.function/read-batch) event))

(gen-interface
  :name onyx-java.IPipeline
  :methods [[writeBatch [clojure.lang.IPersistentMap] clojure.lang.IPersistentMap]])

(defn boolean-string [y] (if (= (type y) java.lang.String) (read-string y) y))

(def casts
  {:boolean (fn [x] (boolean (boolean-string x)))
   :integer (fn [x] (Integer/parseInt (re-find #"\A-?\d+" x)))
   :string (fn [x] (str x))
   :any (fn [x] x)
   :keyword (fn [x] (keyword x))
   :vector (fn [x] (vec x))})

(defn check-choices [choices value]
    (if (boolean (some #{:all} (flatten choices))) value
        (if (some #{value} choices) value nil)))

(defn cast-types [section m]
  (let [section* (keyword section)]
    (reduce-kv
     (fn [m* k v]
       (let [k* (keyword k)
             type (get-in model [section* :model k* :type])
             choices (get-in model [section* :model k* :choices] [:all])
             required (not (get-in model [section* :model k* :optional?] true))
             v* (check-choices choices ((get casts type identity) v))]
         (assoc m* k* v*)))
     {}
     m)))

(defn coerce-workflow [workflow]
  (mapv #(mapv (fn [v] (keyword v)) %) workflow))

(defn coerce-catalog [catalog]
  (mapv #(cast-types :catalog-entry %) catalog))

(defn coerce-lifecycles [lifecycles]
  (mapv #(cast-types :lifecycle-entry %) lifecycles))

(defn coerce-flow-conditions [fcs]
  (mapv #(cast-types :flow-conditions-entry %) fcs))

(defn coerce-windows [windows]
  (mapv #(cast-types :window-entry %) windows))

(defn coerce-trigger [trigger]
  (mapv #(cast-types :trigger-entry %) trigger))