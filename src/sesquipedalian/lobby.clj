(ns sesquipedalian.lobby
  (:require [clojure.set :refer [union]]
            [sesquipedalian.named-pool :as named-pool]
            [clojure.tools.logging :refer [info]]))

(def socket-pool (atom (named-pool/new-named-pool)))

(let [game-id (atom 0)]
  (defn next-game-id! []
    (swap! game-id inc)
    @game-id))

(def max-users-per-game 4)
(def min-users-per-game 2)

(defn add-anonymous-channel [channel]
  (swap! socket-pool named-pool/add-anonymous-member channel))

(defn name-channel [channel name]
  (swap! socket-pool named-pool/name-member name channel))

(defn remove-channel [channel]
  (swap! socket-pool named-pool/remove-member channel))

(defn available-game []
  (let [named-channels (named-pool/get-named-members @socket-pool)
        _ (println named-channels)
        potential-game-members (set (map first (take max-users-per-game named-channels)))]
    (if (< (count potential-game-members) min-users-per-game)
      nil
      potential-game-members)))

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
