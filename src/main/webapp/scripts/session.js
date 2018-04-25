/**
 * Created by guilhermemtr on 6/24/14.
 */

var SESSIONKEY = "campusTvSessionValue";

//The session variable.
var session = null;

//Corresponds to the session
function Session(member, valid) {
    this.valid = valid;
    this.member = member;
}

/**
 * Sets up the member of the tv.
 * It get's the member
 */
function getSession(memberLoadedCallback) {
    function setMember(memberJson) {
        var session = new Session(memberJson, true);
        memberLoadedCallback(session);
    }

    function setDefaultMember(memberJson) {
        var session = new Session(memberJson, false);
        memberLoadedCallback(session);
    }

    function setMemberError() {
        console.log("Error getting member. Invalid cookies.");
        function noDefaultMember() {
            console.log("Error getting default member.");
        }
        getDefaultMember(setDefaultMember, noDefaultMember);
    }
    getMemberRequest(setMember, setMemberError);
}


/**
 * Faz um startup da sessao.
 * Carrega a smart Tv com conteudos.
 */
function setupSession(callback) {
    getSession(function(newSession) {
        session = newSession;
        callback(newSession);
    });
}