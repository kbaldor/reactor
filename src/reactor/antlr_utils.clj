(ns reactor.antlr-utils
  (:import [org.antlr.v4.runtime
            RuleContext ANTLRInputStream ANTLRFileStream CommonTokenStream]
           [org.antlr.v4.runtime.tree
            ErrorNode TerminalNode]))

(defn- rule-names-vector [parser] (into [] (map symbol (.getRuleNames parser))))

(defn- children [node] (map #(.getChild node %1) (range (.getChildCount node))))

(defn- terminal-text [node] (symbol (-> node .getSymbol .getText)))
(defn- terminal-meta [node]
  {:line (-> node .getSymbol .getLine)
   :pos  (-> node .getSymbol .getCharPositionInLine)})

(defn- rule-name [node rule-names]
  (let [index (-> node
                  .getRuleContext
                  .getRuleIndex)]
    (get rule-names index)))

(defn- rule-meta [node]
  {:line (-> node .start .getLine)
   :pos  (-> node .start .getCharPositionInLine)})

(defn make-rule-namer [parser] 
  (let [rule-names (rule-names-vector parser)] 
    (fn [node]
        (cond
           (instance? RuleContext node)  (with-meta (rule-name node rule-names)
                                                    (rule-meta node))
           (instance? ErrorNode node)    "ERROR"
           :else                         "NONE-OF-THE-ABOVE"))))

(defn- make-tree-walker [parser]
  (let [rule-namer (make-rule-namer parser)] 
    (fn tree-walker [node] 
      (if (instance? TerminalNode node)
        (with-meta (terminal-text node)
                   (terminal-meta node))
        (into [] (cons (rule-namer node) 
                       (map tree-walker (children node))))))))

(defn- constructor [klass]
  (fn [& args]
    (clojure.lang.Reflector/invokeConstructor klass (into-array Object args))))

(defn- invoke-method [instance method & args]
  (clojure.lang.Reflector/invokeInstanceMethod
    instance
    (name method)
    (to-array args)))

(defn- parse-input-stream [lexer parser rule input-stream]
  (let [parser      (-> input-stream
                        ((constructor lexer))
                        CommonTokenStream.
                        ((constructor parser)))
        tree-walker (make-tree-walker parser)]
    (tree-walker (invoke-method parser rule))))

(defn parse [lexer parser rule source]
  (parse-input-stream lexer parser rule (ANTLRInputStream. source)))

(defn parse-file [lexer parser rule filename]
  (parse-input-stream lexer parser rule (ANTLRFileStream. filename)))
