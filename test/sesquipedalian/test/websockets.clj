(ns sesquipedalian.test.websockets
  (:require [clojure.test :refer :all]
            [sesquipedalian.websockets :as ws]))

(def fake-socket :fake-web-socket)

(deftest add-anon
  (let [empty (ws/new-name-pool)
        single (ws/add-anonymous-member empty fake-socket)
        single-named (ws/name-member single :test-name fake-socket)
        single-removed (ws/remove-member single-named fake-socket)]
    (testing "Get anonymous"
      (is (= (count (ws/get-anonymous-members single)) 1))
      (is (= (ws/get-anonymous-members single) #{fake-socket})))
    (testing "Naming members depletes anonymous"
      (is (= (ws/get-anonymous-members single-named) #{})))
    (testing "Naming members results in proper get-named-member result"
      (is (= (ws/get-named-member single-named :test-name) fake-socket)))
    (testing "Remove member clears anonymous"
      (is (= (ws/get-anonymous-members single-removed) #{})))
    (testing "Remove member results in no named member"
      (is (= (ws/get-named-member single-removed :test-name) nil)))
))
