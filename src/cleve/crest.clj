(ns cleve.crest
  (:require [clj-http.client :as http]))

(defn resource [path]
  (str "https://crest-tq.eveonline.com" path))

(defn request [token url]
  (println "*" token url)
  (:body (http/get url {:headers {"Authorization" (str "Bearer " token)}
                        :as :json})))


(defn character-info [token id]
  (request token (resource (str "/characters/" id "/"))))


