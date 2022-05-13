'use strict';

let index = {
    init: function () {
        $("#btn-update").on("click", () => {
            let form = document.querySelector("#needs-validation");
            if (form.checkValidity() == false) {
                console.log("회원수정 안됨")
            } else {
                this.update();
            }
        });
    },


    update: function () {
        let data = {
            idu: $("#idu").val(),
            password: $("#password").val(),
            name: $("#name").val()
        }

        $.ajax({
            type: "PUT",
            url: "/api/v1/user",
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function (res) {
            alert("회원수정이 완료되었습니다.");
            location.href = "/user/home";
        }).fail(function (err) {
            alert(JSON.stringify(err));
        });
    }
}
index.init();


var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
});