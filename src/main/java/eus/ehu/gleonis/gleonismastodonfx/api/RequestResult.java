package eus.ehu.gleonis.gleonismastodonfx.api;

public class RequestResult {

    private String response;

    private int responseCode;

    private String paginationLink;

    public RequestResult(String response, int responseCode, String paginationLink) {
        this.response = response;
        this.responseCode = responseCode;
        this.paginationLink = paginationLink;
    }

    public String getResponse() {
        return response;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getPaginationLink() {
        return paginationLink;
    }
}
