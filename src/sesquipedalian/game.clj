(ns sesquipedalian.game
  (:require [clojure.set :refer [union]])
  (:gen-class))

; cf
(def every-consonant (set "BCDFGHJKLMNPQRSTVWXZ"))
(def every-vowel (set "AEIOUY"))
(def every-letter (union every-consonant every-vowel))

(def letters-per-game 15)

(defn get-letter-assortment [amount]
  (let [letters (seq every-letter)
        random (repeatedly #(rand-nth letters))]
    (take amount random)))

(defn new-game [userlist]
  {:letters (get-letter-assortment letters-per-game)})
