'use strict';

    //파일 현재 필드 갯수
	var fileCount = 0;

	//전체 업로드 갯수
	var totalCount = 10;

	//파일 고유 넘버
	var fileNum = 0;

	//첨부파일 배열
	var content_files = new Array();

	//파일 시퀀스들
	var fileIdxs = "";

	//파일 추가 클릭
	function fileClick(){
		$("#input_file").click();
	}

	//파일 change 이벤트
	function fileChange(target){

		var files = target.files;

		//파일 배열 담기
		var filesArr = Array.prototype.slice.call(files);

		if(fileCount + filesArr.length > totalCount){
			alert("파일은 최대 "+totalCount+"개까지 업로드 할 수 있습니다.");
			return false;
		}

		else{
			fileCount = fileCount + filesArr.length;
		}

		// 각각의 파일 배열담기 및 기타
		filesArr.forEach(function (f) {
			var reader = new FileReader();
			reader.onload = function (e) {
			content_files.push(f);
			console.log(content_files)
			$('#articlefileChange').append(
	       		'<div id="file' + fileNum + '" onclick="fileDelete(\'file' + fileNum + '\')">'
	       		+ '<font style="font-size:12px">' + f.name + '</font>'
	       		+ '<img src="/images/icon_minus.png" style="width:20px; height:auto; vertical-align: middle; cursor: pointer;"/>'
	       		+ '<div/>'
			);
	        fileNum++;
	      };
	      reader.readAsDataURL(f);
	    });

	    //초기화 한다.
	    $("#input_file").val("");

	}

	//파일 삭제
	function fileDelete(fileNum){
		var no = fileNum.replace(/[^0-9]/g, "");
	    content_files[no].is_delete = true;
		$('#' + fileNum).remove();
		fileCount --;
	}

	//파일 저장
	function registerAction(){
		console.log(content_files);
		var form = $("form")[0];
	 	var formData = new FormData(form);
		for (var x = 0; x < content_files.length; x++) {
			// 삭제 안한것만 담아 준다.
			if(!content_files[x].is_delete){
				 formData.append("article_file", content_files[x]);
				 formData.append("filePath", "/main");
			}
		}

	   /*
	   * 파일업로드 multiple ajax처리
	   */
		$.ajax({
	   	      type: "POST",
	   	   	  enctype: "multipart/form-data",
	   	      url: "/file-upload",
	       	  data : formData,
	       	  processData: false,
	   	      contentType: false,
	   	      success: function (data) {

	   	    	console.log(data)
	   	    	//파일 시퀀스들
	   	    	fileIdxs = data.fileIdxs;
	   	    	if(data.result == "OK"){
	   	    		writeSubmit();
				} else
					alert("서버내 오류로 처리가 지연되고있습니다. 잠시 후 다시 시도해주세요");
	   	      },
	   	      error: function (xhr, status, error) {
	   	    	alert("서버오류로 지연되고있습니다. 잠시 후 다시 시도해주시기 바랍니다.");
			}
		});
	}

//게시판 등록
	function writeSubmit(){

		//step2. 게시판 등록
		var params = {
			 title : $.trim($("#title").val())
			,content : $.trim($("#content").val())
			,writer : $("#loginId").val()
			,useYn : 'Y'
			,fileIdxs : fileIdxs
			,attachCount : $("#articlefileChange").find("font").length
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