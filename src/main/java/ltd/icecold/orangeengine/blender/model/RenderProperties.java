package ltd.icecold.orangeengine.blender.model;

public class RenderProperties {
    public static final RenderProperties DEFAULT = RenderProperties.builder().build();

    boolean isTransparent;

    public RenderProperties(Builder builder) {
        this.isTransparent = builder.isTransparent;
    }

    public boolean isTransparent() {
        return this.isTransparent;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        boolean isTransparent = false;

        public Builder transparency(boolean isTransparent) {
            this.isTransparent = isTransparent;
            return this;
        }

        public RenderProperties build() {
            return new RenderProperties(this);
        }
    }
}
