(function($) {
  function bindEvents() {
    var socket = new WebSocket('ws://localhost:9899/ws-lobby');
    socket.onopen = function() {
      console.log('onopen');
      enableButton();
    };
    socket.onmessage = function(evt) {
      console.log('onmessage', evt);
    };
    socket.onclose = function() {
      console.log('onclose');
    };
    socket.onerror = function(error) {
      console.log('onerror', error);
      enableButton();
    };

    $('#join-game').on('click', function(evt) {
      var username = $('#username').val();
      disableButton();
      joinGame(socket, username);
    });
  }

  function enableButton() {
    $('#join-game').attr('disabled', false);
    $('#join-game').text('Join Game');
  }

  function disableButton() {
    $('#join-game').attr('disabled', true);
    $('#join-game').text('Waiting...');
  }

  function joinGame(socket, username) {
    console.log(username);
    var request = {'username': username};
    socket.send(JSON.stringify(request));
  }

  disableButton();
  bindEvents();

})(jQuery);