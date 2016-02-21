(ns cleve.login
  (:require [cleve.oauth :as oauth]
            [ring.util.response :as response]))

(defn oauth-redirect []
  (response/redirect (oauth/authorize-uri "faketoken")))

(defn oauth-callback [code state]
  (when-let [token (oauth/get-token code)]
    (-> (response/redirect "/")
        (assoc :session {:oauth token
                         :verify (oauth/verify (:access_token token))}))))
