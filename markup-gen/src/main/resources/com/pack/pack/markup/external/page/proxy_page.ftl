<html>
	<head>
		<meta property="og:title" content="${ogTitle}"/>
		<meta property="og:description" content="${ogDescription}"/>
		<meta property="og:image" content="${ogImage}"/>
		<meta property="og:url" content="${ogUrl}"/>
		<script src="${jsBaseUrl}/js/jquery.min-3.1.1.js"></script>
		
		<script type="text/javascript">   
			$(document).ready(function(){
				window.setTimeout(function () {
					location.href = "${ogUrl}";
				}, 5000);
				$("button").click(function(){
					location.href = "${ogUrl}";
				});
			});
		</script>
	</head>
	<body>
		<button>Skip Me</button>
		<img src="${jsBaseUrl}/images/squill_adv.gif"/>
	</body>
</html>