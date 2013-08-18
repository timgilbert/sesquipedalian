(ns sesquipedalian.websockets
  (:gen-class))

(defn- make-name-pool [anons named] [anons named])

(defn new-name-pool []
  "Returns a new empty client list"
  (make-name-pool #{} {}))

(defn get-anonymous-members [[anon named]] anon)

(defn add-anonymous-member [[anon named] new-item]
  [(conj anon new-item) named])

(defn name-member [[anon named] item name]
  [anon named])

(defn remove-member [[anon named] former-item]
  [anon named])

(defn get-named-member [[anon named] name]
  (named name))

(defn map-to-named-sockets [[anon named] names f]
  [anon named])
