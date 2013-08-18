(ns sesquipedalian.websockets
  (:require [clojure.tools.logging :refer [debug]]))

(defn- make-name-pool [anons named] [anons named])

(defn find-key-for [map value]
  (for [[k v] map :when (= v value)] k))

(defn new-name-pool []
  "Returns a new empty client list"
  (make-name-pool #{} {}))

(defn get-anonymous-members [[anon named]] anon)

(defn add-anonymous-member [[anon named] new-item]
  [(conj anon new-item) named])

(defn name-member [[anon named] item name]
  [(disj anon item)
   (assoc named name item )])

(defn remove-member [[anon named] former-item]
  (debug "removing" former-item)
  [(disj anon former-item)
   (dissoc named (find-key-for named former-item))])

(defn get-named-member [[anon named] name]
  (named name))

(defn map-to-named-sockets [[anon named] names f]
  [anon named])
