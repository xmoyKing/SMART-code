$(function(){
	
	$('#switch_qlogin').click(function(){
		$('#switch_login').removeClass("switch_btn_focus").addClass('switch_btn');
		$('#switch_qlogin').removeClass("switch_btn").addClass('switch_btn_focus');
		$('#switch_bottom').animate({left:'0px',width:'50px'});
		$('#qlogin').css('display','none');
		$('#web_qr_login').css('display','block');
		
		});
	$('#switch_login').click(function(){
		$('#switch_login').removeClass("switch_btn").addClass('switch_btn_focus');
		$('#switch_qlogin').removeClass("switch_btn_focus").addClass('switch_btn');
		$('#switch_bottom').animate({left:'140px',width:'75px'});
		
		$('#qlogin').css('display','block');
		$('#web_qr_login').css('display','none');
		});
if(getParam("a")=='0')
{
	$('#switch_login').trigger('click');
}

	});
	
function logintab(){
	scrollTo(0);
	$('#switch_qlogin').removeClass("switch_btn_focus").addClass('switch_btn');
	$('#switch_login').removeClass("switch_btn").addClass('switch_btn_focus');
	$('#switch_bottom').animate({left:'154px',width:'96px'});
	$('#qlogin').css('display','none');
	$('#web_qr_login').css('display','block');
	
}


//根据参数名获得该参数 pname等于想要的参数名 
function getParam(pname) { 
    var params = location.search.substr(1); // 获取参数 平且去掉？ 
    var ArrParam = params.split('&'); 
    if (ArrParam.length == 1) { 
        //只有一个参数的情况 
        return params.split('=')[1]; 
    } 
    else { 
         //多个参数参数的情况 
        for (var i = 0; i < ArrParam.length; i++) { 
            if (ArrParam[i].split('=')[0] == pname) { 
                return ArrParam[i].split('=')[1]; 
            } 
        } 
    } 
}  

$(document).ready(function() {
	$('#monitor_login').click(function(){
		$.post('./server',{
			username: $("#u").val(),
			userpwd: $("#p").val(),
			type: 'login'
		},function(data, status){
			console.log(data);
			if(data === 'success') location.href = './index.html';
			else $('#login_tips').text(data);
		});
	});
		
	

	//验证注册
	$('#reg').click(function() {
		//用户名不能为空
		if ($('#user').val() == "") {
			$('#user').focus().css({
				border: "1px solid red",
				boxShadow: "0 0 2px red"
			});
			$('#userCue').html("User can not be none");
			return false;
		}


		//用户名在4-16个字符之间
		if ($('#user').val().length < 4 || $('#user').val().length > 16) {
			$('#user').focus().css({
				border: "1px solid red",
				boxShadow: "0 0 2px red"
			});
			$('#userCue').html("User needs 4-16 chars");
			return false;

		}

		//验证密码
		if ($('#passwd').val().length < 6) {
			$('#passwd').focus();
			$('#userCue').html("Password need more than 6 chars");
			return false;
		}
		if ($('#passwd2').val() != $('#passwd').val()) {
			$('#passwd2').focus();
			$('#userCue').html("Password confirm failed");
			return false;
		}
		

		$.post('./server',{
			username: $("#user").val(),
			userpwd: $("#passwd").val(),
			type: 'register'
		},function(data, status){
			console.log(data);
			$("#userCue").text(data).show();
			if(data === 'success')
				location.href = './index.html';
			else if(data === 'fail')
				return false;
			else $('#user').focus().css({
					border: "1px solid red",
					boxShadow: "0 0 2px red"
				});
			
		});

		//$('#regUser').submit();
	});
	

});