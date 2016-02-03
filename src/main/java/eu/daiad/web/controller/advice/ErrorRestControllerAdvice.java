package eu.daiad.web.controller.advice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.daiad.web.model.Error;
import eu.daiad.web.model.ResourceNotFoundException;
import eu.daiad.web.model.RestResponse;

@ControllerAdvice()
public class ErrorRestControllerAdvice {

	private static final Log logger = LogFactory.getLog(ErrorRestControllerAdvice.class);

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResponseEntity<RestResponse> handleException(Exception ex) {
		logger.error("Unhandled exception has occured in Controller class.", ex);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

		int code = Error.ERROR_UNKNOWN;
		String message = "An unhandled exception has occured";

		if (ex instanceof ResourceNotFoundException) {
			status = HttpStatus.NOT_FOUND;

			code = Error.ERROR_NOT_FOUND;
			message = ex.getMessage();
		} else if (ex instanceof AccessDeniedException) {
			status = HttpStatus.FORBIDDEN;

			code = Error.ERROR_FORBIDDEN;
			message = ex.getMessage();
		}

		return new ResponseEntity<RestResponse>(new RestResponse(code, message), headers, status);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseBody
	public ResponseEntity<RestResponse> handleException(HttpMessageNotReadableException ex) {
		Throwable mostSpecificCause = ex.getMostSpecificCause();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpStatus status = HttpStatus.OK;

		if (mostSpecificCause != null) {
			return new ResponseEntity<RestResponse>(
							new RestResponse(Error.ERROR_PARSE, mostSpecificCause.getMessage()), headers, status);
		} else {
			return new ResponseEntity<RestResponse>(new RestResponse(Error.ERROR_PARSE, "Request parsing has failed"),
							headers, status);
		}
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseBody
	public ResponseEntity<RestResponse> handleException(HttpRequestMethodNotSupportedException ex) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpStatus status = HttpStatus.OK;

		return new ResponseEntity<RestResponse>(new RestResponse(Error.ERROR_NOT_FOUND, String.format(
						"Method [%s] not supported.", ex.getMethod())), headers, status);
	}

}
