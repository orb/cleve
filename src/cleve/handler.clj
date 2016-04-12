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

(defn error-page []
  (layout/page "CLEVE: error"
               [:body.container
                [:div.well
                 [:h2 "Clojure EVE"]
                 [:p "Sorry, it looks like the EVE proxy is sad."]
                 [:a.btn.btn-default {:href "/dologin"} "Try again?"]]]))

(defn token-from-session [session]
  (get-in session [:oauth-response :access_token]))

(defn home-page [{:keys [session] :as req}]
  (try
    (let [verify (:verify session)
          character (crest/character-info (token-from-session session)
                                          (:CharacterID verify))
          location (crest/request (token-from-session session)
                                  (get-in character [:location :href]))]
      #_(println "!!!!" character)
      (println "@" location)
      (layout/page "CLEVE: welcome"
                   [:body.container
                    [:div.well
                     [:h2 "Clojure EVE"]
                     [:a.btn.btn-default {:href "/logout"} "Logout"]]


                    [:div.row
                     [:div.col-sm-3 [:img {:src (get-in character [:portrait :256x256 :href])}]]
                     [:div.col-sm-9
                      [:h1 (h/h (:name character))]
                      [:h2 (h/h (get-in character [:corporation :name]))]
                      (if-let [system (get-in location [:solarSystem :name])]
                        [:h2 "Current Location: " (h/h system)]
                        [:h2 "Offline"])]]]))
    (catch Exception e
      (.printStackTrace e)
      (-> (response/redirect "/error")
          (assoc :session {})))))

;; ----------------------------------------


(defn wrap-require-user [handler]
  (fn [req]
    (if-let [oauth-response (get-in req [:session :oauth-response])]
      (handler req)
      (response/redirect "/login"))))

(defroutes auth-routes
  (GET "/login" [] (splash-page))
  (GET "/error" [] (error-page))
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
