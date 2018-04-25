/**
 * Created by sena on 25-06-2014.
 */
var reqLogin = require('./helpers/request-login');
var fctnews = require('./crawler/fctnews');

reqLogin.login(fctnews);
