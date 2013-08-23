(ns sesquipedalian.named-pool
  "Defines a series of routines for operating on named pools,
  where members may be either anonymous or named."
  (:require [clojure.tools.logging :refer [debug]]))

(defn- make-name-pool [anons named] [anons named])

(defn find-key-for [m value]
  "Search the map m for a value value. If found, return the corresponding key
  in m.  Otherwise, return nil."
  (first (for [[k v] m :when (= v value)] k)))

(defn subset-of [m name-set]
  "Select the subset of the map m whose keys are in the set name-set"
  (hash-map (for [[k v] m :when (name-set k)] [k v])))

(defn new-named-pool []
  "Returns a new empty pool, with no named or anonymous members."
  (make-name-pool #{} {}))

(defn get-anonymous-members [[anon named]] anon)

(defn get-named-members [[anon named]] name)

(defn add-anonymous-member [[anon named] new-item]
  ;; TODO: check whether new-item is already present
  [(conj anon new-item) named])

(defn name-member [[anon named] name item]
  ;; TODO: handle case where name not in anon
  [(disj anon item)
   (assoc named name item)])

(defn remove-member [[anon named] former-item]
  (let [new-anon  (disj anon former-item)
        key       (find-key-for named former-item)
        new-named (dissoc named key)]
  (debug "db:" new-anon key new-named)
  [new-anon new-named]))

(defn get-named-member [[anon named] name]
  (named name))

(defn map-to-named-members [[anon named] names f]
  [anon (map f (subset-of named names))])
