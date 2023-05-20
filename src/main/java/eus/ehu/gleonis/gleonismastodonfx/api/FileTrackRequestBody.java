package eus.ehu.gleonis.gleonismastodonfx.api;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import static okhttp3.internal._UtilCommonKt.closeQuietly;

public class FileTrackRequestBody extends RequestBody {
    private static final int SEGMENT_SIZE = 2048; // okio.Segment.SIZE

    private final File file;
    private final Consumer<Double> listener;
    private final MediaType contentType;

    public FileTrackRequestBody(File file, MediaType contentType, Consumer<Double> listener) {
        this.file = file;
        this.contentType = contentType;
        this.listener = listener;
    }

    @Override
    public long contentLength() {
        return file.length();
    }

    @Override
    public MediaType contentType() {
        return contentType;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;
        try {
            source = Okio.source(file);
            long total = 0;
            long read;

            while ((read = source.read(sink.getBuffer(), SEGMENT_SIZE)) != -1) {
                total += read;
                sink.flush();
                listener.accept(total / (double) contentLength());
            }
        } finally {
            if (source != null)
                closeQuietly(source);
        }
    }
}
