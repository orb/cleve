(ns cleve.handler
  (:require [cleve.oauth :as oauth]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.core :as h]
            [hiccup.page :as p]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as response]))

(defn header [title]
  [:head
   (p/include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")
   (p/include-js "https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"
                 "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js")])


(defn home-login-main []
  (p/html5 (header "HOME")
           [:body.container
            [:div.well
             [:h2 "Clojure EVE"]
             [:a.btn.btn-default {:href "/login"} "Login"]]]))

(defn home-page-main [{:keys [session] :as req}]
  (let [verify (:verify session)]
    (println "* v" verify)
    (p/html5 (header "HOME")
             [:body.container
              [:div.well
               [:h2 "Clojure EVE"]
               [:div "Hello, " (h/h (:CharacterName verify))]]])))

(defn home-page [req]
  (if-let [token (get-in req [:session :oauth])]
    (home-page-main req)
    (home-login-main)))

(defn login-page []
  (response/redirect (oauth/authorize-uri "faketoken")))

(defn auth-page [code state]
  (when-let [token (oauth/get-token code)]
    (-> (response/redirect "/")
        (assoc :session {:oauth token
                         :verify (oauth/verify (:access_token token))}))))

(defroutes app-routes
  (GET "/"      [] home-page)
  (GET "/login" [] (login-page))
  (GET "/CCPLZ" [code state] (auth-page code state))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))




