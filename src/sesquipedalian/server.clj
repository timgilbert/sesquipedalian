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

;; TODO
; keep username -> socket map of clients
; when game appears, send it to all users

(def clients (atom {}))                 ; a hub, a map of client => sequence number

(defn lobby-waiting [msg channel]
  "Called when a user indicates they are waiting for a game"
  (let [data (read-json msg)
        username (:username data)
        game (lobby/user-ready data)]
    (debug "data:" data "game:" game)
    (when (:id game)
      (debug "Transfering to game" (:id game))
      (send! channel (json-str game)))))

(defn ws-lobby-handler [waiting-function request]
  "Per-socket handler for requests to /ws/lobby"
  (with-channel request channel
    (info channel "connected")
    (lobby/add-anonymous-channel channel)
    (on-receive channel (fn [data]
                            (waiting-function data channel)))
    (on-close channel (fn [status]
                        (info channel "closed, status" status)
                        (lobby/remove-channel channel channel)))))

(defroutes all-routes
  (GET "/"         [] (file-response "resources/public/index.html"))
  (GET "/game/:id" [] (file-response "resources/public/game.html"))
  (GET "/ws/lobby" [] ws-lobby-handler lobby-waiting)

  (GET "/ws/MOCK/lobby" [] (partial ws-lobby-handler mock/lobby-waiting))
  (GET "/ws/MOCK/game"  [] (partial ws-lobby-handler mock/game-join))
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

