package eu.daiad.web.model.error;

public enum SharedErrorCode implements ErrorCode {
	UNKNOWN,
	NOT_IMPLEMENTED,
	RESOURCE_RELEASE_FAILED,
	PARSE_ERROR,
	JSON_SERIALIZE_ERROR,
	INVALID_PARSED_OBJECT,
	AUTHENTICATION,
	AUTHENTICATION_NO_CREDENTIALS,
	AUTHENTICATION_USERNAME,
	AUTHORIZATION,
	AUTHORIZATION_ANONYMOUS_SESSION,
	AUTHORIZATION_MISSING_ROLE,
	SESSION_EXPIRED,
	RESOURCE_NOT_FOUND,
	METHOD_NOT_SUPPORTED,
	RESOURCE_DOES_NOT_EXIST,
	INVALID_TIME_ZONE,
	TIMEZONE_NOT_FOUND,
	LOCALE_NOT_SUPPORTED,
	DIR_CREATION_FAILED,
	INVALID_SRID,
	FILESYSTEM_NOT_SUPPORTED;

	@Override
	public String getMessageKey() {
		return (this.getClass().getSimpleName() + '.' + name());
	}
}
