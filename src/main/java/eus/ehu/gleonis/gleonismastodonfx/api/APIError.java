package eus.ehu.gleonis.gleonismastodonfx.api;

public class APIError {

    /* 400(Client error):
    */
    // 401(Unauthorized): "error": "The access token is invalid"

    /* 422(Unprocessable entity):
        (App) "error": "Validation failed: Redirect URI must be an absolute URI."



    */

    private int error_code;

    private String error;

    public APIError(int error_code, String error) {
        this.error_code = error_code;
        this.error = error;
    }

    public int getError_code() {
        return error_code;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "APIError{" +
                "error_code=" + error_code +
                ", error='" + error + '\'' +
                '}';
    }
}
