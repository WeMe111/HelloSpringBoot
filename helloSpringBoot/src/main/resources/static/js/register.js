//게시판 등록
	function writeSubmit(){

		//step2. 게시판 등록
		var params = {
			 title : $.trim($("#title").val())
			,content : $.trim($("#content").val())
			,writer : $("#loginId").val()
			,useYn : 'Y'
		}

        console.log(params);

		if(params.title == ""){
			alert("제목을 입력해주세요.");
			return false;
		}

		else if(params.content == ""){
			alert("내용을 입력해주세요.");
			return false;
		}

		$.ajax({
	         type : 'POST'
	        ,url : "/board/register"
	        ,dataType : 'json'
	        ,data : JSON.stringify(params)
	        ,contentType: 'application/json'
	        ,success : function(result) {
				alert("해당글이 정상적으로 등록되었습니다.");
				location.href="/board/list";
	        },
	        error: function(request, status, error) {

	        }
	    })
	}
	
	
var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
});