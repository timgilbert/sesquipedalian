(ns sesquipedalian.t-websockets
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [sesquipedalian.websockets :as ws]))

(def fake-socket :fake-web-socket)

(facts "About ws"
  (let [empty (ws/new-name-pool)
        single (ws/add-anonymous-member empty fake-socket)
        single-named (ws/name-member single :test-name fake-socket)
        single-removed (ws/remove-member single-named fake-socket)]
    (fact "Get anonymous returns single value"
      (count (ws/get-anonymous-members single)) => 1)
    (fact "Get anonymous returns actual value"
      (ws/get-anonymous-members single) => #{fake-socket})
    (fact "Naming members depletes anonymous"
      (ws/get-anonymous-members single-named) => #{})
    (fact "Naming members results in proper get-named-member result"
      (ws/get-named-member single-named :test-name) => fake-socket)
    (fact "Remove member clears anonymous"
      (ws/get-anonymous-members single-removed) => #{})
    (fact "Remove member results in no named member"
      (ws/get-named-member single-removed :test-name) => nil)))