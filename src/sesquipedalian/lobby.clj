(ns sesquipedalian.lobby
  (:require [clojure.set :refer [union]]
            [clojure.data.json :refer [json-str read-json]]
            [sesquipedalian.named-pool :as named-pool]
            [sesquipedalian.game :as game]
            [clojure.tools.logging :refer [info]]
            [org.httpkit.server :refer [send!]]))

(def socket-pool (atom (named-pool/new-named-pool)))
(def active-games (atom {}))
(def game-ids (atom 0))

(defn next-game-id! []
  (swap! game-ids inc))

(defn get-game [id]
  (get @active-games id))

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

(defn anonymous? [channel]
  "Return true is this channel has not yet been named"
  (named-pool/anonymous? @socket-pool channel))

(defn count-anonymous-users []
  (count (named-pool/get-anonymous-members @socket-pool)))

(defn get-connected-usernames []
  (set (keys (named-pool/get-named-members @socket-pool))))

;; This is a little too coupled to http-kit and json for my liking
(defn map-to-named [f & args]
  "For each user with name n and connection c, call (f n c args) and return a
  map from n to the function result"
  (named-pool/map-to-named-members @socket-pool (get-connected-usernames) f args))

(defn create-new-game! [userlist connect-fn]
  "Create a new game (with a newly-generated ID) containing the users from userlist.
  Call connect-fn with each [username channel] pair."
  (let [game-id (next-game-id!)
        new-game (game/create-game game-id userlist)]
    (swap! active-games assoc (:id new-game) new-game)
    (info "Created game" game-id "with members" (reduce #(str %1 ", " %2) userlist))
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

