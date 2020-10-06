package com.roomiemain.roomie.house.invite;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Invite {

    public final static int VALIDITY_TIME = 60 * 60 * 24; // one day

    public final static String LINK_SCHEME = "http";

    public final static String LINK_HOST = "roomie.me";

    public final static String LINK_PATH_PREFIX = "join";

    public final static String LINK_ID_QUERY_PARAM = "id";

    private @DocumentId String id;

    private String houseId;

    private @ServerTimestamp Date expirationDate;


    public Invite() {

    }

    public Invite(String houseId, Date expirationDate) {
        this.houseId = houseId;
        this.expirationDate = expirationDate;
    }


    public String returnInviteLink() {
        return String.format(LINK_SCHEME + "://" + LINK_HOST + "/" + LINK_PATH_PREFIX + "?" + LINK_ID_QUERY_PARAM + "=%s", id);
    }

    public String getId() {
        return id;
    }

    public String getHouseId() {
        return houseId;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
