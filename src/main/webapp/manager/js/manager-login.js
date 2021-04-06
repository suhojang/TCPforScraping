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
									if(response.RESULT_ERCD=='NO_SESSION')
										malert("사용정보를 획득하기 위해 화면을 갱신합니다.",null,function(){document.location.href	= "/kwic/";});
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