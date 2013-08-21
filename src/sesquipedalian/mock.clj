(ns sesquipedalian.mock
  "Routines to generate mock responses for client testing"
  (:require [clojure.set :refer [union]]
            [clojure.tools.logging :refer [info debug]]
            [clojure.data.json :refer [json-str read-json]]
            [org.httpkit.server :refer [send!]]))

;; TODO macros or some abstraction

(defn lobby-waiting [msg channel]
  "Called when a user indicates they are waiting for a game"
  (let [request (read-json msg)
        response (json-str {:id 321})]
    (debug "recieved:" request ", sending:" response)
    (send! channel response)))

(defn game-join [msg channel]
  "Called when a user indicates they are waiting for a game"
  (let [request (read-json msg)
        response (json-str {:id 321, :timeout 60,
                            :letters ["C" "O" "M" "P" "U" "T" "E" "R"]
                            :userlist ["alice" "bob" "charles"] })]
    (debug "recieved:" request ", sending:" response)
    (send! channel response)))

