(ns cleve.oauth
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clj-http.client :as http]
            [ring.util.codec :as codec]))

(def oauth2-params
  (edn/read-string (slurp (io/resource "auth.edn"))))

(defn authorize-uri [state]
  (str (:authorize-uri oauth2-params)
       "?"
       (http/generate-query-string {:response_type "code"
                                    :client_id     (:client-id oauth2-params)
                                    :redirect_uri  (:redirect-uri oauth2-params)
                                    :scope         (:scope oauth2-params)
                                    :state         state})))


(defn get-token [code]
  (:body (http/post (:access-token-uri oauth2-params)
                    {:form-params {:code         code
                                   :grant_type   "authorization_code"
                                   :client_id    (:client-id oauth2-params)
                                   :redirect_uri (:redirect-uri oauth2-params)}
                     :basic-auth [(:client-id oauth2-params) (:client-secret oauth2-params)]
                     :as          :json})))


(defn crest-request [token url]
  (:body (http/get url {:headers {"Authorization" (str "Bearer " token)}
                        :as :json})))

(defn verify [token]
  (crest-request token (:verify-token-uri oauth2-params)))

