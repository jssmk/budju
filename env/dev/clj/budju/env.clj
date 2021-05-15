(ns budju.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [budju.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[budju started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[budju has shut down successfully]=-"))
   :middleware wrap-dev})
