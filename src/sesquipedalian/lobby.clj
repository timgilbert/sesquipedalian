(ns sesquipedalian.lobby
  (:require [clojure.set :refer [union]]
            [sesquipedalian.named-pool :as named-pool]
            [sesquipedalian.game :as game]
            [clojure.tools.logging :refer [info]]))

(def socket-pool (atom (named-pool/new-named-pool)))
(def active-games (atom {}))
(def game-ids (atom 0))

(defn next-game-id! []
  (swap! game-ids inc))

(def max-users-per-game 4)
(def min-users-per-game 3)

(defn reset-lobby! []
  (reset! socket-pool (named-pool/new-named-pool))
  (reset! active-games {})
  (reset! game-ids 0))

(defn add-anonymous-channel! [channel]
  (swap! socket-pool named-pool/add-anonymous-member channel))

(defn name-channel! [channel name]
  (swap! socket-pool named-pool/name-member name channel))

(defn remove-channel! [channel]
  (swap! socket-pool named-pool/remove-member channel))

(defn count-anonymous-users []
  (count (named-pool/get-anonymous-members @socket-pool)))

(defn get-connected-usernames []
  (set (keys (named-pool/get-named-members @socket-pool))))

(defn create-new-game! [userlist connect-fn]
  "Create a new game (with a newly-generated ID) containing the users from userlist.
  Call connect-fn with each [username channel] pair."
  (let [game-id (next-game-id!)
        new-game (game/create-game game-id userlist)]
    (swap! active-games assoc (:id new-game) new-game)
    (named-pool/map-to-named-members @socket-pool userlist connect-fn new-game)))

(defn available-players []
  (let [named-channels (named-pool/get-named-members @socket-pool)
        potential-players (set (map first (take max-users-per-game named-channels)))]
    (if (< (count potential-players) min-users-per-game)
      nil
      potential-players)))

(defn available-game? []
  "Return true if there are enough connected users to form a game"
  (not (nil? (available-players))))

