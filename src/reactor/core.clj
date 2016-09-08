(ns reactor.core
  (:require [instaparse.core :as insta]
            [clojure.core.match :refer [match]]
            [reactor.simple-example :refer :all]
            [reactor.simple-expression :refer :all])
  (:gen-class))


;(println (expr "1234"))

(defn before-after [e-str]
  (println e-str)
  (let [e (expr e-str)]
    (println e)
    (println (to-ast e))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  (before-after "10/2*(123+456)")
  (before-after "1*2+3*4+5/6"))
  ;(println (to-ast (expr "1+2*3")))

;(println (match '(1 2 3)
;                [1 2 2] :c1
;                [1 & r] r
;                [1 2 3] :c3)) )
