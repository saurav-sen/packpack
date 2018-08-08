<html>
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta property="og:title" content="${ogTitle}"/>
		<meta property="og:description" content="${ogDescription}"/>
		<meta property="og:image" content="${jsBaseUrl}/SquillShare.jpg"/>
		<meta property="og:url" content="${ogUrl}"/>
		
		<link rel="stylesheet" href="${jsBaseUrl}/css/w3.css">
		<style type="text/css">
			img {
		      width: auto;
		      height : auto;
		      max-height: 100%;
		      max-width: 100%;
		    }
			.no-decoration-hyperlink {
			  text-decoration: none;
			}
			
			.img-logo {
			   display: block;
			   margin-left: auto;
			   margin-right: auto;
			   max-width: 200px;
			   max-height: 200px;
			   height: auto;
			   width: auto;
			}
			.header-border{
				margin: 3% 0;
			}
			.heading{
			  font-size: 18px;
			}
			.footer-squill{
			float:left;
			margin: 2% 0;
			}
			.footer-img{
			float:right;
			margin: 1% 0;
			}
			
		</style>
	</head>
	
	<body>
	    <div class="w3-container">
	    
			<div class="w3-container w3-border-top w3-border-left w3-border-bottom w3-border-right">
			<div class="w3-container w3-red header-border">
			 <h3><center>SQUILL</center></h3>
			</div>
			
			    <#if logo??>
					<img src="${jsBaseUrl}/css/${logo}" class="img-logo w3-border-bottom">	
				</#if>
				
				<br />
				
				<header class="w3-container heading">			  	
				  <b>${ogTitle}
				</header>
	
				<div class="w3-container w3-center">
					<a href="${ogUrl}" class="no-decoration-hyperlink">
						<img src="${ogImage}">
					</a>
				</div>
				
				<div class="w3-container">
				  <a href="${ogUrl}" class="no-decoration-hyperlink">
					  <p>${summaryText}</p>
				   </a>
				</div>
	
				<footer class="w3-container w3-red">
				  <div class="footer-squill">SQUILL</div>
				  <div class= "footer-img" ><a href="https://play.google.com/store/apps/details?id=com.pack.pack.application" target="_blank">
					   <img src="${jsBaseUrl}/css/playstore.png" style="width:100px"></div>
				  </a>
				</footer>
				<br />
			  </div>
			</div>
			<br />
		</div>
	
	  </body>
</html>