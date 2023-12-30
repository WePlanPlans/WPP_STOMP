package org.tenten.tentenstomp.domain.tour.entity;

import jakarta.persistence.*;
import lombok.*;
import org.tenten.tentenstomp.domain.tour.entity.TourItemImage.ItemImage.ItemImageConverter;
import org.tenten.tentenstomp.global.converter.JsonConverter;

import java.util.List;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourItemImage {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "tourItemImageId")
    private Long id;

    @Convert(converter = ItemImageConverter.class)
    @Column(columnDefinition = "JSON")
    private ItemImage itemImage;

    @OneToOne
    @JoinColumn(name = "tourItemId")
    private TourItem tourItem;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemImage {
        List<ApiItemImage> images;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemImage itemImage = (ItemImage) o;
            return Objects.equals(images, itemImage.images);
        }

        @Override
        public int hashCode() {
            return Objects.hash(images);
        }

        public static class ItemImageConverter extends JsonConverter<ItemImage> {
        }
    }
}