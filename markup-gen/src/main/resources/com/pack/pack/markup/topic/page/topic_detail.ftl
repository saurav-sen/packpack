<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>${topicName}</title>
	
	<meta name="og:title" content="${topicName}">
    <meta property="og:type" content="website" />
    <meta property="og:image" itemprop="image primaryImageOfPage" content="${topicWallpaperUrl}" />

	<script type='text/javascript' src='unitegallery/js/jquery-11.0.min.js'></script>	
	
	<script type='text/javascript' src='unitegallery/js/ug-common-libraries.js'></script>	
	<script type='text/javascript' src='unitegallery/js/ug-functions.js'></script>
	<script type='text/javascript' src='unitegallery/js/ug-thumbsgeneral.js'></script>
	<script type='text/javascript' src='unitegallery/js/ug-thumbsstrip.js'></script>
	<script type='text/javascript' src='unitegallery/js/ug-touchthumbs.js'></script>
	<script type='text/javascript' src='unitegallery/js/ug-panelsbase.js'></script>
	<script type='text/javascript' src='unitegallery/js/ug-strippanel.js'></script>
	<script type='text/javascript' src='unitegallery/js/ug-gridpanel.js'></script>
	<script type='text/javascript' src='unitegallery/js/ug-thumbsgrid.js'></script>
	<script type='text/javascript' src='unitegallery/js/ug-tiles.js'></script>
	<script type='text/javascript' src='unitegallery/js/ug-tiledesign.js'></script>
	<script type='text/javascript' src='unitegallery/js/ug-avia.js'></script>
	<script type='text/javascript' src='unitegallery/js/ug-slider.js'></script>
	<script type='text/javascript' src='unitegallery/js/ug-sliderassets.js'></script>
	<script type='text/javascript' src='unitegallery/js/ug-touchslider.js'></script>
	<script type='text/javascript' src='unitegallery/js/ug-zoomslider.js'></script>	
	<script type='text/javascript' src='unitegallery/js/ug-video.js'></script>
	<script type='text/javascript' src='unitegallery/js/ug-gallery.js'></script>
	<script type='text/javascript' src='unitegallery/js/ug-lightbox.js'></script>
	<script type='text/javascript' src='unitegallery/js/ug-carousel.js'></script>
	<script type='text/javascript' src='unitegallery/js/ug-api.js'></script>

	<link rel='stylesheet' href='unitegallery/css/unite-gallery.css' type='text/css' />
	
	<script type='text/javascript' src='unitegallery/themes/default/ug-theme-default.js'></script>
	<link rel='stylesheet' 		  href='unitegallery/themes/default/ug-theme-default.css' type='text/css' />
	
	<style type="text/css">
		.avoid_scrolls {
			overflow-x: hidden;
			overflow-y: hidden;
		}
	</style>
	
</head>

<body>
    
    <div>
     	<header>
            <h1>${topicName}</h1>
            <br/>
        </header>

			
		<section class="avoid_scrolls">
			<img src="${topicWallpaperUrl}"/>
		</section>

       <article>
		   <h3>Story</h3>
		   <br/>
		   <p>${topicDescription}</p>
		</article>
    </div>
	
	<div>
		<#list packs as pack>
			<h2>${pack.name}</h2>		
	       
			<div id="${pack.id}" style="display:none;">
			
				 <#list pack.attachments as attachment>
	                <#if attachment.mimeType == "IMAGE">
						<img alt="${attachment.title}"
					    	src="${attachment.attachmentThumbnailUrl}"
					    	data-image="${attachment.attachmentUrl}"
					    	data-description="${attachment.description}">
	                <#elseif attachment.mimeType == "VIDEO">
						<img alt="${attachment.title}"
					    	src="${attachment.attachmentThumbnailUrl}"
					   		data-type="html5video"
					    	data-image="${attachment.attachmentThumbnailUrl}"				   
					    	data-videomp4="${attachment.attachmentUrl}"
			    	    	data-description="${attachment.description}">	
	                </#if>		
				
				</#list>			
					 			 
			</div>
	      
			
			<br/>
		
		</#list>
	</div>
	
	
	<script type="text/javascript">

		jQuery(document).ready(function(){

			<#list packs as pack>
				jQuery("#' + pack.id).unitegallery();
            </#list>
			
		});
		
	</script>


</body>
</html>