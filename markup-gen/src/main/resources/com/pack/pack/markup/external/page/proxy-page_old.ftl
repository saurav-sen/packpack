<html>
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta property="og:title" content="${ogTitle}"/>
		<meta property="og:description" content="${ogDescription}"/>
		<meta property="og:image" content="${ogImage}"/>
		<meta property="og:url" content="${ogUrl}"/>
		
		<link rel="stylesheet" href="${jsBaseUrl}/css/normalize_progress.css">
		<link rel="stylesheet" href="${jsBaseUrl}/css/main_progress.css">
		
		<style type="text/css">
			.container {
				  position: relative;
				  display: -webkit-box;
				  display: -ms-flexbox;
				  display: flex;
				  -webkit-box-align: center;
					  -ms-flex-align: center;
						  align-items: center;
				  -webkit-box-pack:center;
					  -ms-flex-pack:center;
						  justify-content:center;
				  width:300px;
				  height: 300px;
				}
		
				button {
				  position: relative;
				  width: 200px;
				  height: 70px;
				  border: none;
				  border-radius: 5px;
				  font-size: 1.5em;
				  background-color:#ff6600;
				  color: #fff;
				  padding: 0 10px;
				  font-family: 'Oswald', sans-serif;
				  outline: 0;
				}
				button:hover {
				  cursor: pointer;
				  -webkit-animation: moveButton 0.4s ease forwards;
						  animation: moveButton 0.4s ease forwards;
				}
				.bar ,
				.load {
				  position: absolute;
				  bottom: 10%;
				  left: 0%;
				  width: 95%;
				  height: 5px;
				  background-color: #b34700;
				  border-radius: 20px;
				  opacity: 0;
				  box-sizing: border-box;
				  margin: 0 4.5px;
				}
		
				.load {
				  background-color: #fff;
				  z-index: 1;
				  width: 0%;
				}
		
				button:active {
				  outline: 0;
				}
		
				button:hover .bar {
				  opacity: 1;
				  -webkit-transition: all 0.2s ease;
				  transition: all 0.2s ease;
				}
				button:hover > strong > i {
				  -webkit-animation: tremble 0.5s 0.4s ease;
						  animation: tremble 0.5s 0.4s ease;
				}
				button:hover .load {
				  opacity: 1;
				  -webkit-transition: all 0.5s ease;
				  transition: all 0.5s ease;
				}
		
				.loading {
				  position: absolute;
				  top: 0px;
				  left: -5px;
				  width: 100%;
				  opacity: 1;
				  -webkit-transition: all 5s 0.1s linear;
				  transition: all 5s 0.1s linear;
				}
		
				button.complete {
				  background-color: #4EBA4E;
				  -webkit-transition: all 0.5s ease-in-out;
				  transition: all 0.5s ease-in-out;
				}
				button.complete .bar,
				button.complete strong,
				button.complete .load {
				  opacity: 0;
				}
				button.complete:before {
				  content: '\f00c';
				  font-family: 'fontawesome';
				  -webkit-transition: all 0.5s ease;
				  transition: all 0.5s ease;
				  font-weight: 600;
				  position: absolute;
				  left: 50%;
				  -webkit-transform: translateX(-50%) scale(1.2);
						  transform: translateX(-50%) scale(1.2);
				  color: #fff;
				  font-size: 1.5em;
				  opacity: 0;
				  -webkit-animation: appear 0.5s 0.2s ease-in-out forwards;
						  animation: appear 0.5s 0.2s ease-in-out forwards;
				}
		
				@-webkit-keyframes appear {
				  from {
					opacity: 0;
				  }
				  to {
					opacity: 1;
				  }
				}
		
				@keyframes appear {
				  from {
					opacity: 0;
				  }
				  to {
					opacity: 1;
				  }
				}
		
				@-webkit-keyframes tremble {
				  0% {
					-webkit-transform: rotate(0deg);
							transform: rotate(0deg);
				  }
				  25% {
					-webkit-transform: rotate(-10deg);
							transform: rotate(-10deg);
				  }
				  50% {
					-webkit-transform: rotate(0deg);
							transform: rotate(0deg);
				  }
				  75% {
					-webkit-transform: rotate(10deg);
							transform: rotate(10deg);
				  }
				  100% {
					-webkit-transform: rotate(0deg);
							transform: rotate(0deg);
				  }
				}
		
				@-webkit-keyframes tremble-end {
				  0% {
					-webkit-transform: rotate(0deg) translateX(-50%);
							transform: rotate(0deg) translateX(-50%);
				  }
				  25% {
					-webkit-transform: rotate(-10deg) translateX(-50%);
							transform: rotate(-10deg) translateX(-50%);
				  }
				  50% {
					-webkit-transform: rotate(0deg) translateX(-50%);
							transform: rotate(0deg) translateX(-50%);
				  }
				  75% {
					-webkit-transform: rotate(10deg) translateX(-50%);
							transform: rotate(10deg) translateX(-50%);
				  }
				  100% {
					-webkit-transform: rotate(0deg) translateX(-50%);
							transform: rotate(0deg) translateX(-50%);
				  }
				}
		
				@keyframes tremble {
				  0% {
					-webkit-transform: rotate(0deg);
							transform: rotate(0deg);
				  }
				  25% {
					-webkit-transform: rotate(-10deg);
							transform: rotate(-10deg);
				  }
				  50% {
					-webkit-transform: rotate(0deg);
							transform: rotate(0deg);
				  }
				  75% {
					-webkit-transform: rotate(10deg);
							transform: rotate(10deg);
				  }
				  100% {
					-webkit-transform: rotate(0deg);
							transform: rotate(0deg);
				  }
				}
		
				@-webkit-keyframes moveButton {
				  0% {
					width: 200px;
					-webkit-transform: skew(0deg);
							transform: skew(0deg);
					border-radius: 5px;
				  }
				  10% {
					-webkit-transform: translateY(-12px) skew(-10deg);
							transform: translateY(-12px) skew(-10deg);
					border-top-left-radius: 10px;
					border-bottom-right-radius: 10px;
				  }
				  15% {
					-webkit-transform: translateY(-7px) skew(10deg);
							transform: translateY(-7px) skew(10deg);
					border-top-left-radius: 10px;
					border-bottom-right-radius: 10px;
				  }
				  20% {
					-webkit-transform: translateY(-2px) skew(-15deg);
							transform: translateY(-2px) skew(-15deg);
					border-top-left-radius: 15px;
					border-bottom-right-radius: 15px;
				  }
				  30% {
					-webkit-transform: translateY(0px) skew(15deg);
							transform: translateY(0px) skew(15deg);
					border-top-left-radius: 15px;
					border-bottom-right-radius: 15px;
				  }
				  40% {
					-webkit-transform: skew(-20deg);
							transform: skew(-20deg);
					width: 210px;
					border-top-left-radius: 20px;
					border-bottom-right-radius: 20px;
				  }
				  50% {
					-webkit-transform: skew(20deg);
							transform: skew(20deg);
					width: 210px;
					border-top-left-radius: 20px;
					border-bottom-right-radius: 20px;
				  }
				  60% {
					-webkit-transform: skew(-15deg);
							transform: skew(-15deg);
					width: 200px;
					border-top-right-radius: 20px;
					border-bottom-left-radius: 20px;
				  }
				  70% {
					-webkit-transform: skew(15deg);
							transform: skew(15deg);
					width: 200px;
					border-top-right-radius: 20px;
					border-bottom-left-radius: 20px;
				  }
				  80% {
					-webkit-transform: skew(-10deg);
							transform: skew(-10deg);
					width: 200px;
					border-top-right-radius: 15px;
					border-bottom-left-radius: 15px;
				  }
				  90% {
					-webkit-transform: skew(5deg);
							transform: skew(5deg);
					width: 200px;
					border-top-right-radius: 10px;
					border-bottom-left-radius: 10px;
				  }
				  100% {
					-webkit-transform: skew(0deg);
							transform: skew(0deg);
					width: 200px;
					border-radius: 5px;
				  }
				}
		
				@keyframes moveButton {
				  0% {
					width: 200px;
					-webkit-transform: skew(0deg);
							transform: skew(0deg);
					border-radius: 5px;
				  }
				  10% {
					-webkit-transform: translateY(-12px) skew(-10deg);
							transform: translateY(-12px) skew(-10deg);
					border-top-left-radius: 10px;
					border-bottom-right-radius: 10px;
				  }
				  15% {
					-webkit-transform: translateY(-7px) skew(10deg);
							transform: translateY(-7px) skew(10deg);
					border-top-left-radius: 10px;
					border-bottom-right-radius: 10px;
				  }
				  20% {
					-webkit-transform: translateY(-2px) skew(-15deg);
							transform: translateY(-2px) skew(-15deg);
					border-top-left-radius: 15px;
					border-bottom-right-radius: 15px;
				  }
				  30% {
					-webkit-transform: translateY(0px) skew(15deg);
							transform: translateY(0px) skew(15deg);
					border-top-left-radius: 15px;
					border-bottom-right-radius: 15px;
				  }
				  40% {
					-webkit-transform: skew(-20deg);
							transform: skew(-20deg);
					width: 210px;
					border-top-left-radius: 20px;
					border-bottom-right-radius: 20px;
				  }
				  50% {
					-webkit-transform: skew(20deg);
							transform: skew(20deg);
					width: 210px;
					border-top-left-radius: 20px;
					border-bottom-right-radius: 20px;
				  }
				  60% {
					-webkit-transform: skew(-15deg);
							transform: skew(-15deg);
					width: 200px;
					border-top-right-radius: 20px;
					border-bottom-left-radius: 20px;
				  }
				  70% {
					-webkit-transform: skew(15deg);
							transform: skew(15deg);
					width: 200px;
					border-top-right-radius: 20px;
					border-bottom-left-radius: 20px;
				  }
				  80% {
					-webkit-transform: skew(-10deg);
							transform: skew(-10deg);
					width: 200px;
					border-top-right-radius: 15px;
					border-bottom-left-radius: 15px;
				  }
				  90% {
					-webkit-transform: skew(5deg);
							transform: skew(5deg);
					width: 200px;
					border-top-right-radius: 10px;
					border-bottom-left-radius: 10px;
				  }
				  100% {
					-webkit-transform: skew(0deg);
							transform: skew(0deg);
					width: 200px;
					border-radius: 5px;
				  }
				}
		</style>
		<style type="text/css">
			.back-link a {
				color: #4ca340;
				text-decoration: none; 
				border-bottom: 1px #4ca340 solid;
			}
			.back-link a:hover,
			.back-link a:focus {
				color: #408536; 
				text-decoration: none;
				border-bottom: 1px #408536 solid;
			}
			h1 {
				height: 100%;
				/* The html and body elements cannot have any padding or margin. */
				margin: 0;
				font-size: 14px;
				font-family: 'Open Sans', sans-serif;
				font-size: 32px;
				margin-bottom: 3px;
			}
			.entry-header {
				text-align: left;
				margin: 0 auto 50px auto;
				width: 80%;
		        max-width: 978px;
				position: relative;
				z-index: 10001;
			}
			#demo-content {
				padding-top: 100px;
			}
		</style>
	
		<script src="${jsBaseUrl}/js/jquery.min-3.1.1.js"></script>
		<script src="${jsBaseUrl}/js/bootstrap.min-3.3.7.js"></script>
		<script src="${jsBaseUrl}/js/main_progress.js"></script>
		
		<script src="${jsBaseUrl}/js/vendor/modernizr-2.6.2.min.js"></script>
		
		<!--script type="text/javascript">   
			$(document).ready(function(){
				window.setTimeout(function () {
					location.href = "${ogUrl}";
				}, 5000);
				$("button").click(function(){
					location.href = "${ogUrl}";
				});
			});
		</script-->
		
		<script type="text/javascript">   
			$(document).ready(function(){
			    var lBar = $(".load");
				var bar = $("button span");
				var button = $("button");
	
				button.on("click", function(){
					 lBar.addClass("loading");
					 setTimeout(function(){
						lBar.removeClass("loading");
						button.addClass("complete");
						location.href = "${ogUrl}";
					 },500);
				});
				window.setTimeout(function () {
					location.href = "${ogUrl}";
				}, 5000);
			});
		</script>
	</head>
	<!--body>
		<button>Skip Me</button>
		<img src="${jsBaseUrl}/images/squill_adv.gif"/>
	</body-->
	<body class="demo">
		<div id="demo-content">

			<header class="entry-header">
	
				<h1 class="entry-title">SQUILL: One application for all your daily social needs</h1>
				<nav class="back-link">
					<span class="nav-previous"><a href="https://play.google.com/store/apps/details?id=com.pack.pack.application" rel="prev"><span class="meta-nav">&larr;</span>CheckUs Out</a></span>
				</nav><!-- .nav-single -->
	
				<div class="container">
				  <button type="submit"><strong>Skip Me <i class="fa fa-paper-plane" aria-hidden="true"></i></strong><span class="bar"><span class="load"></span><span></button>
				</div>
				
			</header>
	
			<div id="loader-wrapper">
				<div id="loader"></div>
	
				<div class="loader-section section-left"></div>
	            <div class="loader-section section-right"></div>
	
			</div>		
			
	
		</div>
	</body>
</html>