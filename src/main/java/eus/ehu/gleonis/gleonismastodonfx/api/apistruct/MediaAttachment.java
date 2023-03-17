package eus.ehu.gleonis.gleonismastodonfx.api.apistruct;

public class MediaAttachment {

        String id;

        MediaAttachmentType type;

        String url;

        String preview_url;

        String description;

        String blurhash;

        public String getId() {
            return id;
        }

        public MediaAttachmentType getType() {
            return type;
        }

        public String getUrl() {
            return url;
        }

        public String getPreview_url() {
            return preview_url;
        }

        public String getDescription() {
            return description;
        }

        public String getBlurhash() {
            return blurhash;
        }
}
