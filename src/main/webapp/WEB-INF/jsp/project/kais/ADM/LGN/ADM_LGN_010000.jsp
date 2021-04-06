<%@ page contentType="text/html;charset=UTF-8" errorPage = "/WEB-INF/jsp/common/error.jsp"%>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/declare.jspf" %>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/error.jspf" %>
 
 <%
try{
	/***********************************************************************
	*프로그램명 : ADM_LGN_010000.jsp
	*설명 : 관리자로그인화면/로그아웃
	*작성자 : 장정훈
	*작성일자 : 2017.06.12
	*
	*수정자		수정일자		                                                            수정내용
	*----------------------------------------------------------------------
	*
	***********************************************************************/
	 String REDIRECT_URI	= request.getParameter("REDIRECT_URI");
	 if(request.getSession().getAttribute("MgrInfoRec")!=null){
		 response.sendRedirect("/ADM_LGN_020000/");
	 }
 %>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=edge">

	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<meta name="description" content="Kwic Admin Login" />
	<meta name="author" content="" />

	<link rel="icon" href="<%=IMG_PATH%>favicon.ico">

	<title>로그인 | KWIC</title>

	<link rel="stylesheet" href="<%=NEON_PATH%>assets/js/jquery-ui/css/no-theme/jquery-ui-1.10.3.custom.min.css">
	<link rel="stylesheet" href="<%=NEON_PATH%>assets/css/font-icons/entypo/css/entypo.css">
	<link rel="stylesheet" href="//fonts.googleapis.com/css?family=Noto+Sans:400,700,400italic">
	<link rel="stylesheet" href="<%=NEON_PATH%>assets/css/bootstrap.css">
	<link rel="stylesheet" href="<%=NEON_PATH%>assets/css/neon-core.css">
	<link rel="stylesheet" href="<%=NEON_PATH%>assets/css/neon-theme.css">
	<link rel="stylesheet" href="<%=NEON_PATH%>assets/css/neon-forms.css">
	<link rel="stylesheet" href="<%=NEON_PATH%>assets/css/custom.css">

	<script src="<%=JS_PATH%>jquery-1.12.4.custom.js"></script>
	<script src="<%=JS_PATH%>common.js"></script>

	<!--[if lt IE 9]><script src="<%=NEON_PATH%>assets/js/ie8-responsive-file-warning.js"></script><![endif]-->

	<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
	<!--[if lt IE 9]>
		<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
		<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
	<![endif]-->

 <input type="hidden" id="REQUEST_TOKEN_ATTRIBUTE_NAME" value="<%=com.kwic.web.servlet.RequestTokenInterceptor.ATTRIBUTE_NAME%>"/>
 <input type="hidden" id="<%=com.kwic.web.servlet.RequestTokenInterceptor.ATTRIBUTE_NAME%>" value="<%=session.getAttribute(com.kwic.web.servlet.RequestTokenInterceptor.ATTRIBUTE_NAME)==null?"":String.valueOf(session.getAttribute(com.kwic.web.servlet.RequestTokenInterceptor.ATTRIBUTE_NAME))%>"/>
<script>
var REDIRECT_URI	= "<%=REDIRECT_URI==null?"":REDIRECT_URI%>";

function fn_init(){
	var MGRINF_ID	= getCookie("MGRINF_ID");
	$('#MGRINF_ID').val(MGRINF_ID==null?"":MGRINF_ID);
}

</script>
</head>
<body class="page-body login-page login-form-fall" style="background:#494a67;" onload="javascript:fn_init();">

<div class="login-container">

	<div class="login-header login-caret" style="background:#525474;">

		<div class="login-content">

			<a href="http://www.kwic.co.kr" class="logo">
				<img src="<%=IMG_PATH%>kwic-logo-cs.png" width="120" alt="" />
			</a>

			<p class="description" id='title-desc'>KAIS 모니터링 기웅관리자용 화면입니다.</p>
		</div>

	</div>

	<div class="login-progressbar">
		<div></div>
	</div>

	<div class="login-form" style="background:#494a67;">

		<div class="login-content">

			<div class="form-login-error">
				<h3>로그인 실패</h3>
				<p><b><span id='err_msg'>올바른 <strong>아이디</strong>/<strong>패스워드</strong>를 입력하여 주십시오.</span></b></p>
			</div>

			<form method="post" role="form" id="form_login">
				<div class="form-group">
					<div class="input-group">
						<div class="input-group-addon">
							<i class="entypo-user"></i>
						</div>
						<input type="text" class="form-control" name="MGRINF_ID" id="MGRINF_ID" placeholder="아이디" autocomplete="off" value=""/>
					</div>
				</div>

				<div class="form-group">
					<div class="input-group">
						<div class="input-group-addon">
							<i class="entypo-key"></i>
						</div>
						<input type="password" class="form-control" name="MGRINF_PWD" id="MGRINF_PWD" placeholder="패스워드" autocomplete="off" />
					</div>
				</div>

				<div class="form-group">
					<button type="submit" class="btn btn-primary btn-block btn-login">
						<i class="entypo-login"></i>
						로그인
					</button>
				</div>

			</form>
		</div>
	</div>
</div>

	<!-- Bottom scripts (common) -->
	<script src="<%=NEON_PATH%>assets/js/gsap/TweenMax.min.js"></script>
	<script src="<%=NEON_PATH%>assets/js/jquery-ui/js/jquery-ui-1.10.3.minimal.min.js"></script>
	<script src="<%=NEON_PATH%>assets/js/bootstrap.js"></script>
	<script src="<%=NEON_PATH%>assets/js/joinable.js"></script>
	<script src="<%=NEON_PATH%>assets/js/resizeable.js"></script>
	<script src="<%=NEON_PATH%>assets/js/neon-api.js"></script>
	<script src="<%=NEON_PATH%>assets/js/jquery.validate.min.js"></script>

	<!-- JavaScripts initializations and stuff -->
	<script src="<%=NEON_PATH%>assets/js/neon-custom.js"></script>

	<!-- Demo Settings -->
	<%-- <script src="<%=NEON_PATH%>assets/js/neon-demo.js"></script> --%>

	<!-- login action script -->
	<!--<script src="<%=JS_PATH%>manager-login.js"></script>-->

<script type="text/javascript">
// 쿠키 생성
function setCookie(cName, cValue, cDay){
    var expire = new Date();
    expire.setDate(expire.getDate() + cDay);
    cookies = cName + '=' + escape(cValue) + '; path=/ '; // 한글 깨짐을 막기위해 escape(cValue)를 합니다.
    if(typeof cDay != 'undefined') cookies += ';expires=' + expire.toGMTString() + ';';
    document.cookie = cookies;
}

// 쿠키 가져오기
function getCookie(cName) {
    cName = cName + '=';
    var cookieData = document.cookie;
    var start = cookieData.indexOf(cName);
    var cValue = '';
    if(start != -1){
        start += cName.length;
        var end = cookieData.indexOf(';', start);
        if(end == -1)end = cookieData.length;
        cValue = cookieData.substring(start, end);
    }
    return unescape(cValue);
}

/**
 *	Neon Login Script
 *
 *	Developed by Arlind Nushi - www.laborator.co
 */

var neonLogin = neonLogin || {};

;(function($, window, undefined)
{
	"use strict";

	$(document).ready(function()
	{
		neonLogin.$container = $("#form_login");


		// Login Form & Validation
		neonLogin.$container.validate({
			rules: {
				MGRINF_ID: {
					required: true
				},

				MGRINF_PWD: {
					required: true
				},

			},

			highlight: function(element){
				$(element).closest('.input-group').addClass('validate-has-error');
			},


			unhighlight: function(element)
			{
				$(element).closest('.input-group').removeClass('validate-has-error');
			},

			submitHandler: function(ev)
			{
				/*
					Updated on v1.1.4
					Login form now processes the login data, here is the file: data/sample-login-form.php
				*/

				$(".login-page").addClass('logging-in'); // This will hide the login form and init the progress bar

				// Hide Errors
				$(".form-login-error").slideUp('fast');

				// We will wait till the transition ends
				setTimeout(function()
				{
					var random_pct = 25 + Math.round(Math.random() * 30);

					// The form data are subbmitted, we can forward the progress to 70%
					neonLogin.setPercentage(40 + random_pct);

					// Send data to the server
					$.ajax({
						url: '/ADM_LGN_01000A/',
						method: 'POST',
						dataType: 'jsonp',
						data: {
							MGRINF_ID: $("input#MGRINF_ID").val(),
							MGRINF_PWD: $("input#MGRINF_PWD").val(),
						},
						error: function()
						{
							neonLogin.setPercentage(100);
							setTimeout(function(){
								$(".login-page").removeClass('logging-in'); // This will hide the login form and init the progress bar
								$('#err_msg').html("서버연결에 실패하였습니다.");
								neonLogin.resetProgressBar(true);
							}, 1000);
						},
						success: function(response)
						{
							neonLogin.setPercentage(100);
							setTimeout(function(){
								if(response.RESULT_CD=='N'){
									$(".login-page").removeClass('logging-in'); // This will hide the login form and init the progress bar
									neonLogin.resetProgressBar(true);
									$('#err_msg').html(response.RESULT_MSG);
									if(response.RESULT_ERCD=='NO_SESSION'){
										malert("접근권한 획득을 위해 3초후 자동으로 화면이 갱신됩니다.");
										setTimeout(function(){$(location).attr('href','/kwic/');},3000);
									}
								}else{
									//create cookie
									setCookie("MGRINF_ID",response.MGRINF_ID,60*60*24*7);
									$('#title-desc').html("로그인 되었습니다.");
									neonLogin.resetProgressBar(false);
									$(location).attr('href', "/ADM_LGN_020000/"+((REDIRECT_URI==""||REDIRECT_URI==null)?"":"?REDIRECT_URI="+REDIRECT_URI));
								}
							}, 1000);
						}
					});
				}, 650);
			}
		});




		// Lockscreen & Validation
		var is_lockscreen = $(".login-page").hasClass('is-lockscreen');

		if(is_lockscreen)
		{
			neonLogin.$container = $("#form_lockscreen");
			neonLogin.$ls_thumb = neonLogin.$container.find('.lockscreen-thumb');

			neonLogin.$container.validate({
				rules: {

					MGRINF_PWD: {
						required: true
					},

				},

				highlight: function(element){
					$(element).closest('.input-group').addClass('validate-has-error');
				},


				unhighlight: function(element)
				{
					$(element).closest('.input-group').removeClass('validate-has-error');
				},

				submitHandler: function(ev)
				{
					/*
						Demo Purpose Only

						Here you can handle the page login, currently it does not process anything, just fills the loader.
					*/

					$(".login-page").addClass('logging-in-lockscreen'); // This will hide the login form and init the progress bar

					// We will wait till the transition ends
					setTimeout(function()
					{
						var random_pct = 25 + Math.round(Math.random() * 30);

						neonLogin.setPercentage(random_pct, function()
						{
							// Just an example, this is phase 1
							// Do some stuff...

							// After 0.77s second we will execute the next phase
							setTimeout(function()
							{
								neonLogin.setPercentage(100, function()
								{
									// Just an example, this is phase 2
									// Do some other stuff...

									// Redirect to the page
									setTimeout("window.location.href = '../../'", 600);
								}, 2);

							}, 820);
						});

					}, 650);
				}
			});
		}






		// Login Form Setup
		neonLogin.$body = $(".login-page");
		neonLogin.$login_progressbar_indicator = $(".login-progressbar-indicator h3");
		neonLogin.$login_progressbar = neonLogin.$body.find(".login-progressbar div");

		neonLogin.$login_progressbar_indicator.html('0%');

		if(neonLogin.$body.hasClass('login-form-fall'))
		{
			var focus_set = false;

			setTimeout(function(){
				neonLogin.$body.addClass('login-form-fall-init')

				setTimeout(function()
				{
					if( !focus_set)
					{
						neonLogin.$container.find('input:first').focus();
						focus_set = true;
					}

				}, 550);

			}, 0);
		}
		else
		{
			neonLogin.$container.find('input:first').focus();
		}

		// Focus Class
		neonLogin.$container.find('.form-control').each(function(i, el)
		{
			var $this = $(el),
				$group = $this.closest('.input-group');

			$this.prev('.input-group-addon').click(function()
			{
				$this.focus();
			});

			$this.on({
				focus: function()
				{
					$group.addClass('focused');
				},

				blur: function()
				{
					$group.removeClass('focused');
				}
			});
		});

		// Functions
		$.extend(neonLogin, {
			setPercentage: function(pct, callback)
			{
				pct = parseInt(pct / 100 * 100, 10) + '%';

				// Lockscreen
				if(is_lockscreen)
				{
					neonLogin.$lockscreen_progress_indicator.html(pct);

					var o = {
						pct: currentProgress
					};

					TweenMax.to(o, .7, {
						pct: parseInt(pct, 10),
						roundProps: ["pct"],
						ease: Sine.easeOut,
						onUpdate: function()
						{
							neonLogin.$lockscreen_progress_indicator.html(o.pct + '%');
							drawProgress(parseInt(o.pct, 10)/100);
						},
						onComplete: callback
					});
					return;
				}

				// Normal Login
				neonLogin.$login_progressbar_indicator.html(pct);
				neonLogin.$login_progressbar.width(pct);

				var o = {
					pct: parseInt(neonLogin.$login_progressbar.width() / neonLogin.$login_progressbar.parent().width() * 100, 10)
				};

				TweenMax.to(o, .7, {
					pct: parseInt(pct, 10),
					roundProps: ["pct"],
					ease: Sine.easeOut,
					onUpdate: function()
					{
						neonLogin.$login_progressbar_indicator.html(o.pct + '%');
					},
					onComplete: callback
				});
			},

			resetProgressBar: function(display_errors)
			{
				TweenMax.set(neonLogin.$container, {css: {opacity: 0}});

				setTimeout(function()
				{
					TweenMax.to(neonLogin.$container, .6, {css: {opacity: 1}, onComplete: function()
					{
						neonLogin.$container.attr('style', '');
					}});

					neonLogin.$login_progressbar_indicator.html('0%');
					neonLogin.$login_progressbar.width(0);

					if(display_errors)
					{
						var $errors_container = $(".form-login-error");

						$errors_container.show();
						var height = $errors_container.outerHeight();

						$errors_container.css({
							height: 0
						});

						TweenMax.to($errors_container, .45, {css: {height: height}, onComplete: function()
						{
							$errors_container.css({height: 'auto'});
						}});

						// Reset MGRINF_PWD fields
						neonLogin.$container.find('input[type="password"]').val('');
					}else{
						var $errors_container = $(".form-login-error");

						$errors_container.hide();
					}

				}, 800);
			}
		});


		// Lockscreen Create Canvas
		if(is_lockscreen)
		{
			neonLogin.$lockscreen_progress_canvas = $('<canvas></canvas>');
			neonLogin.$lockscreen_progress_indicator =  neonLogin.$container.find('.lockscreen-progress-indicator');

			neonLogin.$lockscreen_progress_canvas.appendTo(neonLogin.$ls_thumb);

			var line_width = 3,
				thumb_size = neonLogin.$ls_thumb.width() + line_width;

			neonLogin.$lockscreen_progress_canvas.attr({
				width: thumb_size,
				height: thumb_size
			}).css( {
				top: -line_width/2,
				left: -line_width/2
			} );


			neonLogin.lockscreen_progress_canvas = neonLogin.$lockscreen_progress_canvas.get(0);

			// Create Progress Circle
			var bg = neonLogin.lockscreen_progress_canvas,
				ctx = ctx = bg.getContext('2d'),
				imd = null,
				circ = Math.PI * 2,
				quart = Math.PI / 2,
				currentProgress = 0;

			ctx.beginPath();
			ctx.strokeStyle = '#eb7067';
			ctx.lineCap = 'square';
			ctx.closePath();
			ctx.fill();
			ctx.lineWidth = line_width;

			imd = ctx.getImageData(0, 0, thumb_size, thumb_size);

			var drawProgress = function(current) {
			    ctx.putImageData(imd, 0, 0);
			    ctx.beginPath();
			    ctx.arc(thumb_size/2, thumb_size/2, 70, -(quart), ((circ) * current) - quart, false);
			    ctx.stroke();

			    currentProgress = current * 100;
			}

			drawProgress(0/100);


			neonLogin.$lockscreen_progress_indicator.html('0%');

			ctx.restore();
		}

	});

})(jQuery, window);
</script>
</body>
</html>
<%}catch(Exception e){e.printStackTrace();}%>
