package edu.cnm.deepdive.dicewareservice.model.entity;


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Entity(name = "AuthenticatedUser")
@Component
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "user_id", updatable = false, nullable = false)
  private Long id;

  @NonNull
  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date created;

  @NonNull
  @Column(nullable = false, updatable = false, unique = true)
  private String oauthKey;

  public Long getId() {
    return id;
  }

  @NonNull
  public Date getCreated() {
    return created;
  }

  @NonNull
  public String getOuthKey() {
    return oauthKey;
  }

  public void setOuthKey(@NonNull String outhKey) {
    this.oauthKey = outhKey;
  }


}

