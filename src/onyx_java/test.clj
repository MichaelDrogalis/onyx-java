(ns onyx-java.test
    (:gen-class)
    (:require [onyx-java.utils.test-template :as t]
              [clojure.test :refer [deftest is]]))


(def input-dir "test/resources/onyx_simple/source/")
(def output-dir "test/resources/onyx_simple/target/")

(deftest entity-coercion-test
    (let [input input-dir
          output output-dir
          master-map (t/create-master-map input)
          params (t/add-entity-params master-map input)
          coerced-map (t/coerce-entities master-map)
          expected-map (t/get-expected-entities master-map output)]
    (is (= coerced-map expected-map))))

(defn vector-coercion-test []
    (let [input input-dir
          output output-dir
          master-map (t/create-master-map input)
          params (t/add-entity-params master-map input)
          entities (t/add-vectors-entities master-map)]
    master-map))
