(ns cleve.layout
  (:require [hiccup.page :as p]))

(defn header [title]
  [:head
   (p/include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")
   (p/include-js "https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"
                 "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js")])

(defmacro page [title & content]
  `(p/html5 (header ~title)
            ~@content))
