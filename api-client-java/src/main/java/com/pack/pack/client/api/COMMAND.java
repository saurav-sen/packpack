package com.pack.pack.client.api;

/**
 * 
 * @author Saurav
 *
 */
public enum COMMAND {

	SIGN_IN(APIConstants.Login.CLIENT_KEY, APIConstants.Login.CLIENT_SECRET,
			APIConstants.Login.USERNAME, APIConstants.Login.PASSWORD), SIGN_UP(), SIGN_OUT(), GET_USER_BY_ID(
			APIConstants.User.ID), GET_USER_BY_USERNAME(
			APIConstants.User.USERNAME), SEARCH_USER_BY_NAME(
			APIConstants.User.NAME_SEARCH_PATTERN), GET_USER_FOLLOWED_TOPIC_LIST(
			APIConstants.PageInfo.PAGE_LINK, APIConstants.User.ID), FOLLOW_TOPIC(
			APIConstants.User.ID, APIConstants.Topic.ID), NEGLECT_TOPIC(
			APIConstants.Topic.ID, APIConstants.User.ID), GET_TOPIC_BY_ID(
			APIConstants.Topic.ID), CREATE_NEW_TOPIC(
			APIConstants.Topic.OWNER_ID, APIConstants.Topic.OWNER_NAME,
			APIConstants.Topic.NAME, APIConstants.Topic.DESCRIPTION), GET_PACK_BY_ID(
			APIConstants.Pack.ID), GET_PACK_ATTACHMENT_BY_ID(
			APIConstants.PackAttachment.ID), GET_ALL_PACKS_IN_DEFAULT_TOPICS(
			APIConstants.User.ID, APIConstants.PageInfo.PAGE_LINK), GET_ALL_PACKS_IN_TOPIC(
			APIConstants.User.ID, APIConstants.Topic.ID,
			APIConstants.PageInfo.PAGE_LINK), GET_ALL_ATTACHMENTS_IN_PACK(
			APIConstants.Pack.ID, APIConstants.Topic.ID, APIConstants.User.ID,
			APIConstants.PageInfo.PAGE_LINK), FORWARD_PACK(
			APIConstants.Pack.ID, APIConstants.ForwardPack.FROM_USER_ID,
			APIConstants.ForwardPack.TO_USER_ID), FORWARD_PACK_OVER_EMAIL(
			APIConstants.Pack.ID, APIConstants.ForwardPack.FROM_USER_ID,
			APIConstants.ForwardPack.TO_USER_EMAIL), ADD_COMMENT_TO_PACK(
			APIConstants.Pack.ID, APIConstants.Comment.FROM_USER_ID,
			APIConstants.Comment.COMMENT), ADD_COMMENT_TO_PACK_ATTACHMENT(
			APIConstants.PackAttachment.ID, APIConstants.Comment.FROM_USER_ID,
			APIConstants.Comment.COMMENT), ADD_LIKE_TO_PACK(
			APIConstants.User.ID, APIConstants.Pack.ID), ADD_LIKE_TO_PACK_ATTACHMENT(
			APIConstants.User.ID, APIConstants.PackAttachment.ID), GET_EGIFT_BY_ID(
			APIConstants.EGift.ID), GET_EGIFTS_BY_BRAND_ID(
			APIConstants.Brand.ID, APIConstants.PageInfo.PAGE_LINK), GET_EGIFTS_BY_CATEGORY(
			APIConstants.EGift.CATEGORY, APIConstants.PageInfo.PAGE_LINK), FORWARD_EGIFT(
			APIConstants.ForwardEGift.FROM_USER_ID,
			APIConstants.ForwardEGift.TO_USER_ID,
			APIConstants.ForwardEGift.TITLE, APIConstants.ForwardEGift.MESSAGE), SEARCH_BRANDS_INFO(
			APIConstants.Brand.COMPANY_NAME), GET_PROFILE_PICTURE(
			APIConstants.User.ID, APIConstants.Attachment.FILE_NAME), GET_THUMBNAIL_IMAGE_ATTACHMENT(
			APIConstants.Topic.ID, APIConstants.Pack.ID,
			APIConstants.Attachment.FILE_NAME), GET_ORIGINAL_IMAGE_ATTACHMENT(
			APIConstants.Topic.ID, APIConstants.Pack.ID,
			APIConstants.Attachment.FILE_NAME), GET_THUMBNAIL_VIDEO_ATTACHMENT(
			APIConstants.Topic.ID, APIConstants.Pack.ID,
			APIConstants.Attachment.FILE_NAME), GET_ORIGINAL_VIDEO_ATTACHMENT(
			APIConstants.Topic.ID, APIConstants.Pack.ID,
			APIConstants.Attachment.FILE_NAME), UPLOAD_IMAGE_PACK(
			APIConstants.Attachment.FILE_ATTACHMENT,
			APIConstants.Attachment.TITLE, APIConstants.Attachment.DESCRIPTION,
			APIConstants.Attachment.STORY, APIConstants.Topic.ID,
			APIConstants.User.ID), ADD_IMAGE_TO_PACK(
			APIConstants.Attachment.FILE_ATTACHMENT, APIConstants.Topic.ID,
			APIConstants.Pack.ID, APIConstants.User.ID), UPLOAD_VIDEO_PACK(
			APIConstants.Attachment.FILE_ATTACHMENT,
			APIConstants.Attachment.TITLE, APIConstants.Attachment.DESCRIPTION,
			APIConstants.Attachment.STORY, APIConstants.Topic.ID,
			APIConstants.User.ID), ADD_VIDEO_TO_PACK(
			APIConstants.Attachment.FILE_ATTACHMENT, APIConstants.Topic.ID,
			APIConstants.Pack.ID, APIConstants.User.ID), LOAD_RESOURCE(
			APIConstants.ProtectedResource.RESOURCE_URL);

	private String[] paramNames;

	private COMMAND(String... paramNames) {
		this.paramNames = paramNames;
	}

	public String[] getParamNames() {
		return paramNames;
	}
}