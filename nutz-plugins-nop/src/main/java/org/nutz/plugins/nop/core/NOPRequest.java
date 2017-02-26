package org.nutz.plugins.nop.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.http.Cookie;
import org.nutz.http.Header;
import org.nutz.http.Http;
import org.nutz.http.Request.METHOD;
import org.nutz.json.Json;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.Encoding;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file NOPRequest.java
 *
 * @description NOP请求 warp of Request
 *
 * @time 2016年8月31日 下午2:18:56
 *
 */

public class NOPRequest {

	public static NOPRequest get(String service) {
		return create(service, METHOD.GET, new HashMap<String, Object>());
	}

	public static NOPRequest get(String service, Header header) {
		return NOPRequest.create(service, METHOD.GET, new HashMap<String, Object>(), header);
	}

	public static NOPRequest post(String service) {
		return create(service, METHOD.POST, new HashMap<String, Object>());
	}

	public static NOPRequest post(String service, Header header) {
		return NOPRequest.create(service, METHOD.POST, new HashMap<String, Object>(), header);
	}

	public static NOPRequest create(String service, METHOD method) {
		return create(service, method, new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	public static NOPRequest create(String service, METHOD method, String paramsAsJson, Header header) {
		return create(service, method, (Map<String, Object>) Json.fromJson(paramsAsJson), header);
	}

	@SuppressWarnings("unchecked")
	public static NOPRequest create(String service, METHOD method, String paramsAsJson) {
		return create(service, method, (Map<String, Object>) Json.fromJson(paramsAsJson));
	}
	public static NOPRequest create(String service,  String body, Header header) {
		NOPRequest request = create(service, METHOD.POST);
		request.setHeader(header);
		request.setData(body);
		return request;
	}

	public static NOPRequest create(String service, METHOD method, Map<String, Object> params) {
		return NOPRequest.create(service, method, params, Header.create());
	}

	public static NOPRequest create(String service,
			METHOD method,
			Map<String, Object> params,
			Header header) {
		return new NOPRequest().setMethod(method).setParams(params).setService(service).setHeader(header);
	}

	public String getService() {
		return service;
	}

	private NOPRequest() {
	}

	private String service;
	private METHOD method;
	private Header header;
	private Map<String, Object> params;
	private byte[] data;
	private InputStream inputStream;
	private String enc = Encoding.UTF8;
	private String appSecret;
	
	

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public String getURLEncodedParams() {
		final StringBuilder sb = new StringBuilder();
		if (params != null) {
			for (Entry<String, Object> en : params.entrySet()) {
				final String key = en.getKey();
				Object val = en.getValue();
				if (val == null)
					val = "";
				Lang.each(val, new Each<Object>() {
					@Override
					public void invoke(int index, Object ele, int length) throws ExitLoop, ContinueLoop, LoopException {
						sb.append(Http.encode(key, enc))
								.append('=')
								.append(Http.encode(ele, enc)).append('&');
					}
				});
			}
			if (sb.length() > 0)
				sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	public InputStream getInputStream() {
		if (inputStream != null) {
			return inputStream;
		} else {
			if (null == data) {
				try {
					return new ByteArrayInputStream(getURLEncodedParams().getBytes(enc));
				} catch (UnsupportedEncodingException e) {
					throw Lang.wrapThrow(e);
				}
			}
			return new ByteArrayInputStream(data);
		}
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public byte[] getData() {
		return data;
	}

	public NOPRequest setData(byte[] data) {
		this.data = data;
		return this;
	}

	public NOPRequest setData(String data) {
		try {
			this.data = data.getBytes(Encoding.UTF8);
		} catch (UnsupportedEncodingException e) {
			// 不可能
		}
		return this;
	}

	private NOPRequest setParams(Map<String, Object> params) {
		this.params = params;
		return this;
	}

	public NOPRequest setService(String service) {
		this.service = service;
		return this;
	}

	public METHOD getMethod() {
		return method;
	}

	public boolean isGet() {
		return METHOD.GET == method;
	}

	public boolean isPost() {
		return METHOD.POST == method;
	}

	public boolean isDelete() {
		return METHOD.DELETE == method;
	}

	public boolean isPut() {
		return METHOD.PUT == method;
	}

	public NOPRequest setMethod(METHOD method) {
		this.method = method;
		return this;
	}

	public Header getHeader() {
		return header;
	}

	public NOPRequest setHeader(Header header) {
		this.header = header;
		return this;
	}

	public NOPRequest setCookie(Cookie cookie) {
		header.set("Cookie", cookie.toString());
		return this;
	}

	public Cookie getCookie() {
		String s = header.get("Cookie");
		if (null == s)
			return new Cookie();
		return new Cookie(s);
	}

	/**
	 * 设置发送内容的编码,仅对String或者Map<String,Object>类型的data有效
	 */
	public NOPRequest setEnc(String reqEnc) {
		if (reqEnc != null)
			this.enc = reqEnc;
		return this;
	}

	public String getEnc() {
		return enc;
	}
}
