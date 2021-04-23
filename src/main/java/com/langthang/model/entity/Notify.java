package com.langthang.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "notify")
public class Notify {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    private String content;
    private Date notifyDate;
    private boolean isSeen;

    @Override
    public String toString() {
        return "Notify{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", notifyDate=" + notifyDate +
                ", isSeen=" + isSeen +
                '}';
    }
}
