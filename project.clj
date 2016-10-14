(defproject reactor "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.antlr/antlr4 "4.5.3"]
                 [org.clojure/math.numeric-tower "0.0.4"]]
  :plugins [[lein-antlr "0.3.0"]]
  :main ^:skip-aot reactor.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}

  :java-source-paths ["gen-src"]

  :hooks [leiningen.antlr]

  :antlr-src-dir "src/grammar"
  :antlr-dest-dir "gen-src"
  :antlr-options {:Werror true}
  )
