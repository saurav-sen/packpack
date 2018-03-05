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
			APIConstants.User.Settings.VALUE), GET_PROFILE_PICTURE(
			APIConstants.User.ID, APIConstants.Attachment.FILE_NAME), LOAD_RESOURCE(
			APIConstants.ProtectedResource.RESOURCE_URL,
			APIConstants.Image.WIDTH, APIConstants.Image.HEIGHT), LOAD_EXTERNAL_RESOURCE(
			APIConstants.ExternalResource.RESOURCE_URL), GET_ALL_PROMOTIONAL_FEEDS(
			APIConstants.User.ID, APIConstants.PageInfo.PAGE_LINK), GET_ALL_NEWS_FEEDS(
			APIConstants.User.ID, APIConstants.PageInfo.PAGE_LINK), SYNC_TIME, VALIDATE_USER_NAME(
			APIConstants.User.USERNAME), ANDROID_APK_URL(), CRAWL_FEED(
			APIConstants.ExternalResource.RESOURCE_URL), ISSUE_PASSWD_RESET_LINK(
			APIConstants.User.USERNAME), RESET_USER_PASSWD(
			APIConstants.User.USERNAME,
			APIConstants.User.PasswordReset.VERIFIER_CODE,
			APIConstants.User.PasswordReset.NEW_PASSWORD), ISSUE_SIGNUP_VERIFIER(
			APIConstants.User.Register.EMAIL, APIConstants.User.Register.NAME);

	private String[] paramNames;

	private COMMAND(String... paramNames) {
		this.paramNames = paramNames;
	}

	public String[] getParamNames() {
		return paramNames;
	}
}