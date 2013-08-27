(ns sesquipedalian.mock
  "Routines to generate mock responses for client testing"
  (:require [sesquipedalian.lobby :as lobby]
            [sesquipedalian.game :as game]
            [clojure.set :refer [union]]
            [clojure.tools.logging :refer [info debug]]
            [clojure.data.json :refer [json-str read-json]]
            [org.httpkit.server :refer [send! with-channel on-receive on-close]]))

(defn respond [channel data]
  (let [json (json-str data)]
    (debug "Mock Response:" json)
    (send! channel json)))

(defn login [channel]
  (respond {:action "joined-lobby", :username "Bob"}))

(defn join-game [channel]
  "Called when a user indicates they are waiting for a game"
  (respond {:id 321, :timeout 60,
            :letters ["C" "O" "M" "P" "U" "T" "E" "R"]
            :userlist ["Alice" "Bob" "Charles"] }))

(defn chat [channel]
  "Called when a user indicates they are waiting for a game"
  (respond {:action "chat" :username "Bob"}))

(defn lobby-dispatch [json channel]
  (let [data (read-json json)
        {action :action} data]
    (debug "Mock dispatch:" action data)
    (case action
      "join"  (respond channel {:action "game", :game {:id 321, :timeout 60,
                                :letters ["C" "O" "M" "P" "U" "T" "E" "R"]
                                :userlist ["Alice" "Bob" "Charles"]}})
      "login" (respond channel {:action "joined-lobby", :username "Bob"})
      "chat"  (respond channel {:action "chat", :username "Bob",
                                :text "Mock chatting!"}))))

