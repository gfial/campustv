/**
 * Created by PhilipSena on 19-06-2014.
 */



$(function () {
    setupSession(function(sessionJson) {
        if(sessionJson.valid) {
            window.location.replace('/smarttv.html');
        }
    });
    initEventListeners();
});

function search() {
    var query = $('#search-box').val();
    var windowLocation = "/smarttv.html";
    if(query != '') {
        windowLocation += "?query=" + query;
    }
    window.location.replace(windowLocation);
}

function initEventListeners() {

    $('#login-wrap').click(function() {
        openLoginWindow();
    });

    $("#register-instead").click(function () {

        $("#register").show();
        $("#login").hide();
    });

    $("#cancel-register").click(function() {

        $("#login").show();
        $("#register").hide();
    });

    $(document).keyup(function(e) {

        // If escape is pressed
        if (e.keyCode == 27) {
            //close window
            closeWindows();
        }
    });

    $(document).mousedown(function (e) {

        var $window = $('#login-window');

        if (!$window.is(e.target) // if the target of the click isn't the container...
            && ($window.has(e.target).length === 0)) // ... nor a descendant of the container
        {
            closeWindows();
        }
    });


    $('#input-deco').click(search);
    $('#search-box').keydown(function(event) {
        if(event.keyCode == 13) {
            search();
        }
    });

    $('#submit-login').click(function () {

            var jsonLoginToSend = makeLoginJson();
            console.log(jsonLoginToSend);

            memberLogin(jsonLoginToSend, function(member) {
                console.log(member);
                window.location.replace("/smarttv.html");
            }, function(error) {
                alert("erro");
                console.log(error);

            });
        }
    );

    $('#submit-register').click(function () {
            var $pass1 = $('#register-password');
            var $pass2 = $('#register-password-repeat');

            if($pass1.val() != $pass2.val()){
                console.log("Passwords do not match!");

                $pass1.css({'border':'2px solid red'});
                $pass2.css({'border':'2px solid red'});

                return -1;
            }

            $pass1.css({'border':'none'});
            $pass2.css({'border':'none'});
            var jsonRegisterToSend = makeRegisterJson();

            console.log(jsonRegisterToSend);

            createMember(jsonRegisterToSend, function(member) {
                console.log(member);
                closeWindows();
            }, function(error) {
                console.log(error);
            });
        }
    );

    function closeWindows() {
        $('#outer-window-holder').hide();
        $('#dark-background').hide();
    }

    function openLoginWindow() {
        $('#outer-window-holder').css({'display':'table'});
        $('#dark-background').show();

    }

    $('#logo-tv').click(function(){
        window.location.replace("/tv.html");
    });

    $('#logo-smarttv').click(function(){
        window.location.replace("/smarttv.html");
    });

}

function makeRegisterJson() {

    return (
    {
        "username": $('#register-user').val(),
        "email": $('#register-email').val(),
        "password": $('#register-password').val()
    });
}

function makeLoginJson() {

    return (
    {
        "email": $('#login-email').val(),
        "password": $('#login-password').val()
    });
}