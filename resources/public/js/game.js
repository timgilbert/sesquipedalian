(function($) {

  function getUsername() {
    return "Diana";
  }
  function sendHello(socket, username) {
    socket.send(JSON.stringify({"username": username}));
  }
  function beginGame(socket, data) {
    console.log("beginGame", data);
  }
  function submitGuess(socket, guess) {
    socket.send(JSON.stringify({"guess": guess}))
  }

  $(function() {
    var socket = new WebSocket('ws://localhost:9899/ws/MOCK/game');

    socket.onopen = function() {
      console.log('onopen');
      sendHello(socket, getUsername());
    };
    socket.onmessage = function(evt) {
      var data = JSON.parse(evt.data);
      console.log('onmessage', data);
      beginGame(data);
    };
    socket.onclose = function() {
      console.log('onclose');
    };
    socket.onerror = function(error) {
      console.log('onerror', error);
      enableButton();
    };

    $("#guess").click(function(evt) {
      var guess = $("#word").val();
      console.log(guess);
      submitGuess(socket, guess);
    });

  });
})(jQuery);
