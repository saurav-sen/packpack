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
			APIConstants.User.SEARCH_USER_BY_NAME), GET_USER_FOLLOWED_TOPIC_LIST(
			APIConstants.PageInfo.PAGE_LINK, APIConstants.User.ID), FOLLOW_TOPIC(
			APIConstants.User.ID, APIConstants.Topic.ID), NEGLECT_TOPIC(
			APIConstants.Topic.ID, APIConstants.User.ID), GET_TOPIC_BY_ID(
			APIConstants.Topic.ID), CREATE_NEW_TOPIC(
			APIConstants.Topic.OWNER_ID, APIConstants.Topic.OWNER_NAME,
			APIConstants.Topic.NAME, APIConstants.Topic.DESCRIPTION), GET_PACK_BY_ID(
			APIConstants.Pack.ID), GET_ALL_PACKS_IN_DEFAULT_TOPICS(
			APIConstants.User.ID, APIConstants.PageInfo.PAGE_LINK), GET_ALL_PACKS_IN_TOPIC(
			APIConstants.User.ID, APIConstants.Topic.ID,
			APIConstants.PageInfo.PAGE_LINK), FORWARD_PACK(
			APIConstants.Pack.ID, APIConstants.ForwardPack.FROM_USER_ID,
			APIConstants.ForwardPack.TO_USER_ID), FORWARD_PACK_OVER_EMAIL(
			APIConstants.Pack.ID, APIConstants.ForwardPack.FROM_USER_ID,
			APIConstants.ForwardPack.TO_USER_EMAIL), ADD_COMMENT(
			APIConstants.Pack.ID, APIConstants.Comment.FROM_USER_ID,
			APIConstants.Comment.FROM_USER_NAME, APIConstants.Comment.COMMENT), ADD_LIKE_TO_PACK(
			APIConstants.User.ID, APIConstants.Pack.ID);

	private String[] paramNames;

	private COMMAND(String... paramNames) {
		this.paramNames = paramNames;
	}

	public String[] getParamNames() {
		return paramNames;
	}
}