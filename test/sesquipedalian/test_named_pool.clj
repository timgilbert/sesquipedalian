(ns sesquipedalian.test-named-pool
  (:require [midje.sweet :refer :all]
            [clojure.tools.logging :refer [debug]]
            [sesquipedalian.named-pool :as np]))

(def fake-socket :fake-web-socket)

(let [empty (np/new-named-pool)
      single (np/add-anonymous-member empty fake-socket)
      single-named (np/name-member single :test-name fake-socket)
      single-removed (np/remove-member single-named fake-socket)
      moar (-> (np/new-named-pool)
               (np/add-anonymous-member 5)
               (np/name-member :five 5)
               (np/add-anonymous-member 10)
               (np/name-member :ten 10)
               (np/add-anonymous-member 15)
               (np/name-member :fifteen 15))]

  (facts "About ws"

    (fact "Get anonymous returns single value"
      (count (np/get-anonymous-members single)) => 1)

    (fact "Get anonymous returns actual value"
      (np/get-anonymous-members single) => #{fake-socket})

    (fact "Naming members depletes anonymous"
      (np/get-anonymous-members single-named) => #{})

    (fact "Naming members results in proper get-named-member result"
      (np/get-named-member single-named :test-name) => fake-socket)

    (fact "Remove member clears anonymous"
      (np/get-anonymous-members single-removed) => #{})

    (fact "Remove member results in no named member"
      (np/get-named-member single-removed :test-name) => nil))

    (defn test-callback [name value & args]
      (debug "test-callback: n" name "v" value "a" args)
      (str value))

    (fact "Map works on simple case"
      (np/map-to-named-members single-named #{:test-name} test-callback)
      => {:test-name ":fake-web-socket"})

    (fact "Map works on empty"
      (np/map-to-named-members empty #{:test-name} test-callback) => {})

    (fact "Map works on multi, no users"
      (np/map-to-named-members moar #{} test-callback) => {})

    (fact "Map works on multi, one item"
      (np/map-to-named-members moar #{:five} test-callback) => {:five "5"})

    (fact "Map works on multi, all"
      (np/map-to-named-members moar #{:five :ten :fifteen} test-callback)
      => {:five "5", :ten "10", :fifteen "15"}))
