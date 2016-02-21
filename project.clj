(defproject cleve "0.1.0-SNAPSHOT"
  :description "Clojure EVE test"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]

                 [compojure "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [hiccup "1.0.5"]

                 [clj-http "2.1.0"]
                 [cheshire "5.5.0"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler cleve.handler/app
         :nrepl {:start? true}}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]
         :source-paths ["dev"]}})
