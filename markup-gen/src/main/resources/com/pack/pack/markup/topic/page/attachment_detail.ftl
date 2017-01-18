<!doctype html>
<html lang="en">
    <head>
        <meta charset="utf-8" />
        <title>${model.title}</title>
        
        <meta name="og:title" content="${model.title}">
        <meta property="og:type" content="website" />
        <meta property="og:image" itemprop="image primaryImageOfPage" content="${model.attachmentUrl}" />
        
        <meta name="description" content="${model.title}">
        <meta name="author" content="Squill">
        <meta name="viewport" content="width=device-width, initial-scale=1">
		<style type="text/css">
			.avoid_scrolls {
				overflow-x: hidden;
				overflow-y: hidden;
			}
		</style>
    </head>
    <body>
        <header>
            <h1>${model.title}</h1>
            <br/>
        </header>

			
			<section class="avoid_scrolls">
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
		</section>

       <article>
		   <h3>Story</h3>
		   <br/>
		   <p>${model.description}</p>
		</article>

    </body>
</html>
