'use strict';

(function () {
    window.addEventListener("load", function () {
        let form = this.document.querySelector("#needs-validation");
        let btnUpdate = this.document.querySelector("#btn-update");

        btnUpdate.addEventListener("click", function (event) {
            if (form.checkValidity() == false) {
                event.preventDefault();
                event.stopPropagation();
                form.classList.add("was-validated");
            }
        }, false);
    }, false);
})();

var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
});