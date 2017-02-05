#_((ns reactor.simple-expression
     (:require [instaparse.core :as insta]
               [clojure.core.match :refer [match]]))


   (def expr
     (insta/parser
      "
    expr     = addExpr
    addExpr  = (addExpr  ('+'|'-'))? multExpr
    multExpr = (multExpr ('*'|'/'))? atom\n

    atom     = (INTEGER
               | '(' expr ')')

    INTEGER  = DIGIT+
    DIGIT    = '0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9'+

    "
      ))

   (defn to-ast [t]
     (match [t]
            [[:INTEGER & digits]] (clojure.string/join (map second digits))
            [[:atom "(" tt ")"]] (to-ast tt)
            [[:addExpr t1 op t2]] (list op (to-ast t1) (to-ast t2))
            [[:multExpr t1 op t2]] (list op (to-ast t1) (to-ast t2))
            [[kw tt]] (to-ast tt)
            [[node & nodes]] (cons node (map to-ast nodes))
            [atom] atom
            )))
