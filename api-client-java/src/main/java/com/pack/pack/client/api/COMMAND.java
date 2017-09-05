package com.pack.pack.client.api;

/**
 * 
 * @author Saurav
 *
 */
public enum COMMAND {

	SIGN_IN(APIConstants.Login.CLIENT_KEY, APIConstants.Login.CLIENT_SECRET,
			APIConstants.Login.USERNAME, APIConstants.Login.PASSWORD), SIGN_UP(), SIGN_OUT(), EDIT_USER_CATEGORIES(
			APIConstants.TopicCategories.FOLLOWED_CATEGORIES), GET_USER_CATEGORIES(
			APIConstants.User.ID), GET_USER_BY_ID(APIConstants.User.ID), GET_USER_BY_USERNAME(
			APIConstants.User.USERNAME), SEARCH_USER_BY_NAME(
			APIConstants.User.NAME_SEARCH_PATTERN), UPLOAD_USER_PROFILE_PICTURE(
			APIConstants.User.ID, APIConstants.User.PROFILE_PICTURE), UPDATE_USER_SETTINGS(
			APIConstants.User.ID, APIConstants.User.Settings.KEY,
			APIConstants.User.Settings.VALUE), GET_USER_FOLLOWED_TOPIC_LIST(
			APIConstants.PageInfo.PAGE_LINK, APIConstants.User.ID), FOLLOW_TOPIC(
			APIConstants.User.ID, APIConstants.Topic.ID), PROMOTE_TOPIC(
			APIConstants.Topic.ID, APIConstants.User.ID), NEGLECT_TOPIC(
			APIConstants.Topic.ID, APIConstants.User.ID), GET_TOPIC_BY_ID(
			APIConstants.Topic.ID), CREATE_NEW_TOPIC(
			APIConstants.Topic.OWNER_ID, APIConstants.Topic.OWNER_NAME,
			APIConstants.Topic.NAME, APIConstants.Topic.DESCRIPTION,
			APIConstants.Topic.LOCALITY_ADDRESS, APIConstants.Topic.CITY,
			APIConstants.Topic.COUNTRY), EDIT_EXISTING_TOPIC(
			APIConstants.Topic.OWNER_ID, APIConstants.Topic.ID,
			APIConstants.Topic.NAME, APIConstants.Topic.DESCRIPTION), GET_USER_OWNED_TOPICS(
			APIConstants.User.ID), GET_PACK_BY_ID(APIConstants.Pack.ID), EDIT_TOPIC_SETTINGS(
			APIConstants.Topic.ID, APIConstants.User.ID,
			APIConstants.TopicSettings.KEY, APIConstants.TopicSettings.VALUE), GET_ALL_ATTACHMENT_COMMENTS(
			APIConstants.PackAttachment.ID), GET_ALL_PACKS_IN_DEFAULT_TOPICS(
			APIConstants.User.ID, APIConstants.PageInfo.PAGE_LINK), GET_ALL_PACKS_IN_TOPIC(
			APIConstants.User.ID, APIConstants.Topic.ID,
			APIConstants.PageInfo.PAGE_LINK), PROMOTE_PACK_ATTACHMENT(
			APIConstants.PackAttachment.ID, APIConstants.User.ID), GET_ALL_ATTACHMENTS_IN_PACK(
			APIConstants.Pack.ID, APIConstants.Topic.ID, APIConstants.User.ID,
			APIConstants.PageInfo.PAGE_LINK), PROMOTE_PACK(
			APIConstants.Pack.ID, APIConstants.User.ID), /*
														 * FORWARD_PACK(
														 * APIConstants.Pack.ID,
														 * APIConstants
														 * .ForwardPack
														 * .FROM_USER_ID,
														 * APIConstants
														 * .ForwardPack
														 * .TO_USER_ID),
														 * FORWARD_PACK_OVER_EMAIL
														 * (
														 * APIConstants.Pack.ID,
														 * APIConstants
														 * .ForwardPack
														 * .FROM_USER_ID,
														 * APIConstants
														 * .ForwardPack
														 * .TO_USER_EMAIL),
														 */ADD_COMMENT_TO_PACK(
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
			APIConstants.User.ID, APIConstants.Attachment.FILE_NAME), GET_ORIGINAL_IMAGE_ATTACHMENT(
			APIConstants.Topic.ID, APIConstants.Pack.ID,
			APIConstants.Attachment.FILE_NAME, APIConstants.Image.WIDTH,
			APIConstants.Image.HEIGHT), GET_THUMBNAIL_VIDEO_ATTACHMENT(
			APIConstants.Topic.ID, APIConstants.Pack.ID,
			APIConstants.Attachment.FILE_NAME), GET_ORIGINAL_VIDEO_ATTACHMENT(
			APIConstants.Topic.ID, APIConstants.Pack.ID,
			APIConstants.Attachment.FILE_NAME), CREATE_NEW_PACK(
			APIConstants.User.ID, APIConstants.Topic.ID,
			APIConstants.Pack.TITLE, APIConstants.Pack.STORY), UPLOAD_IMAGE_PACK(
			APIConstants.Attachment.FILE_ATTACHMENT,
			APIConstants.Attachment.TITLE, APIConstants.Attachment.DESCRIPTION,
			APIConstants.Attachment.STORY, APIConstants.Topic.ID,
			APIConstants.User.ID), ADD_IMAGE_TO_PACK(
			APIConstants.Attachment.FILE_ATTACHMENT, APIConstants.Topic.ID,
			APIConstants.Pack.ID, APIConstants.User.ID,
			APIConstants.Attachment.TITLE, APIConstants.Attachment.DESCRIPTION), ADD_SHARED_IMAGE_TO_TOPIC(
			APIConstants.Attachment.FILE_ATTACHMENT, APIConstants.Topic.ID,
			APIConstants.User.ID, APIConstants.Attachment.TITLE,
			APIConstants.Attachment.DESCRIPTION), UPLOAD_VIDEO_PACK(
			APIConstants.Attachment.FILE_ATTACHMENT,
			APIConstants.Attachment.TITLE, APIConstants.Attachment.DESCRIPTION,
			APIConstants.Attachment.STORY, APIConstants.Topic.ID,
			APIConstants.User.ID), ADD_VIDEO_TO_PACK(
			APIConstants.Attachment.FILE_ATTACHMENT, APIConstants.Topic.ID,
			APIConstants.Pack.ID, APIConstants.User.ID), ADD_VIDEO_TO_PACK_EXTERNAL_LINK(
			APIConstants.Topic.ID, APIConstants.Pack.ID, APIConstants.User.ID,
			APIConstants.Attachment.TITLE, APIConstants.Attachment.DESCRIPTION,
			APIConstants.Attachment.ATTACHMENT_URL,
			APIConstants.Attachment.ATTACHMENT_THUMBNAIL_URL), LOAD_RESOURCE(
			APIConstants.ProtectedResource.RESOURCE_URL,
			APIConstants.Image.WIDTH, APIConstants.Image.HEIGHT), LOAD_EXTERNAL_RESOURCE(
			APIConstants.ExternalResource.RESOURCE_URL), GET_ALL_DISCUSSIONS_FOR_TOPIC(
			APIConstants.Topic.ID, APIConstants.User.ID,
			APIConstants.PageInfo.PAGE_LINK), GET_ALL_DISCUSSIONS_FOR_PACK(
			APIConstants.Pack.ID, APIConstants.User.ID,
			APIConstants.PageInfo.PAGE_LINK), GET_ALL_REPLIES_FOR_DISCUSSION(
			APIConstants.Discussion.ID, APIConstants.User.ID,
			APIConstants.PageInfo.PAGE_LINK), START_DISCUSSION_ON_TOPIC(
			APIConstants.Topic.ID, APIConstants.User.ID,
			APIConstants.Discussion.TITLE, APIConstants.Discussion.CONTENT), START_DISCUSSION_ON_PACK(
			APIConstants.Pack.ID, APIConstants.User.ID,
			APIConstants.Discussion.TITLE, APIConstants.Discussion.CONTENT), ADD_REPLY_TO_DISCUSSION(
			APIConstants.Discussion.ID, APIConstants.User.ID,
			APIConstants.Discussion.CONTENT, APIConstants.Discussion.TYPE), GET_DISCUSSION_BY_ID(
			APIConstants.Discussion.ID, APIConstants.User.ID), ADD_LIKE_TO_DISCUSSION(
			APIConstants.Discussion.ID, APIConstants.User.ID,
			APIConstants.Discussion.TYPE), GET_ALL_PROMOTIONAL_FEEDS(
			APIConstants.User.ID, APIConstants.PageInfo.PAGE_LINK), GET_ALL_NEWS_FEEDS(
			APIConstants.User.ID, APIConstants.PageInfo.PAGE_LINK), DELETE_ATTACHMENT(
			APIConstants.PackAttachment.ID, APIConstants.Pack.ID,
			APIConstants.Topic.ID), GET_ALL_SYSTEM_SUPPORTED_CATEGORIES, SYNC_TIME, VALIDATE_USER_NAME(
			APIConstants.User.USERNAME), ANDROID_APK_URL(), CRAWL_FEED(
			APIConstants.ExternalResource.RESOURCE_URL), ADD_STORY_TO_ATTACHMENT(
			APIConstants.PackAttachment.ID, APIConstants.AttachmentStory.STORY), GET_STORY_FROM_ATTACHMENT(
			APIConstants.PackAttachment.ID, APIConstants.User.ID), ISSUE_PASSWD_RESET_LINK(
			APIConstants.User.USERNAME), RESET_USER_PASSWD(
			APIConstants.User.USERNAME,
			APIConstants.User.PasswordReset.VERIFIER_CODE,
			APIConstants.User.PasswordReset.NEW_PASSWORD), ISSUE_SIGNUP_VERIFIER(
			APIConstants.User.Register.EMAIL, APIConstants.User.Register.NAME), GET_ALL_SHARED_FEEDS_TO_TOPIC(
			APIConstants.Topic.ID, APIConstants.User.ID,
			APIConstants.PageInfo.PAGE_LINK), ADD_SHARED_EXTERNAL_LINK_TO_TOPIC(
			APIConstants.Topic.ID, APIConstants.User.ID,
			APIConstants.Attachment.TITLE, APIConstants.Attachment.DESCRIPTION,
			APIConstants.Attachment.ATTACHMENT_URL,
			APIConstants.Attachment.ATTACHMENT_THUMBNAIL_URL), ADD_SHARED_TEXT_MSG_TO_TOPIC(
			APIConstants.Topic.ID, APIConstants.User.ID,
			APIConstants.Attachment.TITLE, APIConstants.Attachment.DESCRIPTION);

	private String[] paramNames;

	private COMMAND(String... paramNames) {
		this.paramNames = paramNames;
	}

	public String[] getParamNames() {
		return paramNames;
	}
}