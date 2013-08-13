(ns sesquipedalian.lobby
  (:require [clojure.set :refer [union]]
            [clojure.tools.logging :refer [info]]))

(def max-users-per-game 4)
(def min-users-per-game 3)

(let [game-id (atom 0)]
  (defn next-game-id! []
    (swap! game-id inc)
    @game-id))

(defn make-new-game [userlist]
   {:id (next-game-id!) :userlist userlist})

(defn get-complete-game! [waiting-room]
  (let [users-in-game (take max-users-per-game @waiting-room)]
    (if (< (count @waiting-room) min-users-per-game)
      nil
      (do
        (info "Creating game: " users-in-game)
        (reset! waiting-room [])        ;; BUG if (count @waiting-room) > max-users
        (make-new-game users-in-game)))))

(let [waiting-room (atom [])]
  (defn user-ready [{username :username}]
    (swap! waiting-room #(conj % username))
    (info "ready: " username)
    (get-complete-game! waiting-room)))

; (defn new-game [userlist]
;   {:letters (get-letter-assortment letters-per-game)})
