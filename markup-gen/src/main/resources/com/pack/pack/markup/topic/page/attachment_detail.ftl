<!doctype html>
<html lang="en">
    <head>
        <meta charset="utf-8" />
        <title>${model.title}</title>
        
        <meta name="og:title" content="${model.title}">
        <meta property="og:type" content="website" />
        <#if model.mimeType == "VIDEO">
        		<meta property="og:image" itemprop="image primaryImageOfPage" content="${model.attachmentThumbnailUrl}" />
        	<#else>
        		<meta property="og:image" itemprop="image primaryImageOfPage" content="${model.attachmentUrl}" />
        </#if>
        
        <meta name="description" content="${model.title}">
        <meta name="author" content="Squill">
        <meta name="viewport" content="width=device-width, initial-scale=1">
		<style type="text/css">
			.heading{
				margin-left:17%;
			}
			.avoid_scrolls {
				overflow-x: hidden;
				overflow-y: hidden;
			}
			.share_img{
					/* height:50%; */
					margin-left:10%;
					width:27%;
					box-shadow:3px 3px 10px gray;
			}
			.desc{			
				margin-right:30%;
				margin-top:10%;
				float:right;
			}
			.desc h2{
				font-family:Swiss;
				font-size:28px;
				margin-left:15%;
			}
			.desc p{
				width:100%;
				height:auto;
				font-size:20px;
				text-align:justify;
				font-family:Swiss;
			}
						
			@media screen and (max-width:400px)
			{
				.heading{
					margin-left:30%;
				}
				.share_img{
						/* height:50%; */
						margin-left:25%;
						width:50%;
						box-shadow:2px 2px 10px gray;
				}
				.desc{		
					margin-top:11%;
					margin-left:35%;
					float:none;
				}
				.desc p{
					font-size:15px;
				}
				.desc h2{
					font-size:20px;
					margin-left:12%;
				}
			}
		</style>
    </head>
    <body>
        <header>
            <h1>${model.title}</h1>
            <br/>
        </header>

			
		<div class="avoid_scrolls">
			<#if model.mimeType == "VIDEO">
  				<#if model.showEmbedded>
  					<iframe width="50%" height="345" src="${model.attachmentUrl}"></iframe>
  				<#else>
  					<video style="width:620px;height:350px;" poster="${model.attachmentThumbnailUrl}" controls crossorigin>
					  <source src="${model.attachmentUrl}" type="video/mp4" />
					</video>
  				</#if>
  			<#elseif model.mimeType == "IMAGE">
             	<img src="${model.attachmentUrl}"/>
            </#if>

	       <div class="desc">
			   <h3>Story</h3>
			   <br/>
			   <p>${model.description}</p>
		   </div>
	   </div>

    </body>
</html>
