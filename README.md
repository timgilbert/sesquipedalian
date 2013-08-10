# sesquipedalian

This implementation uses clojure's http-kit library on the back-end.  It's based
on the approach outlined [in this blog post][blog]

## Prerequisites

You will need [Leiningen][lein] 1.7.0 or above installed.

    $ brew install lein

## Running

To start a web server for the application:

    $ lein run
    #<server$run_server$stop_server__602 org.httpkit.server$run_server$stop_server__602@6f967348>

Once the above line appears, the server is serving on http://localhost:8080/.

## TODO:

- Back-end in Mongo or whatnot
- Script to store dict.txt in that, maybe based on [SCOWL][scowl].

[lein]: https://github.com/technomancy/leiningen
[blog]: http://samrat.me/blog/2013/07/clojure-websockets-with-http-kit/#comments
[scowl]: http://wordlist.sourceforge.net/
