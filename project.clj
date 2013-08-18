(defproject sesquipedalian "0.1.0-SNAPSHOT"
  :description "Sesquipedalian, the game you learn to spell while developing it"
  :url "http://localhost:9899/"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.2.2"]
                 [org.clojure/tools.logging "0.2.6"]
                 [ch.qos.logback/logback-classic "1.0.1"]
                 [compojure "1.1.5"]
                 [ring/ring-core "1.2.0"]
                 [ring/ring-devel "1.2.0"]
                 [ring/ring-json "0.2.0"]
                 [http-kit "2.1.8"]]
  :dev-dependencies [[lein-autodoc "0.9.0"]
                     [midje "1.5.1"]]
  :main sesquipedalian.server
  :profiles {:dev {:dependencies [[midje "1.5.1"]]}}
  ; :plugins [[speclj "2.5.0"]]
  ; :test-paths ["spec"]

  )
