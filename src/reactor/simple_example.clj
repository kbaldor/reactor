#_k((ns reactor.simple-example
   (:require [instaparse.core :as insta]
             [clojure.core.match :refer [match]]))

   (def as-and-bs
     (insta/parser
      "S = AB*
     AB = A B
     A = 'a'+
     B = 'b'+"))


   (defn walk [t]
     (match [t]
            [[:S & rest]] (do (printf "S\n") (walk rest))
            [[:AB & rest]] (do (printf "AB\n") (walk rest))
            [[:A & rest]] (do (printf "A\n") (walk rest))
            [[:B & rest]] (do (printf "B\n") (walk rest))
            [[x & rest]] (do (walk x) (walk rest))
            [[]] nil
            [token] (do (println "token " token))
            )))
