'board strict';

let index = {
    init: function () {
        $("#btn-save").on("click", () => {
            this.save();
        });
        $("#btn-delete").on("click", () => {
            this.delete();
        });
        $("#btn-update").on("click", () => {
            this.update();
        });
    },

    save: function () {
        let data = {
            title: $("#title").val(),
            content: $("#content").val(),
            useYn: 'Y'
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

    delete: function () {
		let id = $("#id").val();
		
        let data = {
		useYn: 'N'
		}

        $.ajax({
            type: "PUT",
            url: "/api/v1/board/delete/" + id,
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
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

index.init();

var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
});