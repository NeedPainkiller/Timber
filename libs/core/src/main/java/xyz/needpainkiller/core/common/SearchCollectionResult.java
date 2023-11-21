package xyz.needpainkiller.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCollectionResult<E extends Serializable> implements Serializable {

    @Serial
    private static final long serialVersionUID = -7372386524381520007L;
    private Collection<E> collection;
    private Long foundRows;

    public Collection<E> getCollection() {
        if (collection == null || collection.isEmpty()) {
            return Collections.emptyList();
        }
        return collection;
    }

    public Long getFoundRows() {
        return Math.max(foundRows, 0);
    }


    public static <E extends Serializable> SearchCollectionResultBuilder<E> builder() {
        return new SearchCollectionResultBuilder<>();
    }

    public static class SearchCollectionResultBuilder<E extends Serializable> {
        private Collection<E> collection;
        private Long foundRows;

        SearchCollectionResultBuilder() {
        }

        public SearchCollectionResultBuilder<E> collection(Collection<E> collection) {
            this.collection = collection;
            return this;
        }

        public SearchCollectionResultBuilder<E> foundRows(Long foundRows) {
            this.foundRows = Objects.requireNonNullElse(foundRows, 0L);
            return this;
        }

        public SearchCollectionResultBuilder<E> foundRows(Integer foundRows) {
            if (foundRows == null) {
                this.foundRows = 0L;
            } else {
                this.foundRows = foundRows.longValue();
            }
            return this;
        }


        public SearchCollectionResult<E> build() {
            return new SearchCollectionResult<>(this.collection, this.foundRows);
        }

        public String toString() {
            return "SearchCollectionResult.SearchCollectionResultBuilder(collection=" + this.collection + ", foundRows=" + this.foundRows + ")";
        }
    }
}
