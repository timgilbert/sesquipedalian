(function($) {
  function bindEvents() {

    $('#login').click(function(evt) {
      var username = $('#username').val();
      var socket = new WebSocket('ws://localhost:9899/ws/MOCK/lobby');
      //var socket = new WebSocket('ws://localhost:9899/ws/lobby');
      disableLogin('Waiting for login...');

      socket.onopen = function() {
        console.log('onopen');
        enableLobby();
        login(socket, username);
      };
      socket.onmessage = function(evt) {
        var data = JSON.parse(evt.data);
        handleMessage(data);
      };
      socket.onclose = function() {
        console.log('onclose');
      };
      socket.onerror = function(error) {
        console.log('onerror', error);
        enableButton();
      };

      // Doing the binding down here is not awesome but at least doesn't
      // leak the socket out of this scope
      $('#chat').click(function(evt) {
        chat(socket, $('#chat-text').val());
      });
      $('#join').click(function(evt) {
        joinGame(socket);
      });
    });
  }

  function handleMessage(data) {
    // React to an incoming JSON packet on the web socket
    // Surely there is a better way to do this than this 1980s-era dispatch routine
    console.debug("handleMessage:", data);
    switch (data.action) {
      case 'joined-lobby':
        appendEvent("User " + data.username + " joined the room.");
      break;
      case 'chat':
        appendEvent(data.username + ": " + data.message);
      break;
      case 'game':
        redirectToGamePage(data);
      break;
    }
  }

  function appendEvent(text) {
    var $message = $("#templates .message").clone().text(text);
    $('#event-log').append($message);
  }

  function notifyJoined(username) {
    appendEvent("User " + username + " joined the room.");
  }

  function disableLogin(str) {
    $('#login-section').hide();
  }

  function enableLobby() {
    $('#events-section').show();
    $('#userlist-section').show();
  }

  function chat(socket, text) {
    console.debug('Chatting:', text);
    socket.send(JSON.stringify({'action': 'chat', 'text': text}));
  }

  function joinGame(socket) {
    console.debug('Joining');
    socket.send(JSON.stringify({'action': 'join'}));
  }

  function login(socket, username) {
    console.log(username);
    var request = {'action': 'login', 'username': username};
    socket.send(JSON.stringify(request));
  }

  function redirectToGamePage(data) {
    var url = '/game/' + data.id;
    console.log('redir:', url, data);
    $('#event-log').append('<a href="' + url + '">REDIRECT TO GAME PAGE</a>');
    //window.location.href = url;
  }

  $(function() { bindEvents(); });

})(jQuery);
