package lmy86263.mapper.client.model;

import lombok.Getter;

/**
 * @author lmy86263
 * @date 2018/08/16
 */
@Getter
public class ColumnDefinition {
    private final String name;
    private final String family;

    private ColumnDefinition(Builder builder) {
        this.name = builder.name;
        this.family = builder.family;
    }

    public static class Builder {
        private final String name;
        private final String family;

        public Builder(String name, String family) {
            this.name = name;
            this.family = family;
        }

        public ColumnDefinition build() {
            return new ColumnDefinition(this);
        }
    }
}
