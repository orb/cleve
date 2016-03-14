(ns cleve.handler
  (:require [cleve.crest :as crest]
            [cleve.oauth :as oauth]
            [cleve.layout :as layout]
            [cleve.login :as login]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.core :as h]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as response]))

(defn splash-page []
  (layout/page "CLEVE: login"
               [:body.container
                [:div.well
                 [:h2 "Clojure EVE"]
                 [:a.btn.btn-default {:href "/dologin"} "Login"]]]))

(defn token-from-session [session]
  (get-in session [:oauth-response :access_token]))

(defn home-page [{:keys [session] :as req}]
  (try
    (let [verify (:verify session)
          character (crest/character-info (token-from-session session)
                                          (:CharacterID verify))]
      (layout/page "CLEVE: welcome"
                   [:body.container
                    [:div.well
                     [:h2 "Clojure EVE"]
                     [:a.btn.btn-default {:href "/logout"} "Logout"]]]

                   [:h1 (h/h (:name character))]
                   [:h2 (h/h (get-in [:corporation :name] character))]
                   [:img {:src (get-in character [:portrait :128x128 :href])}]))
    (catch Exception e
      (.printStackTrace e)
      (-> (response/redirect "/login")
          (assoc :session {})))))

;; ----------------------------------------


(defn wrap-require-user [handler]
  (fn [req]
    (if-let [oauth-response (get-in req [:session :oauth-response])]
      (handler req)
      (response/redirect "/login"))))

(defroutes auth-routes
  (GET "/login" [] (splash-page))
  (GET "/logout" [] (login/logout))
  (GET "/dologin" [] (login/oauth-redirect))
  (GET "/CCPLZ" [code state :as {session :session}]
       (login/oauth-callback session code state)))

(defroutes main-routes
  (GET "/"      [] home-page)
  (route/not-found "Not Found"))

(defroutes app-routes
  auth-routes
  (wrap-require-user main-routes))

(def app
  (wrap-defaults app-routes
                 site-defaults))
