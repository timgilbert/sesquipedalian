(ns sesquipedalian.test-lobby
  (:require [clojure.tools.logging :refer [debug]]
            [midje.sweet :refer :all]
            [sesquipedalian.lobby :as lobby]))

(facts "about lobby"
  (fact "adding and naming connections"
    (lobby/reset-lobby!)
    (lobby/count-anonymous-users) => 0
    (lobby/get-connected-usernames) => #{}
    (lobby/available-game?) => false

    (lobby/add-anonymous-channel! :fake-alice-socket)
    (lobby/add-anonymous-channel! :fake-bob-socket)
    (lobby/add-anonymous-channel! :fake-charles-socket)

    (lobby/count-anonymous-users) => 3
    (lobby/get-connected-usernames) => #{}
    (lobby/available-game?) => false

    (lobby/name-channel! :fake-alice-socket "Alice")
    (lobby/count-anonymous-users) => 2
    (lobby/get-connected-usernames) => #{"Alice"}

    (lobby/name-channel! :fake-bob-socket "Bob")
    (lobby/count-anonymous-users) => 1
    (lobby/get-connected-usernames) => #{"Alice" "Bob"}
    (lobby/available-game?) => false

    (lobby/name-channel! :fake-charles-socket "Chuck")
    (lobby/count-anonymous-users) => 0
    (lobby/get-connected-usernames) => #{"Alice" "Bob" "Chuck"}
    (lobby/available-game?) => true
    (lobby/available-players) => #{"Alice" "Bob" "Chuck"})

  (defn  test-request [& args]
    (debug args)
    (str args))

  (fact "constructing games"
    (lobby/reset-lobby!)

    (lobby/add-anonymous-channel! :fake-alice-socket)
    (lobby/add-anonymous-channel! :fake-bob-socket)
    (lobby/add-anonymous-channel! :fake-charles-socket)
    (lobby/name-channel! :fake-alice-socket "Alice")
    (lobby/name-channel! :fake-bob-socket "Bob")
    (lobby/name-channel! :fake-charles-socket "Chuck")

    (lobby/available-players) => #{"Alice" "Bob" "Chuck"}

    (let [players (lobby/available-players)]
      (lobby/create-new-game! players test-request))
))
