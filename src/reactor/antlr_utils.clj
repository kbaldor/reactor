(ns reactor.antlr-utils
  (:import [org.antlr.v4.runtime
            RuleContext ANTLRInputStream ANTLRFileStream CommonTokenStream]
           [org.antlr.v4.runtime.tree
            ErrorNode TerminalNode]))

(defn- rule-names-vector [parser]
  (let [names (.getRuleNames parser)]
    (into [] (map keyword names))))

(defn- get-children [node]
  (for [i (range (.getChildCount node))]
    (.getChild node i)))

(defn- get-rule-name [node rule-names]
  (let [index (-> node
                  .getRuleContext
                  .getRuleIndex)]
    (keyword (get rule-names index))))

(defn- get-terminal-text [node] (-> node .getSymbol .getText))

(defn node-text [node rule-names]
  (cond
    (instance? RuleContext node)  (get-rule-name node rule-names)
    (instance? ErrorNode node)    "ERROR"
    :else                         "NONE-OF-THE-ABOVE"))

(defn- to-tree [node rule-names]
  (if (instance? TerminalNode node)
    (get-terminal-text node)
    (into [] (cons (node-text node rule-names) (into [] (map #(to-tree % rule-names) (get-children node)))))))

(defn- construct [klass & args]
  (clojure.lang.Reflector/invokeConstructor klass (into-array Object args)))

(defn invoke-method [instance method & args]
  (clojure.lang.Reflector/invokeInstanceMethod
    instance
    (name method)
    (to-array args)))

(defn parse [lexer parser rule source]
  (let [input  (ANTLRInputStream. source)
        lexer  (construct lexer input)
        tokens (CommonTokenStream. lexer)
        parser (construct parser tokens)]
    (to-tree (invoke-method parser rule) (rule-names-vector parser))))

(defn parse-file [lexer parser rule filename]
  (let [input  (ANTLRFileStream. filename)
        lexer  (construct lexer input)
        tokens (CommonTokenStream. lexer)
        parser (construct parser tokens)]
    (to-tree (invoke-method parser rule) (rule-names-vector parser))))