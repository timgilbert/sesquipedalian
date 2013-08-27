# sesquipedalian

This implementation uses the [Clojure][clojure] [http-kit][http-kit] library
on the back-end.  It's based on the approach outlined [in this blog post][blog].

## Prerequisites

You will need [Leiningen][lein] 1.7.0 or above installed.

    $ brew install leiningen

## Running

To start a web server for the application:

    $ lein run
    12:58:53 INFO  sesquipedalian.server - Live server: http://localhost:9899/
    12:58:53 INFO  sesquipedalian.server - Mock server: http://localhost:9899/?mock=1

Once the above line appears, the server is serving on http://localhost:9899/.

You can open mutliple browser tabs, enter a username, and hit "login" on
each one. From the lobby, hit "join game" to join a game - once three
users join, their browsers should automatically redirect to /game/XYZ.

## Mock Driver

If you append ?mock=1 to the URL (second link above) you'll hit the mock server
interface. In this mode, queries to the server will always product the same
results and there won't be communication between multiple clients (this is
meant to help with testing the front end).

On the websocket side, the mock interface lives at `/ws/MOCK/lobby` instead
of `/ws/lobby`. Clients connected to the mock sockets will be able to trigger
messages to the connected sockets by sending async GET/POST requests to
`/ws/MOCK/driver`. This should allow the client and server code to be worked
on more or less independently.

Details about the driver TBD.

## BUGS

Plentiful and mysterious.

## Not sure about

Alternately, session cookie with username or something?

## TODO:

Should likely rework page flow thusly:
* User arrives on site, sees landing page with login screen
* Once user logs in, redirects to lobby with chat
* User can see other users, create game, invite users in
* Users in lobby will receive messages about user joining / leaving lobby,
  winning / losing games, and chatting
** Need to work out network flow for this
* User can indicate "join random game" and be put on a waiting
  list; when enough users are in waiting list, game starts (current behavior)
* Lobby and game page are at the same URL, separated by client display

- Back-end in Mongo or whatnot
- Script to store dict.txt in that, maybe based on [SCOWL][scowl].
- Integration with NYT regi account
- Profanity filter and other tragedy of the commons considerations

## Long-term plans

- Leaderboard / scores etc
- Integration with rest of NYT site / games
- Users in lobby can initiate private games, invite users, etc
- Badges tied to comment profiles?
- More social aspects: friend lists, etc

# Data flow

Here I'll try to document the existing network traffic of the game.

Most packets back and forth are of the form `{"action": "action-name" ...}`

## Lobby page

1. User enters lobby, types username, hits "login"

2. Client opens web socket to ws://localhost:9899/ws/lobby

3. Client sends `{"action": "login", "username": the-name}`

4. Client hangs out in lobby, chatting
   * May periodically send or receive chat messages, which look like this:
     `{"action": "chat", "username": name, "text": "This is what I typed"}`
   ** User's chat messages are broadcast back to themselves
   * Also receives state-change notifications for other users

5. Eventually a client will hit "Join Game" - sends `{"action": "join"}`.

6. When server sees 3 users, it sends json to each one over the socket.

   The json looks roughly like `{"action": "game", id": 123}`, with other
   stuff in it.

7. When client gets the json packet, it constructs a URL based on the game ID
   and redirects the browser to that ID (eg, /game/123).

   *Note*: above behavior should probably change to be a js-based refresh
   of the page to show the game UI, rather than a page reload.

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

7. When time runs out, server sends `{"game": "over"}` or similar to users

   This packet will also contain the name of the winning user and the scores.
   The client of the winning user can display like fireworks or something.
   The user can return to the lobby from there.

   The server should be recording wins and losses in a persistent store of some
   kind, and there should be a high-score page.

[lein]: https://github.com/technomancy/leiningen
[blog]: http://samrat.me/blog/2013/07/clojure-websockets-with-http-kit/#comments
[scowl]: http://wordlist.sourceforge.net/
[local]: http://localhost:9899/
[http-kit]: http://http-kit.org/
[clojure]: http://clojure.org/
