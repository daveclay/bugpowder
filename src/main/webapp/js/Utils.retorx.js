var baseServicePath = "api/";

function log(text) {
    console.log(new Date() + ": " + text);
}

function TODO(text) {
    console.log(new Date() + ": TODO: " + text);
}

function addToBody(elem) {
    $('body').append(elem);
}

function get(url, callback) {
    $.getJSON(baseServicePath + url, callback);
}

