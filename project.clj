(defproject sesquipedalian "0.1.0-SNAPSHOT"
  :description "Sesquipedalian, the game you learn to spell while developing it"
  :url "http://localhost:8080/"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [ring/ring-core "1.2.0"]
                 [ring/ring-devel "1.2.0"]
                 [ring/ring-json "0.2.0"]
                 [ring-cors "0.1.0"]
                 [http-kit "2.1.8"]
                 [cheshire "5.2.0"]]
  :main sesquipedalian.core)
