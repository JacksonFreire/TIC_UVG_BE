package com.uvg.digital.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {

	@Value("${google.recaptcha.secret-key}")
	private String recaptchaSecret;

	private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

	public boolean verifyRecaptcha(String recaptchaToken) {
		RestTemplate restTemplate = new RestTemplate();
		Map<String, String> body = new HashMap<>();
		body.put("secret", recaptchaSecret);
		body.put("response", recaptchaToken);

		Map<String, Object> response = restTemplate.postForObject(RECAPTCHA_VERIFY_URL, body, Map.class);
		return (Boolean) response.get("success");
	}

}
