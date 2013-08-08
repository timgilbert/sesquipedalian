(defproject sesquipedalian "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [aleph "0.3.0-rc2"]
                 [lamina "0.5.0-rc4"]]
  ;:plugins [[lein-ring "0.8.5"]]
  ;:ring {:handler sesquipedalian.handler/app}
  ;:profiles
  ;{:dev {:dependencies [[ring-mock "0.1.5"]]}}
  :main sesquipedalian.core)
