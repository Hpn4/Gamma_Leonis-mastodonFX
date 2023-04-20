package eus.ehu.gleonis.gleonismastodonfx.api;

import java.io.Reader;

public record RequestResult(Reader response, int responseCode, String paginationLink) {

}
