(ns sesquipedalian.test-named-pool
  (:require [midje.sweet :refer :all]
            [sesquipedalian.named-pool :as ws]))

(def fake-socket :fake-web-socket)

(let [empty (ws/new-named-pool)
      single (ws/add-anonymous-member empty fake-socket)
      single-named (ws/name-member single :test-name fake-socket)
      single-removed (ws/remove-member single-named fake-socket)
      moar (-> (ws/new-named-pool)
               (ws/add-anonymous-member 5)
               (ws/name-member :five 5)
               (ws/add-anonymous-member 10)
               (ws/name-member :ten 10)
               (ws/add-anonymous-member 15)
               (ws/name-member :fifteen 15))]

  (facts "About ws"

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
