package org.example.finalsocialnetwork.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class FriendshipRequest extends Entity<Pair<Long, Long>> {
    private Long iduser1;
    private Long iduser2;
    private LocalDateTime date;

    public FriendshipRequest(Long iduser1, Long iduser2, LocalDateTime date) {
        this.iduser1 = iduser1;
        this.iduser2 = iduser2;
        this.date = date;
    }

    public Long getIduser1() {
        return iduser1;
    }

    public void setIduser1(Long iduser1) {
        this.iduser1 = iduser1;
    }

    public Long getIduser2() {
        return iduser2;
    }

    public void setIduser2(Long iduser2) {
        this.iduser2 = iduser2;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipRequest that = (FriendshipRequest) o;
        return Objects.equals(iduser1, that.iduser1) && Objects.equals(iduser2, that.iduser2) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iduser1, iduser2, date);
    }
}
