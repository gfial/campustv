// Generated by CoffeeScript 1.7.1
var cookie, create, https, requestlogin, setOptions;

http = require('http');

requestlogin = require('./request-login');

setOptions = function(content) {
  var headers, options;
  headers = {
    'Content-Type': 'application/json',
    'Content-Length': Buffer.byteLength(content),
    'Cookie': requestlogin.cookie()
  };
  options = {
    hostname: 'greps.herokuapp.com',
    path: '/api/news/create',
    method: 'POST',
    headers: headers
  };
  return options;
};

cookie = null;

create = function(articles, articleId, userInfo, writeArticles) {
  var articleString, request;
  articleString = JSON.stringify(articles[articleId]);
  request = http.request(setOptions(articleString), function(res) {
    res.setEncoding('utf8');
    return res.on('data', function(data) {
        console.log(res.statusCode);
	console.log(data);
        console.log('create');
//      if ((data.err != null) && data.err.code === 403) {
//        console.log('Creating forbidden');
//        articles[articleId] = null;
//        return requestlogin.login(userInfo);
//      } else if (data.err != null) {
//        console.log('Creating failed');
//        articles[articleId] = null;
//        return console.log(data.err);
//      } else {
//        console.log('Article created');
//        return writeArticles();
//      }
    });
  });
  request.on('error', function(e) {
    return console.log('Problem with request: ' + e.message);
  });
  request.write(articleString);
  return request.end();
};

module.exports = {
  create: create
};