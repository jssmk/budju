(ns budju.test.db.core
  (:require
   [budju.db.core :refer [*db*] :as db]
   [luminus-migrations.core :as migrations]
   [clojure.test :refer :all]
   [next.jdbc :as jdbc]
   [budju.config :refer [env]]
   [mount.core :as mount]
   [talltale.core :as tall]))

#_(use-fixtures
  :once
  (fn [f]
    (mount/start
     #'budju.config/env
     #'budju.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(use-fixtures
  :once
  (fn [f]
    (mount/start
     #'budju.config/env
     #'budju.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))



(defn create-new-user [t-conn & {:keys [user-custom]}]
  ;(if (not (map? user-custom)) (println "user-custom not a map"))
  (try
    (let [user-identity (-> (db/new-user! t-conn {} {:connection t-conn})
                            (select-keys [:userid]))
          user-data (assoc (tall/person) :admin false)]
      
      (db/new-user-data!
        t-conn
        (merge user-data user-identity user-custom)
        {:connection t-conn})
      
      user-identity)
    
  (catch Exception ex
    (println "new user insert error"))))


(defn create-new-plan [t-conn user-identity & {:keys [plan-custom]}]
  (try
    (let [plan-identity (-> (db/new-plan! t-conn user-identity {:connection t-conn})
                            (select-keys [:planid]))
          plan-data   {:title (str (tall/color) " " (tall/animal))
                       :status 0
                       :description (tall/lorem-ipsum)}]
        
      (db/new-plan-data!
         t-conn
         (merge plan-data plan-identity plan-custom)
         {:connection t-conn})
      
      plan-identity)
      
  (catch Exception ex
    (println "new plan insert error"))))

(defn create-new-comment [t-conn user-identity plan-identity & {:keys [comm-custom]}]
  (try
    (let [user-plan-identity  (merge user-identity plan-identity)
          comm-identity       (-> (db/new-comment! t-conn user-plan-identity {:connection t-conn})
                                  (select-keys [:commid]))
          comm-data   {:content (str (tall/quality) " " (tall/shape))}]
      (db/new-comment-data!
        t-conn
        (merge comm-data comm-identity comm-custom)
        {:connection t-conn})
      
       comm-identity)
  
  (catch Exception ex
    (println "new comment insert error"))))


(defn create-new-vote [t-conn user-identity plan-identity & {:keys [vote-custom]}]
  (try
    (let [user-plan-identity  (merge user-identity plan-identity)
          vote-identity       (-> (db/new-vote! t-conn user-plan-identity {:connection t-conn})
                                  (select-keys [:voteid]))
          vote-data   {:type (rand-nth ["positive" "negative" "neutral"])}]
      (db/new-vote-data!
        t-conn
        (merge vote-data vote-identity vote-custom)
        {:connection t-conn})
      
       vote-identity)
  
  (catch Exception ex
    (println "new vote insert error"))))





;(defn user-with-plan  [& {:keys [user-custom plan-custom]}]
;  (jdbc/with-transaction [t-conn *db*]
;    (let [user-identity (-> (db/new-user! t-conn {} {:connection t-conn})
;                            (select-keys [:userid]))]
;      (let [user-data     (assoc (tall/person) :admin false)]
;        (is (= 1 (db/new-user-data!
;                  t-conn
;                  (merge user-data user-identity user-custom)
;                  {:connection t-conn}))))
;      (let [plan-identity (-> (db/new-plan! t-conn user-identity {:connection t-conn})
;                              (select-keys [:planid]))]
;        (let [plan-data     {:title (str (tall/color) " " (tall/animal))
;                             :status 0
;                             :description (tall/lorem-ipsum)}]
;          (is (= 1 (db/new-plan-data!
;                    t-conn
;                    (merge plan-data plan-identity plan-custom)
;                    {:connection t-conn})))))
;)))


; ---- basics


(deftest user-test-1
  (jdbc/with-transaction [t-conn *db*]
    (let [new-user (create-new-user t-conn)]
      (is (= 1 (count new-user)))
      (is (some? (:userid new-user)))
      (is (uuid? (:userid new-user)))
      )))

(deftest user-test-2
  (jdbc/with-transaction [t-conn *db*]
    (let [new-user (create-new-user t-conn)
          new-plan (create-new-plan t-conn new-user)]
      (is (= 1 (count new-plan)))
      (is (some? (:planid new-plan)))
      (is (uuid? (:planid new-plan)))
  )))

(deftest user-test-3
  (jdbc/with-transaction [t-conn *db*]
    (let [new-user (create-new-user t-conn)
          new-plan (create-new-plan t-conn new-user)
          new-comment (create-new-comment t-conn new-user new-plan)]
      (is (= 1 (count new-comment)))
      (is (some? (:commid new-comment)))
      (is (int? (:commid new-comment)))
  )))

(deftest user-test-4
  (jdbc/with-transaction [t-conn *db*]
    (let [new-user (create-new-user t-conn)
          new-plan (create-new-plan t-conn new-user)
          new-vote (create-new-vote t-conn new-user new-plan)]
      (println (pr-str new-vote))
      (is (= 1 (count new-vote)))
      (is (some? (:voteid new-vote)))
      (is (int? (:voteid new-vote)))
  )))


;------ interaction


;-- create some user data for later use
(deftest interaction-init
  (jdbc/with-transaction [t-conn *db*]
    (def my-user-1 (create-new-user t-conn))
    (def my-user-2 (create-new-user t-conn))
    (def my-admin-user (create-new-user t-conn :user-custom {:admin true}))
    (def my-would-be-deleted-later-user (create-new-user t-conn)))
  (is (= 1 (count my-user-1) (count my-user-2) (count my-admin-user) (count my-would-be-deleted-later-user))))

;-- multiple user actions in a same plan
(deftest interaction-1
  (jdbc/with-transaction [t-conn *db*]
    (let [new-plan-1 (create-new-plan t-conn my-user-1 :plan-custom {:title "user-1 planning something"})
          new-comment-2 (create-new-comment t-conn my-user-2 new-plan-1 :comm-custom {:content "user-2 commenting"})
          new-comment-1 (create-new-comment t-conn my-user-1 new-plan-1 :comm-custom {:content "user-1 replying"})
          new-vote-1 (create-new-vote t-conn my-user-1 new-plan-1 :vote-custom {:type "pos"})
          new-vote-2 (create-new-vote t-conn my-user-2 new-plan-1 :vote-custom {:type "pos"})]

      (let [the-plan-1 (db/get-plan-details t-conn new-plan-1)
            the-comments (db/get-plan-comments t-conn new-plan-1)
            the-votes (db/get-plan-comments t-conn new-plan-1)]
        (println (pr-str the-plan-1))
        (println (pr-str the-comments))
        (is (= "user-1 planning something" (:title the-plan-1)))
        (is (= 2 (count the-comments)))
        (is (= 2 (count the-votes))))
      )))


;(deftest user-with-plan-test-1
;  (user-with-plan))
;(deftest user-with-plan-test-2
;  (user-with-plan :plan-custom {:description "Custom text example"}))
;(deftest user-with-plan-test-3
;  (user-with-plan :user-custom {:admin true :username (str "admin-user-" (rand-int 1000))} :plan-custom {:title "the admin's plan"}))
;