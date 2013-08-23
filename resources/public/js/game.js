(function($) {

  function getUsername() {
    return "Diana";
  }
  function sendHello(socket, username) {
    socket.send(JSON.stringify({"username": username}));
  }
  function beginGame(socket, data) {
    console.log("beginGame", data);
    populateGame(data.letters);
    populateUserlist(data.userlist);
  }
  function submitGuess(socket, guess) {
    socket.send(JSON.stringify({"guess": guess}));
  }

  function populateGame(letterlist) {
    $("#letter-list").empty();
    for (var i = 0; i < letterlist.length; i++) {
      var $cell = $("#templates .letter").clone();
      $cell.text(letterlist[i]);
      $("#letter-list").append($cell);
    }
  }

  function populateUserlist(userlist) {
    $("#opponent-list").empty();
    for (var i = 0; i < userlist.length; i++) {
      var $cell = $("#templates .opponent").clone();
      $cell.text(userlist[i]);
      $("#opponent-list").append($cell);
    }
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
      beginGame(socket, data);
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
