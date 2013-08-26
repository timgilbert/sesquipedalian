(function($) {
  function bindEvents() {

    // this doesn't seem to work
    $('#join-game').on("keyup", function(evt) {
      console.log('aargh', $('#username').val());
      if ($('#username').val() === "") {
        disableButton('Join Game');
      } else {
        enableButton();
      }
    });

    $('#join-game').click(function(evt) {
      var username = $('#username').val();
      var socket = new WebSocket('ws://localhost:9899/ws/lobby');
      disableButton('Waiting...');

      socket.onopen = function() {
        console.log('onopen');
        enableButton();
        joinGame(socket, username);
      };
      socket.onmessage = function(evt) {
        var data = JSON.parse(evt.data);
        console.log('onmessage', data);
        redirectToGamePage(data);
      };
      socket.onclose = function() {
        console.log('onclose');
      };
      socket.onerror = function(error) {
        console.log('onerror', error);
        enableButton();
      };

      disableButton();
    });
    //disableButton('Join Game');
  }

  function enableButton() {
    $('#join-game').attr('disabled', false);
    $('#join-game').text('Join Game');
  }

  function disableButton(str) {
    $('#join-game').attr('disabled', true);
    $('#join-game').text(str);
  }

  function joinGame(socket, username) {
    console.log(username);
    var request = {'username': username};
    socket.send(JSON.stringify(request));
  }

  function redirectToGamePage(data) {
    var url = '/game/' + data.id;
    window.location.href = url;
  }

  $(function() { bindEvents(); });

})(jQuery);
