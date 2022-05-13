'use strict';

let replyIndex = {
    init: function () {
        $("#reply-btn-save").on("click", () => {
            this.replySave();
        });
    },

    replySave: function () {
        let data = {
            content: $("#reply-content").val(),
        }
        let idb = $("#idb").val();
        console.log(data);
        console.log(idb);
        $.ajax({
            type: "POST",
            url: "/board/{idb}/reply",
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "text"
        }).done(function (res) {
            alert("댓글작성이 완료되었습니다.");
            location.href = "/board/detail/${idb}";
        }).fail(function (err) {
            alert(JSON.stringify(err));
        });
    },

}
replyIndex.init();

var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
});
