(ns cleve.handler
  (:require [cleve.oauth :as oauth]
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

(defn home-page [{:keys [session] :as req}]
  (let [verify (:verify session)]
    (layout/page "CLEVE: welcome"
                 [:body.container
                  [:div.well
                   [:h2 "Clojure EVE"]
                   [:div "Hello, " (h/h (:CharacterName verify))]]])))

;; ----------------------------------------


(defn wrap-require-user [handler]
  (fn [req]
    (if-let [token (get-in req [:session :oauth])]
      (handler req)
      (response/redirect "/login"))))

(defroutes auth-routes
  (GET "/login" [] (splash-page))
  (GET "/dologin" [] (login/oauth-redirect))
  (GET "/CCPLZ" [code state] (login/oauth-callback code state)))

(defroutes main-routes
  (GET "/"      [] home-page)
  (route/not-found "Not Found"))

(defroutes app-routes
  auth-routes
  (wrap-require-user main-routes))

(def app
  (wrap-defaults app-routes
                 site-defaults))




