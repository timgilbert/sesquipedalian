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
users join the browser should redirect each user to /game/XYZ.

## BUGS

Currently only the last user is redirected to /game/XYZ.

## Not sure about

Should redirect from lobby to game be a form post with username in it?

Alternately, session cookie with username or something?

## TODO:

- Back-end in Mongo or whatnot
- Script to store dict.txt in that, maybe based on [SCOWL][scowl].

# Data flow

Here I'll try to document the network traffic of the game.

## Lobby page

1. User enters lobby, types username, hits "join"

2. Client opens web socket to ws://localhost:9899/ws/lobby

3. Server hangs out waiting for 3 users to join (keeping sockets open)

4. When server sees 3 users, it sends json to each one over the socket.

   The json looks roughtly like `{"id": 123}`, maybe with other stuff in it.
   Specifically, down the road it might have some kind of token generated
   by the server or a similar authentication method.

5. When client gets the json packet, it constructs a URL based ont he game ID
   and redirects the browser to that ID (eg, /game/123).

## Game page

1. Client lands on game page.

   Client will know its username, maybe through a session cookie or url param.

2. Client opens web socket to server: ws://localhost:9899/ws/game/123.  On
   connect, it sends JSON down the socket, something like `{"user": "timg"}`.

3. Server hangs out until it has an open connection from all three users
   it thinks will be in game 123.  (NB, users can quit as well.)

   (Past the prototype the server should have more serious auth in here)

4. When all contestants are connected, server sends out a "ready" JSON packet to
   each client.  The ready packet contains all information about the game
   configuration, including duration in seconds and the random letters.

5. Client displays the letters to the user and begins a countdown timer.

6. Each user may enter guesses until the timer runs out.  When a user enters a
   guess:

   1. They send a packet down the socket, something like `{"guess": "compute"}`

   2. The server checks to see whether that guess is valid (in the dictionary).

   3. If the guess is invalid, server sends back a response saying so.

      NB: possibly the user should be prevented from guessing more words for a
      short time as a disincentive to just mashing letters as a strategy

   4. If the guess is valid, the server checks to see whether it's the longest
      guess made so far.

   5. If so, sends a packet to all connected users like `{"winning": "timg", "word": "computer"}`

   6. If not, sends back `{"error": "'compute' is not longer than 'computers'"}`


[lein]: https://github.com/technomancy/leiningen
[blog]: http://samrat.me/blog/2013/07/clojure-websockets-with-http-kit/#comments
[scowl]: http://wordlist.sourceforge.net/
[local]: http://localhost:9899/
