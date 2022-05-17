'use strict';

let index = {
    init: function () {
        $("#btn-save").on("click", () => {
            this.save();
        });
        $("#btn-delete").on("click", () => {
            this.deleteById();
        });
        $("#btn-update").on("click", () => {
            this.update();
        });
    },

    save: function () {
        let data = {
            title: $("#title").val(),
            content: $("#content").val()
        }

        $.ajax({
            type: "POST",
            url: "/api/v1/board",
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function (res) {
            alert("글작성이 완료되었습니다.");
            location.href = "/auth/board/list";
        }).fail(function (err) {
            alert(JSON.stringify(err));
        });
    },

    deleteById: function () {
        let id = $("#id").text();

        $.ajax({
            type: "DELETE",
            url: "/api/v1/board/" + id,
            dataType: "json"
        }).done(function (res) {
            alert("글삭제가 완료되었습니다.");
            location.href = "/auth/board/list";
        }).fail(function (err) {
            alert(JSON.stringify(err));
        });
    },

    update: function () {
        let id = $("#id").val();

        let data = {
            title: $("#title").val(),
            content: $("#content").val()
        }
        console.log(id);
        console.log(data);

        $.ajax({
            type: "PUT",
            url: "/api/v1/board/" + id,
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function (res) {
            alert("글수정이 완료되었습니다.");
            location.href = "/auth/board/list";
        }).fail(function (err) {
            alert(JSON.stringify(err));
        });
    }
}


function uploadFile() {
  $.ajax({
    url: "/uploadFile",
    type: "POST",
    data: new FormData($("#upload-file-form")[0]),
    enctype: 'multipart/form-data',
    processData: false,
    contentType: false,
    cache: false,
    success: function () {
      // Handle upload success
      // ...
    },
    error: function () {
      // Handle upload error
      // ...
    }
  });
}
index.init();

var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
});