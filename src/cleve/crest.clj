(ns cleve.crest
  (:require [clj-http.client :as http]))

(defn resource [path]
  (str "https://crest-tq.eveonline.com" path))

(defn request [token url]
  (println "**CREST" token)
  (println "--GET" url)
  (let [response
        (http/get url {:headers {"Authorization" (str "Bearer " token)}
                       :as :json})]
    #_(println "->" response)
    (:body response)))

(defn character-info [token id]
  (request token (resource (str "/characters/" id "/"))))



