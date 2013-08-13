# sesquipedalian

This implementation uses clojure's http-kit library on the back-end.  It's based
on the approach outlined [in this blog post][blog]

## Prerequisites

You will need [Leiningen][lein] 1.7.0 or above installed.

    $ brew install lein

## Running

To start a web server for the application:

    $ lein run
    17:09:46 INFO  sesquipedalian.server - server started on  9899

Once the above line appears, the server is serving on [http://localhost:9899/][local].
You can open mutliple browser tabs, enter a username, and hit "join game" - once three
users join the browser should redirect each user to /game/XYZ

## BUGS

Currently you won't be auto-redirected to /game/235 once three users join.

## TODO:

- Back-end in Mongo or whatnot
- Script to store dict.txt in that, maybe based on [SCOWL][scowl].

[lein]: https://github.com/technomancy/leiningen
[blog]: http://samrat.me/blog/2013/07/clojure-websockets-with-http-kit/#comments
[scowl]: http://wordlist.sourceforge.net/
[local]: http://localhost:9899/
