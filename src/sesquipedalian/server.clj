; Based on https://github.com/http-kit/chat-websocket/blob/master/src/main.clj
(ns sesquipedalian.server
  (:require [sesquipedalian.game :as game]
            [sesquipedalian.lobby :as lobby]
            [sesquipedalian.mock :as mock]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [compojure.handler :refer [site]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.response :refer [file-response]]
            [clojure.tools.logging :refer [info debug]]
            [clojure.data.json :refer [json-str read-json]]
            [org.httpkit.server :refer [send! on-receive on-close with-channel run-server]]))

(defn broadcast-json [data]
  "For each user with name n and connection c, call (f n c args) and return a
  map from n to the function result"
  (let [json (json-str data)
        f (fn [username channel & args]
            (debug "Sending" json "to" username "ch" channel)
            (send! channel json))]
    (debug "Broadcast:" json "to" (lobby/get-connected-usernames))
    (lobby/map-to-named f)))

(defn broadcast-connection [username]
  (broadcast-json {:action "joined-lobby", :username username}))

(defn login-or-fail [channel data]
  "Called when a user has connected (via channel) but not yet logged in"
  (let [{:keys [username action]} data]
    (if (or (nil? username) (not= action "login"))
      (info "Got weird response waiting for login:" data)
      (do
        (lobby/name-channel! channel username)
        (info "User connected:" username)
        (broadcast-connection username)))))

(defn chat [channel data]
  (info "chatting" data)
  (broadcast-json {:action "chat", :username (lobby/who-owns channel)
                   :text (:text data)}))

(defn send-redirect [username channel & [[game]]]
  "Send a JSON packet down the channel which tells the user to redirect to a game page"
  (debug "redirect u:" username "g:" game)
  (send! channel (json-str {:action "game", :game game})))

(defn join-game [channel data]
  (info "joining" data)
  (when-let [users (lobby/available-players)]
    (lobby/create-new-game! users send-redirect)))

(defn lobby-dispatch [json channel]
  (let [data (read-json json)
        action (:action data)]
    (cond
      (lobby/anonymous? channel) (login-or-fail channel data)
      (= action "chat") (chat channel data)
      (= action "join") (join-game channel data)
      :else (info "discarding weird data" data))))

(defn ws-lobby-handler [waiting-function request]
  "Per-socket handler for requests to /ws/lobby"
  (with-channel request channel
    (info channel "connected")
    (lobby/add-anonymous-channel! channel)
    (on-receive channel (fn [data]
                            (waiting-function data channel)))
    (on-close channel (fn [status]
                        (info channel "closed, status" status)
                        (lobby/remove-channel! channel)))))

(defroutes all-routes
  (GET "/"         [] (file-response "resources/public/index.html"))
  (GET "/game/:id" [] (file-response "resources/public/game.html"))
  (GET "/ws/lobby" [] (partial ws-lobby-handler lobby-dispatch))

  (GET "/ws/MOCK/lobby" [] (partial ws-lobby-handler mock/lobby-dispatch))
  (GET "/ws/MOCK/game"  [] (partial ws-lobby-handler mock/lobby-dispatch))
  (route/files "" {:root "resources/public"})
  (route/not-found "<p>Page not found.</p>" ))

(defn- wrap-request-logging [handler]
  (fn [{:keys [request-method uri] :as req}]
    (let [resp (handler req)]
      (info (name request-method) (:status resp)
            (if-let [qs (:query-string req)]
              (str uri "?" qs) uri))
      resp)))

(defn -main [& args]
  (let [port 9899]
    (run-server (->
                 #'all-routes
                 site
                 wrap-reload
                 wrap-request-logging) {:port port})
    (info "server started on" (format "http://localhost:%d/" port))))
