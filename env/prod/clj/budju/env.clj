(ns budju.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[budju started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[budju has shut down successfully]=-"))
   :middleware identity})
