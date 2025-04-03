(defproject clojure-demo "0.1.0-SNAPSHOT"
  :description "Prime Number Explorer"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.eclipse.jetty/jetty-server "9.4.44.v20210927"]
                 [hiccup "1.0.5"]]
  :main clojure-demo.core
  :aot :all
  :uberjar-name "clojure-demo-0.1.0-SNAPSHOT-standalone.jar") 