(ns sesquipedalian.websockets
  (:require [clojure.tools.logging :refer [debug]]))

(defn- make-name-pool [anons named] [anons named])

(defn find-key-for [map value]
  (first (for [[k v] map :when (= v value)] k)))

(defn new-name-pool []
  "Returns a new empty client list"
  (make-name-pool #{} {}))

(defn get-anonymous-members [[anon named]] anon)

(defn add-anonymous-member [[anon named] new-item]
  [(conj anon new-item) named])

(defn name-member [[anon named] name item]
  [(disj anon item)
   (assoc named name item )])

(defn remove-member [[anon named] former-item]
  (let [new-anon  (disj anon former-item)
        key       (find-key-for named former-item)
        new-named (dissoc named key)]
  (debug "db:" new-anon key new-named)
  [new-anon new-named]))

(defn get-named-member [[anon named] name]
  (named name))

(defn map-to-named-sockets [[anon named] names f]
  [anon named])
