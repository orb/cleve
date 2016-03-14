(ns cleve.login
  (:require [cleve.oauth :as oauth]
            [ring.util.response :as response]))

(defn make-state []
  (str (java.util.UUID/randomUUID)))


(defn oauth-redirect []
  (let [state (make-state)]
    (-> (response/redirect (oauth/authorize-uri state))
        (assoc-in [:session :oauth-state] state))))

(defn logout []
  (-> (response/redirect "/")
      (assoc :session {})))

(defn oauth-callback [{:keys [oauth-state]} code state]
  (when (= oauth-state state)
    (when-let [auth-response (oauth/auth-request code)]
      (-> (response/redirect "/")
          (assoc :session {:oauth-response auth-response
                           :verify (oauth/verify (:access_token auth-response))})))))
