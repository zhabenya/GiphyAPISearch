import com.google.gson.JsonParser;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.logging.Logger.getLogger;

public class GiphyApi {

    private static final Logger logger = getLogger(GiphyApi.class.getName());
    private static final String giphyUrl = "http://api.giphy.com";
    private static final String path = "v1/gifs/search";
    private static final String apiKey = "dc6zaTOxFJmzC";
    private Map<String, String> map;

    public GiphyApi() {
        map = new HashMap<>();
        setParameter("api_key", apiKey);
    }

    public GiphyApi setApiKey(String newApiKey) {
        setParameter("api_key", newApiKey);
        return this;
    }

    public GiphyApi setQuery(String searchQuery) {
        setParameter("q", searchQuery);
        return this;
    }

    public GiphyApi setLanguage(String language) {
        setParameter("lang", language);
        return this;
    }

    public GiphyApi setLimit(String limit) {
        setParameter("limit", limit);
        return this;
    }

    public GiphyApi setOffset(String offset) {
        setParameter("offset", offset);
        return this;
    }

    public List sendRequest() {
        return sendRequestAndGetJson(200, true).getList("data");
    }

    public String sendNoApiKeyRequest() {
        return sendRequestAndGetJson(401, false).get("message");
    }

    public String sendInvalidApiRequest() {
        return sendRequestAndGetJson(403, true).get("message");
    }

    public List sendRequestAndGetResponseIds() {
        List idList = new ArrayList();
        new JsonParser().parse(sendRequestAndGetJson(200, true).prettify()).getAsJsonObject().get("data")
                .getAsJsonArray().forEach(jsonElement -> idList.add(jsonElement.getAsJsonObject().get("id").getAsString()));
        return idList;
    }

    private void setParameter(String key, String value) {
        logger.info(format("Set parameter '%s': '%s'", key, value));
        map.put(key, value);
    }

    private JsonPath sendRequestAndGetJson(int statusCode, boolean useMap) {
        logger.info(format("Send request to %s/%s with%s parameters", giphyUrl, path, useMap ? "" : "out"));
        logger.info(format("Expected response: %s", statusCode));
        return RestAssured.with()
                .baseUri(giphyUrl)
                .queryParameters(useMap ? map : new HashMap<>())
                .get(path)
                .then().statusCode(statusCode)
                .extract()
                .body().jsonPath();
    }
}
