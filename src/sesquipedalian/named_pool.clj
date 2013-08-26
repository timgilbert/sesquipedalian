(ns sesquipedalian.named-pool
  "Defines a series of routines for operating on named pools,
  where members may be either anonymous or named."
  (:require [clojure.tools.logging :refer [debug]]))

;; TODO: turn this into a record type

(defn- make-name-pool [anons named] [anons named])

(defn present? [member coll]
  "Return true is member is present in (seq-like) coll"
  (not (empty? (filter #(= % member) coll))))

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

(defn get-named-members [[anon named]] named)

(defn add-anonymous-member [[anon named] new-item]
  {:pre [(not (present? new-item anon))]}   ; new-item not already in ano n
  [(conj anon new-item) named])

(defn name-member [[anon named] name item]
  {:pre [(not (named name))                       ; name does not already exist
         (present? item anon)]}              ; item in anon already
  [(disj anon item)
   (assoc named name item)])

(defn remove-member [[anon named] former-item]
  ; former-item is present in either anon or the values of named
  {:pre [(present? former-item (concat (vals named) anon))]}
  (let [new-anon  (disj anon former-item)
        key       (find-key-for named former-item)
        new-named (dissoc named key)]
  [new-anon new-named]))

(defn get-named-member [[anon named] name]
  (named name))

(defn map-to-named-members [[anon named] names f & args]
  [anon (map f (subset-of named names))])
