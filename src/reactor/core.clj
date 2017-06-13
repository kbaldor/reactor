(ns reactor.core
  (:require [clojure.core.match :refer [match]]
            [antlr-clj.core :refer :all]
            [clojure.math.numeric-tower :as math])
  (:use [clojure.pprint]
        [clojure.tools.namespace.repl :only (refresh)])  
  (:import reactorLexer reactorParser)
  (:gen-class))

(defn toAST [tree]
  (match [tree]
         [[:equation lhs [:relop op] rhs]] [op lhs rhs]
         [[:expr lhs op rhs]]              [op lhs rhs]
         [[:expr expr]]                    expr
         [[:scientific num]]               num
         [[:scientific m E e]]             [:scientific m e]
         [[:atom atom]]                    atom

         :else                             tree))

(defn evalAST [ast]
  (match [ast]
         [["+" lhs rhs]]     (+ lhs rhs)
         [["-" lhs rhs]]     (- lhs rhs)
         [["*" lhs rhs]]     (* lhs rhs)
         [["/" lhs rhs]]     (/ lhs rhs)
         [["=" lhs rhs]]     (format "%s = %s" lhs rhs)
         [[:scientific m e]] (* m (math/expt 10 e))
         [[:number num]]     (read-string num)
         :else               ast
         ))

(defn printer [item]
  (print item)
  (println (meta item)))

(defn metaTree [tree]
  (printer (if (seq? tree) (first tree) tree)))

(def tree (parse reactorLexer reactorParser 'reactor " reactor R { input: in : Integer output: } "))
(def ast (clojure.walk/postwalk metaTree tree))
(println "parse tree: " tree)
;(println "pretty    : " ast)
;(println "value     : " (clojure.walk/postwalk evalAST ast))

;(def rtree (parse-file reactorLexer reactorParser 'reactor "examples/ex1.rct"))
;(println "react tree: " rtree)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;(println "Hello, World!")
  ;(before-after "10/2*(123+456)")
  #_(before-after "1*2+3*4+5/6"))
  ;(println (to-ast (expr "1+2*3")))

;(println (match '(1 2 3)
;                [1 2 2] :c1
;                [1 & r] r
;                [1 2 3] :c3)) )
