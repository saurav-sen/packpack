<html>
	<head>
		<meta property="og:title" content="${ogTitle}"/>
		<meta property="og:description" content="${ogDescription}"/>
		<meta property="og:image" content="${ogImage}"/>
		<script src="./jquery.min.js"></script>
		
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
		<img src="./squill_adv.gif"/>
	</body>
</html>