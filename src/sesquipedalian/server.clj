; Based on https://github.com/http-kit/chat-websocket/blob/master/src/main.clj
(ns sesquipedalian.server
  (:require [sesquipedalian.game :as game]
            [sesquipedalian.lobby :as lobby]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [compojure.handler :refer [site]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.response :refer [file-response]]
            [clojure.tools.logging :refer [info]]
            [clojure.data.json :refer [json-str read-json]]
            [org.httpkit.server :refer [send! on-receive on-close with-channel run-server]]))

(defn- now [] (quot (System/currentTimeMillis) 1000))

(def clients (atom {}))                 ; a hub, a map of client => sequence number

(let [max-id (atom 0)]
  (defn next-id []
    (swap! max-id inc)))

(defonce all-msgs (ref [{:id (next-id),            ; all message, in a list
                         :time (now)
                         :msg "this is a live chatroom, have fun",
                         :author "system"}]))

(defn mesg-received [msg]
  (let [data (read-json msg)
        game (lobby/user-ready data)]
    (info "data: " data)
    (info "game: " game)
    (when (:id data)
      (let [data (merge data {:time (now) :id (next-id)})]
        (dosync
         (let [all-msgs* (conj @all-msgs data)
               total (count all-msgs*)]
           (if (> total 100)
             (ref-set all-msgs (vec (drop (- total 100) all-msgs*)))
             (ref-set all-msgs all-msgs*))))))
    (doseq [client (keys @clients)]
      ;; send all, client will filter them
      (send! client (json-str @all-msgs)))))

(defn ws-lobby-handler [request]
  (let [new-game (game/new-game "xyzzy")]
    (info "ws-lobby-handler" new-game)
    (with-channel request channel
      (info channel "connected")
      (swap! clients assoc channel true)
      (on-receive channel mesg-received)
      (on-close channel (fn [status]
                          (swap! clients dissoc channel)
                          (info channel "closed, status" status))))))

(defroutes all-routes
  (GET "/"         [] (file-response "resources/public/index.html"))
  (GET "/game/:id" [] (file-response "resources/public/game.html"))
  (GET "/ws/lobby" [] ws-lobby-handler)
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
    ;(run-server (-> #'all-routes site wrap-request-logging) {:port 9899})
    ;(run-server (-> (wrap-reload '(site #'all-routes))
    ;                wrap-request-logging)
    ;            {:port 9899})
    (info "server started on" "" port)))
