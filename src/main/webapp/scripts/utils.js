

// REQUEST
function ajaxRequest(url, data, method, success, error) {
    return $.ajax({
        url: url,
        data: JSON.stringify(data),
        type: method,
        contentType: "application/json; charset=utf-8",
        xhrFields: {
            withCredentials: true
        }
    }).done(success).fail(error);
}

// REQUEST
function ajaxGet(url, success, error) {
    return $.ajax({
        url: url,
        type: "GET",
        xhrFields: {
            withCredentials: true
        }
    }).done(success).fail(error);
}

// REQUEST
function ajaxRequest(url, data, method, success, error) {
    return $.ajax({
        url: url,
        data: JSON.stringify(data),
        type: method,
        contentType: "application/json; charset=utf-8",
        xhrFields: {
            withCredentials: true
        }
    }).done(success).fail(error);
}


// TODO: Given a string that contains tags separated by commas ',' or spaces ' ' or even both (yet to decide), return an array of tags.
function parseTags(str) {

    var tags = [];
    var tagNames = split( str );

    var i = 0;
    for(i = 0; i < tagNames.length; i++) {
        tags[i] = {"name" : tagNames[i]};
    }
    return tags;
}

function split( val ) {
    return val.split( /,\s*/ );
}

function extractLast( term ) {
    return split( term ).pop();
}