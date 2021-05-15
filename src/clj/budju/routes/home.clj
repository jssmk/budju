(ns budju.routes.home
  (:require
   [budju.layout :as layout]
   [budju.db.core :as db]
   [clojure.java.io :as io]
   [budju.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]
   [struct.core :as st]))

(defn home-page [request]
  (layout/render request "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page [request]
  (layout/render request "about.html"))


(def user-schema
  [[:name st/required st/string]
   [:email st/required st/string
    {:message "email must look like email"
     :validate #(re-matches #".+\@.+\..+" %) }]
   [:admin st/required st/boolean]
   [:passw st/required st/string
    {:message "passwd has to be longer"
     :validate #(> (count %) 9) }]
   ])

(defn validate-user [params]
  (first (st/validate params user-schema)))


(defn new-user! [{:keys [params]}]
  (if-let [errors (validate-user params)]
    (-> (response/found "/")
        (assoc :flash (assoc params :errors errors)))
    (do
      (db/new-user!
       (assoc params :timestamp (java.util.Date.)))
      (response/found "/"))))


(defn home-routes []
  [ "" 
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/about" {:get about-page}]])

