/**
 * Created by guilhermemtr on 6/24/14.
 */

var session = null;

var key = "campusSessionData";

$(document).ready(function() {
    /*try {
        session = $.jStorage.get(key, key);
    } catch(exception) {
        alert(exception);
    }
    console.log("Saved session:");
    console.log(session);
    //try {
        //$.jStorage.flush();
    //} catch (exception) {
    //    alert(exception);
    //}
    console.log(session == key);
    if(session != key) {
        session = JSON.parse(session);
        console.log(session);
    } else {*/
        setupSession(function (sessionJson) {
            //try {
            //    $.jStorage.set(key, JSON.stringify(sessionJson));
            //} catch (exception) {
            //    alert(exception);
            //}
            //console.log("Got session:");
            //console.log(sessionJson);
            session = sessionJson;
        });
    /*}
    console.log("Final session:");
    console.log(session);*/
});